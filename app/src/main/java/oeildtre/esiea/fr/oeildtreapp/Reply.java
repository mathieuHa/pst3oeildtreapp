package oeildtre.esiea.fr.oeildtreapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Reply extends Activity{
    private MultiAutoCompleteTextView edit;
    private ImageButton send;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reply);
        edit = (MultiAutoCompleteTextView) findViewById(R.id.edit);
        send = (ImageButton) findViewById(R.id.send);
       // mSocket = SocketIO.getInstance().getSocket();
            Log.e("Tchat",mSocket.toString());
        edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("autor",getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname",""));
                    if(edit.getText().length() > 5)mSocket.emit("writing",obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                send.performClick();
                return false;
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("autor",getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname",""));
                    obj.put("msg",edit.getText().toString());
                    obj.put("token",getSharedPreferences("MyPref", MODE_PRIVATE).getString("Token",""));
                    obj.put("id",getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId",""));
                    obj.put("color",getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserColor",""));
                    Date d = new Date();
                    SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                    String s = f.format(d);
                    if (edit.getText().length() > 0) {
                        Log.e("edit","envoie de message");
                        mSocket.emit("message", obj.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
