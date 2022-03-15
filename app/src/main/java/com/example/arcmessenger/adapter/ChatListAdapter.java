package com.example.arcmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.arcmessenger.R;
import com.example.arcmessenger.chats.ChatsActivity;
import com.example.arcmessenger.model.ChatList;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {

    private List<ChatList> list;
    private Context context;

    public ChatListAdapter(List<ChatList> list, Context context){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chat, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final ChatList chatList = list.get(position);
        holder.user_name.setText(chatList.getUserName());
        holder.user_message.setText(chatList.getUserMessage());
        holder.user_date.setText(chatList.getUserDate());

        if (chatList.getUrlImage().equals("")){
            holder.user_image.setImageResource(R.drawable.icon_user_login);
        }
        else {
            Glide.with(context).load(chatList.getUrlImage()).into(holder.user_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userID", chatList.getUserID())
                        .putExtra("userName", chatList.getUserName())
                        .putExtra("imgProfile", chatList.getUrlImage()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView user_name, user_message, user_date;
        private CircleImageView user_image;

        public Holder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.userName);
            user_message = itemView.findViewById(R.id.userMessage);
            user_date = itemView.findViewById(R.id.userDate);
            user_image = itemView.findViewById(R.id.userImage);

        }
    }
}
