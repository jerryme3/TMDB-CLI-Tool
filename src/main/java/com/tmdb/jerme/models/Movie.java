package com.tmdb.jerme.models;

public record Movie(String title,
                    String releaseDate,
                    String overview,
                    double rating,
                    int votes,
                    boolean isAdult) {
}
