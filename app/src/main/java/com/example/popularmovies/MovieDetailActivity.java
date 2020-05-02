package com.example.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.popularmovies.db.MovieDatabase;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utils.AppExecutors;
import com.example.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";
    Movie details;
    ImageView mMoviePoster;
    Button mFav;
    TextView mTitle, mReleaseDate, mVoteAvg, mSynopsis, mReview, mTrailer;
    MovieDatabase mDB;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mMoviePoster = findViewById(R.id.poster_imageview);
        mTitle = findViewById(R.id.title_text);
        mReleaseDate = findViewById(R.id.release_date_text);
        mVoteAvg = findViewById(R.id.vote_avg_text);
        mSynopsis = findViewById(R.id.synopsis_text);
        mReview = findViewById(R.id.review_textview);
        mTrailer = findViewById(R.id.trailer_textview);
        mFav = findViewById(R.id.fav_btn);
        mDB = MovieDatabase.getInstance(getApplicationContext());

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        if (getIntent().hasExtra("details")) {
            details = getIntent().getParcelableExtra("details");
            Log.d(TAG, "onCreate: " + details.getID());

            Picasso.get().load(details.getMoviePoster()).into(mMoviePoster);
            mTitle.setText(details.getMovieTitle());
            mReleaseDate.setText(details.getReleaseDate());
            mVoteAvg.setText(details.getVoteAverage());
            mSynopsis.setText(details.getSynopsis());


            if (mSharedPreferences.getBoolean(details.getID(), false)) {
                mFav.setText(R.string.remove_from_favourites);
            } else {
                mFav.setText(R.string.add_to_favourites);
            }

        } else {
            Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show();
        }

        URL reviewURL = NetworkUtils.buildRnTUrl(details.getID(), "reviews");
        URL trailerURL = NetworkUtils.buildRnTUrl(details.getID(), "videos");
        Log.d(TAG, "onCreate: " + reviewURL);
        Log.d(TAG, "onCreate: " + trailerURL);

        if (isOnline()) {
            new FetchExtras().execute(trailerURL, reviewURL);
        } else {
            Toast.makeText(this, "No Internet Connection. App Needs Internet", Toast.LENGTH_SHORT).show();
        }

        mTrailer.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (details.getTrailer() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(details.getTrailer()));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {

                    Toast.makeText(MovieDetailActivity.this, "Could not fetch trailer", Toast.LENGTH_SHORT).show();
                }
            }
        }));

        mFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Movie mDetails = new Movie();
                mDetails.setID(details.getID());
                mDetails.setMovieTitle(details.getMovieTitle());
                mDetails.setReleaseDate(details.getReleaseDate());
                mDetails.setVoteAverage(details.getVoteAverage());
                mDetails.setSynopsis(details.getSynopsis());
                mDetails.setMoviePoster(details.getMoviePoster());
                mDetails.setTrailer(details.getTrailer());
                mDetails.setReview(details.getReview());
                Log.d(TAG, "onClick: " + mDetails.getReview());

                if (!mSharedPreferences.getBoolean(mDetails.getID(), false)) {

                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean(mDetails.getID(), true);
                    editor.apply();
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDB.movieDao().insertMovie(mDetails);
                        }
                    });
                    Toast.makeText(MovieDetailActivity.this, "Movie added to favourites", Toast.LENGTH_SHORT).show();
                    mFav.setText(R.string.remove_from_favourites);
                } else {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean(mDetails.getID(), false);
                    editor.apply();
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDB.movieDao().deleteMovie(details.getID());
                        }
                    });
                    mFav.setText(R.string.add_to_favourites);
                    Toast.makeText(MovieDetailActivity.this, "Movie removed from favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    class FetchExtras extends AsyncTask<URL, Void, String[]> {
        private final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";

        @Override
        protected String[] doInBackground(URL... urls) {

            URL trailerURL = urls[0];
            URL reviewURL = urls[1];

            String trailerResponse = null;
            String reviewResponse = null;

            try {
                trailerResponse = NetworkUtils.getResponseFromUrl(trailerURL);
                reviewResponse = NetworkUtils.getResponseFromUrl(reviewURL);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "doInBackground: 1" + trailerResponse);
            Log.d(TAG, "doInBackground: 2" + reviewResponse);

            return new String[]{trailerResponse, reviewResponse};
        }

        @Override
        protected void onPostExecute(String[] s) {

            if (s != null) {
                try {
                    JSONObject trailerJSON = new JSONObject(s[0]);
                    JSONArray v = trailerJSON.getJSONArray("results");
                    if (v.length() != 0) {
                        JSONObject v2 = v.getJSONObject(0);
                        String key = v2.getString("key");
                        details.setTrailer(TRAILER_BASE_URL + key);
                        Drawable playBtn = getApplicationContext().getResources().getDrawable(R.drawable.ic_play_24dp);
                        playBtn.setBounds(0, 0, 60, 60);
                        mTrailer.setCompoundDrawables(playBtn, null, null, null);
                        mTrailer.setText(getString(R.string.watch_trailer));
                    } else {
                        details.setTrailer(null);
                        mTrailer.setText(R.string.no_trailers_available);
                    }

                    JSONObject reviewJSON = new JSONObject(s[1]);
                    JSONArray r = reviewJSON.getJSONArray("results");
                    if (r.length() != 0) {
                        JSONObject r2 = r.getJSONObject(0);
                        String review = r2.getString("content");
                        mReview.setText(review);
                    } else {
                        details.setReview(null);
                        mReview.setText(R.string.no_reviews_error);
                        mReview.setTextSize(20);
                    }
                    Log.d(TAG, "onPostExecuteDetails: " + details.getTrailer());
                } catch (JSONException e) {
                    mTrailer.setText(R.string.no_trailers_available);
                    mReview.setText(R.string.no_reviews_error);
                    mReview.setTextSize(20);
                    Log.d(TAG, "onPostExecuteJSONEXCEPTION: " + e);
                }
            }
        }
    }
}

