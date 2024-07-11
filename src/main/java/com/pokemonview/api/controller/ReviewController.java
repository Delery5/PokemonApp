package com.pokemonview.api.controller;

import com.pokemonview.api.dto.ReviewDTO;
import com.pokemonview.api.exceptions.PokemonNotFoundException;
import com.pokemonview.api.exceptions.ReviewAlreadyExistsException;
import com.pokemonview.api.exceptions.ReviewNotFoundException;
import com.pokemonview.api.service.PokemonService;
import com.pokemonview.api.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class ReviewController {

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private ReviewService reviewService;

    public ReviewController(PokemonService pokemonService, ReviewService reviewService) {
        this.pokemonService = pokemonService;
        this.reviewService = reviewService;
    }

    @GetMapping("/pokemon/{pokemonId}/reviews/{id}")
    public ResponseEntity<Object> getReviewsByPokemonId(@PathVariable(value = "pokemonId") int pokemonId, @PathVariable(value = "id") int reviewId) {
        try {

            return ResponseEntity.ok(reviewService.getReviewById(pokemonId, reviewId));
        }catch (ReviewNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Pokemon does not exist",
                            e.getMessage()
                    ));
        }catch (PokemonNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Pokemon does not exist",
                            e.getMessage()
                    ));
        }
    }

    @PostMapping("/pokemon/{pokemonId}/reviews")
    public ResponseEntity<Object> createReview(@PathVariable(value = "pokemonId") int pokemonId, @RequestBody ReviewDTO reviewDTO) {

        try {
            ReviewDTO newReview = reviewService.createReview(pokemonId,reviewDTO);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse(
                    201,
                    "Review successfully created",
                    newReview
            ));
        }catch (ReviewAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(
                            409,
                            "Review already exists",
                            e.getMessage()
                    ));
        }
    }

    @PutMapping("/pokemon/{pokemonId}/reviews/{id}")
    public ResponseEntity<Object> updateReview(@PathVariable(value = "pokemonId") int pokemonId,@PathVariable(value = "id") int reviewId, @RequestBody ReviewDTO reviewDTO) {
        try {
            ReviewDTO updateNewReview = reviewService.updateReview(pokemonId, reviewId, reviewDTO);
            return new ResponseEntity<>(updateNewReview, HttpStatus.OK);
        }catch (ReviewNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Review does not exist",
                            e.getMessage()
                    ));
        }
    }


        @DeleteMapping("/pokemon/{pokemonId}/reviews/{id}")
    public ResponseEntity<Object> deleteReview(@PathVariable(value = "pokemonId") int pokemonId, @PathVariable(value = "id") int reviewId) {
        try {
            reviewService.deleteReview(pokemonId, reviewId);
            return new ResponseEntity<>("Pokemon delete", HttpStatus.OK);
        }catch (PokemonNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Pokemon does not exist",
                            e.getMessage()
                    ));
        }catch (ReviewNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            404,
                            "Review does not exist",
                            e.getMessage()
                    ));
        }
    }
}
