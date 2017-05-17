package oeildtre.esiea.fr.oeildtreapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


public class Connexion extends Fragment {
    private SharedPreferences Properties;
    private SharedPreferences.Editor editor;
    private GraphService gs = new GraphService();
    private EditText id,mdp,name;
    private String sid,smdp,sname,result;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View conn = inflater.inflate(R.layout.connexion, container, false);
        final LinearLayout first = (LinearLayout) conn.findViewById(R.id.first);
        final LinearLayout second = (LinearLayout) conn.findViewById(R.id.second);

        final Button con = (Button) conn.findViewById(R.id.con);
        final Button ins = (Button) conn.findViewById(R.id.ins);
        final Button in = (Button) conn.findViewById(R.id.in);
        final Button up = (Button) conn.findViewById(R.id.up);
        final ImageButton back = (ImageButton) conn.findViewById(R.id.back);
        name = (EditText) conn.findViewById(R.id.name);
        id = (EditText) conn.findViewById(R.id.id);
        mdp = (EditText) conn.findViewById(R.id.mdp);

        second.setVisibility(View.INVISIBLE);
        second.setEnabled(false);
        back.setVisibility(View.INVISIBLE);
        back.setEnabled(false);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                second.setVisibility(View.INVISIBLE);
                second.setEnabled(false);
                first.setEnabled(true);
                first.setVisibility(View.VISIBLE);
                back.setVisibility(View.INVISIBLE);
                back.setEnabled(false);
            }
        });
        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first.setEnabled(false);
                first.setVisibility(View.INVISIBLE);
                second.setVisibility(View.VISIBLE);
                second.setEnabled(true);
                second.bringToFront();

                name.setVisibility(View.INVISIBLE);
                name.setEnabled(false);

                con.bringToFront();
                con.setEnabled(true);
                ins.setEnabled(false);
                back.setVisibility(View.VISIBLE);
                back.setEnabled(true);

                id.setText(getContext().getSharedPreferences("MyPref",1).getString("Smail",""));
                mdp.setText(getContext().getSharedPreferences("MyPref",1).getString("Smdp",""));
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first.setVisibility(View.INVISIBLE);
                first.setEnabled(false);
                second.setVisibility(View.VISIBLE);
                second.setEnabled(true);
                second.bringToFront();

                name.setVisibility(View.VISIBLE);
                name.setEnabled(true);

                ins.bringToFront();
                ins.setEnabled(true);
                con.setEnabled(false);
                back.setVisibility(View.VISIBLE);
                back.setEnabled(true);

                id.setText("");
                mdp.setText("");
            }
        });


        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sid = id.getText().toString();
                smdp = mdp.getText().toString();
                new SendSignInRequest().execute();

            }
        });
        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sname = name.getText().toString();
                sid = id.getText().toString();
                smdp = mdp.getText().toString();
                new SendSignUpRequest().execute();
            }
        });

        return conn;
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

    private class SendSignInRequest extends AsyncTask<String, Void, String> {
        JSONObject postDataParams;

        @Override
        protected void onPreExecute() {
            try {
                postDataParams = new JSONObject();
                postDataParams.put("login", sid);
                postDataParams.put("password", smdp);
                Log.e("params", postDataParams.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        protected String doInBackground(String... params) {

            try {
                //Init JSON and url de destination
                URL url = new URL("https://oeildtapi.hanotaux.fr/api/auth-tokens");
                //Init la connexion à l'API
                HttpsURLConnection connec = (HttpsURLConnection) url.openConnection();
                connec.setReadTimeout(15000);
                connec.setConnectTimeout(15000);
                connec.setRequestMethod("POST");
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

                if (responseCode == 201) {
                    Log.e("token3","Jusque la ca va !");
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
                    Log.e("token",result);
                    in.close();
                    JSONObject resultat = new JSONObject(result);
                    if (resultat != null){

//Initialise tes préférences
                        Properties = getContext().getSharedPreferences("MyPref", 1);
                        editor = Properties.edit();
                        editor.putString("Token", resultat.getString("value"));
                        editor.putString("Smail", sid);
                        editor.putString("Smdp", smdp);
                        editor.putString("user", resultat.getJSONObject("user").getString("id"));
                        editor.putString("Sname", resultat.getJSONObject("user").getString("login"));

                        editor.commit();
                    }
                    return sb.toString();
                }
                else {
                    return "false : " + responseCode;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("1","");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("2","");
            } catch (ProtocolException e) {
                e.printStackTrace();
                Log.e("3","");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("4","");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("5","");
            }
            return "true";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    private class SendSignUpRequest extends AsyncTask<String, Void, String> {
        JSONObject postDataParams;

        @Override
        protected void onPreExecute() {
            try {
                postDataParams = new JSONObject();
                postDataParams.put("login", sname);
                postDataParams.put("mail", sid);
                postDataParams.put("plainpassword", smdp);
                Log.e("params", postDataParams.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        protected String doInBackground(String... params) {

            try {
                //Init JSON and url de destination
                URL url = new URL("https://oeildtapi.hanotaux.fr/api/users");
                //Init la connexion à l'API
                HttpURLConnection connec = (HttpURLConnection) url.openConnection();
                connec.setReadTimeout(15000);
                connec.setConnectTimeout(15000);
                connec.setRequestMethod("POST");
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

                if (responseCode == 201) {
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
                    Log.e("token",result);
                    in.close();
                    return sb.toString();

                }
                else {
                    return "false : " + responseCode;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("1","");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("2","");
            } catch (ProtocolException e) {
                e.printStackTrace();
                Log.e("3","");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("4","");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("5","");
            }
            return "true";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
