package com.example.joaopfsilva.moviestage1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by joaopfsilva on 5/7/16.
 */
public class MovieDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MOVIESTAGE_2";
    private static String DICTIONARY_TABLE_CREATE_MOVIE = "";
    private static String DICTIONARY_TABLE_CREATE_SYNOPSIS = "";
    private static String DICTIONARY_TABLE_CREATE_TRAILER = "";
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDatabase.class);

    MovieDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //FORMAT CREATE INST
        createMoviesTable();
        createSynopsisTable();
        createTrailerTable();

        //CREATE TABLES
        db.execSQL(DICTIONARY_TABLE_CREATE_MOVIE);
        db.execSQL(DICTIONARY_TABLE_CREATE_SYNOPSIS);
        db.execSQL(DICTIONARY_TABLE_CREATE_TRAILER);
    }

    private void createMoviesTable() {
        String field_ID = "ID_MOVIE"; //primary key
        String field_name = "NAME_MOVIE";
        String field_date = "REL_DATE";
        String field_rating = "RATING";
        String field_IDsynopsis = "ID_SYN"; //ID for synopsis table
        String field_tableName = "MOVIES";

        DICTIONARY_TABLE_CREATE_MOVIE =
                "CREATE TABLE " + field_tableName + " (" +
                        field_ID + " NUMBER, " +
                        field_name + " TEXT" +
                        field_date + " DATE," +
                        field_rating + " NUMBER(2,1)," +
                        field_IDsynopsis + " NUMBER);";
    }

    private void createSynopsisTable() {
        String field_ID = "ID_SYNOPSIS"; //primary key
        String field_synopsis = "RESUME"; //store synopsis of a specific number
        String field_tableName = "SYNOPSIS";

        DICTIONARY_TABLE_CREATE_SYNOPSIS =
                "CREATE TABLE " + field_tableName + " (" +
                        field_ID + " NUMBER, " +
                        field_synopsis + " TEXT);";
    }

    private void createTrailerTable() {
        String field_ID = "ID_TRAILER"; //primary key
        String field_IDMovie = "ID_MOVIE"; //ID for movie identification
        String field_trailer = "TRAILER_LINK"; //store trailer link
        String field_tableName = "TRAILER";

        DICTIONARY_TABLE_CREATE_TRAILER =
                "CREATE TABLE " + field_tableName + " (" +
                        field_ID + " NUMBER, " +
                        field_IDMovie + " NUMBER," +
                        field_trailer + " TEXT);";
    }

    public void getMovie(String movieName){
        ContentValues values = new ContentValues();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("MOVIES", new String[] { "NAME_MOVIE" }, "NAME_MOVIE" + "=?",  new String[] { movieName },null,null,null,null);
        if (cursor != null)
            cursor.moveToFirst();
    }


    public void addMovie(String movieName){
        ContentValues values = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();
        values.put("ID_MOVIE", 1);
        values.put("NAME_MOVIE", movieName);
        db.insert("MOVIES", null, values);
        db.close(); // Closing database connection
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
