package com.example.joaopfsilva.moviestage1;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DetailMovie extends AppCompatActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetailMovie.class);
    public MovieDatabase db = null;
    int tm = 0;
    String[] items;

    /*
    * When its the first time we are using the
    * application this require a special
    * handle. This happens because the app
    * is executing a query to the favorite table
    * and that table does not exist in the
    * first use.
    * */
    Boolean firstUse = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOGGER.info("DetailMovie: onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView originalTitle = (TextView) findViewById(R.id.nameMovie);
        TextView yearRelease = (TextView) findViewById(R.id.yearRelease);
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView synopsis = (TextView) findViewById(R.id.synopsis);
        final String movietitle = getIntent().getStringExtra("name_movie");

        if (movietitle.length() > 20) //pretty print the movie title to fill the screen
            originalTitle.setTextSize(originalTitle.getTextSize() / 4);


        //get poster url path
        String moviePosterUrl = getIntent().getStringExtra("urlPath");
        // originalTilte
        originalTitle.setText(movietitle);

        // movieposterPath
        ImageView posterMovie = (ImageView) findViewById(R.id.poster);
        Picasso.with(getApplicationContext()).load(moviePosterUrl).into(posterMovie);

        // yearRelease
        final String releaseYear = getIntent().getStringExtra("yearRelease").substring(0, 4);
        yearRelease.setText(releaseYear);

        // rating
        final String ratingText = String.format("%s/10", getIntent().getStringExtra("rating"));
        rating.setText(ratingText);

        //synopsis
        final String synopsisText = getIntent().getStringExtra("synopsis");
        synopsis.setText(synopsisText);

        //trailer
        final List<String> trailer_links = getIntent().getStringArrayListExtra("trailerLinks");

        //movie ID in API
        final Integer movieAPIID = Integer.valueOf(getIntent().getStringExtra("movieAPIID"));

        db = new MovieDatabase(getApplicationContext());

        final Button addfavorite = (Button) findViewById(R.id.but_favorite);
        addfavorite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LOGGER.info("DetailMovie: addFavorite : onClick");
                if (db.isFavorite(movietitle)) {
                    db.dropMovie(movietitle);
                    handleToastMsg(movietitle + " removed from favorites!", Toast.LENGTH_SHORT);
                    addfavorite.setBackgroundColor(getResources().getColor(R.color.button_material_light));
                } else {
                    db.addFavorite(movietitle, releaseYear, synopsisText, ratingText, trailer_links, movieAPIID);// add new favorite
                    handleToastMsg(movietitle + " added to favorites!", Toast.LENGTH_SHORT);
                    addfavorite.setBackgroundColor(Color.parseColor("#FFDFD02D"));
                    //addfavorite.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
                }
            }

        });

        if (db.isFavorite(movietitle)) {
            addfavorite.setBackgroundColor(Color.parseColor("#FFDFD02D"));//color yellow
        } else {
            addfavorite.setBackgroundColor(getResources().getColor(R.color.button_material_light));
        }
        LOGGER.info("»»»»");
        handleListTrailerCreation();
        LOGGER.info(">>>>");

    }

    //Method to handle a Toast message, with user defined duration
    public void handleToastMsg(String msg, int Toastduration) {
        Toast toast = new Toast(getApplicationContext());
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplicationContext(), msg, Toastduration);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void handleListTrailerCreation() {
        LOGGER.info("************************");

        items = new String[]{"Vegetables", "Fruits", "Flower Buds", "Legumes", "Bulbs", "Tubers"};

        ListView lst_trailer = (ListView) findViewById(R.id.list_trailer);
        lst_trailer.setAdapter(new ListAdapter());
        Helper.getListViewSize(lst_trailer); // set

        lst_trailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(""));//add youtube url link
                startActivity(intent);
            }
        });
    }

    static class ViewHolder {
        ImageView iv;
        TextView tv;
        int position;
    }

    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return "Poster " + position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_trailer, parent, false);
            }

            ViewHolder holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iconTrailer);
            holder.tv = (TextView) convertView.findViewById(R.id.id_trailer_list);
            holder.position = position;
            holder.tv.setText("Poster " + position);
            convertView.setTag(holder);

            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                DetailMovie.this.finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
