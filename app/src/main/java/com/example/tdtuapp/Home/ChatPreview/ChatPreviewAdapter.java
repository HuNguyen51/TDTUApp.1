package com.example.tdtuapp.Home.ChatPreview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ViewHolder> {
    private Context context;
    private Activity activity;
    private List<ChatPreview> chatPreviewLists = new ArrayList<>();
    public ChatPreviewAdapter(Context context) {
        this.context = context;
    }
    public void updateData(List<ChatPreview> chatPreviewLists){
        this.chatPreviewLists = chatPreviewLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_chat_list, null));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ChatPreviewAdapter.ViewHolder holder, int position) {
        ChatPreview chatPreview = chatPreviewLists.get(position);
        // online
        if (chatPreview.isOnline())  holder.cardUserOnline.setVisibility(View.VISIBLE);
        else  holder.cardUserOnline.setVisibility(View.GONE);
        // avatar
        if (!chatPreview.getAvatar().isEmpty()) Picasso.get().load(chatPreview.getAvatar()).into(holder.ivUserChat);

        holder.tvNameUserChat.setText(chatPreview.getName());
        holder.tvLastMessage.setText(chatPreview.getLastMessage());
        holder.tvTimeChatPreview.setText(chatPreview.getTime());

        if (chatPreview.isSeen()){
            holder.tvNameUserChat.setTextColor(Color.parseColor("#A5A5A5"));
            holder.tvLastMessage.setTextColor(Color.parseColor("#A5A5A5"));
            holder.tvTimeChatPreview.setTextColor(Color.parseColor("#A5A5A5"));
        } else {
            holder.tvNameUserChat.setTextColor(Color.parseColor("#000000"));
            holder.tvLastMessage.setTextColor(Color.parseColor("#000000"));
            holder.tvTimeChatPreview.setTextColor(Color.parseColor("#000000"));
        }

        // chuyển qua activity chat giữa các users
        holder.relativeLayoutChatPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", chatPreview.getName());
                intent.putExtra("username", chatPreview.getUsername());
                intent.putExtra("avatar", chatPreview.getAvatar());
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
        RelativeLayout relativeLayoutChatPreview;
        ImageView ivUserChat;
        CircleImageView cardUserOnline;
        TextView tvNameUserChat, tvLastMessage, tvTimeChatPreview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayoutChatPreview = itemView.findViewById(R.id.relativeLayoutChatPreview);
            ivUserChat = itemView.findViewById(R.id.ivUserChat);
            cardUserOnline = itemView.findViewById(R.id.cardUserOnline);
            tvNameUserChat = itemView.findViewById(R.id.tvNameUserChat);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimeChatPreview = itemView.findViewById(R.id.tvTimeChatPreview);

        }
    }
}
