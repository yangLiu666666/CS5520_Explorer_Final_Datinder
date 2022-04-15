package edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile;

import android.Manifest;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;

public class EditProfileActivity extends AppCompatActivity {

    private Button man, woman, nongender;
    private ImageButton back;
    private TextView man_text, women_text, nongender_text;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView;
    private DatabaseReference userDatabase;
    private String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
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

    /**
     * Request CAMERA & STORAGE (WRITE & READ) permissions
     * showAlertDialog1 as default
     * showAlertDialog2 for previously denied request
     * update sharedPreferences
     */
    private void requestMultiplePermissions() {
        if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, permissionsRequired[2])) {
                //show alertDialog asking for permission
                showAlertDialog1();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //redirect to settings after showing info why need the permission
                //previously cancelled
                showAlertDialog2();
            } else {
                //request permission
                ActivityCompat.requestPermissions(EditProfileActivity.this, permissionsRequired, 100);
            }
            //update SharedPreferences
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.apply();
        } else {
            //means already have this permission
            //nothing to show period => continue
        }
    }

    private void proceedAfterPermission() {
        final CharSequence[] menu = {"Take Photo", "Choose from Photo Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);

        builder.setTitle("Add Photo!");

        builder.setItems(menu, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (menu[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (menu[item].equals("Choose from Gallery")) {
                    galleryIntent();
                } else if (menu[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @SuppressWarnings("deprecation")
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @SuppressWarnings("deprecation")
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //permission already granted
                proceedAfterPermission();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) //select file
                onSelectFromGalleryResult(data);
            else if (requestCode == 0) //request camera
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

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

        imageView.setImageBitmap(thumbnail);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(bm);
    }

    private void showAlertDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Permissions needed to proceed");
        builder.setMessage("Camera and Location permissions need to be granted.");
        builder.setPositiveButton("Grant", (dialogInterface, i) -> {
            dialogInterface.cancel();
            ActivityCompat.requestPermissions(EditProfileActivity.this, permissionsRequired, 100);
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }

    @SuppressWarnings("deprecation")
    private void showAlertDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Permissions needed to proceed");
        builder.setMessage("Camera and Location permissions need to be granted.");
        builder.setPositiveButton("Grant", (dialogInterface, i) -> {
            dialogInterface.cancel();
            sentToSettings = true;
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
            Toast.makeText(getBaseContext(), "Go to SETTINGS to grant Camera and Location permissions.", Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
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
