package com.example.arcmessenger.menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arcmessenger.R;
import com.example.arcmessenger.adapter.ChatListAdapter;
import com.example.arcmessenger.databinding.FragmentChatBinding;
import com.example.arcmessenger.model.ChatList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
/*
    public ChatFragment() {

    }

    private List<ChatList> list = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getChatlist();
        return view;
    }

    private void getChatlist() {
        //list.add(new ChatList("1","Pam","hello","27/03/2021","https://i.pinimg.com/originals/4c/4f/d7/4c4fd7a61bdb0f95d9e1f782ad80cf72.jpg"));
        //list.add(new ChatList("2","Sam","kida","27/03/2021","https://i.pinimg.com/originals/d4/28/b1/d428b1aeab6f8dc0b1155fc7bf450812.jpg"));

        recyclerView.setAdapter(new ChatListAdapter(list, getContext()));
    }
 */

    private static final String TAG = "ChatFragment";

    public ChatFragment() {

    }

    private FragmentChatBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;

    private List<ChatList> list;
    private ArrayList<String> addUserID;
    private Handler handler = new Handler();
    private ChatListAdapter chatListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);

        list = new ArrayList<>();
        addUserID = new ArrayList<>();

        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatListAdapter = new ChatListAdapter(list, getContext());
        binding.recycleView.setAdapter(chatListAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseUser!=null) {
            chatList();
        }
        
        return binding.getRoot();
    }

    private void chatList() {
        //binding.progressBar.setVisibility(View.VISIBLE);
        databaseReference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                addUserID.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String userID = Objects.requireNonNull(snapshot.child("chatID").getValue()).toString();
                    Log.d(TAG, "onDataChange: userID "+userID);

                    //binding.progressBar.setVisibility(View.GONE);
                    addUserID.add(userID);
                }
                getData();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getData() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (String userID : addUserID){
                    firebaseFirestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {
                                ChatList chatList = new ChatList(
                                        documentSnapshot.getString("userID"),
                                        documentSnapshot.getString("userName"),
                                        "Message will be shown here...",
                                        "10-Jan-2021",
                                        documentSnapshot.getString("imgProfile")
                                );
                                list.add(chatList);
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onSuccess: "+e.getMessage());
                            }
                            if (chatListAdapter!=null){
                                chatListAdapter.notifyItemInserted(0);
                                chatListAdapter.notifyDataSetChanged();

                                Log.d(TAG, "onSuccess: adapter "+chatListAdapter.getItemCount());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Error "+e.getMessage());
                        }
                    });
                }
            }
        });
    }

}