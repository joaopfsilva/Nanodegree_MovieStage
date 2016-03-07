package com.example.joaopfsilva.moviestage1;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static Toast toast;
    private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

    private String API_KEY = "****";
    private String BASE_IMG_URL = "http://image.tmdb.org/t/p/w342/";
    private Boolean RATED_MODE = false;

    TmdbMovies movies = null;
    List<MovieDb> popular_movies = null;
    MovieDb movies1 = null;
    MovieDb movies2 = null;
    MovieDb movies3 = null;
    MovieDb movies4 = null;

    int tmp = 0;
    Logger logger = LoggerFactory.getLogger(MainActivity.class);
    StrictMode.ThreadPolicy policy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOGGER.debug("ONCREATE");

        policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!isAirplaneModeOn(getApplicationContext())) {
            movies = new TmdbApi(API_KEY).getMovies();
        } else {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(getApplicationContext(), "No internet connection!!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            return;
        }


        //Populate the 4-grid view with the poster of each movie
        GridArrangement(getPopularNMovies(movies, 4));

    }

    private void GridArrangement(List<MovieDb> grid_movies) {

        if (!isAirplaneModeOn(getApplicationContext())) {

            //IMAGE 11
            ImageView imageView11 = (ImageView) findViewById(R.id.imageView11);
            imageView11.setOnClickListener(this);
            movies1 = grid_movies.get(0);
            Picasso.with(getApplicationContext()).load(BASE_IMG_URL + movies1.getPosterPath()).into(imageView11);


            //IMAGE 12
            ImageView imageView12 = (ImageView) findViewById(R.id.imageView12);
            imageView12.setOnClickListener(this);
            movies2 = grid_movies.get(1);
            Picasso.with(getApplicationContext()).load(BASE_IMG_URL + movies2.getPosterPath()).into(imageView12);


            //IMAGE 21
            ImageView imageView21 = (ImageView) findViewById(R.id.imageView21);
            imageView21.setOnClickListener(this);
            movies3 = grid_movies.get(2);
            Picasso.with(getApplicationContext()).load(BASE_IMG_URL + movies3.getPosterPath()).into(imageView21);


            //IMAGE 22
            ImageView imageView22 = (ImageView) findViewById(R.id.imageView22);
            imageView22.setOnClickListener(this);
            movies4 = grid_movies.get(3);
            Picasso.with(getApplicationContext()).load(BASE_IMG_URL + movies4.getPosterPath()).into(imageView22);


        }
    }

    private List<MovieDb> getPopularNMovies(TmdbMovies movies, int i) {

        if (i > movies.getPopularMovies("USA", 1).getResults().size()) {
            LOGGER.error("ERROR: number of N top: {} too high", i);
            return null;
        }

        return movies.getPopularMovies("USA", 1).getResults().subList(0, i);
    }

    private List<MovieDb> getTopRatedNMovies(TmdbMovies movies, int i) {

        if (i > movies.getTopRatedMovies("USA", 1).getResults().size()) {
            LOGGER.error("ERROR: number of N top: {} too high", i);
            return null;
        }

        return movies.getTopRatedMovies("USA", 1).getResults().subList(0, i);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public void SwapOrder(View v) {
        if (RATED_MODE) {
            GridArrangement(getTopRatedNMovies(movies, 4));
            RATED_MODE = false;
        } else {
            GridArrangement(getPopularNMovies(movies, 4));
            RATED_MODE = true;
        }
    }

    @Override
    public void onClick(View v) {
        LOGGER.info("ONVIEW");
        int duration = Toast.LENGTH_SHORT;
        String text = null;

        if (toast != null)
            toast.cancel();


        switch (v.getId()) {

            case R.id.imageView11:
                tmp++;
                switch (tmp) {
                    case 1:
                        text = movies1.getTitle();
                        break;
                    case 2:
                        text = movies1.getReleaseDate();
                        break;
                    case 3:
                        //text = String.format("Popularity: %s", Float.toString(movies1.getPopularity()));

                        break;
                    case 4:
                        //handle_detail_Intent(movies1);

                        if (getResources().getConfiguration().orientation == 1) {//PORTRAIT
                        Intent i_detail1 = i_detail1 = new Intent(getApplicationContext(), DetailMovie.class);

                        i_detail1.putExtra("name_movie", movies1.getTitle());
                        i_detail1.putExtra("synopsis", movies1.getOverview());
                        i_detail1.putExtra("rating", String.valueOf(movies1.getVoteAverage()));
                        i_detail1.putExtra("yearRelease", movies1.getReleaseDate());
                        i_detail1.putExtra("urlPath", String.format("%s%s", BASE_IMG_URL, movies1.getPosterPath()));
                        //  i_detail1.putExtra("duration", movies1.getRuntime());
                        //_detail1.putExtra("trailers", movies1.getVideos().toArray());
                        startActivity(i_detail1);

                    }
                    else{ //LANDSCAPE

                        TextView originalTitle = (TextView) findViewById(R.id.nameMovie);
                        TextView yearRelease = (TextView) findViewById(R.id.yearRelease);
                        TextView rating = (TextView) findViewById(R.id.rating);
                        //TextView movie_duration = (TextView) findViewById(R.id.Duration);
                        TextView synopsis = (TextView) findViewById(R.id.synopsis);

                        // movieposterPath
                        ImageView posterMovie = (ImageView) findViewById(R.id.poster);
                        Picasso.with(getApplicationContext()).load(BASE_IMG_URL + movies1.getPosterPath()).into(posterMovie);

                        // yearRelease
                        yearRelease.setText(movies1.getReleaseDate().substring(0, 4));

                        // rating
                        rating.setText(movies1.getVoteAverage() + "/10");

                        // Duration
                       // movie_duration.setText(movies1.getretRuntime());

                        //synopsis
                        synopsis.setText(movies1.getOverview());

                    }


                /*toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();*/

                        break;

                    case R.id.imageView12:
                        //handle_detail_Intent(movies2);
                        Intent i_detail2 = new Intent(getApplicationContext(), DetailMovie.class);

                        i_detail2.putExtra("name_movie", movies2.getTitle());
                        i_detail2.putExtra("synopsis", movies2.getOverview());
                        i_detail2.putExtra("rating", movies2.getVoteAverage());
                        i_detail2.putExtra("yearRelease", movies2.getReleaseDate());
                        i_detail2.putExtra("urlPath", String.format("%s%s", BASE_IMG_URL, movies2.getPosterPath()));
                        i_detail2.putExtra("duration", movies2.getRuntime());
                        i_detail2.putExtra("trailers", movies2.getVideos().toArray());

                        startActivity(i_detail2);
                /*tmp = 0;
                toast = Toast.makeText(getApplicationContext(), movies2.getTitle(), duration);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();*/
                        break;

                    case R.id.imageView21:
                        //handle_detail_Intent(movies3);
                        Intent i_detail3 = new Intent(getApplicationContext(), DetailMovie.class);

                        i_detail3.putExtra("name_movie", movies3.getTitle());
                        i_detail3.putExtra("synopsis", movies3.getOverview());
                        i_detail3.putExtra("rating", movies3.getVoteAverage());
                        i_detail3.putExtra("yearRelease", movies3.getReleaseDate());
                        i_detail3.putExtra("urlPath", String.format("%s%s", BASE_IMG_URL, movies3.getPosterPath()));
                        i_detail3.putExtra("duration", movies3.getRuntime());
                        i_detail3.putExtra("trailers", movies3.getVideos().toArray());

                        startActivity(i_detail3);

                /*tmp = 0;
                toast = Toast.makeText(getApplicationContext(), movies3.getTitle(), duration);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();*/
                        break;

                    case R.id.imageView22:
                        //handle_detail_Intent(movies4);
                        Intent i_detail4 = new Intent(getApplicationContext(), DetailMovie.class);

                        i_detail4.putExtra("name_movie", movies4.getTitle());
                        i_detail4.putExtra("synopsis", movies4.getOverview());
                        i_detail4.putExtra("rating", movies4.getVoteAverage());
                        i_detail4.putExtra("yearRelease", movies4.getReleaseDate());
                        i_detail4.putExtra("urlPath", String.format("%s%s", BASE_IMG_URL, movies4.getPosterPath()));
                        i_detail4.putExtra("duration", movies4.getRuntime());
                        i_detail4.putExtra("trailers", movies4.getVideos().toArray());

                        startActivity(i_detail4);
                /*tmp = 0;
                toast = Toast.makeText(getApplicationContext(), movies4.getTitle(), duration);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();*/
                        break;
                }
        }
    }

    public void handle_detail_Intent(MovieDb movie) {

        Intent i_detail = new Intent(getApplicationContext(), DetailMovie.class);

        i_detail.putExtra("name_movie", movie.getTitle());
        i_detail.putExtra("synopsis", movie.getOverview());
        i_detail.putExtra("rating", movie.getVoteAverage());
        i_detail.putExtra("yearRelease", movie.getReleaseDate());
        i_detail.putExtra("urlPath", String.format("%s%s", BASE_IMG_URL, movie.getPosterPath()));
        i_detail.putExtra("duration", movie.getRuntime());
        i_detail.putExtra("trailers", movie.getVideos().toArray());

        startActivity(i_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LOGGER.info("ONCREATEOPTIONSMENU");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LOGGER.info("ONOPTIONSITEMSELECTED");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
