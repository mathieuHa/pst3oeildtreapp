package oeildtre.esiea.fr.oeildtreapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GraphService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "oeildtre.esiea.fr.oeildtreapp.action.FOO";
    private static final String ACTION_BAZ1 = "oeildtre.esiea.fr.oeildtreapp.action.BAZ1";
    private static final String ACTION_BAZ2 = "oeildtre.esiea.fr.oeildtreapp.action.BAZ2";
    private static final String ACTION_BAZ3 = "oeildtre.esiea.fr.oeildtreapp.action.BAZ3";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "oeildtre.esiea.fr.oeildtreapp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "oeildtre.esiea.fr.oeildtreapp.extra.PARAM2";

    public GraphService() {
        super("GraphService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GraphService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
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
    public static void startActionBaz3(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GraphService.class);
        intent.setAction(ACTION_BAZ3);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1,param2);
            } else if (ACTION_BAZ1.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz1(param1, param2);
            } else if (ACTION_BAZ2.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz2(param1, param2);
            } else if (ACTION_BAZ3.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz3(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        Log.d("Max","Thread service name : " + Thread.currentThread().getName());
        URL url = null;
        try {
            url = new URL ("http://90.92.227.92/pst3oeildtre/web/app.php/"+param1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                copyInputStreamToFile(connection.getInputStream(),
                        new File(getCacheDir(), "/"+param1+".json"));
                Log.d("Max",param1+param2+" DL");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragDay.UPDATES_SENSOR1));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragMonth.UPDATES_SENSOR2));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragYear.UPDATES_SENSOR3));


    }
    private void copyInputStreamToFile (InputStream in, File file){
        try {
            OutputStream ou =  new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len=in.read(buf))>0){
                ou.write(buf,0,len);
            }
            ou.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz1(String param1, String param2) {
        Log.d("Max","Thread service name : " + Thread.currentThread().getName());
        URL url = null;
        try {
            url = new URL ("http://90.92.227.92/pst3oeildtre/web/app.php/"+param1+param2);
            Log.e("coq",url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                copyInputStreamToFile(connection.getInputStream(),
                        new File(getCacheDir(), "/sensors_data1.json"));
                Log.d("Max",param1+param2+" DL");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragDay.UPDATES_DATA1));
    }
    private void handleActionBaz2(String param1, String param2) {
        Log.d("Max","Thread service name : " + Thread.currentThread().getName());
        URL url = null;
        try {
            url = new URL ("http://90.92.227.92/pst3oeildtre/web/app.php/"+param1+param2);
            Log.e("coq",url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                copyInputStreamToFile(connection.getInputStream(),
                        new File(getCacheDir(), "/sensors_data2.json"));
                Log.d("Max",param1+param2+" DL");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragMonth.UPDATES_DATA2));
    }
    private void handleActionBaz3(String param1, String param2) {
        Log.d("Max","Thread service name : " + Thread.currentThread().getName());
        URL url = null;
        try {
            url = new URL ("http://90.92.227.92/pst3oeildtre/web/app.php/"+param1+param2);
            Log.e("coq",url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                copyInputStreamToFile(connection.getInputStream(),
                        new File(getCacheDir(), "/sensors_data3.json"));
                Log.d("Max",param1+param2+" DL");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FragYear.UPDATES_DATA3));
    }
}


