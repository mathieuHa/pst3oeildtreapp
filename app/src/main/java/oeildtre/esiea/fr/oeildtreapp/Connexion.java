package oeildtre.esiea.fr.oeildtreapp;

import android.drm.DrmStore;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


public class Connexion extends Fragment {
    WebView webView;
    private GraphService gs = new GraphService();
    private Button con,ins;
    private EditText id,mdp;
    private String sid,smdp,result;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getFragmentManager().getBackStackEntryCount() == 0) {
                    if (webView.getAlpha() == 1) {
                        webView.setAlpha(0);
                        webView.setEnabled(false);
                        con.setEnabled(true);
                        ins.setEnabled(true);
                        id.setEnabled(true);
                        mdp.setEnabled(true);
                    } else getActivity().finish();
                }
            }
        });
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View conn = inflater.inflate(R.layout.connexion, container, false);

        con = (Button) conn.findViewById(R.id.con);
        ins = (Button) conn.findViewById(R.id.ins);
        id = (EditText) conn.findViewById(R.id.id);
        mdp = (EditText) conn.findViewById(R.id.mdp);
        webView = (WebView) conn.findViewById(R.id.webView);
        webView.setAlpha(0);
        webView.setEnabled(false);

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sid = id.getText().toString();
                smdp = mdp.getText().toString();
                //new SendPostRequest().execute();
                try {
                    downloadUrl("http://oeildtreapi.hanotaux.fr/api/users");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con.setEnabled(false);
                ins.setEnabled(false);
                id.setEnabled(false);
                mdp.setEnabled(false);
                webView.loadUrl(gs.getSource()+"/register/");
                webView.setEnabled(true);
                webView.setAlpha(1);
                webView.bringToFront();
                Log.d("alpha one", String.valueOf(webView.getAlpha()));
            }
        });

        return conn;
    }
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("debug", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            is.close();
            conn.disconnect();
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }finally{}
    }
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }
        return builder.toString();
    }
    /*public String getPostDataString(JSONObject params) throws Exception {

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
    private class SendPostRequest extends AsyncTask<String, Void, String> {
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
                Log.e("token1","Jusque la ca va !");
                //On lache la sauce
                OutputStream os = connec.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                Log.e("token2","Jusque la ca va !");
                int responseCode = connec.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
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
    }*/
}
