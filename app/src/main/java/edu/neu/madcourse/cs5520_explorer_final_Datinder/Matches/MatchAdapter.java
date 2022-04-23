package edu.neu.madcourse.cs5520_explorer_final_Datinder.Matches;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;

public class MatchAdapter extends RecyclerView.Adapter<MatchViewHolder>{
    private List<Match> matchesList;
    private Context context;


    public MatchAdapter(List<Match> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchViewHolder rcv = new MatchViewHolder(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        holder.mMatchId.setText(matchesList.get(position).getUserId());
        holder.mProfile.setText(matchesList.get(position).getIconUrl());
        holder.mMatchName.setText(matchesList.get(position).getName());
        holder.mLastMessage.setText(matchesList.get(position).getLastMessage());
        String lastSeen = "";
        lastSeen  = matchesList.get(position).getLastSeen();
//        Log.d("matches", lastSeen);

        // lastSeen actually works as lastSend. if lastSend is true (other person has send a message), then make dot visible.
        if(lastSeen.equals("true"))
            holder.mNotificationDot.setVisibility(View.VISIBLE);
        else
            holder.mNotificationDot.setVisibility(View.INVISIBLE);
        holder.mLastTimeStamp.setText(matchesList.get(position).getLastTimeStamp());
        if(!matchesList.get(position).getIconUrl().equals("default")){
            Glide.with(context).load(matchesList.get(position).getIconUrl()).into(holder.mMatchImage);
        }

        switch(matchesList.get(position).getIconUrl()){
            case "default":
                Glide.with(context).load(R.drawable.empty_user_profile).into(holder.mMatchImage);
                break;
            default:
                Glide.with(holder.mMatchImage).clear(holder.mMatchImage);
                Glide.with(context).load(matchesList.get(position).getIconUrl()).into(holder.mMatchImage);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}
