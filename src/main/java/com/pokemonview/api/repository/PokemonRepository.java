package com.pokemonview.api.repository;

import com.pokemonview.api.models.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {

    Optional<Pokemon> findByType(String type);

    boolean existsByName(String name);
}
