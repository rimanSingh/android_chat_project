package com.example.arcmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.arcmessenger.R;
import com.example.arcmessenger.model.chat.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Chat> list;
    private Context context;

    public ChatAdapter(List<Chat> list, Context context){
        this.list = list;
        this.context = context;
    }

    public void setList(List<Chat> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public static final int Message_Person_Left = 0;
    public static final int Message_Person_Right = 1;
    private FirebaseUser firebaseUser;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Message_Person_Left) {
            View view = LayoutInflater.from(context).inflate(R.layout.left_chat, parent, false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.right_chat, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private LinearLayout layoutText, layoutImage;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.textMessage);
            layoutText = itemView.findViewById(R.id.layout_text);
            layoutImage = itemView.findViewById(R.id.layout_image);
            imageView = itemView.findViewById(R.id.image_chat);
        }
        void bind(Chat chat){
            switch (chat.getMessageType()){
                case "TEXT" :
                    layoutText.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);

                    message.setText(chat.getTextMessage());
                    break;
                case "IMAGE" :
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);

                    Glide.with(context).load(chat.getUrl()).into(imageView);
                    break;
            }
            message.setText(chat.getTextMessage());
        }
    }

    @Override
    public int getItemViewType(int position){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())){
            return Message_Person_Right;
        }
        else {
            return Message_Person_Left;
        }
    }
}
