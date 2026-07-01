package com.tmdb.jerme.reader;

//it reads the APIs from the .env file

import io.github.cdimascio.dotenv.Dotenv;

public class APIReader {

    private static final Dotenv READER = Dotenv.load();

    public static String getAPIKey() {
        return READER.get("API_KEY");
    }

    public static String getTMDBToken() {
        return READER.get("TMDB_TOKEN");
    }

    public static String getAPI(String action) {
        return READER.get(action) + "?api_key=" + getAPIKey();
    }

}
