package com.example.joaopfsilva.moviestage1;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HandleMovieAPI {

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String MovieJsonStr = null;
    String urlMovie = null;
    String urlMovieDetail = null;
    String sortBy = null;
    String page = null;
    String id_movie = null;
    String API_KEY = "";
    private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

    JSONArray JSONmovieList = null;  //jsonString with movieList details
    JSONArray JSONmovieDetail = null;  //jsonString with movie detail

    public HandleMovieAPI() {
    }

    // build URL to get movie list detail
    public boolean CallAPImovieList(String sortBy, String page) {
        this.page = page;
        this.sortBy = sortBy;
        urlMovie = Uri.parse("http://api.themoviedb.org/3/discover/movie").buildUpon()
                .appendQueryParameter("page", page)
                .appendQueryParameter("sort_by", sortBy)
                .appendQueryParameter("api_key", API_KEY)
                .build()
                .toString();

        //retrieve and parse json string from url defined
        JSONmovieList = ConnectAPI(urlMovie);

        if (JSONmovieList == null)
            return false;
        return true;
    }

    // build URL to get details of a specific movie
    public boolean CallAPImovieDetail(String id_movie) {
        this.id_movie = id_movie;

        urlMovieDetail = Uri.parse("http://api.themoviedb.org/3/movie/" + id_movie + "/videos").buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build()
                .toString();
        LOGGER.info(urlMovieDetail);

        //retrieve and parse json string from url defined
        JSONmovieDetail = ConnectAPI(urlMovieDetail);

        if (JSONmovieDetail == null)
            return false;
        return true;
    }

    // handle API calls using urlMovie built in setURLMovie
    private JSONArray ConnectAPI(String urlformat) {
        if (urlMovie == null) {
            return null;
        }

        JSONArray JSONresult = null;

        try {

            URL url = new URL(urlformat);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                MovieJsonStr = null;
            }
            assert inputStream != null;
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                MovieJsonStr = null;
            } else {
                MovieJsonStr = buffer.toString();
            }

        } catch (ProtocolException e) {
            Log.e("HandleMovieAPI", "ConnectAPI: ProtocolException", e);
            MovieJsonStr = null;
            return null;
        } catch (MalformedURLException e) {
            Log.e("HandleMovieAPI", "ConnectAPI: MalformedURLException", e);
            MovieJsonStr = null;
            return null;
        } catch (IOException e) {
            Log.e("HandleMovieAPI", "ConnectAPI: IOException ", e);
            MovieJsonStr = null;
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("HandleMovieAPI", "ConnectAPI: Error closing stream", e);
                    //noinspection ReturnInsideFinallyBlock
                    return null;
                }
            }
        }
        try {
            JSONresult = new JSONObject(MovieJsonStr).getJSONArray("results");

        } catch (JSONException e) {
            Log.e("HandleMovieAPI", "ConnectAPI: JSONException", e);
            return null;
        }

        return JSONresult;
    }


    public List<String> getMoviesTitle() {
        List<String> pageMovieTitle = new ArrayList<>();

        for (int i = 0; i < JSONmovieList.length(); i++) {
            try {
                pageMovieTitle.add(i, JSONmovieList.getJSONObject(i).getString("title"));
            } catch (JSONException e) {
                Log.e("HandleMovieAPI", "getMoviesTitle: JSONException", e);
            }
        }
        return pageMovieTitle;
    }

    //method to retrieve movie title of all movies with IDs in movieIDS
    //used for favorite order
    public List<String> getMoviesTitleFilter(List<Integer> movieIDS) {
        List<String> pageMovieTitle = new ArrayList<>();
        int tmpID = -1;
        int counter = 0;

        for (int i = 0; i < JSONmovieList.length(); i++) {
            try {
                tmpID = JSONmovieList.getJSONObject(i).getInt("id");
                if (movieIDS.contains(tmpID)) {
                    pageMovieTitle.add(counter, JSONmovieList.getJSONObject(i).getString("title"));
                    counter = counter + 1;
                }
            } catch (JSONException e) {
                Log.e("HandleMovieAPI", "getMoviesTitle: JSONException", e);
            }
        }
        return pageMovieTitle;
    }

    public List<String> getPosterPath(String size) {
        List<String> pagePoster = new ArrayList<>();

        for (int i = 0; i < JSONmovieList.length(); i++) {
            try {
                pagePoster.add(i, "http://image.tmdb.org/t/p/" + size + JSONmovieList.getJSONObject(i).getString("poster_path"));
            } catch (JSONException e) {
                Log.e("HandleMovieAPI", "getPosterPath: JSONException", e);
            }
        }
        return pagePoster;
    }

    public List<String> getPosterPathFilter(String size, List<Integer> movieIDs) {
        List<String> pagePoster = new ArrayList<>();
        int tmpID = -1;
        int counter = 0;

        for (int i = 0; i < JSONmovieList.length(); i++) {
            try {
                tmpID = JSONmovieList.getJSONObject(i).getInt("id");
                if (movieIDs.contains(tmpID)) {
                    pagePoster.add(counter, "http://image.tmdb.org/t/p/" + size + JSONmovieList.getJSONObject(i).getString("poster_path"));
                    counter = counter + 1;
                }
            } catch (JSONException e) {
                Log.e("HandleMovieAPI", "getPosterPath: JSONException", e);
            }
        }
        return pagePoster;
    }


    public List<String> getDetailsMovie(int position, String size) {
        List<String> details = new ArrayList<>();

        try {
            details.add(0, JSONmovieList.getJSONObject(position).getString("title"));
            details.add(1, JSONmovieList.getJSONObject(position).getString("release_date"));
            details.add(2, "http://image.tmdb.org/t/p/" + size + JSONmovieList.getJSONObject(position).getString("poster_path"));
            details.add(3, JSONmovieList.getJSONObject(position).getString("popularity"));
            details.add(4, JSONmovieList.getJSONObject(position).getString("vote_count"));
            details.add(5, JSONmovieList.getJSONObject(position).getString("overview"));
            details.add(6, JSONmovieList.getJSONObject(position).getString("vote_average"));
            details.add(7, JSONmovieList.getJSONObject(position).getString("id")); //movie id in api
        } catch (JSONException e) {
            Log.e("HandleMovieAPI", "getDetailsMovie: JSONException", e);
        }
        return details;
    }


    public ArrayList<String> getTrailers() {
        ArrayList<String> trailers = new ArrayList<>();

        for (int i = 0; i < JSONmovieDetail.length(); i++) {
            try {
                trailers.add(i, JSONmovieDetail.getJSONObject(i).getString("key"));

            } catch (JSONException e) {
                Log.e("HandleMovieAPI", "getPosterPath: JSONException", e);
            }
        }

        return trailers;

    }

}
