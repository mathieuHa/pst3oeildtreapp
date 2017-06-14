package oeildtre.esiea.fr.oeildtreapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_PICTURES;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<ItemRecyclerView> mDataSet2;
    private int position;
    private String url;

    CustomAdapter(List<ItemRecyclerView> dataSet) {
        mDataSet2 = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item, viewGroup, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextView().setText(mDataSet2.get(position).getText());
        String url = mDataSet2.get(position).getImageUrl();
        Log.e("url : ",url);
        Picasso.with(viewHolder.getImageView().getContext()).load(url).into(viewHolder.getImageView());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet2.size();
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final PhotoView imageView;
        private final ImageView send;
        private final ImageView dl;
        private final ImageView chat;

        ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            final Target img = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap finalBitmap, Picasso.LoadedFrom from) {
                    String root = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString();
                    File myDir = new File(root+"/oeildt");
                    myDir.mkdirs();
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fname = "image_" + n + ".jpg";

                    //Log.e("MediaStore" , MediaStore.Images.Media.insertImage(imageView.getContext().getContentResolver(),((BitmapDrawable)imageView.getDrawable()).getBitmap(), fname,"none"));
                    Log.e("Save",myDir+"/"+fname);
                    File file = new File(myDir, fname);
                    try {
                        if (file.exists()) file.delete();
                        //file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                        Log.e("Picture", "Download !");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Enregistrement",e.getCause().toString());
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            textView = (TextView) v.findViewById(R.id.textView);
            imageView = (PhotoView) v.findViewById(R.id.img);
            send = (ImageView) v.findViewById(R.id.send);
            dl = (ImageView) v.findViewById(R.id.download);
            chat = (ImageView) v.findViewById(R.id.chat);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(send.getContext(),Photo.class);
                    in.putExtra("url", mDataSet2.get(getAdapterPosition()).getImageUrlTh());
                    imageView.getContext().startActivity(in);
                }
            });
            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Socket mSocket;
                    {
                        try {
                            mSocket = IO.socket(GraphService.getChat());
                            mSocket.connect();
                            JSONObject obj = new JSONObject();
                            obj.put("autor",chat.getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("Sname",""));
                            obj.put("msg",mDataSet2.get(getAdapterPosition()).getImageUrlTh());
                            obj.put("token",chat.getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("Token",""));
                            obj.put("id",chat.getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserId",""));
                            obj.put("color",chat.getContext().getSharedPreferences("MyPref", MODE_PRIVATE).getString("UserColor",""));
                            mSocket.emit("message",obj.toString());
                            mSocket.disconnect();
                        } catch (JSONException | URISyntaxException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            dl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Picasso.with(imageView.getContext()).load(mDataSet2.get(getAdapterPosition()).getImageUrlTh()).into(img);}//new DownloadFiles().execute();
                });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(send.getContext());
                    final EditText input = new EditText(send.getContext());
                    builder
                            .setTitle("What is the number of the receiver ?")
                            .setMessage(mDataSet2.get(getAdapterPosition()).getImageUrlTh())
                            .setView(input)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    String value = input.getText().toString();
                                    sendSMS(value,mDataSet2.get(getAdapterPosition()).getImageUrlTh());
                                    InputMethodManager imm = (InputMethodManager) send.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    InputMethodManager imm = (InputMethodManager) send.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                }

                            });

                    builder.show();
                    input.requestFocus();
                    InputMethodManager imm = (InputMethodManager) send.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                }
            });
        }
        private void sendSMS(String phoneNumber, String message) {
            ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
            try {
                SmsManager sms = SmsManager.getDefault();
                ArrayList<String> mSMSMessage = sms.divideMessage(message);
                sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
                        sentPendingIntents, deliveredPendingIntents);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(send.getContext(), "SMS sending failed...",Toast.LENGTH_SHORT).show();
            }
        }

        TextView getTextView() { return textView; }

        PhotoView getImageView() {
            return imageView;
        }
    }
}