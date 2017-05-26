package oeildtre.esiea.fr.oeildtreapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
        private final ImageView imageView;
        private final ImageButton join;

        ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });

            textView = (TextView) v.findViewById(R.id.textView);
            imageView = (ImageView) v.findViewById(R.id.img);
            join = (ImageButton) v.findViewById(R.id.join);
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(join.getContext());
                    final EditText input = new EditText(join.getContext());
                    builder
                            .setTitle("What is the number of the receiver ?")
                            .setMessage(mDataSet2.get(getAdapterPosition()).getImageUrlTh())
                            .setView(input)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    String value = input.getText().toString();
                                    sendSMS(value,mDataSet2.get(getAdapterPosition()).getImageUrlTh());
                                    InputMethodManager imm = (InputMethodManager) join.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    InputMethodManager imm = (InputMethodManager) join.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                }

                            });

                    builder.show();
                    input.requestFocus();
                    InputMethodManager imm = (InputMethodManager) join.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                }
            });
        }
        private void sendSMS(String phoneNumber, String message) {
            ArrayList<PendingIntent> sentPendingIntents = new ArrayList<>();
            ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<>();
            try {
                SmsManager sms = SmsManager.getDefault();
                ArrayList<String> mSMSMessage = sms.divideMessage(message);
                sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
                        sentPendingIntents, deliveredPendingIntents);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(join.getContext(), "SMS sending failed...",Toast.LENGTH_SHORT).show();
            }
        }
        TextView getTextView() { return textView; }
        ImageView getImageView() {
            return imageView;
        }
        ImageButton getJoin() { return join; }
    }

}