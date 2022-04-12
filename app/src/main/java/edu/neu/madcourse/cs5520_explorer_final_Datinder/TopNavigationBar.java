package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
                    //TODO: intent to profile activity
                    Intent intent2 = new Intent(context, UserSetting.class);
                    System.out.println("this is test");
                    context.startActivity(intent2);
                    break;

                case R.id.ic_main:
                    //TODO: intent to MainActivity
                    break;

                case R.id.ic_matched:
                    //TODO: intent to matched activity
                    break;
            }
            return false;
        });
    }
}
