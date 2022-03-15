package com.example.arcmessenger.manageChatAndImage.manageChat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.arcmessenger.adapter.ChatAdapter;
import com.example.arcmessenger.chats.ChatsActivity;
import com.example.arcmessenger.manageChatAndImage.interfaces.OnReadChatCallback;
import com.example.arcmessenger.model.chat.Chat;
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
import java.util.List;

public class ChatService {
    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private String receiverID;

    public ChatService(Context context, String receiverID) {
        this.context = context;
        this.receiverID = receiverID;
    }

    public void readChat(final OnReadChatCallback onReadChatCallback){
        databaseReference.child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Chat> list = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat!=null && chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(receiverID)
                            || chat.getReceiver().equals(receiverID) && chat.getSender().equals(firebaseUser.getUid()))
                    {
                        list.add(chat);
                    }
                }
                onReadChatCallback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onReadChatCallback.onFailed();
            }
        });
    }

    public void sendText(String toString){
        Chat chat = new Chat(
                getCurrentDate(),
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

    public void sendImage(String imageUrl){
        Chat chat = new Chat(
                getCurrentDate(),
                "",
                imageUrl,
                "IMAGE",
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

    public String getCurrentDate(){
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String today = format.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());

        return today+", "+currentTime;
    }
}
