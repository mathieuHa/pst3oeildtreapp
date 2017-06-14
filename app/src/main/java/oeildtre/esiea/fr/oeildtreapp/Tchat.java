package oeildtre.esiea.fr.oeildtreapp;




import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static oeildtre.esiea.fr.oeildtreapp.MyService.NOTIFICATION_ID;


public class Tchat extends Fragment {

    public static final String UPDATES_CHAT = "UPDATES_CHAT";
    private UpdateChat uc;
    private String id;
    private boolean dl = false;
    private MultiAutoCompleteTextView edit;
    private ImageButton send;
    private boolean anim = true;
    private ListView lv;
    private TextView typing;
    private ArrayList<Message> list;
    private MessageAdapter adapter;
    private Emitter.Listener onWriting = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                JSONObject data = new JSONObject((String)args[0]);
                String autor = data.getString("autor");
                String msg = data.getString("msg");
                String id2verif = data.getString("id");
                if(!id2verif.equals(id)) isTyping(autor +" "+ msg, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isTyping("", false);
                    try {
                        NotificationManager mNotificationManager =
                                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.cancel(NOTIFICATION_ID);
                        Log.e("Args 0", args[0].toString());
                        JSONObject data = new JSONObject((String)args[0]);
                        String autor,msg,color,id;
                        Date d = new Date();
                        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                        String s = f.format(d);
                        autor = data.getString("autor");
                        msg = data.getString("msg");
                        id = data.getString("id");
                        color = data.getString("color");

                        // add the message to view
                        list.add(new Message(autor,msg,id,color,s));
                        anim = true;
                        adapter.notifyDataSetChanged();
                        lv.setSelection(list.size() - 1);

                    } catch (JSONException e) {
                        e.getStackTrace();
                    }



                }
            });
        }
    };

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://oeildtcam.hanotaux.fr:8080");
        } catch (URISyntaxException e) {
            e.getStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View tchat = inflater.inflate(R.layout.tchat, container, false);
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        SharedPreferences Properties = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = Properties.edit();
        editor.putInt("position", 0);
        editor.commit();

        id = getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId","-1");
        edit = (MultiAutoCompleteTextView) tchat.findViewById(R.id.edit);
        send = (ImageButton) tchat.findViewById(R.id.send);
        typing = (TextView) tchat.findViewById(R.id.typing);
        typing.setText("");
        typing.setVisibility(View.INVISIBLE);
        lv = (ListView) tchat.findViewById(R.id.list);
        list = new ArrayList<>();
        GraphService.startActionBaz2(getContext(), "chat/", "messages");
        IntentFilter inF = new IntentFilter(UPDATES_CHAT);
        uc = new UpdateChat();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(uc, inF);

        mSocket.on("writing",onWriting);
        mSocket.on("message",onNewMessage);
        mSocket.connect();


        edit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after. It is an error to attempt to make changes to s from this callback.*/
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("autor", getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname", ""));
                    obj.put("msg","is typing...");
                    obj.put("id",getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId", ""));
                    if (edit.getText().length()>2)mSocket.emit("writing", obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                    obj.put("autor", getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname", ""));
                    obj.put("msg", edit.getText().toString());
                    obj.put("token", getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("Token", ""));
                    obj.put("id", getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId", ""));
                    obj.put("color", getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserColor", ""));
                    Date d = new Date();
                    SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                    String s = f.format(d);
                    if (edit.getText().length() > 0) {
                        Log.e("edit", "envoie de message");
                        mSocket.emit("message", obj.toString());
                        list.add(new Message(getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname", ""),
                                edit.getText().toString(),
                                getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId", ""),
                                getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserColor", ""),
                                s));
                        anim = true;
                        adapter.notifyDataSetChanged();


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
        mSocket.off("message", onNewMessage);
        mSocket.off("writing", onWriting);
        mSocket.disconnect();
        getContext().startService(new Intent(getContext(),MyService.class));
    }

    public JSONArray getFromFile() {
        try {
            InputStream is;
            if (dl) is = new FileInputStream(getContext().getCacheDir() + "/messages.json");
            else is = new FileInputStream(getContext().getCacheDir() + "/users.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text = new String(buffer);
            return new JSONArray(text);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private void isTyping(final String msg, final boolean put){
        getActivity().runOnUiThread (new Thread(new Runnable() {
            public void run() {
                typing.bringToFront();
                typing.setText(msg);
                if (put) typing.setVisibility(View.VISIBLE);
                else typing.setVisibility(View.INVISIBLE);
            }
        }));

    }

    private class Message {
        String msg, autor, id, color, date;

        Message(String autor, String msg, String id, String color, String date) {
            this.autor = autor;
            this.msg = msg;
            this.id = id;
            this.color = color;
            this.date = date;
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
                viewHolder.msg.setAlpha(0);
                viewHolder.msg.animate().setDuration(400).alpha(1);
                viewHolder.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
                viewHolder.text = (TextView) convertView.findViewById(R.id.sms);
                viewHolder.date = (TextView) convertView.findViewById(R.id.date);
                convertView.setTag(viewHolder);
            }

            //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
            Message message = getItem(position);
            if (position == list.size() - 1 && anim) {
                viewHolder.msg.setAlpha(0);
                viewHolder.msg.animate().setDuration(500).alpha(1);
                anim = false;
            }
            //il ne reste plus qu'à remplir notre vue
            assert message != null;
            if (message.id.equals(getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId", ""))) {
                viewHolder.msg.setGravity(Gravity.RIGHT);
                viewHolder.pseudo.setText(message.autor);
                if (message.color.contains("#"))
                    viewHolder.pseudo.setTextColor(Color.parseColor(message.color));
            } else {
                viewHolder.msg.setGravity(Gravity.LEFT);
                viewHolder.pseudo.setText(message.autor);
                viewHolder.pseudo.setTextColor(Color.RED);
                if (message.color.contains("#"))
                    viewHolder.pseudo.setTextColor(Color.parseColor(message.color));//Integer.valueOf(message.color));
            }
            viewHolder.text.setTextColor(Color.GRAY);
            viewHolder.text.setOnClickListener(null);
            viewHolder.text.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            if (message.msg.contains(".jpg") || message.msg.contains(".com") || message.msg.contains(".fr")) {
                viewHolder.text.setTextColor(Color.parseColor("#8be2ff"));
                viewHolder.text.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                final TweetViewHolder finalViewHolder = viewHolder;
                viewHolder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = finalViewHolder.text.getText().toString();
                        if (!finalViewHolder.text.getText().toString().contains("http"))
                            str = "http://" + finalViewHolder.text.getText().toString();
                        String url = str;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }
            viewHolder.date.setText(message.date);
            viewHolder.text.setText(message.msg);


            return convertView;
        }

        private class TweetViewHolder {
            private TextView pseudo;
            private TextView text;
            private LinearLayout msg;
            private TextView date;

        }
    }

    public class UpdateChat extends BroadcastReceiver {
        //@Override
        public void onReceive(Context context, Intent intent) {
            list.clear();
            if (null != intent) {
                dl = true;
                JSONArray list_obj = getFromFile();
                Log.d("Tchat", list_obj.toString());
                try {
                    for (int i = 0; i < list_obj.length(); i++) {
                        String autor = list_obj.getJSONObject(i).getJSONObject("user").getString("login");
                        String msg = list_obj.getJSONObject(i).getString("text");
                        String id = list_obj.getJSONObject(i).getJSONObject("user").getString("id");
                        String color = list_obj.getJSONObject(i).getJSONObject("user").getString("color");
                        String date = list_obj.getJSONObject(i).getString("date");
                        date = date.substring(date.length() - 14, date.length() - 9);
                        list.add(new Message(autor, msg, id, color, date));
                    }
                    adapter = new MessageAdapter(getContext(), list);
                    lv.setAdapter(adapter);
                    lv.setSelection(list.size() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}