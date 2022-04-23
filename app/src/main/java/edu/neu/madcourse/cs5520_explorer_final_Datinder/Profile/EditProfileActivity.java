package edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;

@SuppressWarnings("ALL")
public class EditProfileActivity extends AppCompatActivity {

    private Button man, woman, nongender;
    private ImageButton back;
    private TextView man_text, women_text, nongender_text;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView;
    private DatabaseReference userDatabase;
    private final String[] permissionsRequired = new String[]{android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SharedPreferences permissionStatus;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private boolean sentToSettings = false;
    private final static int REQUEST_CAMERA = 0;
    private final static int REQUEST_GALLERY = 1;

    private FirebaseAuth mAuth;
    private String userId, additionalProfileImageUrl, additionalProfileImageUrlX, introduction, school, imageviewString;
    private EditText selfIntroText, schoolText;
    private Uri resultUri;




    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        imageView1 = findViewById(R.id.image_view_1);
        imageView2 = findViewById(R.id.image_view_2);
        imageView3 = findViewById(R.id.image_view_3);
        imageView4 = findViewById(R.id.image_view_4);
        imageView5 = findViewById(R.id.image_view_5);
        imageView6 = findViewById(R.id.image_view_6);
        //default imageView to first one
        imageView = findViewById(R.id.image_view_1);
        man = findViewById(R.id.man_button);
        woman = findViewById(R.id.woman_button);
        nongender = findViewById(R.id.nongender_button);
        man_text = findViewById(R.id.man_text);
        women_text = findViewById(R.id.woman_text);
        nongender_text = findViewById(R.id.nongender_text);
        selfIntroText = findViewById(R.id.selfIntroText);
        schoolText = findViewById(R.id.edit_SchoolText);

        back = findViewById(R.id.back);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        //if click back imageButton => go back
        back.setOnClickListener(view -> {
            onBackPressed();
        });

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

        imageView1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showAlertDialogForDelete(imageView1, "imageview1");
                return true;
            }
        });

        imageView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showAlertDialogForDelete(imageView2, "imageview2");
                return true;
            }
        });

        imageView3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showAlertDialogForDelete(imageView3, "imageview3");
                return true;
            }
        });
        imageView4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showAlertDialogForDelete(imageView4, "imageview4");
                return true;
            }
        });
        imageView5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showAlertDialogForDelete(imageView5, "imageview5");
                return true;
            }
        });
        imageView6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showAlertDialogForDelete(imageView6, "imageview6");
                return true;
            }
        });

    };

    @Override
    public void onBackPressed() {
        saveUserTextInfo();
        super.onBackPressed();
    }


    public void showAlertDialogForDelete(ImageView imageView, String imageviewString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Delete image?");
        builder.setMessage("Choose the options below to delete this image");
        builder.setPositiveButton("Delete", (dialogInterface, i) -> {
            dialogInterface.cancel();
            imageView.setImageDrawable(null);
            userDatabase.child("additionalProfileImageUrl").child(imageviewString).setValue(null);
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }


    //set up onClick for edit profile imageview and buttons
    public void onClickUploadProfilePhoto(View view) {
        switch (view.getId()) {
            case R.id.image_view_1:
                imageView = imageView1;
                requestMultiplePermissions();
                break;
            case R.id.image_view_2:
                imageView = imageView2;
                requestMultiplePermissions();
                break;
            case R.id.image_view_3:
                imageView = imageView3;
                requestMultiplePermissions();
                break;
            case R.id.image_view_4:
                imageView = imageView3;
                requestMultiplePermissions();
                break;
            case R.id.image_view_5:
                imageView = imageView3;
                requestMultiplePermissions();
                break;
            case R.id.image_view_6:
                imageView = imageView3;
                requestMultiplePermissions();
                break;
            case R.id.image_btn_1:
                imageView = imageView1;
                requestMultiplePermissions();
                break;
            case R.id.image_btn_2:
                imageView = imageView2;
                requestMultiplePermissions();
                break;
            case R.id.image_btn_3:
                imageView = imageView3;
                requestMultiplePermissions();
                break;
            case R.id.image_btn_4:
                imageView = imageView4;
                requestMultiplePermissions();
                break;
            case R.id.image_btn_5:
                imageView = imageView5;
                requestMultiplePermissions();
                break;
            case R.id.image_btn_6:
                imageView = imageView6;
                requestMultiplePermissions();
                break;
        }
    }

    /**
     * Request CAMERA & STORAGE (WRITE & READ) permissions
     * showAlertDialog1 as default
     * showAlertDialog2 for previously denied request
     * update sharedPreferences
     */
    private void requestMultiplePermissions() {
        for (String permission: permissionsRequired) {
            if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditProfileActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            } else {
                //means already have this permission
                //nothing to show => continue
                Toast.makeText(getApplicationContext(), "Permission already granted.", Toast.LENGTH_SHORT).show();
                proceedAfterPermission();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissionsRequired, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissionsRequired, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT && grantResults.length >= 0) {
            boolean allgranted = false;
            if (grantResults.length > 0) {
                for (int i=0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        allgranted = true;
                    } else {
                        allgranted = false;
                        break;
                    }
                }
            }
            if (allgranted) {
                Intent intent = new Intent(EditProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, permissionsRequired[2])) {
                showAlertDialog1();
            } else {
                //denied with "never ask again" => got go to settings
                showAlertDialog2();
                Toast.makeText(getApplicationContext(), "Access Denied. Go to Settings to allow permissions.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void proceedAfterPermission() {
        try {
            final CharSequence[] menu = {"Take Photo", "Choose from Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);

            builder.setTitle("Add Photo");

            builder.setItems(menu, (DialogInterface dialog, int item) -> {

                if (menu[item].equals("Take Photo")) {
                    dialog.dismiss();
                    cameraIntent();

                } else if (menu[item].equals("Choose from Gallery")) {
//                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(pickPhoto, 1);
                    dialog.dismiss();
                    galleryIntent();
                } else if (menu[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            Toast.makeText(EditProfileActivity.this, "Permission error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void cameraIntent() {
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePhoto, REQUEST_CAMERA);
    }

    @SuppressWarnings("deprecation")
    private void galleryIntent() {
        Intent pickPhoto = new Intent();
        pickPhoto.setType("image/*");
        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(pickPhoto, "Select File"), REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        final Uri imageUri = data.getData();
        resultUri = imageUri;
        Bitmap bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(bm);
        saveUserImageInformation(imageView);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(bm);
        saveUserImageInformation(imageView);
    }

    private void showAlertDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Permissions needed to proceed");
        builder.setMessage("Camera and Storage permissions need to be granted.");
        builder.setPositiveButton("Grant", (dialogInterface, i) -> {
            dialogInterface.cancel();
            ActivityCompat.requestPermissions(EditProfileActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }

    @SuppressWarnings("deprecation")
    private void showAlertDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Please allow Permissions to proceed");
        builder.setMessage("Camera and Storage permissions need to be granted.");
        builder.setPositiveButton("Go to Settings", (dialogInterface, i) -> {
            dialogInterface.cancel();
            sentToSettings = true;
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//            Toast.makeText(getBaseContext(), "Go to PERMISSIONS to grant Camera and Storage permissions.", Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }


    private void getUserInfo() {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("introduction") != null) {
                        introduction = map.get("introduction").toString();
                        selfIntroText.setText(introduction);
                    }
                    if (map.get("school") != null) {
                        school = map.get("school").toString();
                        schoolText.setText(school);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        userDatabase.child("additionalProfileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("imageview1") != null) {
                        additionalProfileImageUrlX = map.get("imageview1").toString();
                        Glide.with(getApplication()).load(additionalProfileImageUrlX).into(imageView1);
                    }
                    if (map.get("imageview2") != null) {
                        additionalProfileImageUrlX = map.get("imageview2").toString();
                        Glide.with(getApplication()).load(additionalProfileImageUrlX).into(imageView2);
                    }
                    if (map.get("imageview3") != null) {
                        additionalProfileImageUrlX = map.get("imageview3").toString();
                        Glide.with(getApplication()).load(additionalProfileImageUrlX).into(imageView3);
                    }
                    if (map.get("imageview4") != null) {
                        additionalProfileImageUrlX = map.get("imageview4").toString();
                        Glide.with(getApplication()).load(additionalProfileImageUrlX).into(imageView4);
                    }
                    if (map.get("imageview5") != null) {
                        additionalProfileImageUrlX = map.get("imageview5").toString();
                        Glide.with(getApplication()).load(additionalProfileImageUrlX).into(imageView5);
                    }
                    if (map.get("imageview6") != null) {
                        additionalProfileImageUrlX = map.get("imageview6").toString();
                        Glide.with(getApplication()).load(additionalProfileImageUrlX).into(imageView6);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void saveUserTextInfo() {
        introduction = selfIntroText.getText().toString();
        school = schoolText.getText().toString();

        //save the two EditText content
        userDatabase.child("introduction").setValue(introduction);
        userDatabase.child("school").setValue(school);
    }


    private void saveUserImageInformation(ImageView imageView) {
        //save uploaded image
        if(resultUri != null){
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("additionalProfileImages").child(userId);
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
                    //save this image under additionalProfileImageUrl's child
                    Map additionalProfileImageUrlInfo = new HashMap();
                    if ("imageview1".equals(imageView.getTag())) {
                        additionalProfileImageUrlInfo.put("imageview1", downloadUrl.toString());
                    } else if ("imageview2".equals(imageView.getTag())) {
                        additionalProfileImageUrlInfo.put("imageview2", downloadUrl.toString());
                    } else if ("imageview3".equals(imageView.getTag())) {
                        additionalProfileImageUrlInfo.put("imageview3", downloadUrl.toString());
                    } else if ("imageview4".equals(imageView.getTag())) {
                        additionalProfileImageUrlInfo.put("imageview4", downloadUrl.toString());
                    } else if ("imageview5".equals(imageView.getTag())) {
                        additionalProfileImageUrlInfo.put("imageview5", downloadUrl.toString());
                    } else if ("imageview6".equals(imageView.getTag())) {
                        additionalProfileImageUrlInfo.put("imageview6", downloadUrl.toString());
                    }
//                    additionalProfileImageUrlInfo.put(imageView.toString(), downloadUrl.toString());
                    userDatabase.child("additionalProfileImageUrl").updateChildren(additionalProfileImageUrlInfo);
                    Toast.makeText(getApplicationContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditProfileActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                    return;
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Error! No image was chosen", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImageView) {
            ImageView other = (ImageView) obj;
            return this.equals(other);
        }
        return false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

}
