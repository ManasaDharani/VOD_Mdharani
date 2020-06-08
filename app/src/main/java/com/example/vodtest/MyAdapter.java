package com.example.vodtest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.ListVideosQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListVideosQuery.Item> mData = new ArrayList<>();;
    private LayoutInflater mInflater;
    private static final String TAG = MyAdapter.class.getSimpleName();
    private RecyclerViewClickListener mListener;



    // data is passed into the constructor
    MyAdapter(Context context, RecyclerViewClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view, mListener);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // resets the list with a new set of data
    public void setItems(List<ListVideosQuery.Item> items) {
        mData = items;
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_title;
        TextView txt_genre;
        ImageView image_view;
        private RecyclerViewClickListener mListener;

        ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_genre = itemView.findViewById(R.id.txt_genre);
            image_view = itemView.findViewById(R.id.image_view);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        void bindData(ListVideosQuery.Item item) {
            txt_title.setText(item.title());
            txt_genre.setText(item.genre());
            if (item.thumbNailsUrls() == null) {
                Picasso.get().load("https://i.stack.imgur.com/h6viz.gif").into(image_view);
            } else {
                Picasso.get().load(item.thumbNailsUrls().get(0)).into(image_view);
            }

        }
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Clicked");
            mListener.onClick(v, getAdapterPosition());

        }
    }
    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
