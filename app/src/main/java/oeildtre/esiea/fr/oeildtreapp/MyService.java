package oeildtre.esiea.fr.oeildtreapp;


import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat.Action;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;


import java.net.URISyntaxException;


import static android.app.PendingIntent.FLAG_ONE_SHOT;

public class MyService extends Service {
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    public static final int NOTIFICATION_ID= 99999;
    String username,message;
    private Socket mSocket;
    private NotificationManager notificationManager;
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                if (getSharedPreferences("MyPref", MODE_PRIVATE).getInt("position", -1) != 0) {
                    Log.i("MyService", args[0].toString());
                    JSONObject data = new JSONObject(args[0].toString());
                    username = data.getString("autor");
                    message = data.getString("msg");


                    String replyLabel = "Reply";
                    RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                            .setLabel(replyLabel)
                            .build();

                    Intent resultIntent = new Intent(getApplicationContext(), NotifacationActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
// Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(NotifacationActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);

                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    // Add to your action, enabling Direct reply for it
                    NotificationCompat.Action action =
                            new NotificationCompat.Action.Builder(R.drawable.logo_round, replyLabel, resultPendingIntent)
                                    .addRemoteInput(remoteInput)
                                    .setAllowGeneratedReplies(true)
                                    .build();

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .addAction(action)
                                    .setAutoCancel(true)
                                    .setSmallIcon(R.drawable.logo_round)
                                    .setContentTitle(getString(R.string.app_name))
                                    .setContentText(data.getString("autor") + " " + data.getString("msg"));

                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    //Show it
                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    };

    {
        try {
            mSocket = IO.socket("http://oeildtcam.hanotaux.fr:8080");
        } catch (URISyntaxException e) {
            e.getStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSocket.connect();
        mSocket.on("message", onNewMessage);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("message",onNewMessage);
        mSocket.disconnect();
        //startService(new Intent(MyService.this, MyService.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
