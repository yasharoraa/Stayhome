package com.fieapps.wish.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fieapps.wish.R;
import com.fieapps.wish.Utils.Constants;
import com.fieapps.wish.Utils.TouchImageView;

public class ViewImageActivity extends AppCompatActivity {
     TouchImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        imageView = findViewById(R.id.image_view);
        String url = getIntent().getStringExtra(Constants.IMAGE_URL);
        if (url == null) return;
        Uri uri = Uri.parse(url);
        Glide.with(this).load(uri).into(imageView);

    }
}
