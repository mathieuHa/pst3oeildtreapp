package oeildtre.esiea.fr.oeildtreapp;

import android.content.Context;
import android.content.DialogInterface;
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

public class Option extends Fragment {
    private ImageView img;
    private LinearLayout param, color, color_layout, password, password_layout;
    private ImageButton back1,back2;
    private String couleur;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View option = inflater.inflate(R.layout.option, container, false);
        back1 = (ImageButton) option.findViewById(R.id.back1);
        back2 = (ImageButton) option.findViewById(R.id.back2);
        param = (LinearLayout) option.findViewById(R.id.param);
        color = (LinearLayout) option.findViewById(R.id.color);
        color_layout = (LinearLayout) option.findViewById(R.id.color_layout);
        password = (LinearLayout) option.findViewById(R.id.password);
        password_layout = (LinearLayout) option.findViewById(R.id.password_layout);



        paramVisible();
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle("Choose color")
                        .initialColor(Color.parseColor(getContext().getSharedPreferences("MyPref",MODE_PRIVATE).getString("UserColor","")))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(getContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                (option.findViewById(R.id.target)).setBackgroundColor(selectedColor);
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
                param.setEnabled(false);
                param.setVisibility(View.INVISIBLE);
                color_layout.setEnabled(true);
                color_layout.setVisibility(View.VISIBLE);
                back1.setEnabled(true);
                back1.setVisibility(View.VISIBLE);
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
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramVisible();
            }
        });
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramVisible();
            }
        });


        return option;
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
        color_layout.setEnabled(false);
        color_layout.setVisibility(View.INVISIBLE);
        password_layout.setEnabled(false);
        password_layout.setVisibility(View.INVISIBLE);
        back1.setEnabled(false);
        back1.setVisibility(View.INVISIBLE);
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
                URL url = new URL("https://oeildtapi.hanotaux.fr/api/users/"+getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId",""));
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
                    Log.e("Color",result);
                    in.close();
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
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        }
    }

}
