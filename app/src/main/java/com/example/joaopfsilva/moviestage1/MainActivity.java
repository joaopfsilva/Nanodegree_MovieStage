package com.example.joaopfsilva.moviestage1;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://api.themoviedb.org/3/authentication/session/new").request(MediaType.TEXT_PLAIN_TYPE).header("Accept", "application/json").get();
*/
        String basicURL = "http://image.tmdb.org/t/p/w185";

        Button Interstellar = (Button) findViewById(R.id.Interstellar);
        Interstellar.setOnClickListener(this);

        Button Spotlight = (Button) findViewById(R.id.Spotlight);
        Spotlight.setOnClickListener(this);

  /*      ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(getApplicationContext()).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);
        Picasso.with(getApplicationContext()).load("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=66fcfb532e5ca2995b845341b8dd5de1").into(imageView3);
*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.Interstellar:
                ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);
                Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg").into(imageView3);
                break;

            case R.id.Spotlight:
                ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
                Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w185/ngKxbvsn9Si5TYVJfi1EGAGwThU.jpg").into(imageView2);
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
