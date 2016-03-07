package com.example.joaopfsilva.moviestage1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.movito.themoviedbapi.model.*;

public class DetailMovie extends AppCompatActivity {
    private String API_KEY = "***";
    private String BASE_IMG_URL = "http://image.tmdb.org/t/p/w185/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView originalTitle = (TextView) findViewById(R.id.nameMovie);
        TextView yearRelease = (TextView) findViewById(R.id.yearRelease);
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView duration = (TextView) findViewById(R.id.Duration);
        TextView synopsis = (TextView) findViewById(R.id.synopsis);
        //ListView trailer_list = (ListView)findViewById(R.id.trailer_movies);

        // originalTilte
        String moviePosterUrl = getIntent().getStringExtra("urlPath");
        originalTitle.setText(getIntent().getStringExtra("name_movie"));

        // movieposterPath
        ImageView posterMovie = (ImageView) findViewById(R.id.poster);
        Picasso.with(getApplicationContext()).load(moviePosterUrl).into(posterMovie);

        // yearRelease
        yearRelease.setText(getIntent().getStringExtra("yearRelease").substring(0, 4));

        // rating
        rating.setText(Float.toString(getIntent().getFloatExtra("rating",0)) + "/10");

        // Duration
        duration.setText(Integer.toString(getIntent().getIntExtra("duration",0)));

        //synopsis
        synopsis.setText(getIntent().getStringExtra("synopsis"));
/*
        //trailer
        trailer_list.setOnClickListener((View.OnClickListener) this);
*/
    }

    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.trailer_movies:
                //extras.get("trailers");
                break;
        }

    }
}
