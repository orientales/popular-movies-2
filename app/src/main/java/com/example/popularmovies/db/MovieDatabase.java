package com.example.popularmovies.db;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.popularmovies.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    private static final String TAG = MovieDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "movie_db";
    private static MovieDatabase sInstance;

    public static MovieDatabase getInstance(Context mContext) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "getInstance: Creating new DB instance");
                sInstance = Room.databaseBuilder(mContext.getApplicationContext(), MovieDatabase.class, MovieDatabase.DB_NAME).build();
            }
        }
        Log.d(TAG, "getInstance: getting the db instance");
        return sInstance;
    }

    public abstract MovieDao movieDao();
}
