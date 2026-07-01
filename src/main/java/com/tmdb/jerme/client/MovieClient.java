package com.tmdb.jerme.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tmdb.jerme.models.Movie;
import com.tmdb.jerme.reader.APIReader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieClient {

    private final HttpClient client;
    private final Gson gson;

    public MovieClient() {
        this.client = HttpClient.newBuilder().build();
        this.gson  = new Gson();
    }

    public List<Movie> getMovies(String genre, String action) {
        String apiToExecute = switch (action) {
            case "np"      -> APIReader.getAPI("NOW_PLAYING_API");
            case "popular" -> APIReader.getAPI("POPULAR_API");
            case "top"     -> APIReader.getAPI("TOP_RATED_API");
            case "upc"     -> APIReader.getAPI("UPCOMING_API");
            default        -> throw new IllegalArgumentException("Not executable command");
        };

        var listOfMovies = new ArrayList<Movie>();
        var mapOfGenres  = mapGenres();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiToExecute))
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + APIReader.getTMDBToken())
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

            var root      = gson.fromJson(resp.body(), JsonObject.class);
            var arrObj    = root.getAsJsonArray("results");

            for (var element : arrObj) {
                var genreIds = new ArrayList<Integer>();
                var movie    = element.getAsJsonObject();

                var title       = movie.get("title").getAsString();
                var releaseDate = movie.get("release_date").getAsString();
                var overview    = movie.get("overview").getAsString();
                var rating      = movie.get("vote_average").getAsDouble();
                var votes       = movie.get("vote_count").getAsInt();
                var isAdult     = movie.get("adult").getAsBoolean();
                var genreCode   = movie.get("genre_ids").getAsJsonArray();

                for (var id : genreCode) {
                    genreIds.add(id.getAsInt()); //extraction of codes will be used to extract the real string value of its genre
                }

                listOfMovies.add(new Movie(
                   title, releaseDate, overview, rating, votes, isAdult, filterGenre(genreIds, mapOfGenres)
                ));
            }

            return listOfMovies
                    .stream()
                    .filter(movie -> movie.genres().contains(genre.trim().toLowerCase()))
                    .toList();


        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException("Error: ", e);
        }
    }

    public Map<Integer, String> mapGenres() {
        var mapOfGenres = new HashMap<Integer, String>();

        var request = HttpRequest
                .newBuilder()
                .uri(URI.create(APIReader.getAPI("GENRE_MAP_API")))
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + APIReader.getTMDBToken())
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            var root       = gson.fromJson(response.body(), JsonObject.class);
            var arrWithMap = root.getAsJsonArray("genres");

            for (JsonElement genre : arrWithMap) {
                var curMap = genre.getAsJsonObject();

                mapOfGenres.put(curMap.get("id").getAsInt(), curMap.get("name").getAsString().toLowerCase());
            }

            return mapOfGenres;

        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException("Error: ", e);
        }
    }

    public List<String> filterGenre(List<Integer> genreIds, Map<Integer, String> mapOfGenres) {
        var genres = new ArrayList<String>();

        for (int id : genreIds) {
            genres.add(mapOfGenres.get(id).toLowerCase());
        }

        return genres;
    }

}
