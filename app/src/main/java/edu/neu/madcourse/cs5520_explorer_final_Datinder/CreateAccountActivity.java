package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
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
import com.google.firebase.messaging.FirebaseMessaging;


public class CreateAccountActivity extends AppCompatActivity {
    private static final String TAG = CreateAccountActivity.class.getSimpleName();
    private Button createAccount;
    private EditText email, password, name, phone;
    private TextView signIn;
    private DatabaseReference database;
    private String deviceToken, userInfo, passwordInfo, emailInfo, phoneInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        name = findViewById(R.id.create_account_name);
        password = findViewById(R.id.create_account_password);
        email = findViewById(R.id.create_account_email);
        phone = findViewById(R.id.create_account_phone);
        createAccount = findViewById(R.id.create_account_button);
        signIn = findViewById(R.id.transfer_sign_in);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateAccountActivity.this, SigninActivity.class));
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        Toast.makeText(CreateAccountActivity.this, "Fetching FCM registration token failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Get new FCM registration token
                    deviceToken = task.getResult();
                    Toast.makeText(CreateAccountActivity.this, "token: " + deviceToken, Toast.LENGTH_LONG).show();
                    database = FirebaseDatabase.getInstance().getReference();

                });

    }


    public void createAccount(View view) {
        if (deviceToken == null) {
            Toast.makeText(CreateAccountActivity.this, "Get device token failed", Toast.LENGTH_SHORT).show();
            return;
        }

        // get data from EditTexts
        userInfo = name.getText().toString().trim();
        passwordInfo = password.getText().toString().trim();
        emailInfo = email.getText().toString().trim();
        phoneInfo = phone.getText().toString().trim();

//
//        //reset EditText when click
//        name.setText("");
//        password.setText("");
//        email.setText("");

        //check if user fill all the fields before sending data to firebase
        if (userInfo.isEmpty()) {
            Toast.makeText(CreateAccountActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        } else if (!emailInfo.endsWith(".edu")) {
            Toast.makeText(CreateAccountActivity.this, "Please enter your edu email.", Toast.LENGTH_SHORT).show();
            return;
        } else {

            database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // check phone is not register before
                    if (snapshot.hasChild(phoneInfo)) {
                        Toast.makeText(CreateAccountActivity.this, "Phone is already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        //send data to firebase realtime database
                        // we are using phone as unique identity of every user
                        // so all other details of users comes under email

                        database.child("users").child(phoneInfo).child("name").setValue(userInfo);
                        database.child("users").child(phoneInfo).child("password").setValue(passwordInfo);
                        database.child("users").child(phoneInfo).child("email").setValue(emailInfo);
                        database.child("users").child(phoneInfo).child("token").setValue(deviceToken);

                        // show a success message then finish the activity
                        Toast.makeText(CreateAccountActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    Intent intent = new Intent(CreateAccountActivity.this, RegisterQuestionActivity.class);
                    intent.putExtra("email", emailInfo);
                    intent.putExtra("password", passwordInfo);
                    intent.putExtra("users", phoneInfo);
                    intent.putExtra("name", userInfo);
                    intent.putExtra("token", deviceToken);
                    Log.d("TOKEN", deviceToken);
                    name.setText("");
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
