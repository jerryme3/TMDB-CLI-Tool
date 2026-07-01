package com.tmdb.jerme;

import com.tmdb.jerme.client.MovieClient;

public class CheckMovies {

    private static final MovieClient MOVIE_CLIENT = new MovieClient();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Argument is expected but was passed.");
            return;
        }

        if (args[0].equalsIgnoreCase("help")) {
            System.out.println("1. np - shows the currently playing movies. format: java -jar target\\com-tmdb-jerme-1.jar np");
            System.out.println("2. popular - shows the popular movies. format: java -jar target\\com-tmdb-jerme-1.jar popular");
            System.out.println("3. top - shows the current top movies. format: java -jar target\\com-tmdb-jerme-1.jar top");
            System.out.println("4. upc - shows the upcoming movies. format java -jar target\\com-tmdb-jerme-1.jar upc");
            return;
        }

        var listOfMovies = MOVIE_CLIENT.getMovies(args[0]);

        for (var movie : listOfMovies) {
            System.out.printf("Title: %s%nRelease Date: %s%nOverview: %s%nRatings: %.2f%nTotal votes: %d%nAdults only? %s%n%n",
                    movie.title(), movie.releaseDate(), movie.overview(), movie.rating(), movie.votes(), movie.isAdult() ? "Yes" : "No");
        }

    }

}
