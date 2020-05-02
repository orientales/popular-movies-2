package com.example.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies_table")
public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel src) {
            return new Movie(src);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    private int dbMovieId;
    private String mID;
    private String mReview;
    private String mTrailer;
    private String mMovieTitle;
    private String mReleaseDate;
    private String mMoviePoster;
    private String mVoteAverage;
    private String mSynopsis;

    public Movie() {
    }

    public Movie(Parcel parcel) {
        mMovieTitle = parcel.readString();
        mSynopsis = parcel.readString();
        mMoviePoster = parcel.readString();
        mReleaseDate = parcel.readString();
        mVoteAverage = parcel.readString();
        mID = parcel.readString();
        mReview = parcel.readString();
        mTrailer = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieTitle);
        dest.writeString(mSynopsis);
        dest.writeString(mMoviePoster);
        dest.writeString(mReleaseDate);
        dest.writeString(mVoteAverage);
        dest.writeString(mID);
        dest.writeString(mReview);
        dest.writeString(mTrailer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getDbMovieId() {
        return dbMovieId;
    }

    public void setDbMovieId(int dbMovieId) {
        this.dbMovieId = dbMovieId;
    }

    public String getID() {
        return mID;
    }

    public void setID(String ID) {
        mID = ID;
    }

    public String getReview() {
        return mReview;
    }

    public void setReview(String review) {
        mReview = review;
    }

    public String getTrailer() {
        return mTrailer;
    }

    public void setTrailer(String trailer) {
        mTrailer = trailer;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        mMovieTitle = movieTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getMoviePoster() {
        return mMoviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        mMoviePoster = moviePoster;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public void setSynopsis(String synopsis) {
        mSynopsis = synopsis;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mMovieTitle='" + mMovieTitle + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mMoviePoster='" + mMoviePoster + '\'' +
                ", mVoteAverage='" + mVoteAverage + '\'' +
                ", mSynopsis='" + mSynopsis + '\'' +
                '}';
    }
}
