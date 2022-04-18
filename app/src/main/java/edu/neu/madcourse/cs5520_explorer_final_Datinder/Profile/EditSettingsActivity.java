package edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.CreateAccountActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.MainActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.SigninActivity;

public class EditSettingsActivity extends AppCompatActivity {
    private SwitchCompat man;
    private SwitchCompat woman;
    private TextView gender_tv, distance_tv;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set up top navigation back bar
        TextView title = findViewById(R.id.title_tag);
        title.setText("Profile");
        ImageButton back = findViewById(R.id.back);

        //if click back imageButton => go back
        back.setOnClickListener(view -> onBackPressed());

        SeekBar distance = findViewById(R.id.distance);
        man = findViewById(R.id.switch_man);
        woman = findViewById(R.id.switch_woman);
        distance_tv = findViewById(R.id.distance_text);
        gender_tv = findViewById(R.id.gender_text);

        mAuth = FirebaseAuth.getInstance();


        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                distance_tv.setText(i + "miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        man.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                man.setChecked(true);
                woman.setChecked(false);
                gender_tv.setText("Men");
            }
        });

        woman.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                woman.setChecked(true);
                man.setChecked(false);
                gender_tv.setText("Women");
            }
        });

    }

    //Logout onClick of Button "logout" in activity.settings.xml
    //go back to main page
    //clear previous SharedPreferences
    public void Logout(View view) {
        mAuth.signOut();
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(EditSettingsActivity.this, SigninActivity.class);
        startActivity(intent);
        finish();
    }

    //deleteAccount onClick of Button "Delete Account" in activity
    public void DeleteAccount(View view) {
        Objects.requireNonNull(mAuth.getCurrentUser()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Delete Account", "Deletion Success");
                Intent intent = new Intent(EditSettingsActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
