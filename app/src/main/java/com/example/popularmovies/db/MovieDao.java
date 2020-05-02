package com.example.popularmovies.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.popularmovies.model.Movie;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies_table ORDER BY mID")
    LiveData<Movie[]> loadMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movieDetails);

    @Query("DELETE FROM movies_table WHERE mID = :mID")
    void deleteMovie(String mID);

}
