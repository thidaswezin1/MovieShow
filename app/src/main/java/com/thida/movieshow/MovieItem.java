package com.thida.movieshow;

import android.graphics.Bitmap;

public class MovieItem {
    Bitmap image;
    String movieName;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
}
