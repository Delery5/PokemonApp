package com.pokemonview.api.controller;

import com.pokemonview.api.dto.PokemonDTO;
import com.pokemonview.api.dto.PokemonResponse;
import com.pokemonview.api.exceptions.PokemonAlreadyExistsException;
import com.pokemonview.api.exceptions.PokemonNotFoundException;
import com.pokemonview.api.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class PokemonController {

    private PokemonService pokemonService;

    @Autowired
    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("pokemon")
    public ResponseEntity<PokemonResponse> getPokemons(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return new ResponseEntity<>(pokemonService.getAllPokemon(pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("pokemon/{id}")
    public ResponseEntity<Object> pokemonDetail(@PathVariable int id) {
        try {
            return ResponseEntity.ok(pokemonService.getPokemonById(id));
        }catch (PokemonNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Pokemon does not exist",
                            e.getMessage()
                    ));
        }
    }

    @PostMapping("pokemon/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createPokemon(@RequestBody PokemonDTO pokemonDto) {
        try {
            PokemonDTO newPokemon = pokemonService.createPokemon(pokemonDto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse(
                            201,
                            "Pokemon successfully created",
                            newPokemon
                    ));
        }catch (PokemonAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(
                            409,
                            "Pokemon already exists",
                            e.getMessage()
                    ));
        }
    }
    @PutMapping("pokemon/{id}/update")
    public ResponseEntity<Object> updatePokemon(@RequestBody PokemonDTO pokemonDto, @PathVariable("id") int pokemonId) {

        try {
            PokemonDTO response = pokemonService.updatePokemon(pokemonDto, pokemonId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PokemonNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Pokemon not found",
                            ex.getMessage()
                    ));  // Return 404 error response
        } catch (PokemonAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(
                            409,
                            "Conflict with existing Pokemon",
                            ex.getMessage()
                    ));  // Return 409 error response
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            500,
                            "Internal Server Error",
                            ex.getMessage()
                    ));  // Return 500 error response
        }
    }

    @DeleteMapping("pokemon/{id}/delete")
    public ResponseEntity<Object> deletePokemon(@PathVariable("id") int pokemonId) {
        try {
            pokemonService.deletePokemonId(pokemonId);
            return new ResponseEntity<>("Pokemon delete", HttpStatus.OK);
        }catch (PokemonNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Pokemon does not exist",
                            e.getMessage()
                    ));
        }
    }
}