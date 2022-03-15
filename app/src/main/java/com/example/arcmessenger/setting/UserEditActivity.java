package com.example.arcmessenger.setting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arcmessenger.BuildConfig;
import com.example.arcmessenger.R;
import com.example.arcmessenger.databinding.ActivityUserEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class UserEditActivity extends AppCompatActivity {

    private ActivityUserEditBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private BottomSheetDialog bottomSheetDialog, userEditName;
    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_edit);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(this);

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (firebaseUser!=null)
        {
            getUserInformation();
        }

        onClickUpload();
    }

    private void getUserInformation(){
        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.getString("userName");
                String userImage = documentSnapshot.getString("imgProfile");
                String userPhone = documentSnapshot.getString("userPhone");

                binding.userName.setText(username);
                binding.userPhone.setText(userPhone);
                Glide.with(UserEditActivity.this).load(userImage).into(binding.userImage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Data", "onFailure: "+e.getMessage());
            }
        });
    }

    private void onClickUpload() {
        binding.buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserImageChangeOption();
            }
        });
        binding.buttonEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogNameUpdate();
            }
        });
    }

    private void showUserImageChangeOption() {
        View view = getLayoutInflater().inflate(R.layout.activity_user_edit_image_pick_option,null);

        ((View) view.findViewById(R.id.photoGallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhoneGallery();
                bottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraPermission();
                bottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bottomSheetDialog=null;
            }
        });
        bottomSheetDialog.show();
    }

    private void openCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.CAMERA },
                    221);
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    222);
        }
        else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        try {
            File file = File.createTempFile("IMG_" + timeStamp, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, 440);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openPhoneGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image"), IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null)
        {
            imageUri = data.getData();
            uploadToFirebaseDatabase();
        }

        if (requestCode == 440
                && resultCode == RESULT_OK)
        {
            uploadToFirebaseDatabase();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadToFirebaseDatabase() {
        if (imageUri != null)
        {
            progressDialog.setMessage("Uploading....");
            progressDialog.show();
            StorageReference riversRef = FirebaseStorage.getInstance().getReference()
                    .child("User_Images/" +System.currentTimeMillis()+"."+getFileExtension(imageUri));
            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    final String download_url = String.valueOf(downloadUrl);

                    final HashMap<String, Object> hashMap = new HashMap<>();
                    //hashMap.put("imgProfile", download_url);
                    Glide.with(UserEditActivity.this)
                            .load(hashMap.put("imgProfile", download_url))
                            .into(binding.userImage);

                    progressDialog.dismiss();

                    firebaseFirestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"Successfully Upload", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Upload Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void showDialogNameUpdate(){
        View view = getLayoutInflater().inflate(R.layout.activity_user_edit_name,null);

        ((View) view.findViewById(R.id.buttonCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEditName.dismiss();
            }
        });

        final EditText updateName = view.findViewById(R.id.editUsername);
        ((View) view.findViewById(R.id.buttonSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(updateName.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Name can't be empty",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    edit_name(updateName.getText().toString());
                    userEditName.dismiss();
                }
            }
        });

        userEditName = new BottomSheetDialog(this);
        userEditName.setContentView(view);

        userEditName.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                userEditName=null;
            }
        });
        userEditName.show();
    }

    private void edit_name(String newName){
        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).update("userName",newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Update Successful",Toast.LENGTH_SHORT).show();
                getUserInformation();
            }
        });
    }
}