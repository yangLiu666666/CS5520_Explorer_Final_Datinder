package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class SigninActivity extends AppCompatActivity {
    private static final String TAG = "SigninActivity";
    private ProgressBar spinner;
    private Button signIn;
    private EditText email, password;
    private TextView createAccount;
    private boolean loginBtnClicked;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        loginBtnClicked = false;
        spinner = (ProgressBar)findViewById(R.id.sign_bar);
        spinner.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        signIn = (Button) findViewById(R.id.sign_in);
        email = (EditText) findViewById(R.id.sign_email);
        password = findViewById(R.id.sign_password);
        createAccount = (TextView) findViewById(R.id.sign_tip);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtnClicked = true;
                spinner.setVisibility(View.VISIBLE);
                final String emailInfo = email.getText().toString();
                final String passwordInfo = password.getText().toString();
                if(emailInfo.isEmpty()){
                    Toast.makeText(SigninActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(emailInfo, passwordInfo).addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SigninActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(SigninActivity.this, Swip.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                spinner.setVisibility(View.GONE);

            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                Intent intent = new Intent(SigninActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                spinner.setVisibility(View.GONE);
                finish();
                return;
            }
        });

        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //uncomment this for production
                if (user !=null && !loginBtnClicked){
                    spinner.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(SigninActivity.this, MatchScreenActivity.class);
                    startActivity(intent);
                    finish();
                    spinner.setVisibility(View.GONE);
                    return;
                }

            }
        };

    }

    //    private boolean isStringNull(String string) {
//        Log.d(TAG, "isStringNull: checking string if null.");
//
//        return string.equals("");
//    }

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
        Intent btnClick = new Intent(SigninActivity.this, MainActivity.class);
        startActivity(btnClick);
        finish();
        return;
    }
}