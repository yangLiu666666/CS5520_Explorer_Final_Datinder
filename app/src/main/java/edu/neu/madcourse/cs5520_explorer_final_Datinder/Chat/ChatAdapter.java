package edu.neu.madcourse.cs5520_explorer_final_Datinder.Chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{
    private List<Chat> chatList;
    private Context context;


    public ChatAdapter(List<Chat> chatList, Context context){
        this.chatList = chatList;
        this.context = context;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolder rcv = new ChatViewHolder(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.mMessage.setText(chatList.get(position).getMessage());

        if(chatList.get(position).getCurrentUser()){
            GradientDrawable shape =  new GradientDrawable();
            shape.setCornerRadius(20);
            shape.setCornerRadii(new float[] { 25, 25, 3, 25, 25, 25, 25, 25 });
            shape.setColor(Color.parseColor("#5fc9f8"));
            holder.mContainer.setGravity(Gravity.END);
            holder.mMessage.setBackground(shape);
            holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));

        }else{
            GradientDrawable shape =  new GradientDrawable();
            shape.setCornerRadius(20);
            shape.setColor(Color.parseColor("#53d769"));
            shape.setCornerRadii(new float[] { 25, 3, 25, 25, 25, 25, 25, 25 });
            holder.mMessage.setBackground(shape);
            holder.mContainer.setGravity(Gravity.START);
            holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            holder.mMessage.setText(chatList.get(position).getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}

