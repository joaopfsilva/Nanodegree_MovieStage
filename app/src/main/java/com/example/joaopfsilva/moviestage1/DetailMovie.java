package com.example.joaopfsilva.moviestage1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailMovie extends AppCompatActivity {
    private String API_KEY = "";
    private String BASE_IMG_URL = "http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView originalTitle = (TextView) findViewById(R.id.nameMovie);
        TextView yearRelease = (TextView) findViewById(R.id.yearRelease);
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView synopsis = (TextView) findViewById(R.id.synopsis);
        String movietitle = getIntent().getStringExtra("name_movie");
        if (movietitle.length() > 20) {
            originalTitle.setTextSize(originalTitle.getTextSize() / 3);

        }
        //get poster url path
        String moviePosterUrl = getIntent().getStringExtra("urlPath");

        // originalTilte
        originalTitle.setText(getIntent().getStringExtra("name_movie"));

        // movieposterPath
        ImageView posterMovie = (ImageView) findViewById(R.id.poster);
        Picasso.with(getApplicationContext()).load(moviePosterUrl).into(posterMovie);

        // yearRelease
        yearRelease.setText(getIntent().getStringExtra("yearRelease").substring(0, 4));

        // rating
        rating.setText(getIntent().getStringExtra("rating") + "/10");

        //synopsis
        synopsis.setText(getIntent().getStringExtra("synopsis"));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                DetailMovie.this.finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
