package oeildtre.esiea.fr.oeildtreapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button con = (Button) findViewById(R.id.con);
        final Button ins = (Button) findViewById(R.id.ins);
        final EditText id = (EditText) findViewById(R.id.id);
        final EditText mdp = (EditText) findViewById(R.id.mdp);

        ins.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = "http://90.92.227.92/pst3oeildtresite/web/app_dev.php/login";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        con.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Fragments.class);
                startActivity(intent);
            }
        });

    }
}
