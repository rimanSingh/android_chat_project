package com.example.arcmessenger.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.arcmessenger.R;
import com.example.arcmessenger.adapter.ChatAdapter;
import com.example.arcmessenger.databinding.ActivityChatsBinding;
import com.example.arcmessenger.manageChatAndImage.dialog.DialogReviewSendImage;
import com.example.arcmessenger.manageChatAndImage.interfaces.OnReadChatCallback;
import com.example.arcmessenger.manageChatAndImage.manageChat.ChatService;
import com.example.arcmessenger.manageChatAndImage.service.FirebaseService;
import com.example.arcmessenger.model.chat.Chat;
import com.example.arcmessenger.startUp.UserInfoActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    private static final String TAG = "ChatsActivity";
    private ActivityChatsBinding binding;
    private String receiverID;
    private List<Chat> list = new ArrayList<>();
    private ChatAdapter adapter;
    private boolean actionShow = false;

    private ChatService chatService;

    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chats);

        initialize();
        buttonClick();
        readChat();
    }

    private void initialize(){
        Intent intent = getIntent();
        receiverID = intent.getStringExtra("userID");
        String userName = intent.getStringExtra("userName");
        String userImage = intent.getStringExtra("imgProfile");

        chatService = new ChatService(this, receiverID);

        if (receiverID != null) {
            binding.userName.setText(userName);
            if (userImage != null) {
                if (userImage.equals("")){
                    binding.userImage.setImageResource(R.drawable.icon_user_login);
                }
                else {
                    Glide.with(this).load(userImage).into(binding.userImage);
                }
            }
        }

        binding.textMessageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(binding.textMessageBox.getText().toString())){
                    binding.buttonSend.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_voice_24));
                }
                else {
                    binding.buttonSend.setImageDrawable(getDrawable(R.drawable.ic_baseline_send_24));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(list, this);
        binding.recyclerView.setAdapter(adapter);
    }

    private void readChat() {
        chatService.readChat(new OnReadChatCallback() {
            @Override
            public void onSuccess(List<Chat> list) {
                adapter.setList(list);
            }

            @Override
            public void onFailed() {
                Log.d(TAG, "onFailed: ");
            }
        });
    }

    private void buttonClick() {
        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(binding.textMessageBox.getText().toString())) {
                    chatService.sendText(binding.textMessageBox.getText().toString());
                    binding.textMessageBox.setText("");
                }
            }
        });

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.iconAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionShow){
                    binding.menuCard.setVisibility(View.GONE);
                    actionShow = false;
                }
                else {
                    binding.menuCard.setVisibility(View.VISIBLE);
                    actionShow = true;
                }
            }
        });

        binding.buttonSendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneGallery();
            }
        });
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
            //uploadToFirebaseDatabase();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                reviewImage(bitmap);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void reviewImage(Bitmap bitmap){
        new DialogReviewSendImage(ChatsActivity.this, bitmap).showImage(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void onButtonSend() {
                if (imageUri!=null){

                    binding.menuCard.setVisibility(View.GONE);
                    actionShow = false;

                    new FirebaseService(ChatsActivity.this).uploadImageToFirebase(imageUri, new FirebaseService.OnCallBack() {
                        @Override
                        public void onUploadSuccessful(String imageUrl) {
                            chatService.sendImage(imageUrl);
                        }

                        @Override
                        public void onUploadFailed(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
    
/*
    private void sendText(String toString) {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String today = format.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());

        Chat chat = new Chat(
                today+", "+currentTime,
                toString,
                "",
                "TEXT",
                firebaseUser.getUid(),
                receiverID
        );

        databaseReference.child("Chat").push().setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Send", "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Send", "onFailure: "+e.getMessage());
            }
        });

        DatabaseReference chatReference1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatReference1.child("chatID").setValue(receiverID);

        DatabaseReference chatReference2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatReference2.child("chatID").setValue(firebaseUser.getUid());
    }
 */
/*
    private void readChat() {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Chat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat!=null && chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(receiverID)
                                || chat.getReceiver().equals(receiverID) && chat.getSender().equals(firebaseUser.getUid()))
                        {
                            list.add(chat);
                            //Log.d(TAG, "onDataChange: UserName: "+chats.getTextMessage());
                        }
                    }
                    if (adapter!=null){
                        adapter.notifyDataSetChanged();
                    }else {
                        adapter = new ChatAdapter(list, ChatsActivity.this);
                        binding.recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
 */

}