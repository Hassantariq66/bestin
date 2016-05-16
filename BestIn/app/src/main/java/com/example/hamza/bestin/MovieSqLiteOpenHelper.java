package com.example.hamza.bestin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zeeshan on 6/17/2015.
 */
public class MovieSqLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MOVIE = "movie";

    public static final String COLUMN_MOVIE_ID = "id";
    public static final String COLUMN_MOVIE_ID_IMDB = "idIMDB";
    public static final String COLUMN_MOVIE_RANKING = "ranking";
    public static final String COLUMN_MOVIE_RATING = "rating";
    public static final String COLUMN_MOVIE_TITLE = "title";
    public static final String COLUMN_MOVIE_URL_POSTER = "urlPoster";
    public static final String COLUMN_MOVIE_YEAR = "year";
    public static final String COLUMN_MOVIE_SIMPLE_PLOT = "simplePlot";
    public static final String COLUMN_MOVIE_VOTES = "votes";


    private static final String CREATE_MOVIE_TABLE =
            "CREATE TABLE " + TABLE_MOVIE
                    + " ("+COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COLUMN_MOVIE_ID_IMDB + " INTEGER, "+
                    COLUMN_MOVIE_RANKING + " INTEGER," +
                    COLUMN_MOVIE_RATING + " DOUBLE," +
                    COLUMN_MOVIE_TITLE + " TEXT," +
                    COLUMN_MOVIE_URL_POSTER + " TEXT," +
                    COLUMN_MOVIE_YEAR + " TEXT," +
                    COLUMN_MOVIE_SIMPLE_PLOT + " TEXT," +
                    COLUMN_MOVIE_VOTES + " TEXT);";

    public MovieSqLiteOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXITS "+TABLE_MOVIE);
        onCreate(db);
    }

}
