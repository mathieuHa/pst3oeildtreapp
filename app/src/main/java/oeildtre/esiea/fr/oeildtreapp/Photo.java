package oeildtre.esiea.fr.oeildtreapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

/**
 * Created by Max on 27/05/2017.
 */

public class Photo extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.photo);
        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        //photoView.setImageResource(R.drawable.image);
        Picasso.with(this).load(getIntent().getStringExtra("url")).into(photoView);

    }
}
