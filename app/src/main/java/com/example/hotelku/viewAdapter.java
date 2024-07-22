package com.example.hotelku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class viewAdapter extends RecyclerView.Adapter<detailViewHolder> {
    List<detailModel> list = Collections.emptyList();
    Context context;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position, String room);
    }

    public viewAdapter(List<detailModel> list, Context context, OnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public detailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View photoView = inflater.inflate(R.layout.detail_card, parent, false);

        detailViewHolder viewHolder = new detailViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull detailViewHolder holder, int position) {
        final int index = holder.getAdapterPosition();
        holder.noRoom.setText(list.get(position).noRoom);
        holder.classType.setText(list.get(position).classType);
        holder.availability.setText(list.get(position).availability);
        holder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                listener.onItemClick(index, list.get(position).noRoom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
