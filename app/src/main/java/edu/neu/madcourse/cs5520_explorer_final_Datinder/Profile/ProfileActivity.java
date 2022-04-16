package edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.TopNavigationBar;

public class ProfileActivity extends AppCompatActivity {

    //first icon in the navigation bar
    private static final int ACTIVITY_ITEM = 0;
    private CircleImageView circleImageView;
    private TextView userName;
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;
    private String userId, nameInfo, userImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //set up top navigation bar view
        setUpTopNavigationBar();


        circleImageView = findViewById(R.id.circleImage_profile);
        userName = findViewById(R.id.name_profile);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        } else { finish();}

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserName();
    }

    //set up name in the textview
    private void getUserName() {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null ){
                        nameInfo = map.get("name").toString();
                        userName.setText(nameInfo);
                    }

                    if (map.get("userImageUrl") != null) {
                        userImageUrl = map.get("userImageUrl").toString();
                        if ("default".equals(userImageUrl)) {
                            Glide.with(getApplication()).load(R.drawable.empty_user_profile).into(circleImageView);
                        } else {
                            Glide.with(getApplication()).load(userImageUrl).into(circleImageView);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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