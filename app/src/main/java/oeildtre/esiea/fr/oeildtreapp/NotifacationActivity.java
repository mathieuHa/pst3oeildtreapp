package oeildtre.esiea.fr.oeildtreapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
            mSocket = IO.socket(GraphService.getChat());
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
        if (textView.getText().length() < 1 && getSharedPreferences("MyPref", MODE_PRIVATE).getInt("position", -1) != 0) {
            SharedPreferences Properties = getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = Properties.edit();
            editor.putInt("position", 0);
            editor.commit();
        }
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //update notification
        mNotificationManager.cancel(NOTIFICATION_ID);
        finish();

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