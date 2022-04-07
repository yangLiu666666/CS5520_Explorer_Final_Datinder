package edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.MainActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;

public class EditSettingsActivity extends AppCompatActivity {
    private SwitchCompat man;
    private SwitchCompat woman;
    private TextView gender_tv, distance_tv;

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

    //Logout onClick of Button "logout" and "delete account" in activity.settings.xml
    //go back to main page
    public void Logout(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
