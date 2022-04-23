package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<Card>{

    Context context;

    public arrayAdapter(Context context, int resourceId, List<Card> items){
        super(context, resourceId, items);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        Card card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        name.setText(card_item.getName());

        TextView school = (TextView) convertView.findViewById(R.id.schoolText);
        school.setText(card_item.getSchool());

        TextView story = (TextView) convertView.findViewById(R.id.storyText);
        story.setText(card_item.getStory());



        switch(card_item.getUserImageUrl()){
            case "default":
                Glide.with(convertView.getContext()).load(R.drawable.empty_user_profile).into(image);
                break;
            default:
                Glide.with(image).clear(image);

                Glide.with(convertView.getContext()).load(card_item.getUserImageUrl()).into(image);
                break;
        }


        return convertView;

    }
}
