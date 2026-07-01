package com.tmdb.jerme.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tmdb.jerme.models.Movie;
import com.tmdb.jerme.reader.APIReader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MovieClient {

    private final HttpClient client;
    private final Gson gson;

    public MovieClient() {
        this.client = HttpClient.newBuilder().build();
        this.gson  = new Gson();
    }

    public List<Movie> getMovies(String action) {
        String apiToExecute = switch (action) {
            case "np"      -> APIReader.getAPI("NOW_PLAYING_API");
            case "popular" -> APIReader.getAPI("POPULAR_API");
            case "top"     -> APIReader.getAPI("TOP_RATED_API");
            case "upc"     -> APIReader.getAPI("UPCOMING_API");
            default        -> throw new IllegalArgumentException("Not executable command");
        };

        var listOfMovies = new ArrayList<Movie>();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiToExecute))
                .header("accept", "application/json")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0NGQ2OTBiOWExZGUxOGViMjc4MmQyMjQ3ZTkwNWExYyIsIm5iZiI6MTc4Mjg2OTIyOS4xMTgsInN1YiI6IjZhNDQ2Y2VkYjFlNDM2NWY4MGM0N2IzZiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.3jX8bZZiYjKDSWgIVLhZoXYiW4swrSu66xiteVy30tw")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

            var root      = gson.fromJson(resp.body(), JsonObject.class);
            var arrObj    = root.getAsJsonArray("results");

            for (var element : arrObj) {
                var movie = element.getAsJsonObject();

                var title       = movie.get("title").getAsString();
                var releaseDate = movie.get("release_date").getAsString();
                var overview    = movie.get("overview").getAsString();
                var rating      = movie.get("vote_average").getAsDouble();
                var votes       = movie.get("vote_count").getAsInt();
                var isAdult     = movie.get("adult").getAsBoolean();

                listOfMovies.add(new Movie(
                   title, releaseDate, overview, rating, votes, isAdult
                ));
            }

            return listOfMovies;

        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException("Error: ", e);
        }
    }

}
