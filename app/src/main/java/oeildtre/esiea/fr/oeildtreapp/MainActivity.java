package oeildtre.esiea.fr.oeildtreapp;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new SendSignInRequest().execute();
        mTitle = getTitle();
        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        SharedPreferences settings = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("coco",true);
        editor.commit();

        setupToolbar();

        DataModel[] drawerItem = new DataModel[7];
        drawerItem[0] = new DataModel(R.drawable.table, "Connexion");
        drawerItem[1] = new DataModel(R.drawable.jour2, "Day");
        drawerItem[2] = new DataModel(R.drawable.mois2, "Month");
        drawerItem[3] = new DataModel(R.drawable.annee2, "Year");
        drawerItem[4] = new DataModel(R.drawable.table, "Camera");
        drawerItem[5] = new DataModel(R.drawable.table, "Medias");
        drawerItem[6] = new DataModel(R.drawable.table, "Tchat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.drawer, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new Connexion();
                break;
            case 1:
                fragment = new FragDay();
                break;
            case 2:
                fragment = new FragMonth();
                break;
            case 3:
                fragment = new FragYear();
                break;
            case 4:
                fragment = new Camera();
                break;
            case 5:
                fragment = new Medias();
                break;
            case 6:
                fragment = new Tchat();
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle() {
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private class SendSignInRequest extends AsyncTask<String, Void, String> {
        JSONObject postDataParams;

        @Override
        protected void onPreExecute() {
            try {
                postDataParams = new JSONObject();
                postDataParams.put("login", getSharedPreferences("MyPref", MODE_PRIVATE).getString("Smail", " "));
                postDataParams.put("password", getSharedPreferences("MyPref", MODE_PRIVATE).getString("Smdp", " "));
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
                    Log.e("token3", "Jusque la ca va !");
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    connec.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while (null != (line = in.readLine())) {

                        sb.append(line);
                        break;
                    }
                    String result = sb.toString();
                    Log.e("token", result);
                    in.close();
                    JSONObject resultat = new JSONObject(result);
                    if (resultat != null) {

//Initialise tes préférences
                        SharedPreferences Properties = getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = Properties.edit();
                        editor.putString("Token", resultat.getString("value"));
                        editor.putString("UserId", resultat.getJSONObject("user").getString("id"));
                        editor.putString("Sname", resultat.getJSONObject("user").getString("login"));

                        editor.commit();
                    }
                    return "You're connected as " + getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname", "");
                } else {
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