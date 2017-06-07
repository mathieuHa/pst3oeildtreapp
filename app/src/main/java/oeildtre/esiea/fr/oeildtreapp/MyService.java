package oeildtre.esiea.fr.oeildtreapp;


import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat.Action;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.RemoteInput;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

public class MyService extends Service {
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    public static final int NOTIFICATION_ID= 99999;
    public static final int NOTIFICATION_ID_WRITING = 99998;
    String username,message;
    private Socket mSocket;
    private NotificationManager notificationManager;
    /*private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                Log.i("MyService", args[0].toString());
                JSONObject data = new JSONObject(args[0].toString());
                username = data.getString("autor");
                message = data.getString("msg");


                final Intent ino = new Intent(getApplicationContext(),Reply.class);
                final PendingIntent replyPendingIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID, ino, FLAG_ONE_SHOT);


                String replyLabel = "Reply";
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                        .setLabel(replyLabel)
                        .build();

                Action action = new Action.Builder(R.drawable.table, "Reply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

                NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.logo_round)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(username + " : " + message)
                        .setContentIntent(replyPendingIntent)
                        .addAction(action);
                if (!username.equals(getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname", "")) && !username.equals("Server")) {
                    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    if (!getBaseContext().getSharedPreferences("MyPref",MODE_PRIVATE).getString("Vibreur","").equals("N")) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(300);
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };*/
    private Emitter.Listener onWriting = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                Log.i("MyService", args[0].toString());
                JSONObject data = new JSONObject(args[0].toString());
                username = data.getString("autor");
                message = data.getString("msg");
                final Intent in = new Intent(getApplicationContext(),MainActivity.class);
                final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID_WRITING, in, FLAG_ONE_SHOT);

                NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.logo_round)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(username + " : " + message)
                        .setContentIntent(pendingIntent);
                if (!username.equals(getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname", "")) && !username.equals("Server")) {
                    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID_WRITING, mBuilder.build());
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
        //mSocket.on("message", onNewMessage);
        mSocket.on("writing", onWriting);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mSocket.off("message",onNewMessage);
        mSocket.disconnect();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
