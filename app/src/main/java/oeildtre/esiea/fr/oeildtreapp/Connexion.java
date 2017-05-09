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
    private EditText id,mdp;
    private String sid,smdp,result;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View conn = inflater.inflate(R.layout.connexion, container, false);

        Button con = (Button) conn.findViewById(R.id.con);
        Button ins = (Button) conn.findViewById(R.id.ins);
        id = (EditText) conn.findViewById(R.id.id);
        mdp = (EditText) conn.findViewById(R.id.mdp);

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
                URL url = new URL("http://oeildtreapi.hanotaux.fr/api/auth-tokens");
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
                postDataParams.put("prenom", "mathieu");
                postDataParams.put("nom", "hanotaux");
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
                URL url = new URL("http://oeildtreapi.hanotaux.fr/api/users");
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
