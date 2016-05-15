package com.example.joaopfsilva.moviestage1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by joaopfsilva on 5/7/16.
 */
public class MovieDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MOVIESTAGE_2";
    private static String DICTIONARY_TABLE_CREATE_MOVIE = "";
    private static String DICTIONARY_TABLE_CREATE_TRAILER = "";
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDatabase.class);

    private static final String Movies_tableName = "MOVIES"; //MOVIES(ID_MOVIE*, NAME_MOVIE, REL_DATE, RATING, ID_SYN[synopsis])
    private static final String Trailer_tableName = "TRAILER"; //TRAILER(ID_TRAILER*, ID_MOVIE, TRAILER_LINK)

    //fields MOVIES table
    private static final String Movies_field_ID = "ID_MOVIE"; //primary key
    private static final String Movies_field_MovieID = "ID_MOVIEAPI"; //ID used in movieAPI
    private static final String Movies_field_nameMovie = "NAME_MOVIE";
    private static final String Movies_field_date = "REL_DATE";
    private static final String Movies_field_rating = "RATING";
    private static final String Movies_field_synopsis = "SYNOPSIS"; //ID for synopsis table

    //fields TRAILER table
    private static final String Trailer_field_ID = "ID_TRAILER"; //primary key
    private static final String Trailer_field_IDMovie = "ID_MOVIE"; //ID for movie identification
    private static final String Trailer_field_trailer = "TRAILER_LINK"; //store trailer link


    MovieDatabase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LOGGER.info("MovieDatabase: onCreate");
        //FORMAT CREATE INST
        createMoviesTable();
        createTrailerTable();

        //CREATE TABLES
        db.execSQL(DICTIONARY_TABLE_CREATE_MOVIE);
        db.execSQL(DICTIONARY_TABLE_CREATE_TRAILER);

    }

    private void createMoviesTable() {
        DICTIONARY_TABLE_CREATE_MOVIE =
                "CREATE TABLE " + Movies_tableName + " (" +
                        Movies_field_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, " +
                        Movies_field_MovieID + " NUMBER(10,0), " +
                        Movies_field_nameMovie + " TEXT, " +
                        Movies_field_date + " TEXT," +
                        Movies_field_rating + " TEXT," +
                        Movies_field_synopsis + " TEXT);";
    }

    private void createTrailerTable() {
        DICTIONARY_TABLE_CREATE_TRAILER =
                "CREATE TABLE " + Trailer_tableName + " (" +
                        Trailer_field_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, " +
                        Trailer_field_IDMovie + " INTEGER," +
                        Trailer_field_trailer + " TEXT," +
                        buildForeignSyntax(Trailer_field_IDMovie, Movies_tableName, Movies_field_ID) + ");";

    }

    //handle the syntax of foreign key
    private String buildForeignSyntax(String foreignField, String foreignTable, String foreignTableField) {
        return " FOREIGN KEY(" + foreignField + ") REFERENCES " + foreignTable + "(" + foreignTableField + ")";
    }

    //method to retrieve all favorites movies API IDs
    public List<Integer> getFavorites() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor2 = db.rawQuery("SELECT " + Movies_field_MovieID + " FROM " + Movies_tableName, null);
        List<Integer> movieIDs = new ArrayList<>();

        if (cursor2.moveToFirst()) {

            while (cursor2.isAfterLast() == false) {
                Integer movieAPIID = cursor2.getInt(cursor2
                        .getColumnIndex(Movies_field_MovieID));

                movieIDs.add(cursor2.getPosition(), movieAPIID);
                cursor2.moveToNext();
            }
        }
        return movieIDs;
    }

    public void dropTable(String tableName) {
        LOGGER.info("MovieDatabase: dropTable");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public void dropDatabase(Context context) {
        LOGGER.info("MovieDatabase: dropDatabase");
        context.deleteDatabase(DATABASE_NAME);
    }

    public void delMovie(String movieName) {
        ContentValues values = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("MOVIES", "NAME_MOVIE = ?", new String[]{movieName});
    }

    public List<String> listTablesInDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> favorites = new ArrayList<String>();

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                LOGGER.info("Table Name=> " + c.getString(0));
                favorites.add(c.getPosition(), c.getString(0));
                c.moveToNext();
            }
        }
        return favorites;
    }

    //method to handle the addition of a new favorite
    public boolean addFavorite(String movieName, String release_date, String synopsis, String rating, List<String> trailer_links, Integer movieAPIID) {
        //verify if the movie is already in favorite list
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor c = db.query(Movies_tableName, null, Movies_field_nameMovie + "=?", new String[]{movieName}, null, null, null);
        Cursor c = db.rawQuery("SELECT * FROM " + Movies_tableName + " WHERE " + Movies_field_nameMovie + " = ?", new String[]{movieName});
        long id_movie = -1;

        //if not -> add to the database
        if (!c.moveToFirst()) {
            //add to MOVIES table
            id_movie = addMovie(movieName, release_date, synopsis, rating, movieAPIID);

            //error if id_movie is -1
            if (id_movie == -1) {
                return false;
            }
            //add to TRAILER table
            addTrailer((int) id_movie, trailer_links);

            //add to POSTER_IMAGES folder

        }

        //verify if found entry created in MOVIES table
        return true;
    }

    public void reCreateDB(Context context) {
        //Delete DB
        dropTable("MOVIES");
        dropTable("TRAILER");
        LOGGER.info("MovieDatabase: reCreateDB");
       /* SQLiteDatabase db = this.getWritableDatabase();
        //FORMAT CREATE INST
        createMoviesTable();
        createTrailerTable();

        //CREATE TABLES
        db.execSQL(DICTIONARY_TABLE_CREATE_MOVIE);
        db.execSQL(DICTIONARY_TABLE_CREATE_TRAILER);*/
    }

    public void addTrailer(int id_movie, List<String> trailer_link) {
        ContentValues values = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();
        Iterator<String> links_iterator = trailer_link.iterator();

        try {
            db.beginTransaction();
            while (links_iterator.hasNext()) {
                values.put(Trailer_field_IDMovie, id_movie);
                values.put(Trailer_field_trailer, links_iterator.next());
                db.insert(Trailer_tableName, null, values);
            }
        } catch (Exception e) {
            LOGGER.info("MovieDatabase: addTrailer: Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public long addMovie(String movieName, String rel_date, String synopsis, String rating, Integer movieAPIID) {
        ContentValues values = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();
        long id_movie = -1;

        values.put(Movies_field_nameMovie, movieName);
        values.put(Movies_field_date, rel_date); //rel_date contains YEAR
        values.put(Movies_field_rating, rating);
        values.put(Movies_field_synopsis, synopsis);
        values.put(Movies_field_MovieID, movieAPIID);
        id_movie = db.insert(Movies_tableName, null, values);
        db.close(); // Closing database connection

        return id_movie;
    }

    public boolean isFavorite(String name_movie) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Movies_tableName + " WHERE " + Movies_field_nameMovie + " = ?", new String[]{name_movie});
        return c.moveToFirst();
    }

    public void listFieldTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + tableName, null);
        for (int i = 0; i < cur.getColumnCount(); i++) {
            LOGGER.info("COLUMN: " + cur.getColumnName(i));

        }
        return;
    }

    public void dropMovie(String movieName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("MOVIES", "NAME_MOVIE = ?", new String[]{movieName});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(Movies_tableName);
        dropTable(Trailer_tableName);

        onCreate(db);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
