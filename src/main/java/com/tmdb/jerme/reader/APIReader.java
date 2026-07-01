package com.tmdb.jerme.reader;

//it reads the API from the .env file

import io.github.cdimascio.dotenv.Dotenv;

public class APIReader {

    private static final Dotenv READER = Dotenv.load();

    public static String getAPIKey() {
        return READER.get("API_KEY");
    }

    public static String getAPI(String action) {
        return READER.get(action) + "?api_key=" + getAPIKey();
    }

}
