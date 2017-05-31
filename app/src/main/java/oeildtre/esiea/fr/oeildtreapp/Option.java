package oeildtre.esiea.fr.oeildtreapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Max on 26/05/2017.
 */

public class Option extends Activity {
    private ImageView img;
    private EditText psw1, psw2;
    private LinearLayout param, color, password, password_layout, deco;
    private ImageButton back2;
    private Button push;
    private String couleur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("Ouverture","?");
        setContentView(R.layout.option);
        back2 = (ImageButton) findViewById(R.id.back2);
        param = (LinearLayout) findViewById(R.id.param);
        color = (LinearLayout) findViewById(R.id.color);
        password = (LinearLayout) findViewById(R.id.password);
        password_layout = (LinearLayout) findViewById(R.id.password_layout);
        psw1 = (EditText) findViewById(R.id.psw1);
        psw2 = (EditText) findViewById(R.id.psw2);
        push = (Button) findViewById(R.id.push);
        deco = (LinearLayout) findViewById(R.id.deco);

        paramVisible();
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(getApplicationContext())
                        .setTitle("Choose color")
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(getApplicationContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                couleur = Integer.toHexString(selectedColor);
                                couleur = "#"+couleur.substring(2,couleur.length());
                                new SendColorRequest().execute();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                param.setEnabled(false);
                param.setVisibility(View.INVISIBLE);
                password_layout.setEnabled(true);
                password_layout.setVisibility(View.VISIBLE);
                back2.setEnabled(true);
                back2.setVisibility(View.VISIBLE);
            }
        });
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //"/users/new-pass/{id}"

            }
        });
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramVisible();
            }
        });
        deco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                builder.setMessage("Do you want to log out ?");
                builder.setCancelable(false);
                builder.setTitle("Confirmation");

                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences properties = getSharedPreferences("MyPref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = properties.edit();
                                editor.putString("Token", "");
                                editor.putString("UserId", "");
                                editor.putString("UserColor", "");
                                editor.putString("Sname", "");
                                editor.putString("Smail", "");
                                editor.putString("Smdp", "");
                                editor.commit();
                                Toast.makeText(getApplicationContext(), "You're disconnected...", Toast.LENGTH_LONG).show();
                            }
                        });

                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                dialog = builder.create();
                dialog.show();
            }
        });
    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public void paramVisible() {
        param.setEnabled(true);
        param.setVisibility(View.VISIBLE);
        param.bringToFront();
        password_layout.setEnabled(false);
        password_layout.setVisibility(View.INVISIBLE);
        back2.setEnabled(false);
        back2.setVisibility(View.INVISIBLE);
    }

    private class SendColorRequest extends AsyncTask<String, Void, String> {
        JSONObject postDataParams;
        String result;

        @Override
        protected void onPreExecute() {
            try {
                postDataParams = new JSONObject();
                postDataParams.put("color", couleur);
                Log.e("params", postDataParams.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        protected String doInBackground(String... params) {

            try {
                //Init JSON and url de destination
                URL url = new URL("https://oeildtapi.hanotaux.fr/api/users/"+getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId",""));
                //Init la connexion à l'API
                HttpURLConnection connec = (HttpURLConnection) url.openConnection();
                connec.setReadTimeout(15000);
                connec.setConnectTimeout(15000);
                connec.setRequestMethod("PATCH");
                connec.setDoInput(true);
                connec.setDoOutput(true);
                //On lache la sauce
                OutputStream os = connec.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = connec.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    connec.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }
                    result = sb.toString();
                    JSONObject resultat = new JSONObject(result);
                    SharedPreferences properties = getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = properties.edit();
                    editor.putString("UserColor", resultat.getString("color"));
                    editor.commit();
                    return "Color changed";

                }
                else {
                    return connec.getResponseMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Error";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
