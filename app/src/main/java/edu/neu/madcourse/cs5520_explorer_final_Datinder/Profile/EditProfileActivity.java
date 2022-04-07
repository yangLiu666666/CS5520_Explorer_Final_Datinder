package edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;

public class EditProfileActivity extends AppCompatActivity {

    Button man, woman, nongender;
    ImageButton back;
    TextView man_text, women_text, nongender_text;
    ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        requestMultiplePermissions();

        imageView1 = findViewById(R.id.image_view_1);
        imageView2 = findViewById(R.id.image_view_2);
        imageView3 = findViewById(R.id.image_view_3);
        imageView4 = findViewById(R.id.image_view_4);
        imageView5 = findViewById(R.id.image_view_5);
        imageView6 = findViewById(R.id.image_view_6);
        man = findViewById(R.id.man_button);
        woman = findViewById(R.id.woman_button);
        nongender = findViewById(R.id.nongender_button);
        man_text = findViewById(R.id.man_text);
        women_text = findViewById(R.id.woman_text);
        nongender_text = findViewById(R.id.nongender_text);
        back = findViewById(R.id.back);

        //if click back imageButton => go back
        back.setOnClickListener(view -> onBackPressed());

        //set up gender selection
        woman.setOnClickListener(v -> {
            women_text.setTextColor(R.color.colorPrimary);
            woman.setBackgroundResource(R.drawable.ic_selected);

            man_text.setTextColor(R.color.black);
            man.setBackgroundResource(R.drawable.ic_unselected);
            nongender_text.setTextColor(R.color.black);
            nongender.setBackgroundResource(R.drawable.ic_unselected);
        });

        man.setOnClickListener(v -> {
            man_text.setTextColor(R.color.colorPrimary);
            man.setBackgroundResource(R.drawable.ic_selected);

            women_text.setTextColor(R.color.black);
            woman.setBackgroundResource(R.drawable.ic_unselected);
            nongender_text.setTextColor(R.color.black);
            nongender.setBackgroundResource(R.drawable.ic_unselected);
        });

        nongender.setOnClickListener(view -> {
            nongender_text.setTextColor(R.color.colorPrimary);
            nongender.setBackgroundResource(R.drawable.ic_selected);

            man_text.setTextColor(R.color.black);
            man.setBackgroundResource(R.drawable.ic_unselected);
            women_text.setTextColor(R.color.black);
            woman.setBackgroundResource(R.drawable.ic_unselected);
        });


        imageView1.setOnClickListener(v -> {
            imageView = imageView1;
            proceedAfterPermission();

        });
        imageView2.setOnClickListener(v -> {
            imageView = imageView2;
            proceedAfterPermission();

        });

        imageView3.setOnClickListener(v -> {
            imageView = imageView3;
            proceedAfterPermission();

        });

        imageView4.setOnClickListener(v -> {
            imageView = imageView4;
            proceedAfterPermission();

        });

        imageView5.setOnClickListener(v -> {
            imageView = imageView5;
            proceedAfterPermission();

        });

        imageView6.setOnClickListener(v -> {
            imageView = imageView6;
            proceedAfterPermission();

        });
    }


    private void requestMultiplePermissions() {
        //TODO: Request camera and location permissions
    }

    private void proceedAfterPermission() {
        //TODO: Choose photos after permission granted
    }
}
