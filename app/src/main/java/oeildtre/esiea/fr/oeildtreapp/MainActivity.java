package oeildtre.esiea.fr.oeildtreapp;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button but = (Button) findViewById(R.id.but);
        but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Fragments.class);
                startActivity(intent);
            }
        });
        String time ="10";
        for (int i=10; i<24; i+=1)
            time = time+","+(Math.random()*20);
        Log.e("time", time);
        final ImageView img = (ImageView) findViewById(R.id.img);
        Picasso.with(this).load(
                "http://chart.apis.google.com/chart?cht=lc&chs=300x150" +
                        "&chd=t:"+time+"&chl=time").into(img);
    }
}
