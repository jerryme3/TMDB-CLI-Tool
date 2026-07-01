package com.tmdb.jerme.models;

import java.util.List;

public record Movie(String title,
                    String releaseDate,
                    String overview,
                    double rating,
                    int votes,
                    boolean isAdult,
                    List<String> genres) {
}
