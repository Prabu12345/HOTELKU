package com.example.hotelku;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class detailViewHolder extends RecyclerView.ViewHolder {
        TextView noRoom;
        TextView classType;
        TextView availability;
        Button detailButton;
        View view;

        detailViewHolder(View itemView)
        {
            super(itemView);
            noRoom
                    = (TextView)itemView
                    .findViewById(R.id.noRoom);
            classType
                    = (TextView)itemView
                    .findViewById(R.id.typeClass);
            availability
                    = (TextView)itemView
                    .findViewById(R.id.availability);
            detailButton
                    = (Button) itemView
                    .findViewById(R.id.seeDetailButton);
            view  = itemView;
        }
    }