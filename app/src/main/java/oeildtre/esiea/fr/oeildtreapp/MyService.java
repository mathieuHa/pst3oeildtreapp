package oeildtre.esiea.fr.oeildtreapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

public class MyService extends Service {
    public static final int NOTIFICATION_ID= 99999;
    private Socket mSocket;
    private NotificationManager notificationManager;
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                Log.i("MyService", args[0].toString());
                JSONObject data = new JSONObject(args[0].toString());
                String username = data.getString("autor");
                String message = data.getString("msg");

                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                final Intent in = new Intent(getApplicationContext(),MainActivity.class);
                final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID, in, FLAG_ONE_SHOT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.drawable.logo_round)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(username+" : "+message)
                        .setContentIntent(pendingIntent);

                if (!username.equals(getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname", "")) && !username.equals("Server")) {
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    if (!getBaseContext().getSharedPreferences("MyPref",MODE_PRIVATE).getString("Vibreur","").equals("N")) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Emitter.Listener onWriting = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                Log.i("MyService", args[0].toString());
                JSONObject data = new JSONObject(args[0].toString());
                String username = data.getString("autor");
                String message = data.getString("msg");

                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                final Intent in = new Intent(getApplicationContext(),MainActivity.class);
                final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID, in, FLAG_ONE_SHOT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.drawable.logo_round)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(username+" "+message)
                        .setContentIntent(pendingIntent);

                if (!username.equals(getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname", "")) && !username.equals("Server")) {
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mSocket = SocketIO.getInstance().getSocket();
        mSocket.on("message", onNewMessage);
        mSocket.on("writing", onWriting);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("message",onNewMessage);
        mSocket.disconnect();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
