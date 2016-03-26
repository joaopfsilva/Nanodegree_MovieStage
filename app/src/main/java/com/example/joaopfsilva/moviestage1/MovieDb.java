package com.example.joaopfsilva.moviestage1;

/**
 * Created by joaopfsilva on 2/20/16.
 */

import android.net.Uri;
import android.os.StrictMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDb {

    private String API_KEY = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDb.class);
    StrictMode.ThreadPolicy policy = null;

    public MovieDb(String API_KEY) {
        LOGGER.info("New object was created");
        this.API_KEY = API_KEY;
        // To keep this example simple, we allow network access
// in the user interface thread
        policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    public Boolean connectAPI() {
        LOGGER.info("CALL_METHOD: CONNECTAPI");
        // Return TRUE if connection was successful
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        String sort = "popularity.desc";
        try {
            // Construct the URL
            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie/?";
            final String SORT_PARAM = "sort_by";
            final String API_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sort)
                    .appendQueryParameter(API_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            //Log.v(LOG_TAG, "Query URI: " + url);

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            LOGGER.info("response code {}", urlConnection.getResponseCode());

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            LOGGER.info("information: {}", buffer);
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return false;
            }
            movieJsonStr = buffer.toString();

            //Log.v(LOG_TAG, "JSON String: " + movieJsonStr);

        } catch (IOException e) {
            LOGGER.error("ERROR: {}", e.getMessage());
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    LOGGER.error("ERROR: {}", e.getMessage());
                    return false;
                }
            }
        }
        return true;
    /*    try {

            //return getMovieDataFromJson(movieJsonStr, movieItems);
        } catch (JSONException e) {
            LOGGER.error("ERROR: {}", e.getMessage());
            e.printStackTrace();
        }
        */

    }

}
