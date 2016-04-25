package com.example.joaopfsilva.moviestage1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import static android.widget.AdapterView.OnItemClickListener;
import static android.widget.AdapterView.OnItemLongClickListener;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnItemClickListener {

    private static Toast toast;
    private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

    private String posterSize = "w342";
    private Boolean TO_RATE_MODE = false;
    private String sortBy = "popularity.desc"; //"rated.desc"
    private String page = "1"; //page to be loaded

    StrictMode.ThreadPolicy policy = null;

    GridView gv;
    HandleMovieAPI movieApi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOGGER.debug("ONCREATE");

        policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!isConnected()) {
            handleToastMsg("No Internet Connection", Toast.LENGTH_LONG);
            return;
        }
        movieApi = new HandleMovieAPI();
        movieApi.setURLMovie(sortBy, page);
        if (!movieApi.ConnectAPI()) {
            finish();
        }

        FloatingActionButton changeToRated = (FloatingActionButton) findViewById(R.id.swapOrder);
        changeToRated.setOnClickListener(this);

        gv = (GridView) findViewById(R.id.gridView);
        gv.setAdapter(new GridAdapter());


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleMovieDetail(movieApi.getDetailsMovie(position, posterSize));
            }
        });

        gv.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                handleToastMsg(movieApi.getDetailsMovie(position, posterSize).get(0), Toast.LENGTH_SHORT);
                return true;
            }
        });

    }

    //Method to handle movie information to be sent to DetailMovie activity
    private void handleMovieDetail(List<String> movie) {
        if (getResources().getConfiguration().orientation == 1) {//PORTRAIT
            Intent i_detail1 = new Intent(getApplicationContext(), DetailMovie.class);

            i_detail1.putExtra("name_movie", movie.get(0));
            i_detail1.putExtra("synopsis", movie.get(5));
            i_detail1.putExtra("rating", valueOf(movie.get(6))); //stands for popularity
            i_detail1.putExtra("yearRelease", movie.get(1));
            i_detail1.putExtra("urlPath", movie.get(2));
            startActivity(i_detail1);

        } else { //LANDSCAPE

            TextView originalTitle = (TextView) findViewById(R.id.nameMovie);
            TextView yearRelease = (TextView) findViewById(R.id.yearRelease);
            TextView rating = (TextView) findViewById(R.id.rating);
            TextView synopsis = (TextView) findViewById(R.id.synopsis);

            // movieposterPath
            ImageView posterMovie = (ImageView) findViewById(R.id.poster);
            Picasso.with(getApplicationContext()).load(movie.get(2)).into(posterMovie);

            // yearRelease
            yearRelease.setText(movie.get(1).substring(0, 4));

            // rating
            rating.setText(getResources().getString(R.string.rating_movie));

            // Title
            originalTitle.setText(movie.get(0));

            //synopsis
            synopsis.setText(movie.get(5));

        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    static class ViewHolder {
        ImageView iv;
        int position;
    }

    public class GridAdapter extends BaseAdapter {
        String posterpath;

        @Override
        public int getCount() {
            return movieApi.getMoviesTitle().size();
        }

        @Override
        public Object getItem(int position) {
            return movieApi.getDetailsMovie(position, posterSize);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.single_grid, parent, false);
            }

            ViewHolder holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.imageView);
            holder.position = position;
            convertView.setTag(holder);

            posterpath = movieApi.getPosterPath(posterSize).get(position);
            Picasso.with(getApplicationContext()).load(posterpath).into(holder.iv);

            return convertView;
        }
    }


    //Method to handle a Toast message, with user defined duration
    public void handleToastMsg(String msg, int Toastduration) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplicationContext(), msg, Toastduration);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


    @Override
    public void onClick(View v) {
        LOGGER.info("ONVIEW");

        switch (v.getId()) {
            case R.id.swapOrder:
                TO_RATE_MODE = !TO_RATE_MODE;

                if (TO_RATE_MODE) { //order by rated
                    handleToastMsg("RATED MODE", Toast.LENGTH_SHORT);
                    sortBy = "rated.desc";
                    movieApi.setURLMovie(sortBy, page);
                    movieApi.ConnectAPI();

                } else { //order by popularity
                    handleToastMsg("POPULARITY MODE", Toast.LENGTH_SHORT);
                    sortBy = "popularity.desc";
                    movieApi.setURLMovie(sortBy, page);
                    movieApi.ConnectAPI();

                }

                gv = (GridView) findViewById(R.id.gridView);
                gv.setAdapter(new GridAdapter());
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
            case android.R.id.home:
                //MainActivity.this.finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


}
