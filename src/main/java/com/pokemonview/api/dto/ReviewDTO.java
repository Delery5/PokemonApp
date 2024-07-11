package com.pokemonview.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    private int id;
    private String title;
    private String content;
    private int stars;
}
