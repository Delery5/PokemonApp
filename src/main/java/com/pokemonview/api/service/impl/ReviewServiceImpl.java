package com.pokemonview.api.service.impl;

import com.pokemonview.api.dto.ReviewDTO;
import com.pokemonview.api.exceptions.PokemonNotFoundException;
import com.pokemonview.api.exceptions.ReviewAlreadyExistsException;
import com.pokemonview.api.exceptions.ReviewNotFoundException;
import com.pokemonview.api.models.Pokemon;
import com.pokemonview.api.models.Review;
import com.pokemonview.api.repository.PokemonRepository;
import com.pokemonview.api.repository.ReviewRepository;
import com.pokemonview.api.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Autowired
    private final ReviewRepository reviewRepository;

    @Autowired
    private final PokemonRepository pokemonRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, PokemonRepository pokemonRepository) {
        this.reviewRepository = reviewRepository;
        this.pokemonRepository = pokemonRepository;
    }

    @Override
    public ReviewDTO createReview(int pokemonId, ReviewDTO reviewDTO) throws ReviewAlreadyExistsException {
        Review review = mapToEntity(reviewDTO);

        Pokemon pokemon = pokemonRepository.findById(pokemonId).orElseThrow(() -> new PokemonNotFoundException("Pokemon with associated review not found"));

        review.setPokemon(pokemon);

        Review newReview = reviewRepository.save(review);

        return mapToDTO(newReview);
    }

    @Override
    public List<ReviewDTO> getReviewsByPokemonId(int id) throws ReviewNotFoundException {
        List<Review> reviews = reviewRepository.findByPokemonId(id);

        // Check if the list is empty and throw an exception if it is
        if (reviews == null || reviews.isEmpty()) { // Additional null check
            logger.error("No reviews found for Pokémon with ID: {}", id);
            throw new ReviewNotFoundException("No reviews found for Pokémon with ID: " + id);
        }

        return reviews
                .stream()
                .map(review -> mapToDTO(review))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO getReviewById(int reviewId, int pokemonId) {
        // Check if the Pokémon exists by ID
        Pokemon pokemon = pokemonRepository.findById(pokemonId)
                .orElseThrow(() -> {
                    logger.error("Pokemon ID '{}' not found.", pokemonId);
                    return new PokemonNotFoundException("Pokemon ID " + pokemonId + " not found");
                });

        // Check if the Review exists by ID
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("Review ID '{}' not found.", reviewId);
                    return new PokemonNotFoundException("Review ID " + reviewId + " not found");
                });

        if(review.getPokemon().getId() != pokemon.getId()) {
            throw new ReviewNotFoundException("This review does not belong to a pokemon");
        }

        return mapToDTO(review);
    }

    @Override
    public ReviewDTO updateReview(int pokemonId, int reviewId, ReviewDTO reviewDTO) throws ReviewNotFoundException, ReviewAlreadyExistsException {
        // Check if the Pokémon exists by ID
        Pokemon pokemon = pokemonRepository.findById(pokemonId)
                .orElseThrow(() -> {
                    logger.error("Pokemon ID '{}' not found.", pokemonId);
                    return new PokemonNotFoundException("Pokemon ID " + pokemonId + " not found");
                });

        // Check if the Review exists by ID
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("Review ID '{}' not found.", reviewId);
                    return new PokemonNotFoundException("Review ID " + reviewId + " not found");
                });

        if(review.getPokemon().getId() != pokemon.getId()) {
            throw new ReviewNotFoundException("This review does not belong to a pokemon");
        }

        review.setTitle(reviewDTO.getTitle());
        review.setContent(reviewDTO.getContent());
        review.setStart(reviewDTO.getStars());

        Review updateReview = reviewRepository.save(review);

        return mapToDTO(updateReview);
    }

    @Override
    public void deleteReview(int pokemonId, int reviewId) throws ReviewNotFoundException, PokemonNotFoundException {
        // Check if the Pokémon exists by ID
        Pokemon pokemon = pokemonRepository.findById(pokemonId)
                .orElseThrow(() -> {
                    logger.error("Pokemon ID '{}' not found.", pokemonId);
                    return new PokemonNotFoundException("Pokemon ID " + pokemonId + " not found");
                });

        // Check if the Review exists by ID
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("Review ID '{}' not found.", reviewId);
                    return new PokemonNotFoundException("Review ID " + reviewId + " not found");
                });

        if(review.getPokemon().getId() != pokemon.getId()) {
            throw new ReviewNotFoundException("This review does not belong to a pokemon");
        }

        reviewRepository.delete(review);
    }

    private ReviewDTO mapToDTO(Review review){
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(review.getId());
        reviewDTO.setTitle(review.getTitle());
        reviewDTO.setContent(review.getContent());
        reviewDTO.setStars(reviewDTO.getStars());

        return reviewDTO;
    }

    private Review mapToEntity(ReviewDTO reviewDTO){
        Review review = new Review();
        review.setContent(reviewDTO.getContent());
        review.setTitle(reviewDTO.getTitle());
        review.setContent(reviewDTO.getContent());
        review.setStart(reviewDTO.getStars());

        return review;
    }
}
