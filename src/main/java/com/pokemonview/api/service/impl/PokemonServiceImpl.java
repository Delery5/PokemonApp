package com.pokemonview.api.service.impl;

import com.pokemonview.api.dto.PokemonDTO;
import com.pokemonview.api.dto.PokemonResponse;
import com.pokemonview.api.exceptions.PokemonAlreadyExistsException;
import com.pokemonview.api.exceptions.PokemonNotFoundException;
import com.pokemonview.api.models.Pokemon;
import com.pokemonview.api.repository.PokemonRepository;
import com.pokemonview.api.service.PokemonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PokemonServiceImpl implements PokemonService {

    // Pokemon Service Impl class done
    private static final Logger logger = LoggerFactory.getLogger(PokemonServiceImpl.class);
    @Autowired
    private final PokemonRepository pokemonRepository;

    public PokemonServiceImpl(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    @Override
    public PokemonDTO createPokemon(PokemonDTO pokemonDTO) throws PokemonAlreadyExistsException {

        // Check if a Pokemon with the same name already exists
        boolean pokemonExists = pokemonRepository.existsByName(pokemonDTO.getName());

        if (pokemonExists) {
            logger.error("Pokemon name '{}' already exists.", pokemonDTO.getName());
            throw new PokemonAlreadyExistsException("Pokemon name '" + pokemonDTO.getName() + "' already exists.");
        }

        // If the name is unique, create the new Pokemon
        Pokemon pokemon = mapToEntity(pokemonDTO);  // Convert DTO to entity
        Pokemon savedPokemon = pokemonRepository.save(pokemon);  // Save to repository

        logger.info("Pokemon name '{}' created successfully.", savedPokemon.getName());
        return mapToDTO(savedPokemon);  // Convert saved entity back to DTO
    }


    @Override
    public PokemonResponse getAllPokemon(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Pokemon> pokemons = pokemonRepository.findAll(pageable);
        List<Pokemon> listOfPokemon = pokemons.getContent();
        List<PokemonDTO> content = listOfPokemon.stream().map(p -> mapToDTO(p)).collect(Collectors.toList());

        PokemonResponse pokemonResponse = new PokemonResponse();
        pokemonResponse.setContent(content);
        pokemonResponse.setPageNo(pokemons.getNumber());
        pokemonResponse.setPageSize(pokemons.getSize());
        pokemonResponse.setTotalElements(pokemons.getTotalElements());
        pokemonResponse.setTotalPages(pokemons.getTotalPages());
        pokemonResponse.setLast(pokemons.isLast());

        return pokemonResponse;
    }

    @Override
    public PokemonDTO getPokemonById(int id) throws PokemonNotFoundException {

        // Retrieve the Pokemon by ID and handle the case when it's not found
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pokemon Id '{}' not found.", id);
                    return new PokemonNotFoundException("Pokemon Id " + id + " not found.");
                });

        // Map the Pokemon to PokemonDTO
        return mapToDTO(pokemon);
    }


    @Override
    public PokemonDTO updatePokemon(PokemonDTO pokemonDTO, int id) throws PokemonNotFoundException, PokemonAlreadyExistsException{
        // Check if the Pokémon exists by ID
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pokemon ID '{}' not found.", id);
                    return new PokemonNotFoundException("Pokemon ID " + id + " not found");
                });

        // Check if another Pokémon with the same name already exists (optional)
        if (pokemonRepository.existsByName(pokemonDTO.getName()) && !pokemon.getName().equals(pokemonDTO.getName())) {
            logger.error("Pokemon with name '{}' already exists.", pokemonDTO.getName());
            throw new PokemonAlreadyExistsException("Pokemon with name '" + pokemonDTO.getName() + "' already exists.");
        }
        // Update the Pokémon details
        pokemon.setName(pokemonDTO.getName());
        pokemon.setType(pokemonDTO.getType());

        Pokemon updatedPokemon = pokemonRepository.save(pokemon);  // Save the updated Pokémon

        return mapToDTO(updatedPokemon);  // Return the mapped DTO
    }

    @Override
    public void deletePokemonId(int id) throws PokemonNotFoundException {
        // Use orElseThrow to check if Pokémon exists
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pokemon ID '{}' not found.", id);
                    return new PokemonNotFoundException("Pokemon ID " + id + " not found.");
                });

        // Delete the retrieved Pokémon
        pokemonRepository.delete(pokemon);
        logger.info("Pokemon ID '{}' deleted successfully.", id);
    }


    private PokemonDTO mapToDTO (Pokemon pokemon){

        PokemonDTO pokemonDto = new PokemonDTO();
        pokemonDto.setId(pokemon.getId());
        pokemonDto.setName(pokemon.getName());
        pokemonDto.setType(pokemon.getType());
        return pokemonDto;
    }

    private Pokemon mapToEntity(PokemonDTO pokemonDTO){
        Pokemon pokemon = new Pokemon();
        pokemon.setName(pokemonDTO.getName());
        pokemon.setType(pokemonDTO.getType());
        return pokemon;
    }
}
