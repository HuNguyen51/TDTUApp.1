package com.example.tdtuapp.Home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tdtuapp.Home.ChatPreview.ChatPreview;
import com.example.tdtuapp.Home.ChatPreview.ChatPreviewAdapter;
import com.example.tdtuapp.Home.ChatPreview.OnlineUserAdapter;
import com.example.tdtuapp.LocalMemory.ConstantData;
import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatListFragment extends Fragment {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ConstantData.realTimeDatabaseUrl);
    RecyclerView rvChatList, rvUserOnline;
    ChatPreviewAdapter adapter ;
    OnlineUserAdapter adapterUserOnline;
    List<ChatPreview> chatPreviewList, userOnlineList;
    Context context;
    String localUser = "";
    String chatKey = "";
    String lastMessage = "", time = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("life", "create");
        context = getContext();

        adapter = new ChatPreviewAdapter(context);
        adapterUserOnline = new OnlineUserAdapter(context);

        chatPreviewList = new ArrayList<>();
        userOnlineList = new ArrayList<>();

        // lấy token (mỗi máy có 1 token riêng)
        localUser = LocalMemory.loadLocalUser(context);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    String token = task.getResult();
                    databaseReference.child("USERS").child(localUser).child("token").setValue(token);
                } else {
                    Log.d("get token failed", "Fetching FCM registration token failed", task.getException());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("life", "create view");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat_list, container, false);
        rvChatList = view.findViewById(R.id.rvChatList);
        rvUserOnline = view.findViewById(R.id.rvUserOnline);

        rvChatList.setLayoutManager(new LinearLayoutManager(context));
        rvUserOnline.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

        rvChatList.setAdapter(adapter);
        rvUserOnline.setAdapter(adapterUserOnline);

        loadUserOnline();
        return view;
    }
    private void loadUserOnline(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userOnlineList.clear();
                adapterUserOnline.updateData(userOnlineList);
                chatKey = "";
                for (DataSnapshot userSnapshot : snapshot.child("USERS").getChildren()){
                    String otherUser = userSnapshot.getKey();
                    if (otherUser.equals(localUser)) continue; // bỏ phần phía sau
                    // -----------------------------------------------------------
                    if (!userSnapshot.hasChild("avatar") || !userSnapshot.hasChild("isOnline")) continue;
                    String avatar = userSnapshot.child("avatar").getValue(String.class);
                    String name = otherUser; //userSnapshot.child("name").getValue(String.class);
                    Boolean isOnline = userSnapshot.child("isOnline").getValue(Boolean.class);
                    if (!isOnline) continue;
                    // -----------------------------------------------------------
                    int chatChildrenCount = (int) snapshot.child("CHAT").getChildrenCount();
                    if (chatChildrenCount == 0) continue; // bỏ phần phía sau
                    // -----------------------------------------------------------
                    for (DataSnapshot chatSnapshot : snapshot.child("CHAT").getChildren()){
                        chatKey = chatSnapshot.getKey(); // id của đoạn chat
                        // check members
                        if (!chatSnapshot.hasChild("members") || !chatSnapshot.hasChild("messages")) continue;
                        // -----------------------------------------------------------
                        // kiểm tra thành viên trong đoạn chat của chatKey
                        List<String> usersInChat = new ArrayList<>();
                        for (DataSnapshot memberSnapshot : chatSnapshot.child("members").getChildren()){
                            usersInChat.add(memberSnapshot.getKey());
                        }
                        // check message
                        // nếu đoạn chat bao gồm các user này thì đã xác định đúng đoạn chat cần tìm
                        if (usersInChat.contains(otherUser) && usersInChat.contains(localUser)){
                            ChatPreview chatPreview = new ChatPreview(avatar, name, "", "", chatKey, false, isOnline);
                            userOnlineList.add(chatPreview);
                            adapterUserOnline.updateData(userOnlineList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("life", "resume");
        loadUser();
    }

    private void loadUser() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatPreviewList.clear();
                chatKey = "";
                for (DataSnapshot userSnapshot : snapshot.child("USERS").getChildren()){
                    String otherUser = userSnapshot.getKey();
                    if (otherUser.equals(localUser)) continue; // bỏ phần phía sau
                    // -----------------------------------------------------------
                    if (!userSnapshot.hasChild("avatar") || !userSnapshot.hasChild("isOnline")) continue;
                    String avatar = userSnapshot.child("avatar").getValue(String.class);
                    String name = otherUser; //userSnapshot.child("name").getValue(String.class);
                    Boolean isOnline = userSnapshot.child("isOnline").getValue(Boolean.class);
                    Boolean isSeen = true;

                    int chatChildrenCount = (int) snapshot.child("CHAT").getChildrenCount();
                    if (chatChildrenCount == 0) continue; // bỏ phần phía sau
                    // -----------------------------------------------------------
                    for (DataSnapshot chatSnapshot : snapshot.child("CHAT").getChildren()){
                        chatKey = chatSnapshot.getKey(); // id của đoạn chat
                        // check members
                        if (!chatSnapshot.hasChild("members") || !chatSnapshot.hasChild("messages")) continue;
                        // -----------------------------------------------------------
                        // kiểm tra thành viên trong đoạn chat của chatKey
                        List<String> usersInChat = new ArrayList<>();
                        for (DataSnapshot memberSnapshot : chatSnapshot.child("members").getChildren()){
                            usersInChat.add(memberSnapshot.getKey());
                        }
                        // check message
                        // nếu đoạn chat bao gồm các user này thì đã xác định đúng đoạn chat cần tìm
                        if (usersInChat.contains(otherUser) && usersInChat.contains(localUser)){
                            long timeReadLastest = Long.parseLong(LocalMemory.loadLastMessageTime(context, chatKey));
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                            for (DataSnapshot messageSnapshot : chatSnapshot.child("messages").getChildren()){
                                lastMessage = messageSnapshot.child("msg").getValue(String.class);
                                long timeSentenceChat = Long.parseLong(messageSnapshot.getKey());
                                if (timeReadLastest < timeSentenceChat) isSeen = false;

                                time = timeFormat.format(timeSentenceChat);
                            }
                            ChatPreview chatPreview = new ChatPreview(avatar, name, lastMessage, time, chatKey, isSeen, isOnline);
                            chatPreviewList.add(chatPreview);
                            adapter.updateData(chatPreviewList);
                        }
                    }

                }
                loadRemainUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadRemainUser() {
        databaseReference.child("USERS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    String otherUser = userSnapshot.getKey();
                    if (otherUser.equals(localUser)) continue; // bỏ phần phía sau
                    boolean isExists = false;
                    // nếu user đã tồn tại
                    for (ChatPreview cp : chatPreviewList){
                        Log.d("not exists user", cp.getName() + " " + otherUser);
                        if (otherUser.equals(cp.getName())){
                            isExists = true;
                            break;
                        }
                    }
                    Log.d("not exists user", String.valueOf(isExists));
                    if (isExists == false){
                        Log.d("not exists user", "Thỏa điều kiện user chưa tồn tại trong danh sách bạn bè");
                        String avatar = "";
                        if (userSnapshot.hasChild("avatar"))
                                userSnapshot.child("avatar").getValue(String.class);
                        String name = otherUser; //userSnapshot.child("name").getValue(String.class);
                        Boolean isOnline = true;
                        if (userSnapshot.hasChild("isOnline"))
                            isOnline = userSnapshot.child("isOnline").getValue(Boolean.class);

                        ChatPreview chatPreview = new ChatPreview(avatar, name, "Người lạ", "", "", false, isOnline);
                        chatPreviewList.add(chatPreview);
                        adapter.updateData(chatPreviewList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}