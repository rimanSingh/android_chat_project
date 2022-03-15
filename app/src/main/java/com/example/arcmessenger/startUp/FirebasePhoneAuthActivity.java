package com.example.arcmessenger.startUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.arcmessenger.R;
import com.example.arcmessenger.databinding.ActivityFirebasePhoneAuthBinding;
import com.example.arcmessenger.model.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class FirebasePhoneAuthActivity extends AppCompatActivity {

    private ActivityFirebasePhoneAuthBinding binding;

    private static final String TAG = "PhoneAuth_TAG";

    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationID;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirebasePhoneAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phoneLl.setVisibility(View.VISIBLE);
        binding.codeLl.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(FirebasePhoneAuthActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationID, forceResendingToken);

                Log.d(TAG, "onCodeSent: "+ verificationID);

                mVerificationID = verificationID;
                forceResendingToken = token;
                progressDialog.dismiss();
                closeKeyBoard();

                binding.phoneLl.setVisibility(View.GONE);
                binding.codeLl.setVisibility(View.VISIBLE);

                Toast.makeText(FirebasePhoneAuthActivity.this, "Verification code sent...", Toast.LENGTH_SHORT).show();

                String phone = binding.phoneLlPhoneNumber.getText().toString().trim();
                String phoneNumber = "+" + binding.phoneLlCountryCodePicker.getSelectedCountryCode() + phone;
                binding.codeLlPhoneNumber.setText(phoneNumber);
            }
        };

        binding.phoneLlButtonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.phoneLlPhoneNumber.getText().toString().trim();
                String phoneNumber = "+" + binding.phoneLlCountryCodePicker.getSelectedCountryCode() + phone;

                if (TextUtils.isEmpty((phone))){
                    binding.phoneLlPhoneNumber.setError("Number is required...");
                    binding.phoneLlPhoneNumber.requestFocus();
                    return;
                }
                else {
                    phoneNumberVerification(phoneNumber);
                }
            }
        });

        binding.codeLlResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = binding.phoneLlPhoneNumber.getText().toString().trim();
                String phoneNumber = binding.phoneLlCountryCodePicker.getSelectedCountryCode() + phone;

                if (TextUtils.isEmpty((phone))){
                    binding.phoneLlPhoneNumber.setError("Number is required...");
                    binding.phoneLlPhoneNumber.requestFocus();
                    return;
                }
                else {
                    resendVerificationCode(phoneNumber, forceResendingToken);
                }
            }
        });

        binding.codeLlButtonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpCode = binding.codeLlOtpBox.getText().toString().trim();

                if (otpCode.isEmpty()){
                    binding.codeLlOtpBox.setError("Please enter OTP code...");
                    binding.codeLlOtpBox.requestFocus();
                    return;
                }
                else {
                    verifyCode(mVerificationID, otpCode);
                }
            }
        });
    }

    private void phoneNumberVerification(String phoneNumber) {
        progressDialog.setMessage("Verifying Phone Number");
        progressDialog.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        progressDialog.setMessage("Resending Code");
        progressDialog.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String mVerificationID, String otpCode) {
        progressDialog.setMessage("Verifying Code...");
        progressDialog.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, otpCode);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressDialog.setMessage("Please wait...");

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            Intent intent = new Intent(FirebasePhoneAuthActivity.this, UserInfoActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            if (firebaseUser != null){
                                String userID = firebaseAuth.getCurrentUser().getUid();
                                UserDetail userDetail = new UserDetail(
                                        userID,
                                        "",
                                        firebaseUser.getPhoneNumber(),
                                        "",
                                        "",
                                        "",
                                        ""
                                );
                                DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
                                documentReference.set(userDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                            }
                        }
                    }
                });
//                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        progressDialog.dismiss();
//                        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
//
//                        Toast.makeText(FirebasePhoneAuthActivity.this, "Welcome to your profile", Toast.LENGTH_SHORT).show();
//
//                        startActivity(new Intent(FirebasePhoneAuthActivity.this, UserInfoActivity.class));
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(FirebasePhoneAuthActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}