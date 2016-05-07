package com.example.joaopfsilva.moviestage1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetailMovie extends AppCompatActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetailMovie.class);

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
        String movietitle = getIntent().getStringExtra("name_movie");
        if (movietitle.length() > 20) {
            originalTitle.setTextSize(originalTitle.getTextSize() / 4);

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
        rating.setText(String.format("%s/10", getIntent().getStringExtra("rating")));

        //synopsis
        synopsis.setText(getIntent().getStringExtra("synopsis"));


        Button favorite = (Button) findViewById(R.id.but_favorite);
        favorite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                MovieDatabase db = new MovieDatabase(getApplicationContext());
                db.addMovie("");// sample test
                db.getMovie("");// sample test
            }
        });
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
