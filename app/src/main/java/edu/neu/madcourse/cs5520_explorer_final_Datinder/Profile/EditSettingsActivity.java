package edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.Chat.ChatActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.CreateAccountActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.MainActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.Matches.MatchActivity;
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
        title.setText("Settings");
        ImageButton back = findViewById(R.id.back);

        //if click back imageButton => go back
        back.setOnClickListener(view -> onBackPressed());

        SeekBar distance = findViewById(R.id.distance);
        man = findViewById(R.id.switch_man);
        woman = findViewById(R.id.switch_woman);
        distance_tv = findViewById(R.id.distance_text);
        gender_tv = findViewById(R.id.gender_text);

        mAuth = FirebaseAuth.getInstance();

        //toolbar
        Toolbar toolbar = findViewById(R.id.profile_toolBar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout) {
            new AlertDialog.Builder(EditSettingsActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPrefs.edit();
                            editor.clear();
                            editor.apply();
                            Intent intent = new Intent(EditSettingsActivity.this, SigninActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(),"Logged out successful", Toast.LENGTH_LONG).show();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        } else if(item.getItemId() == R.id.deleteAccount){
            new AlertDialog.Builder(EditSettingsActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete this account?")
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Delete it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Objects.requireNonNull(mAuth.getCurrentUser()).delete();
                            Intent intent = new Intent(EditSettingsActivity.this, CreateAccountActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(),"Deleted account successful", Toast.LENGTH_LONG).show();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        return super.onOptionsItemSelected(item);
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
