package com.example.tdtuapp.Home.ChatPreview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tdtuapp.Home.Chat.ChatActivity;
import com.example.tdtuapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineUserAdapter extends RecyclerView.Adapter<OnlineUserAdapter.ViewHolder> {
    private Context context;
    private List<ChatPreview> chatPreviewLists = new ArrayList<>();

    public OnlineUserAdapter(Context context) {
        this.context = context;
    }
    public void updateData(List<ChatPreview> chatPreviewLists){
        this.chatPreviewLists = chatPreviewLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OnlineUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_online, null));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull OnlineUserAdapter.ViewHolder holder, int position) {
        ChatPreview chatPreview = chatPreviewLists.get(position);

        // avatar
        if (!chatPreview.getAvatar().isEmpty())
            Picasso.get().load(chatPreview.getAvatar()).into(holder.ivUserChat);

        holder.tvNameUserChat.setText(chatPreview.getName());
        // chuyển qua activity chat giữa các users
        holder.linearLayoutChatPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", chatPreview.getName());
                intent.putExtra("avatar", chatPreview.getAvatar());
                intent.putExtra("isOnline", String.valueOf(chatPreview.isOnline()));
                intent.putExtra("chatKey", chatPreview.getChatKey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatPreviewLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayoutChatPreview;
        CircleImageView ivUserChat;
        TextView tvNameUserChat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutChatPreview = itemView.findViewById(R.id.linearLayoutChatPreview);
            ivUserChat = itemView.findViewById(R.id.ivUserChat);
            tvNameUserChat = itemView.findViewById(R.id.tvNameUserChat);
        }
    }
}
