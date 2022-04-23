package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

@SuppressWarnings("ALL")
public class RegisterQuestionActivity extends AppCompatActivity {
    private EditText userName, userStory, userSchool;
    private ProgressBar spinner;
    private Button confirm;
    //private ImageButton back;
    private ImageView userImage;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    private RadioGroup gender, likeGender;

    private String userId, nameInfo, userImageUrl, genderInfo, schoolInfo, storyInfo;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_question);

        spinner = findViewById(R.id.profile_bar);
        spinner.setVisibility(View.GONE);

        userName = findViewById(R.id.profile_name);
        userStory = findViewById(R.id.story);
        userSchool = findViewById(R.id.school_name);
        userImage = findViewById(R.id.user_image);
        confirm = findViewById(R.id.confirm);
        gender = findViewById(R.id.gender_radio_group);


        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null && mAuth.getCurrentUser()!= null)
            userId = mAuth.getCurrentUser().getUid();
        else {
            finish();
        }

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermission()) {  // this line checks permission everytime you access this activity
                    Toast.makeText(getApplicationContext(), "Please allow access to continue!", Toast.LENGTH_SHORT).show();
                    requestPermission();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                Intent intent = new Intent(RegisterQuestionActivity.this, Swip.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            } else {
                Toast.makeText(getApplicationContext(), "Please allow access to continue!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUserInfo() {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        nameInfo = map.get("name").toString();
                        userName.setText(nameInfo);
                    }

                    if(map.get("gender")!=null) {
                        genderInfo = map.get("gender").toString();
                    }

                    if (map.get("introduction") != null) {
                        storyInfo = map.get("introduction").toString();
                    }

                    if (map.get("school") != null) {
                        schoolInfo = map.get("school").toString();
                    }


                    if(map.get("userImageUrl")!=null){
                        userImageUrl = map.get("userImageUrl").toString();
                        switch(userImageUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.drawable.empty_user_profile).into(userImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(userImageUrl).into(userImage);
                                break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void saveUserInformation() {
        nameInfo = userName.getText().toString();
        schoolInfo = userSchool.getText().toString();
        storyInfo = userStory.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", nameInfo);
        userInfo.put("introduction", storyInfo);
        userInfo.put("school", schoolInfo);

        userDatabase.updateChildren(userInfo);
        if(resultUri != null){
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
            byte[] data = stream.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uri.isComplete());
                    Uri downloadUrl = uri.getResult();
                    Map userInfo = new HashMap();
                    userInfo.put("userImageUrl", downloadUrl.toString());
                    userDatabase.updateChildren(userInfo);

                    finish();
                    return;
                }
            });
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            userImage.setImageURI(resultUri);
        }
    }

}