package com.pokemonview.api.service;

import com.pokemonview.api.dto.PokemonDTO;
import com.pokemonview.api.dto.PokemonResponse;
import com.pokemonview.api.exceptions.PokemonAlreadyExistsException;
import com.pokemonview.api.exceptions.PokemonNotFoundException;

public interface PokemonService {

    PokemonDTO createPokemon(PokemonDTO pokemonDTO) throws PokemonAlreadyExistsException;
    PokemonResponse getAllPokemon(int pageNo, int PageSize);
    PokemonDTO getPokemonById(int id) throws PokemonNotFoundException;
    PokemonDTO updatePokemon(PokemonDTO pokemonDTO, int id) throws PokemonNotFoundException, PokemonAlreadyExistsException;
    void deletePokemonId(int id) throws PokemonNotFoundException;
}
