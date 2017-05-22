package oeildtre.esiea.fr.oeildtreapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class GraphService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "oeildtre.esiea.fr.oeildtreapp.action.FOO";
    private static final String ACTION_BAZ1 = "oeildtre.esiea.fr.oeildtreapp.action.BAZ1";
    private static final String ACTION_BAZ2 = "oeildtre.esiea.fr.oeildtreapp.action.BAZ2";

    private static final String EXTRA_PARAM1 = "oeildtre.esiea.fr.oeildtreapp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "oeildtre.esiea.fr.oeildtreapp.extra.PARAM2";

    private static final String stream = "http://mathieuhanotaux.ddns.net";
    private static final String api = "https://oeildtapi.hanotaux.fr/api";
    private static final String media = "https://oeildtmedia.hanotaux.fr";


    public GraphService() {
        super("GraphService");
    }

    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GraphService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionBaz1(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GraphService.class);
        intent.setAction(ACTION_BAZ1);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionBaz2(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GraphService.class);
        intent.setAction(ACTION_BAZ2);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public String getApi() { return api; }
    public String getStream() {
        return stream;
    }
    public String getMedia() {
        return media;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ1.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz1(param1, param2);
            } else if (ACTION_BAZ2.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz2(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        Log.d("Max", "Thread service name : " + Thread.currentThread().getName());
        try {
            URL url = new URL(api + "/" + param1);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Auth-Token", getApplicationContext().getSharedPreferences("MyPref", 1).getString("Token", "xx"));
            connection.connect();
            if (HttpsURLConnection.HTTP_OK == connection.getResponseCode()) {
                Log.i("Code et Reponse", connection.getResponseMessage() + " " + connection.getResponseCode());
                copyInputStreamToFile(connection.getInputStream(), new File(getCacheDir() + "/" + param1 + ".json"));
                Log.d("Sensors", param1 + ".json DL");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (param2) {
            case "day":
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragDay.UPDATES_SENSORS1));
                break;
            case "month":
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragMonth.UPDATES_SENSOR2));
                break;
            case "year":
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragYear.UPDATES_SENSOR3));
                break;
            default:
                Log.e("Raté", param2);
                break;
        }
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream ou = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                ou.write(buf, 0, len);
            }
            ou.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz1(String param1, String param2) {
        Log.d("Max", "Thread service name : " + Thread.currentThread().getName());
        try {
            URL url = new URL(api + "/" + param2);
            Log.e("coq", url.toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Auth-Token", getApplicationContext().getSharedPreferences("MyPref", 1).getString("Token", ""));
            connection.connect();
            if (HttpsURLConnection.HTTP_OK == connection.getResponseCode()) {
                copyInputStreamToFile(connection.getInputStream(), new File(getCacheDir(), "/sensors_data"+param1+".json"));
                Log.d("Max", param1 + param2 + " DL");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (param1) {
            case "1":
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragDay.UPDATES_DATA1));
                break;
            case "2":
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragMonth.UPDATES_DATA2));
                break;
            case "3":
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragYear.UPDATES_DATA3));
                break;
        }

    }

    private void handleActionBaz2(String param1, String param2) {
        Log.d("Max", "Thread service name : " + Thread.currentThread().getName());
        try {
            URL url = new URL(api + "/" + param1 + param2);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Auth-Token", getApplicationContext().getSharedPreferences("MyPref", 1).getString("Token", ""));
            connection.connect();
            Log.d("Max", api + "/" + param1 + param2);
            if (HttpsURLConnection.HTTP_OK == connection.getResponseCode()) {
                copyInputStreamToFile(connection.getInputStream(), new File(getCacheDir(), "/" + param2 + ".json"));
                Log.d("Max", param2 + " DL");
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Medias.UPDATES_IMAGES));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


