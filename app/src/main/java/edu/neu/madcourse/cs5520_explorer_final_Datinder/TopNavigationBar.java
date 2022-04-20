package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.Matches.MatchActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile.ProfileActivity;

/**
 * This class serves for transferring to three different activities when
 * make selections on the top navigation bar
 * utilized an open resource/library for modified and enhanced android navigation view on github
 */
public class TopNavigationBar {

    public static void logTopNav(BottomNavigationView tv) {
        Log.d("TOPNAVBAR", "log navi bar");
    }


    public static void setupTopBar(final Context context, BottomNavigationView topNavigationView) {
        topNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.ic_profile:
                    Intent intent1 = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent1);
                    break;

                case R.id.ic_main:
                    Intent intent2 = new Intent(context, Swip.class);
                    context.startActivity(intent2);
                    break;

                case R.id.ic_matched:
                    Intent intent = new Intent(context, MatchActivity.class);
                    context.startActivity(intent);
                    break;
            }
            return false;
        });
    }
}
