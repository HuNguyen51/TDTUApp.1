package com.example.tdtuapp.Home.Chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    Context context;
    List<Chat> chatList = new ArrayList<>();
    String localUser;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
        localUser = LocalMemory.loadLocalUser(context);
    }
    public void updateData(List<Chat> chatList){
        this.chatList = chatList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Chat item = chatList.get(position);

        if (item.getSender().equals(localUser)){
            holder.myChatLayout.setVisibility(View.VISIBLE);
            holder.oppoChatLayout.setVisibility(View.GONE);

            holder.tvMyMessage.setText(item.getMsg());
            holder.tvMyTime.setText(item.getTime() );
        } else {
            holder.myChatLayout.setVisibility(View.GONE);
            holder.oppoChatLayout.setVisibility(View.VISIBLE);

            holder.tvOppoMessage.setText(item.getMsg());
            holder.tvOppoTime.setText(item.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout myChatLayout, oppoChatLayout;
        TextView tvOppoMessage, tvOppoTime, tvMyMessage, tvMyTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myChatLayout = itemView.findViewById(R.id.myChatLayout);
            oppoChatLayout = itemView.findViewById(R.id.oppoChatLayout);

            tvOppoMessage = itemView.findViewById(R.id.tvOppoMessage);
            tvOppoTime = itemView.findViewById(R.id.tvOppoTime);

            tvMyMessage = itemView.findViewById(R.id.tvMyMessage);
            tvMyTime = itemView.findViewById(R.id.tvMyTime);


        }
    }
}
