package com.example.mapmaker;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.MapViewHolder> {

    private ArrayList<MapItem> mMapList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class MapViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public ImageView mDeleteImage;

        public MapViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.tw1);
            mTextView2 = itemView.findViewById(R.id.tw2);
            mDeleteImage = itemView.findViewById(R.id.img_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        //making sure not about to get deleted
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        //making sure not about to get deleted
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public MapAdapter(ArrayList<MapItem> mapList){
        mMapList = mapList;

    }

    @NonNull
    @Override
    public MapViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.map_item, viewGroup, false);
        MapViewHolder ivh = new MapViewHolder(v, mListener);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull MapViewHolder mapViewHolder, int i) {
        MapItem currentItem = mMapList.get(i);

        mapViewHolder.mImageView.setImageResource(currentItem.getImageResource());
        mapViewHolder.mTextView1.setText(currentItem.getText1());
        mapViewHolder.mTextView2.setText(currentItem.getText2());
        mapViewHolder.mTextView1.setTextColor(currentItem.getText1Color());
    }

    @Override
    public int getItemCount() {
        return mMapList.size();
    }
}
