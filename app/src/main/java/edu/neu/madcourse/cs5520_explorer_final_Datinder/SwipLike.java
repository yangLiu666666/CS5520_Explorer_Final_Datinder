package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class SwipLike extends AppCompatActivity {

    private Context context = SwipLike.this;
    private ImageView like;
    private static final int anum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swip_like);

        setupTopNavigationBar();
        like = findViewById(R.id.like);

        Intent intent = getIntent();
        String profileUrl = intent.getStringExtra("url");

        switch (profileUrl) {
            case "default":
                Glide.with(context).load(R.drawable.empty_user_profile).into(like);
                break;
            default:
                Glide.with(context).load(profileUrl).into(like);
                break;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent mainIntent = new Intent(SwipLike.this, MainActivity.class);
                startActivity(mainIntent);
            }
        }).start();
    }


    private void setupTopNavigationBar() {
        Log.d("SwipLike", "setupTopNavigationBar: setting up Top Navigation Bar");
        BottomNavigationViewEx ex = findViewById(R.id.topNavBar);
        TopNavigationBar.logTopNav(ex);
        TopNavigationBar.setupTopBar(context, ex);
        Menu menu = ex.getMenu();
        MenuItem menuItem = menu.getItem(anum);
        menuItem.setChecked(true);
    }
}