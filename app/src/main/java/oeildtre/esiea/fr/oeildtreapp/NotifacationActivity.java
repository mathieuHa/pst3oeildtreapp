package oeildtre.esiea.fr.oeildtreapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import static oeildtre.esiea.fr.oeildtreapp.MyService.NOTIFICATION_ID;
// Key for the string that's delivered in the action's intent.


public class NotifacationActivity extends AppCompatActivity {
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    // mRequestCode allows you to update the notification.
    int mRequestCode = 1000;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://oeildtcam.hanotaux.fr:8080");
        } catch (URISyntaxException e) {
            e.getStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_activity);
        mSocket.connect();

        try {
            JSONObject obj = new JSONObject();
            obj.put("autor", getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname", ""));
            obj.put("msg", getMessageText(getIntent()));
            obj.put("token", getSharedPreferences("MyPref", MODE_PRIVATE).getString("Token", ""));
            obj.put("id", getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId", ""));
            obj.put("color", getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserColor", ""));
            mSocket.emit("message",obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        TextView textView = (TextView) findViewById(R.id.replyMessage);
        textView.setText(getMessageText(getIntent()));

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //update notification
        mNotificationManager.cancel(NOTIFICATION_ID);
        finish();
        //startService(new Intent(getBaseContext(),MyService.class));
        startActivity(new Intent(getBaseContext(),MainActivity.class));
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }
}