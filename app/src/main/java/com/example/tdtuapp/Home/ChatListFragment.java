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
import com.example.tdtuapp.firestore.firestoreAPI;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatListFragment extends Fragment {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ConstantData.realTimeDatabaseUrl);
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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

        localUser = LocalMemory.loadLocalUser(context);
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

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("life", "resume chat");
        databaseReference.child("USERS_ONLINE").child(localUser).setValue(true);

        loadUser();
        loadUserOnline();
    }
    @Override
    public void onDestroy() {
        databaseReference.child("USERS_ONLINE").child(localUser).setValue(false);
        Log.d("life", "stop chat");
        super.onDestroy();
    }

    private void loadUserOnline(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                db.collection("Users")
                    .whereNotEqualTo("username", localUser)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                userOnlineList.clear();
                                adapterUserOnline.updateData(userOnlineList);
                                chatKey = "";
                                for (QueryDocumentSnapshot document : task.getResult()) { // c??c user
//                                    Log.d("load online user", document.getId() + " => " + document.getData());
                                    String avatar = document.get("avatar", String.class);
                                    String otherUser = document.get("username", String.class); // t??n ????ng nh???p
                                    String otherUserName = document.get("name", String.class); // t??n ng?????i d??ng
                                    if (otherUser.equals("admin")) continue;

                                    if (!snapshot.child("USERS_ONLINE").hasChild(otherUser)) continue;
                                    boolean isOnline = snapshot.child("USERS_ONLINE").child(otherUser).getValue(Boolean.class);
                                    if (isOnline == false) continue;
                                    // -----------------------------

                                    int chatChildrenCount = (int) snapshot.child("CHAT").getChildrenCount();
                                    if (chatChildrenCount == 0) continue; // b??? ph???n ph??a sau
                                    // ---------------------------------------------------------
                                    for (DataSnapshot chatSnapshot : snapshot.child("CHAT").getChildren()){
                                        chatKey = chatSnapshot.getKey(); // id c???a ??o???n chat
                                        // check members
                                        if (!chatSnapshot.hasChild("members") || !chatSnapshot.hasChild("messages")) continue;
                                        // -----------------------------------------------------------
                                        // ki???m tra th??nh vi??n trong ??o???n chat c???a chatKey

                                        List<String> usersInChat = new ArrayList<>();
                                        for (DataSnapshot memberSnapshot : chatSnapshot.child("members").getChildren()){
                                            usersInChat.add(memberSnapshot.getKey());
                                        }
                                        // check message
                                        // n???u ??o???n chat bao g???m c??c user n??y th?? ???? x??c ?????nh ????ng ??o???n chat c???n t??m
                                        if (usersInChat.contains(otherUser) && usersInChat.contains(localUser)){
                                            ChatPreview chatPreview = new ChatPreview(avatar, otherUserName, "", "", chatKey, false, true);
                                            chatPreview.setUsername(otherUser);
                                            userOnlineList.add(chatPreview);
                                            adapterUserOnline.updateData(userOnlineList);
                                        }
                                    }
                                }
                            }

                        }

                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadUser() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                db.collection("Users")
                        .whereNotEqualTo("username", localUser)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    chatPreviewList.clear();
                                    adapter.updateData(chatPreviewList);
                                    chatKey = "";
                                    for (QueryDocumentSnapshot document : task.getResult()) { // c??c user
                                        Log.d("load read chat", document.getId() + " => " + document.getData());
                                        String avatar = document.get("avatar", String.class);
                                        String otherUser = document.get("username", String.class); // t??n ????ng nh???p
                                        String otherUserName = document.get("name", String.class); // t??n ng?????i d??ng
                                        Log.d("admin check", otherUser);
                                        if (otherUser.equals("admin")) continue;
//                                        Boolean isOnline = userSnapshot.child("isOnline").getValue(Boolean.class);
                                        Boolean isSeen = true;
                                        Boolean isOnline = false;
                                        if (snapshot.child("USERS_ONLINE").hasChild(otherUser)){
                                            isOnline = snapshot.child("USERS_ONLINE").child(otherUser).getValue(Boolean.class);
                                        }

                                        int chatChildrenCount = (int) snapshot.child("CHAT").getChildrenCount();
                                        if (chatChildrenCount == 0) continue; // b??? ph???n ph??a sau

                                        // add user ch??a xem
                                        for (DataSnapshot chatSnapshot : snapshot.child("CHAT").getChildren()){
                                            chatKey = chatSnapshot.getKey(); // id c???a ??o???n chat
                                            // check members
                                            if (!chatSnapshot.hasChild("members") || !chatSnapshot.hasChild("messages")) continue;
                                            // -----------------------------------------------------------
                                            // ki???m tra th??nh vi??n trong ??o???n chat c???a chatKey
                                            List<String> usersInChat = new ArrayList<>();
                                            for (DataSnapshot memberSnapshot : chatSnapshot.child("members").getChildren()){
                                                usersInChat.add(memberSnapshot.getKey());
                                            }
                                            // check message
                                            // n???u ??o???n chat bao g???m c??c user n??y th?? ???? x??c ?????nh ????ng ??o???n chat c???n t??m
                                            if (usersInChat.contains(otherUser) && usersInChat.contains(localUser)){

                                                long timeReadLastest = Long.parseLong(LocalMemory.loadLastMessageTime(context, chatKey));
                                                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                                                for (DataSnapshot messageSnapshot : chatSnapshot.child("messages").getChildren()){
                                                    lastMessage = messageSnapshot.child("msg").getValue(String.class);
                                                    long timeSentenceChat = Long.parseLong(messageSnapshot.getKey());
                                                    if (timeReadLastest < timeSentenceChat) isSeen = false;

                                                    time = timeFormat.format(timeSentenceChat);
                                                }

                                                if (isSeen == false){
                                                    ChatPreview chatPreview = new ChatPreview(avatar, otherUserName, lastMessage, time, chatKey, isSeen, isOnline);
                                                    chatPreview.setUsername(otherUser);
                                                    chatPreviewList.add(chatPreview);
                                                    adapter.updateData(chatPreviewList);
                                                }
                                            }
                                        }
                                    }
                                    for (QueryDocumentSnapshot document : task.getResult()) { // c??c user
                                        Log.d("load read chat", document.getId() + " => " + document.getData());
                                        String avatar = document.get("avatar", String.class); // h??nh ?????i di???n
                                        String otherUser = document.get("username", String.class); // t??n ????ng nh???p
                                        String otherUserName = document.get("name", String.class); // t??n ng?????i d??ng
                                        if (otherUser.equals("admin")) continue;
//                                        Boolean isOnline = userSnapshot.child("isOnline").getValue(Boolean.class);
                                        Boolean isSeen = true;

                                        Boolean isOnline = false;
                                        if (snapshot.child("USERS_ONLINE").hasChild(otherUser)){
                                            isOnline = snapshot.child("USERS_ONLINE").child(otherUser).getValue(Boolean.class);
                                        }
                                        ;
                                        int chatChildrenCount = (int) snapshot.child("CHAT").getChildrenCount();
                                        if (chatChildrenCount == 0) continue; // b??? ph???n ph??a sau
                                        for (DataSnapshot chatSnapshot : snapshot.child("CHAT").getChildren()){
                                            chatKey = chatSnapshot.getKey(); // id c???a ??o???n chat
                                            // check members
                                            if (!chatSnapshot.hasChild("members") || !chatSnapshot.hasChild("messages")) continue;
                                            // -----------------------------------------------------------
                                            // ki???m tra th??nh vi??n trong ??o???n chat c???a chatKey
                                            List<String> usersInChat = new ArrayList<>();
                                            for (DataSnapshot memberSnapshot : chatSnapshot.child("members").getChildren()){
                                                usersInChat.add(memberSnapshot.getKey());
                                            }
                                            // check message
                                            // n???u ??o???n chat bao g???m c??c user n??y th?? ???? x??c ?????nh ????ng ??o???n chat c???n t??m
                                            if (usersInChat.contains(otherUser) && usersInChat.contains(localUser)){
                                                long timeReadLastest = Long.parseLong(LocalMemory.loadLastMessageTime(context, chatKey));
                                                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                                                for (DataSnapshot messageSnapshot : chatSnapshot.child("messages").getChildren()){
                                                    lastMessage = messageSnapshot.child("msg").getValue(String.class);
                                                    long timeSentenceChat = Long.parseLong(messageSnapshot.getKey());
                                                    if (timeReadLastest < timeSentenceChat) isSeen = false;

                                                    time = timeFormat.format(timeSentenceChat);
                                                }
                                                if (isSeen == true){
                                                    ChatPreview chatPreview = new ChatPreview(avatar, otherUserName, lastMessage, time, chatKey, isSeen, isOnline);
                                                    chatPreview.setUsername(otherUser);
                                                    chatPreviewList.add(chatPreview);
                                                    adapter.updateData(chatPreviewList);
                                                }
                                            }
                                        }
                                    }
                                    for (QueryDocumentSnapshot document : task.getResult()) { // c??c user
                                        Log.d("load read chat", document.getId() + " => " + document.getData());
                                        String avatar = document.get("avatar", String.class); // h??nh ?????i di???n
                                        String otherUser = document.get("username", String.class); // t??n ????ng nh???p
                                        String otherUserName = document.get("name", String.class); // t??n ng?????i d??ng
                                        if (otherUser.equals("admin")) continue;

                                        boolean isExists = false;
                                        // n???u user ???? t???n t???i
                                        for (ChatPreview cp : chatPreviewList){
                                            if (otherUser.equals(cp.getUsername())){
                                                isExists = true;
                                                break;
                                            }
                                        }

                                        if (isExists == false){
                                            Log.d("not exists user", "Th???a ??i???u ki???n user ch??a t???n t???i trong danh s??ch b???n b??");
                                            ChatPreview chatPreview = new ChatPreview(avatar, otherUserName, "Ng?????i l???", "", "", true, false);
                                            chatPreview.setUsername(otherUser);
                                            chatPreviewList.add(chatPreview);
                                            adapter.updateData(chatPreviewList);
                                        }
                                    }
                                } else {
                                    Log.d("load read chat", "Error getting documents: ", task.getException());
                                }
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//    private void loadreadUser() {
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                db.collection("Users")
//                        .whereNotEqualTo("username", localUser)
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    chatKey = "";
//                                    for (QueryDocumentSnapshot document : task.getResult()) { // c??c user
//                                        Log.d("load read chat", document.getId() + " => " + document.getData());
//                                        String avatar = document.get("avatar", String.class); // h??nh ?????i di???n
//                                        String otherUser = document.get("username", String.class); // t??n ????ng nh???p
//                                        String otherUserName = document.get("name", String.class); // t??n ng?????i d??ng
////                                        Boolean isOnline = userSnapshot.child("isOnline").getValue(Boolean.class);
//                                        Boolean isSeen = true;
//
//                                        Boolean isOnline = false;
//                                        if (snapshot.child("USERS_ONLINE").hasChild(otherUser)){
//                                            isOnline = snapshot.child("USERS_ONLINE").child(otherUser).getValue(Boolean.class);
//                                        }
//;
//                                        int chatChildrenCount = (int) snapshot.child("CHAT").getChildrenCount();
//                                        if (chatChildrenCount == 0) continue; // b??? ph???n ph??a sau
//                                        for (DataSnapshot chatSnapshot : snapshot.child("CHAT").getChildren()){
//                                            chatKey = chatSnapshot.getKey(); // id c???a ??o???n chat
//                                            // check members
//                                            if (!chatSnapshot.hasChild("members") || !chatSnapshot.hasChild("messages")) continue;
//                                            // -----------------------------------------------------------
//                                            // ki???m tra th??nh vi??n trong ??o???n chat c???a chatKey
//                                            List<String> usersInChat = new ArrayList<>();
//                                            for (DataSnapshot memberSnapshot : chatSnapshot.child("members").getChildren()){
//                                                usersInChat.add(memberSnapshot.getKey());
//                                            }
//                                            // check message
//                                            // n???u ??o???n chat bao g???m c??c user n??y th?? ???? x??c ?????nh ????ng ??o???n chat c???n t??m
//                                            if (usersInChat.contains(otherUser) && usersInChat.contains(localUser)){
//                                                long timeReadLastest = Long.parseLong(LocalMemory.loadLastMessageTime(context, chatKey));
//                                                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
//                                                for (DataSnapshot messageSnapshot : chatSnapshot.child("messages").getChildren()){
//                                                    lastMessage = messageSnapshot.child("msg").getValue(String.class);
//                                                    long timeSentenceChat = Long.parseLong(messageSnapshot.getKey());
//                                                    if (timeReadLastest < timeSentenceChat) isSeen = false;
//
//                                                    time = timeFormat.format(timeSentenceChat);
//                                                }
//                                                if (isSeen == true){
//                                                    ChatPreview chatPreview = new ChatPreview(avatar, otherUserName, lastMessage, time, chatKey, isSeen, isOnline);
//                                                    chatPreview.setUsername(otherUser);
//                                                    chatPreviewList.add(chatPreview);
//                                                    adapter.updateData(chatPreviewList);
//                                                }
//                                            }
//                                        }
//                                    }
//                                    loadRemainerUser();
//                                } else {
//                                    Log.d("load read chat", "Error getting documents: ", task.getException());
//                                }
//                            }
//                        });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//    private void loadRemainerUser() {
//        databaseReference.child("CHAT").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                db.collection("Users")
//                        .whereNotEqualTo("username", localUser)
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot document : task.getResult()) { // c??c user
//                                        Log.d("load read chat", document.getId() + " => " + document.getData());
//                                        String avatar = document.get("avatar", String.class); // h??nh ?????i di???n
//                                        String otherUser = document.get("username", String.class); // t??n ????ng nh???p
//                                        String otherUserName = document.get("name", String.class); // t??n ng?????i d??ng
//
//                                        boolean isExists = false;
//                                        // n???u user ???? t???n t???i
//                                        for (ChatPreview cp : chatPreviewList){
//                                            if (otherUser.equals(cp.getUsername())){
//                                                isExists = true;
//                                                break;
//                                            }
//                                        }
//
//                                        if (isExists == false){
//                                            Log.d("not exists user", "Th???a ??i???u ki???n user ch??a t???n t???i trong danh s??ch b???n b??");
//                                            ChatPreview chatPreview = new ChatPreview(avatar, otherUserName, "Ng?????i l???", "", "", true, false);
//                                            chatPreview.setUsername(otherUser);
//                                            chatPreviewList.add(chatPreview);
//                                            adapter.updateData(chatPreviewList);
//                                        }
//                                    }
//                                } else {
//                                    Log.d("load read chat", "Error getting documents: ", task.getException());
//                                }
//                            }
//                        });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}