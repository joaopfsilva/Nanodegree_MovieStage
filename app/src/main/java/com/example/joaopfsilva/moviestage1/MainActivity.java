package com.example.joaopfsilva.moviestage1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
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

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static java.lang.String.format;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static Toast toast;
    private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

    private String API_KEY = "";
    private String BASE_IMG_URL = "http://image.tmdb.org/t/p/w342/";
    private String COUNTRY = "USA";
    private Boolean TO_RATE_MODE = false;

    TmdbMovies movies = null;

    StrictMode.ThreadPolicy policy = null;

    GridView gv;
    List<MovieDb> urlMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOGGER.debug("ONCREATE");

        policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (connectedDevice()) {
            movies = new TmdbApi(API_KEY).getMovies();
        } else

        {
            handleToastMsg("No internet connection!!", Toast.LENGTH_LONG);
            return;
        }

        FloatingActionButton changeToRated = (FloatingActionButton) findViewById(R.id.swapOrder);
        changeToRated.setOnClickListener(this);

        urlMovies = getPopularNMovies(movies, 1); //populate urlMovies with list of popular movies
        urlMovies.addAll(getPopularNMovies(movies, 2)); //populate urlMovies with list of popular movies
        urlMovies.addAll(getPopularNMovies(movies, 3));

        gv = (GridView) findViewById(R.id.gridView);
        gv.setAdapter(new GridAdapter());

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleMovieDetail(urlMovies.get(position));
            }
        });


    }

    public class GridAdapter extends BaseAdapter {
        MovieDb posterpath;

        @Override
        public int getCount() {
            return urlMovies.size();
        }

        @Override
        public Object getItem(int position) {
            return urlMovies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.single_grid, parent, false);
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);

            posterpath = (MovieDb) getItem(position); //get posterpath from object getItem
            Picasso.with(getApplicationContext()).load(format("%s%s", BASE_IMG_URL, posterpath.getPosterPath())).into(iv);

            return convertView;
        }
    }

    private boolean connectedDevice() {
        ConnectivityManager com = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return com.getNetworkInfo(TYPE_MOBILE).isConnectedOrConnecting() || com.getNetworkInfo(TYPE_WIFI).isConnectedOrConnecting();

    }

    private List<MovieDb> getPopularNMovies(TmdbMovies movies, int page) {
        return movies.getPopularMovies(COUNTRY, page).getResults();
    }

    private List<MovieDb> getTopRatedNMovies(TmdbMovies movies, int page) {
        try {
            LOGGER.info("»»»» " + movies.getLatestMovie().getOriginalTitle());
            return movies.getTopRatedMovies(COUNTRY, page).getResults();//.getResults();
        }catch(Exception e){
            LOGGER.info("***** " + e.getMessage());
        }
        return null;
    }

    //Method to handle a Toast message, with user defined duration
    public void handleToastMsg(String msg, int Toastduration) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplicationContext(), msg, Toastduration);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    //Method to handle movie information to be sent to DetailMovie activity
    private void handleMovieDetail(MovieDb movie) {
        if (getResources().getConfiguration().orientation == 1) {//PORTRAIT
            Intent i_detail1 = new Intent(getApplicationContext(), DetailMovie.class);

            i_detail1.putExtra("name_movie", movie.getOriginalTitle());
            i_detail1.putExtra("synopsis", movie.getOverview());
            i_detail1.putExtra("rating", String.valueOf(movie.getVoteAverage()));
            i_detail1.putExtra("yearRelease", movie.getReleaseDate());
            i_detail1.putExtra("urlPath", format("%s%s", BASE_IMG_URL, movie.getPosterPath()));
            startActivity(i_detail1);

        } else { //LANDSCAPE

            TextView originalTitle = (TextView) findViewById(R.id.nameMovie);
            TextView yearRelease = (TextView) findViewById(R.id.yearRelease);
            TextView rating = (TextView) findViewById(R.id.rating);
            TextView synopsis = (TextView) findViewById(R.id.synopsis);

            // movieposterPath
            ImageView posterMovie = (ImageView) findViewById(R.id.poster);
            Picasso.with(getApplicationContext()).load(BASE_IMG_URL + movie.getPosterPath()).into(posterMovie);

            // yearRelease
            yearRelease.setText(movie.getReleaseDate().substring(0, 4));

            // rating
            rating.setText(String.valueOf(movie.getVoteAverage()) + "/10");

            // Title
            originalTitle.setText(movie.getOriginalTitle());

            //synopsis
            synopsis.setText(movie.getOverview());

        }
    }

    @Override
    public void onClick(View v) {
        LOGGER.info("ONVIEW");

        switch (v.getId()) {
            case R.id.swapOrder:
                TO_RATE_MODE = !TO_RATE_MODE;
                if (TO_RATE_MODE) {
                    getTopRatedNMovies(movies, 1);
                    //handleToastMsg(getTopRatedNMovies(movies, 1), Toast.LENGTH_LONG);
                   /* urlMovies = getTopRatedNMovies(movies, 1); //populate urlMovies with list of popular movies
                    urlMovies.addAll(getTopRatedNMovies(movies, 2)); //populate urlMovies with list of popular movies
                    urlMovies.addAll(getTopRatedNMovies(movies, 3));*/
                } else {
                    urlMovies = getPopularNMovies(movies, 1); //populate urlMovies with list of popular movies
                    urlMovies.addAll(getPopularNMovies(movies, 2)); //populate urlMovies with list of popular movies
                    urlMovies.addAll(getPopularNMovies(movies, 3));
                }

                gv.setAdapter(new GridAdapter());

                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        handleMovieDetail(urlMovies.get(position));
                    }
                });
                break;
        }
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
        switch (id) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                //MainActivity.this.finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
