package com.example.joaopfsilva.moviestage1;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static java.lang.String.format;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static Toast toast;
    private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

    private String API_KEY = "";
    private String BASE_IMG_URL = "http://image.tmdb.org/t/p/w342/";
    private String COUNTRY = "USA";
    private Boolean RATED_MODE = false;

    public int page_no = 3; //define the number of pages to be shown.
    public int page = 1; //define the first page to be shown

    TmdbMovies movies = null;
    List<MovieDb> popular_movies = null;
    MovieDb movies1 = null;
    MovieDb movies2 = null;
    MovieDb movies3 = null;
    MovieDb movies4 = null;

    int tmp = 0;
    Logger logger = LoggerFactory.getLogger(MainActivity.class);
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


        if (connectedDevice(getApplicationContext())) {
            movies = new TmdbApi(API_KEY).getMovies();
        } else

        {
            handleToastMsg("No internet connection!!", Toast.LENGTH_LONG);
            return;
        }

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

    private boolean connectedDevice(Context context) {
        ConnectivityManager com = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return com.getNetworkInfo(TYPE_MOBILE).isConnectedOrConnecting() || com.getNetworkInfo(TYPE_WIFI).isConnectedOrConnecting();

    }

    private List<MovieDb> getPopularNMovies(TmdbMovies movies, int page) {
        return movies.getPopularMovies("USA", page).getResults();
    }

    private List<MovieDb> getTopRatedNMovies(TmdbMovies movies, int page) {
        return movies.getTopRatedMovies("USA", page).getResults();
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

        //GridArrangement(getPopularNMovies(movies, 4, page));
        page = (page + 1) % page_no;
    }

    public void handleToastMsg(String msg, int Toastduration) {

        //Toast.makeText(MainActivity.this, msg, Toastduration).show();
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplicationContext(), msg, Toastduration);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

    }

    private void handleMovieDetail(MovieDb movie) {
        if (getResources().getConfiguration().orientation == 1) {//PORTRAIT
            Intent i_detail1 = new Intent(getApplicationContext(), DetailMovie.class);

            i_detail1.putExtra("name_movie", movie.getOriginalTitle());
            i_detail1.putExtra("synopsis", movie.getOverview());
            i_detail1.putExtra("rating", valueOf(movie.getVoteAverage()));
            i_detail1.putExtra("yearRelease", movie.getReleaseDate());
            i_detail1.putExtra("urlPath", format("%s%s", BASE_IMG_URL, movie.getPosterPath()));
            //i_detail1.putExtra("duration", movies1.getRuntime());
            //_detail1.putExtra("trailers", movies1.getVideos().toArray());
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
            rating.setText(movie.getVoteAverage() + "/10");

            // Title
            originalTitle.setText(movie.getOriginalTitle());

            //synopsis
            synopsis.setText(movie.getOverview());

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

                if (getResources().getConfiguration().orientation == 1) {//PORTRAIT
                    Intent i_detail1 = new Intent(getApplicationContext(), DetailMovie.class);

                    i_detail1.putExtra("name_movie", movies1.getTitle());
                    i_detail1.putExtra("synopsis", movies1.getOverview());
                    i_detail1.putExtra("rating", valueOf(movies1.getVoteAverage()));
                    i_detail1.putExtra("yearRelease", movies1.getReleaseDate());
                    i_detail1.putExtra("urlPath", format("%s%s", BASE_IMG_URL, movies1.getPosterPath()));
                    startActivity(i_detail1);

                } else { //LANDSCAPE

                    TextView originalTitle = (TextView) findViewById(R.id.nameMovie);
                    TextView yearRelease = (TextView) findViewById(R.id.yearRelease);
                    TextView rating = (TextView) findViewById(R.id.rating);
                    TextView synopsis = (TextView) findViewById(R.id.synopsis);

                    // movieposterPath
                    ImageView posterMovie = (ImageView) findViewById(R.id.poster);
                    Picasso.with(getApplicationContext()).load(BASE_IMG_URL + movies1.getPosterPath()).into(posterMovie);

                    // yearRelease
                    yearRelease.setText(movies1.getReleaseDate().substring(0, 4));

                    // rating
                    rating.setText(movies1.getVoteAverage() + "/10");

                    //synopsis
                    synopsis.setText(movies1.getOverview());

                }

                break;

            case R.id.imageView12:
                if (getResources().getConfiguration().orientation == 1) {//PORTRAIT
                    Intent i_detail1 = new Intent(getApplicationContext(), DetailMovie.class);

                    i_detail1.putExtra("name_movie", movies2.getTitle());
                    i_detail1.putExtra("synopsis", movies2.getOverview());
                    i_detail1.putExtra("rating", valueOf(movies2.getVoteAverage()));
                    i_detail1.putExtra("yearRelease", movies2.getReleaseDate());
                    i_detail1.putExtra("urlPath", format("%s%s", BASE_IMG_URL, movies2.getPosterPath()));
                    startActivity(i_detail1);

                } else { //LANDSCAPE

                    TextView originalTitle = (TextView) findViewById(R.id.nameMovie);
                    TextView yearRelease = (TextView) findViewById(R.id.yearRelease);
                    TextView rating = (TextView) findViewById(R.id.rating);
                    TextView synopsis = (TextView) findViewById(R.id.synopsis);

                    // movieposterPath
                    ImageView posterMovie = (ImageView) findViewById(R.id.poster);
                    Picasso.with(getApplicationContext()).load(BASE_IMG_URL + movies2.getPosterPath()).into(posterMovie);

                    // yearRelease
                    yearRelease.setText(movies2.getReleaseDate().substring(0, 4));

                    // rating
                    rating.setText(movies2.getVoteAverage() + "/10");

                    //synopsis
                    synopsis.setText(movies2.getOverview());

                }
                break;

            case R.id.imageView21:
                if (getResources().getConfiguration().orientation == 1) {//PORTRAIT
                    Intent i_detail1 = i_detail1 = new Intent(getApplicationContext(), DetailMovie.class);

                    i_detail1.putExtra("name_movie", movies3.getTitle());
                    i_detail1.putExtra("synopsis", movies3.getOverview());
                    i_detail1.putExtra("rating", Float.toString(movies3.getVoteAverage()));
                    i_detail1.putExtra("yearRelease", movies3.getReleaseDate());
                    i_detail1.putExtra("urlPath", format("%s%s", BASE_IMG_URL, movies3.getPosterPath()));
                    startActivity(i_detail1);

                } else { //LANDSCAPE

                    TextView originalTitle = (TextView) findViewById(R.id.nameMovie);
                    TextView yearRelease = (TextView) findViewById(R.id.yearRelease);
                    TextView rating = (TextView) findViewById(R.id.rating);
                    TextView synopsis = (TextView) findViewById(R.id.synopsis);

                    // movieposterPath
                    ImageView posterMovie = (ImageView) findViewById(R.id.poster);
                    Picasso.with(getApplicationContext()).load(BASE_IMG_URL + movies3.getPosterPath()).into(posterMovie);

                    // yearRelease
                    yearRelease.setText(movies3.getReleaseDate().substring(0, 4));

                    // rating
                    rating.setText(Float.toString(movies3.getVoteAverage()));//+ "/10");

                    //synopsis
                    synopsis.setText(movies3.getOverview());
                }
                break;

            case R.id.imageView22:
                if (getResources().getConfiguration().orientation == 1) {//PORTRAIT
                    Intent i_detail1 = i_detail1 = new Intent(getApplicationContext(), DetailMovie.class);

                    i_detail1.putExtra("name_movie", movies4.getTitle());
                    i_detail1.putExtra("synopsis", movies4.getOverview());
                    i_detail1.putExtra("rating", valueOf(movies4.getVoteAverage()));
                    i_detail1.putExtra("yearRelease", movies4.getReleaseDate());
                    i_detail1.putExtra("urlPath", format("%s%s", BASE_IMG_URL, movies4.getPosterPath()));
                    startActivity(i_detail1);

                } else { //LANDSCAPE

                    TextView originalTitle = (TextView) findViewById(R.id.nameMovie);
                    TextView yearRelease = (TextView) findViewById(R.id.yearRelease);
                    TextView rating = (TextView) findViewById(R.id.rating);
                    TextView synopsis = (TextView) findViewById(R.id.synopsis);

                    // movieposterPath
                    ImageView posterMovie = (ImageView) findViewById(R.id.poster);
                    Picasso.with(getApplicationContext()).load(BASE_IMG_URL + movies4.getPosterPath()).into(posterMovie);

                    // yearRelease
                    yearRelease.setText(movies4.getReleaseDate().substring(0, 4));

                    // rating
                    rating.setText(movies4.getVoteAverage() + "/10");

                    //synopsis
                    synopsis.setText(movies4.getOverview());

                }
                break;
        }
    }


    public void handle_detail_Intent(MovieDb movie) {

        Intent i_detail = new Intent(getApplicationContext(), DetailMovie.class);

        i_detail.putExtra("name_movie", movie.getTitle());
        i_detail.putExtra("synopsis", movie.getOverview());
        i_detail.putExtra("rating", movie.getVoteAverage());
        i_detail.putExtra("yearRelease", movie.getReleaseDate());
        i_detail.putExtra("urlPath", format("%s%s", BASE_IMG_URL, movie.getPosterPath()));
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
