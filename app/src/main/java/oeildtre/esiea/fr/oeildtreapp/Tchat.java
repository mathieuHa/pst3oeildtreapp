package oeildtre.esiea.fr.oeildtreapp;




import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static oeildtre.esiea.fr.oeildtreapp.MyService.NOTIFICATION_ID;


public class Tchat extends Fragment {
    public static final String UPDATES_CHAT="UPDATES_CHAT";
    private UpdateChat uc;
    private boolean dl = false, valid = false;
    private EditText edit;
    private ImageButton send;
    private ListView lv;
    private ArrayList<Message> list;
    private MessageAdapter adapter;
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.cancel(NOTIFICATION_ID);
                        Log.i("Message" , args[0].toString());
                        JSONObject data = new JSONObject(args[0].toString());
                        String username;
                        String message;
                        String id;
                        String colour;
                        username = data.getString("autor");
                        message =data.getString("msg");
                        id = data.getString("id");
                        colour = data.getString("color");
                        list.add(new Message(username, message, id, colour));
                        adapter = new MessageAdapter(getContext(),list);
                        lv.setAdapter(adapter);
                        lv.setSelection(list.size()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Socket mSocket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View tchat = inflater.inflate(R.layout.tchat, container, false);
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        if (!getContext().getSharedPreferences("MyPref",MODE_PRIVATE).getString("Token","").equals("")) valid = true;
        edit = (EditText) tchat.findViewById(R.id.edit);
        send = (ImageButton) tchat.findViewById(R.id.send);
        lv = (ListView) tchat.findViewById(R.id.list);
        list = new ArrayList<>();
        GraphService.startActionBaz2(getContext(),"chat/","messages");
        IntentFilter inF = new IntentFilter(UPDATES_CHAT);
        uc = new UpdateChat();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(uc, inF);

        if (valid){
            mSocket = SocketIO.getInstance().getSocket();
        }
        if (valid)mSocket.on("message", onNewMessage);

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
                    obj.put("autor",getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname",""));
                    obj.put("msg",edit.getText().toString());
                    obj.put("token",getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("Token",""));
                    obj.put("id",getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId",""));
                    obj.put("color",getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserColor",""));
                    if (valid && edit.getText().length() > 0) {
                        mSocket.emit("message", obj);
                        list.add(new Message(getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname", ""),
                                edit.getText().toString(),
                                getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId", ""),
                                getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserColor", "")));

                        adapter = new MessageAdapter(getContext(), list);
                        lv.setAdapter(adapter);
                        lv.setSelection(list.size() - 1);
                        edit.requestFocus();
                        edit.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        return tchat;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(uc);
        if (valid)mSocket.off("message", onNewMessage);
    }

    public JSONArray getFromFile() {
        try {
            InputStream is;
            if (dl) is = new FileInputStream(getContext().getCacheDir()+"/messages.json");
            else is = new FileInputStream(getContext().getCacheDir()+"/users.json");
            int size=is.available();
            byte[] buffer=new byte[size];
            is.read(buffer);
            is.close();
            String text=new String(buffer);
            return new JSONArray(text);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private class Message{
        String msg, autor, id, color;
        Message(String autor, String msg, String id, String color){
            this.autor = autor;
            this.msg = msg;
            this.id = id;
            this.color = color;
        }
    }

    private class MessageAdapter extends ArrayAdapter<Message> {

        //tweets est la liste des models à afficher
        MessageAdapter(Context context, List<Message> messages) {
            super(context, 0, messages);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message, parent, false);
            }

            TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new TweetViewHolder();
                viewHolder.msg = (LinearLayout) convertView.findViewById(R.id.msg);
                viewHolder.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
                viewHolder.text = (TextView) convertView.findViewById(R.id.sms);
                convertView.setTag(viewHolder);
            }

            //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
            Message message = getItem(position);
            //il ne reste plus qu'à remplir notre vue
            assert message != null;
            if (message.id.equals(getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId", ""))){
                viewHolder.msg.setGravity(Gravity.RIGHT);
                viewHolder.pseudo.setText(message.autor);
                if (valid && message.color.contains("#")) viewHolder.pseudo.setTextColor(Color.parseColor(message.color));
            } else {
                viewHolder.msg.setGravity(Gravity.LEFT);
                viewHolder.pseudo.setText(message.autor);
                viewHolder.pseudo.setTextColor(Color.RED);
                if (message.color.contains("#")) viewHolder.pseudo.setTextColor(Color.parseColor(message.color));//Integer.valueOf(message.color));
            }
            viewHolder.text.setTextColor(Color.DKGRAY);
            viewHolder.text.setOnClickListener(null);
            viewHolder.text.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            if (message.msg.contains(".jpg") || message.msg.contains(".com") || message.msg.contains(".fr")) {
                viewHolder.text.setTextColor(Color.parseColor("#8be2ff"));
                viewHolder.text.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                final TweetViewHolder finalViewHolder = viewHolder;
                viewHolder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = finalViewHolder.text.getText().toString();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }
            viewHolder.text.setText(message.msg);


            return convertView;
        }
        private class TweetViewHolder{
            private TextView pseudo;
            private TextView text;
            private LinearLayout msg;

        }
    }

    public class UpdateChat extends BroadcastReceiver {
        //@Override
        public void onReceive(Context context, Intent intent) {
            list.clear();
            if (null != intent) {
                dl=true;
                JSONArray list_obj = getFromFile();
                Log.d("Tchat", list_obj.toString());
                try {
                    for (int i=0; i<list_obj.length();i++) {
                        String autor = list_obj.getJSONObject(i).getJSONObject("user").getString("login");
                        String msg = list_obj.getJSONObject(i).getString("text");
                        String id = list_obj.getJSONObject(i).getJSONObject("user").getString("id");
                        String color = list_obj.getJSONObject(i).getJSONObject("user").getString("color");
                        list.add(new Message(autor, msg, id, color));
                    }
                    adapter = new MessageAdapter(getContext(),list);
                    lv.setAdapter(adapter);
                    lv.setSelection(list.size()-1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}