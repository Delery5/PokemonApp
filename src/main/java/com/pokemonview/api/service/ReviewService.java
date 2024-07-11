package com.pokemonview.api.service;

import com.pokemonview.api.dto.ReviewDTO;
import com.pokemonview.api.exceptions.ReviewAlreadyExistsException;
import com.pokemonview.api.exceptions.ReviewNotFoundException;

import java.util.List;

public interface ReviewService {

    ReviewDTO createReview(int pokemonId, ReviewDTO reviewDTO) throws ReviewAlreadyExistsException;

    List<ReviewDTO> getReviewsByPokemonId(int id) throws ReviewNotFoundException;

    ReviewDTO getReviewById(int reviewId, int pokemonId) throws ReviewNotFoundException;

    ReviewDTO updateReview(int pokemonId, int reviewId, ReviewDTO reviewDTO) throws ReviewNotFoundException, ReviewAlreadyExistsException;

    void deleteReview(int pokemonId, int reviewId) throws ReviewNotFoundException;
}
