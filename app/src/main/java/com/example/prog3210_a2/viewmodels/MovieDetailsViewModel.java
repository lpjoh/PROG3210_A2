package com.example.prog3210_a2.viewmodels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDetailsViewModel extends ViewModel {
    public MutableLiveData<Bitmap> poster = new MutableLiveData<>();

    private void setPoster(AppCompatActivity activity, Bitmap value) {
        activity.runOnUiThread(() -> {
            poster.setValue(value);
        });
    }

    private void loadPosterAsync(AppCompatActivity activity, String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            InputStream is = connection.getInputStream();
            Bitmap image = BitmapFactory.decodeStream(is);

            setPoster(activity, image);
        }
        catch (IOException e) {
            return;
        }
    }

    public void loadPoster(AppCompatActivity activity, String urlStr) {
        new Thread() {
            public void run() {
                loadPosterAsync(activity, urlStr);
            }
        }.start();
    }
}
