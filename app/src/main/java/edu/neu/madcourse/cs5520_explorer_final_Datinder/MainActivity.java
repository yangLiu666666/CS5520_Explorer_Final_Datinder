package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button button;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.create_account);
        button.setOnClickListener(this);
        button = findViewById(R.id.sign_in);
        button.setOnClickListener(this);

        setUpTopNavigationBar();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_account:
                intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                break;
            case R.id.sign_in:
                intent = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * set up top navigation bar view
     */
    private void setUpTopNavigationBar() {
        BottomNavigationViewEx tvNB = findViewById(R.id.topNavBar);
        tvNB.enableAnimation(false);
        tvNB.enableShiftingMode(false);
        tvNB.enableItemShiftingMode(false);
        TopNavigationBar.setupTopBar(MainActivity.this, tvNB);
        Menu menu = tvNB.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }
}



