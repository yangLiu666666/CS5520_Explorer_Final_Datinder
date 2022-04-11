package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public String TAG = "MainActivity";
    private Button signIn, createAccount;
    private FirebaseAuth mAuth;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        spinner = findViewById(R.id.pBar);
        spinner.setVisibility(View.GONE);
        if(mAuth != null){
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user !=null){  //uncomment for production
                spinner.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this, Swip.class);
                startActivity(intent);
                finish();
                spinner.setVisibility(View.GONE);
                return;
            }
            else {
                Log.d(TAG, "user is null");
            }
        }

        signIn = findViewById(R.id.main_sign_in);
        createAccount = findViewById(R.id.main_create_account);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
                spinner.setVisibility(View.GONE);
                return;
            }
        });


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                finish();
                spinner.setVisibility(View.GONE);
                return;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }
}



