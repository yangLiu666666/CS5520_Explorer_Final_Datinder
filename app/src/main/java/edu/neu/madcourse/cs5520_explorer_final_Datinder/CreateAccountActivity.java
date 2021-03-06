

package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    private Button createAccount;
    private ProgressBar spinner;
    private EditText email, password, name;
    private RadioGroup gender;
    private TextView dateBirth, existAccount;
    private String ageInfo, location;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+edu+";
    private static final String TAG = "RegisterActivity";
    private String latitude = "Latitude: %s";
    private String longitude = "Longitude: %s";
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        spinner = (ProgressBar) findViewById(R.id.create_bar);
        spinner.setVisibility(View.GONE);

        existAccount = (TextView) findViewById(R.id.create_exist_account);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                spinner.setVisibility(View.VISIBLE);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(CreateAccountActivity.this, Swip.class);
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
        email = (EditText) findViewById(R.id.create_account_email);
        password = (EditText) findViewById(R.id.create_password);
        name = (EditText) findViewById(R.id.create_name);
        gender = findViewById(R.id.gender_radio_group);
        dateBirth = findViewById(R.id.create_birthday);

        dateBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), datePickerListener, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            noGpsMessage();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                final String emailInfo = email.getText().toString();
                final String passwordInfo = password.getText().toString();
                final String nameInfo = name.getText().toString();

                int selectId = gender.getCheckedRadioButtonId();
                final RadioButton genderInfo = (RadioButton) findViewById(selectId);

                if (genderInfo.getText() == null) {
                    return;
                }

                final String genderInformation = genderInfo.getText().toString();
                final String birthDay = dateBirth.toString();

                final String locationInfo = getLocation();

                if (checkInputs(emailInfo, nameInfo, passwordInfo, genderInformation, birthDay)) {
                    mAuth.createUserWithEmailAndPassword(emailInfo, passwordInfo).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CreateAccountActivity.this, "Registration successfully.", Toast.LENGTH_LONG).show();
                                String userId = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                Log.d("DB_debug", FirebaseDatabase.getInstance().getReference().getDatabase() + "");
                                Map userInfo = new HashMap<>();
                                userInfo.put("name", nameInfo);
                                userInfo.put("userImageUrl", "default");
                                userInfo.put("gender", genderInformation);
                                userInfo.put("age", ageInfo);
                                userInfo.put("location", locationInfo);
                                userInfo.put("introduction", null);
                                userInfo.put("school", null);
                                userInfo.put("additionalProfileImageUrl", "default");
                                currentUserDb.updateChildren(userInfo);

                                //clear the fields
                                email.setText("");
                                name.setText("");
                                password.setText("");
                                Intent btnClick = new Intent(CreateAccountActivity.this, RegisterQuestionActivity.class);
                                startActivity(btnClick);
                                finish();
                            } else {
                                Toast.makeText(CreateAccountActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);

            String format = new SimpleDateFormat("dd/MM/YYYY").format(c.getTime());
            dateBirth.setText(format);
            if (calculateAge(c.getTimeInMillis()) < 16) {
                Toast.makeText(CreateAccountActivity.this, "You are under the age to use our app, we hope to meet you again after you are 16 years old. Thank you!", Toast.LENGTH_LONG).show();
            }
            ageInfo = Integer.toString(calculateAge(c.getTimeInMillis()));

        }
    };

    private int calculateAge(long date) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
        return age;

    }

    private boolean checkInputs(String email, String username, String password, String gender, String birthday) {
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || gender.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(CreateAccountActivity.this, "Please file out all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Below code checks if the email id is valid or not.
        if (!email.matches(emailPattern)) {
            Toast.makeText(CreateAccountActivity.this, "Please enter a valid school email", Toast.LENGTH_SHORT).show();
            return false;

        }

        if (password.length() < 6) {
            Toast.makeText(CreateAccountActivity.this, "The password contains at least six characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getLocation() {
        if (ActivityCompat.checkSelfPermission(CreateAccountActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateAccountActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // text.setText("This application requires location access. Please grant location access.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (locationGPS != null) {
                location = String.format("(" + latitude + "," + longitude + ")", locationGPS.getLatitude(), locationGPS.getLongitude());
                //System.out.println(location);
            }
            if (locationNetwork != null) {
                location = String.format("(" + latitude + "," + longitude + ")", locationNetwork.getLatitude(), locationNetwork.getLongitude());
            }
            if (locationPassive != null) {
                location = String.format("(" + latitude + "," + longitude + ")", locationPassive.getLatitude(), locationPassive.getLongitude());
            } else {
                // Snackbar.make(location, "Unable to trace your location, please try again.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, getResources().getInteger(R.integer.update_time), getResources().getInteger(R.integer.update_distance), v -> {
                });
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, getResources().getInteger(R.integer.update_time), getResources().getInteger(R.integer.update_distance), v -> {
                });
            }
        }
        return location;
    }


    protected void noGpsMessage() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Please turn on your GPS connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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