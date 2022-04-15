package edu.neu.madcourse.cs5520_explorer_final_Datinder.Chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;

public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mMessage;
    public LinearLayout mContainer;
    public ImageView mIcon;
    public ChatViewHolder(View itemView){
        super(itemView);
        itemView.setOnClickListener(this);
        mMessage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);
        mIcon = itemView.findViewById(R.id.MatchImage);
    }

    @Override
    public void onClick(View view) {

    }
}
