package com.example.arcmessenger.startUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.arcmessenger.MainActivity;
import com.example.arcmessenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    //private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //userID = firebaseAuth.getCurrentUser().getUid();

        if (firebaseUser!=null){

            new Handler().postDelayed(new Runnable() {
                @Override

                public void run() {

                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }

            },1000);
//            firebaseFirestore.collection("Users").document(firebaseUser.getUid()).get()
//                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.getResult().exists()){
//                                DocumentSnapshot document = task.getResult();
//                                if (document.exists()){
//                                    if (!document.get("userName").equals(null)){
//                                        new Handler().postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
//                                            }
//                                        },1000);
//                                    }
//                                    else {
//                                        startActivity(new Intent(SplashScreenActivity.this, UserInfoActivity.class));
//                                    }
//                                }
//                            }
//                        }
//                    });
        }
        else {

            new Handler().postDelayed(new Runnable() {
                @Override

                public void run() {

                    startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                }

            },2000);

        }
    }
}