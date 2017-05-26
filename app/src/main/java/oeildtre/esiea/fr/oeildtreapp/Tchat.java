package oeildtre.esiea.fr.oeildtreapp;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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


public class Tchat extends Fragment {
    public static final String UPDATES_CHAT="UPDATES_CHAT";
    private UpdateChat uc;
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
                        Log.i("Message" , args[0].toString());
                        JSONObject data = new JSONObject(args[0].toString());
                        String username;
                        String message;
                        username = data.getString("autor");
                        message = " : "+data.getString("msg");
                        list.add(new Message(username, message));
                        adapter = new MessageAdapter(getContext(),list);
                        lv.setAdapter(adapter);
                        lv.setSelection(list.size()-1);
                        lv.requestFocus();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://oeildtcam.hanotaux.fr:8080/");
        } catch (URISyntaxException e) {
            e.getStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View tchat = inflater.inflate(R.layout.tchat, container, false);
        edit = (EditText) tchat.findViewById(R.id.edit);
        send = (ImageButton) tchat.findViewById(R.id.send);
        lv = (ListView) tchat.findViewById(R.id.list);
        list = new ArrayList<>();
        GraphService.startActionBaz2(getContext(),"chat/","messages");
        IntentFilter inF = new IntentFilter(UPDATES_CHAT);
        uc = new UpdateChat();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(uc, inF);

        mSocket.on("message", onNewMessage);
        mSocket.connect();
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
                    mSocket.emit("message",obj);
                    list.add(new Message(getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname","")," : " + edit.getText().toString()));
                    adapter = new MessageAdapter(getContext(),list);
                    lv.setAdapter(adapter);
                    lv.setSelection(list.size()-1);
                    lv.requestFocus();
                    edit.setText("");
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

        mSocket.disconnect();
        mSocket.off("message", onNewMessage);
    }

    public JSONArray getFromFile() {
        try {
            InputStream is = new FileInputStream(getContext().getCacheDir()+"/messages.json");
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
        String msg, autor;
        Message(String autor, String msg){
            this.autor = autor;
            this.msg = msg;
        }
    }

    private class MessageAdapter extends ArrayAdapter<Message> {

        //tweets est la liste des models à afficher
        MessageAdapter(Context context, List<Message> messages) {
            super(context, 0, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message,parent, false);
            }

            TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
            if(viewHolder == null){
                viewHolder = new TweetViewHolder();
                viewHolder.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
                viewHolder.text = (TextView) convertView.findViewById(R.id.sms);
                convertView.setTag(viewHolder);
            }

            //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
            Message message = getItem(position);

            //il ne reste plus qu'à remplir notre vue
            viewHolder.pseudo.setText(message.autor);
            viewHolder.text.setText(message.msg);
            return convertView;
        }
        private class TweetViewHolder{
            private TextView pseudo;
            private TextView text;
        }
    }

    public class UpdateChat extends BroadcastReceiver {
        //@Override

        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                JSONArray list_obj = getFromFile();
                Log.d("Tchat", list_obj.toString());
                try {
                    for (int i=0; i<list_obj.length();i++) {
                        String autor = list_obj.getJSONObject(i).getJSONObject("user").getString("login");
                        String msg = " : "+list_obj.getJSONObject(i).getString("text");
                        list.add(new Message(autor, msg));
                    }
                    adapter = new MessageAdapter(getContext(),list);
                    lv.setAdapter(adapter);
                    lv.setSelection(list.size()-1);
                    lv.requestFocus();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}