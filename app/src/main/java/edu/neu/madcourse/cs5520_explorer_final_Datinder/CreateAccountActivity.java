package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    private Button createAccount;
    private ProgressBar spinner;
    private EditText email, password, name;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        spinner = (ProgressBar)findViewById(R.id.create_bar);
        spinner.setVisibility(View.GONE);

        TextView existAccount = (TextView) findViewById(R.id.create_exist_account);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                spinner.setVisibility(View.VISIBLE);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Intent intent = new Intent(CreateAccountActivity.this, MatchScreenActivity.class);
                    startActivity(intent);
                    finish();
                    spinner.setVisibility(View.GONE);
                    return;
                }
                spinner.setVisibility(View.GONE);

            }
        };

        existAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent btnClick = new Intent(CreateAccountActivity.this, SigninActivity.class);
                startActivity(btnClick);
                finish();
                return;
            }
        });

        createAccount = (Button) findViewById(R.id.create_register);
        email = (EditText) findViewById(R.id.create_email);
        password = (EditText) findViewById(R.id.create_password);
        name = (EditText) findViewById(R.id.create_name);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                final String emailInfo = email.getText().toString();
                final String passwordInfo = password.getText().toString();
                final String nameInfo = name.getText().toString();

                if(checkInputs(emailInfo, nameInfo, passwordInfo)){
                    mAuth.createUserWithEmailAndPassword(emailInfo,passwordInfo).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
//                                Toast.makeText(CreateAccountActivity.this, "Registration successfully.", Toast.LENGTH_LONG).show();
//                                String userId = mAuth.getCurrentUser().getUid();
//                                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
//                                Log.d("DB_debug", FirebaseDatabase.getInstance().getReference().getDatabase() + "");
//                                Map userInfo = new HashMap<>();
//                                userInfo.put("name", name);
//                                userInfo.put("profileImageUrl", "default");
                                // currentUserDb.updateChildren(userInfo);

                                //clear the fields
                                email.setText("");
                                name.setText("");
                                password.setText("");
                                Intent btnClick = new Intent(CreateAccountActivity.this, MainActivity.class);
                                startActivity(btnClick);
                                finish();
                                return;
                            } else {
                                Toast.makeText(CreateAccountActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });
    }

    private boolean checkInputs(String email, String username, String password) {
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if (email.isEmpty() || username.isEmpty()|| password.isEmpty()) {
            Toast.makeText(CreateAccountActivity.this, "Please file out all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Below code checks if the email id is valid or not.
        if (!email.matches(emailPattern) && !email.endsWith(".edu")) {
            Toast.makeText(CreateAccountActivity.this, "Please enter a valid school email", Toast.LENGTH_SHORT).show();
            return false;

        }

        if(password.length() < 6){
            Toast.makeText(CreateAccountActivity.this, "The password contains at least six characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        Intent btnClick = new Intent(CreateAccountActivity.this, MainActivity.class);
        startActivity(btnClick);
        finish();
    }
}