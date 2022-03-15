package com.example.arcmessenger.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.arcmessenger.R;
import com.example.arcmessenger.databinding.ActivitySettingBinding;
import com.example.arcmessenger.startUp.SplashScreenActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

        onClick();
    }

    private void onClick() {
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAccountLogout();
            }
        });

        findViewById(R.id.editDetail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, UserEditActivity.class));
            }
        });
    }

    private void onClickAccountLogout(){
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(SettingActivity.this);
        dialogBox.setMessage("Are you sure you want to logout?");
        dialogBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingActivity.this, SplashScreenActivity.class));
            }
        });
        dialogBox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = dialogBox.create();
        dialog.show();
    }

    private void getUserInformation(){
        firebaseFirestore.collection("Users")
                .document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.getString("userName");
                String userImage = documentSnapshot.getString("imgProfile");
                String userPhone = documentSnapshot.getString("userPhone");

                binding.userName.setText(username);
                binding.userPhone.setText(userPhone);
                Glide.with(SettingActivity.this).load(userImage).into(binding.userImage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Data", "onFailure: "+e.getMessage());
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }
}