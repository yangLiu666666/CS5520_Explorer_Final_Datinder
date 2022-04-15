package edu.neu.madcourse.cs5520_explorer_final_Datinder.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.Chat.ChatActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;

public class MatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mMatchId, mMatchName, mLastTimeStamp, mLastMessage, mProfile;
    public ImageView mNotificationDot;
    public ImageView mMatchImage;

    public MatchViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);
        mLastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
        mLastTimeStamp = (TextView) itemView.findViewById(R.id.lastTimeStamp);

        mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
        mProfile = (TextView) itemView.findViewById(R.id.profileid);
        mNotificationDot = (ImageView) itemView.findViewById(R.id.notification_dot);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mMatchId.getText().toString());
        b.putString("matchName", mMatchName.getText().toString());
        b.putString("lastMessage", mLastMessage.getText().toString());
        b.putString("lastTimeStamp", mLastTimeStamp.getText().toString());
        b.putString("profile", mProfile.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
