package edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.TopNavigationBar;

public class ProfileActivity extends AppCompatActivity {

    //first icon in the navigation bar
    private static final int ACTIVITY_ITEM = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //set up top navigation bar view
        setUpTopNavigationBar();

    }

    public void onProfileClick(View view) {
        switch (view.getId()) {
            case R.id.edit_profile:
                Intent intent1 = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent1);
                break;

            case R.id.edit_setting:
                Intent intent2 = new Intent(ProfileActivity.this, EditSettingsActivity.class);
                startActivity(intent2);
                break;
        }
    }


    /**
     * set up top navigation bar view
     */
    private void setUpTopNavigationBar() {
        BottomNavigationView tvNB = findViewById(R.id.topNavBar);
        TopNavigationBar.logTopNav(tvNB);
        TopNavigationBar.setupTopBar(ProfileActivity.this, tvNB);
        Menu menu = tvNB.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_ITEM);
        menuItem.setChecked(true);
    }
}
