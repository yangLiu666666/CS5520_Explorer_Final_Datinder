package edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.TopNavigationBar;

public class ProfileActivity extends AppCompatActivity {

    //first icon in the navigation bar
    private static final int ACTIVITY_ITEM = 0;
    private CircleImageView circleImageView;
    private TextView userName;
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;
    private String userId, nameInfo, userImageUrl;
    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //set up top navigation bar view
        setUpTopNavigationBar();


        circleImageView = findViewById(R.id.circleImage_profile);
        userName = findViewById(R.id.name_profile);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        } else { finish();}

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserName();

        circleImageView.setOnClickListener(view -> {
            if (!checkPermission()) {  // this line checks permission everytime you access this activity
                Toast.makeText(getApplicationContext(), "Please allow access to continue!", Toast.LENGTH_SHORT).show();
                requestPermission();
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
            saveUserInformation();
        });
    }

    //set up name in the textview
    private void getUserName() {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null ){
                        nameInfo = map.get("name").toString();
                        userName.setText(nameInfo);
                    }

                    if (map.get("userImageUrl") != null) {
                        userImageUrl = map.get("userImageUrl").toString();
                        if ("default".equals(userImageUrl)) {
                            Glide.with(getApplication()).load(R.drawable.empty_user_profile).into(circleImageView);
                        } else {
                            Glide.with(getApplication()).load(userImageUrl).into(circleImageView);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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


    private void saveUserInformation() {
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
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete()) {
                    Uri downloadUrl = uri.getResult();
                    Map userInfo1 = new HashMap();
                    userInfo1.put("userImageUrl", downloadUrl.toString());
                    userDatabase.updateChildren(userInfo1);
                    finish();
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
            circleImageView.setImageURI(resultUri);
        }
    }


    public void onProfileClick(View view) {
        switch (view.getId()) {
            case R.id.edit_profile:
                Intent intent1 = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent1);
                break;

            case R.id.edit_setting:
                Intent intent2 = new Intent(ProfileActivity.this, EditSettingsActivity.class);
                startActivity(intent2);
                break;
        }
    }


    /**
     * set up top navigation bar view
     */
    private void setUpTopNavigationBar() {
        BottomNavigationView tvNB = findViewById(R.id.topNavBar);
        TopNavigationBar.logTopNav(tvNB);
        TopNavigationBar.setupTopBar(ProfileActivity.this, tvNB);
        Menu menu = tvNB.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_ITEM);
        menuItem.setChecked(true);
    }
}
