package oeildtre.esiea.fr.oeildtreapp;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<ItemRecyclerView> mDataSet2;

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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;
        private final ImageButton join;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");


                    getJoin().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri smsUri = Uri.parse("tel:0674429272");
                            Intent returnIt = new Intent(Intent.ACTION_VIEW, smsUri);
                            returnIt.putExtra("sms_body", "Va voir sur l'app OeilDeLaDtre, tu vas kiffer ;)");
                            returnIt.setType("vnd.android-dir/mms-sms");
                            Log.e("SMS","Element n° : ");
                        }
                    });
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
            imageView = (ImageView) v.findViewById(R.id.img);
            join = (ImageButton) v.findViewById(R.id.join);
        }

        TextView getTextView() { return textView; }
        ImageView getImageView() {
            return imageView;
        }
        ImageButton getJoin() { return join; }
    }
}