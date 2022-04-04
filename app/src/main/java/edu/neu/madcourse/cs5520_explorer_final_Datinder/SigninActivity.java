package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SigninActivity extends AppCompatActivity {
    private EditText email, password, phone;
    private Button signIn;
    private TextView createAccount;
    private String deviceToken, passwordInfo, phoneInfo;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        phone = findViewById(R.id.sign_phone);
        password = findViewById(R.id.sign_password);
        signIn = findViewById(R.id.sign_button);
        createAccount = findViewById(R.id.transfer_create_account);

        database = FirebaseDatabase.getInstance().getReference();
        // database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://cs5520-finalproject-datinder-default-rtdb.firebaseio.com");

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneInfo = phone.getText().toString();
                passwordInfo = password.getText().toString();

                if(phoneInfo.isEmpty() || passwordInfo.isEmpty()) {
                    Toast.makeText(SigninActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }else {
                    database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //check if phone number is exist
                            if(snapshot.hasChild(phoneInfo)){
                                final String getPassword = snapshot.child(phoneInfo).child("password").getValue(String.class);
                                if(getPassword.equals(passwordInfo)){
                                    Toast.makeText(SigninActivity.this,"Successfully signed in",Toast.LENGTH_SHORT ).show();
                                    Intent intent = new Intent(SigninActivity.this, MatchScreenActivity.class);
                                    intent.putExtra("user", phoneInfo);
                                    intent.putExtra("password",passwordInfo);
                                    intent.putExtra("token", deviceToken);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SigninActivity.this,"Wrong password, please try again",Toast.LENGTH_SHORT ).show();
                                }

                            }else{
                                Toast.makeText(SigninActivity.this,"Wrong phone number, please try again",Toast.LENGTH_SHORT ).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Create Account Activity
                startActivity(new Intent(SigninActivity.this, CreateAccountActivity.class));

            }
        });

    }
}