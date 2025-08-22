package com.gigfinder.dto;

import jakarta.validation.constraints.*;

public class RatingDTO {
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    @Size(max = 500, message = "Comment must be less than 500 characters")
    private String comment;
    
    // Constructors, getters, and setters
    public RatingDTO() {}
    
    public RatingDTO(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}







