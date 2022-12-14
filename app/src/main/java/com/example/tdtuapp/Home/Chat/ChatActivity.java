package com.example.tdtuapp.Home.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tdtuapp.LocalMemory.ConstantData;
import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.MainActivity;
import com.example.tdtuapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tvUsernameInChat, tvStatus;
    ImageView btnBack, imProfileInChat, btnSend;
    EditText edtTypeText;

    RecyclerView rvChatContent;

    String localUser;
    String chatKey;
    Boolean isOnline;
    String otherUser;
    String avatar;
    String localName;
    String otherUserName;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ConstantData.realTimeDatabaseUrl);
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Chat> chatList = new ArrayList<>();
    ChatAdapter chatAdapter;

    boolean loadingFirstTime = true;
    Boolean isBackClicked = false;
    String from = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
        // kh???i t???o c??c th??nh ph???n trong xml
        isBackClicked = false;
        init();
        // local user
        localUser = LocalMemory.loadLocalUser(this);
        localName = LocalMemory.loadLocalName(this);
        // l???y d??? li???u sau khi nh???p v??o cu???c tr?? chuy???n t??? ChatPreview
        otherUser = getIntent().getStringExtra("username");
        otherUserName = getIntent().getStringExtra("name");
        avatar = getIntent().getStringExtra("avatar");
        chatKey = getIntent().getStringExtra("chatKey");
        // n???u ch??a c?? cu???c tr?? chuy???n th?? t???o key cho cu???c tr?? chuy???n ????
        if (chatKey.isEmpty()) {
            chatKey = databaseReference.child("CHAT").push().getKey();
        }
        Log.d("chat key", chatKey);

        // ki???m tra online
        databaseReference.child("USERS_ONLINE").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(otherUser)){
                    Boolean isOnline = snapshot.child(otherUser).getValue(Boolean.class);
                    if (isOnline){
                        tvStatus.setText("online");
                        tvStatus.setTextColor(Color.parseColor("#FF03DAC5"));
                    } else {
                        tvStatus.setText("offline");
                        tvStatus.setTextColor(Color.parseColor("#cccccc"));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // L??u th???i gian khi b???m v??o cu???c tr?? chuy???n (t???c l?? th???i gian khi xem tin nh???n)

        LocalMemory.saveLastMessageTime(this, chatKey);

        // khi m??? app t????ng ???ng v???i vi???c m??? xem th??ng tin trong app
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (getIntent().hasExtra("from")){
            from = getIntent().getStringExtra("from");
        }

        // ?????t t??n user kh??c
        tvUsernameInChat.setText(otherUserName);
        // load ???nh c???a user kh??c
        if (!avatar.isEmpty()){
            Picasso.get().load(avatar).into(imProfileInChat);
        }
        // kh???i t???o giao di???n n???i dung chat
        rvChatContent.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        chatAdapter = new ChatAdapter(this, chatList);
        rvChatContent.setAdapter(chatAdapter);
        // x??? l?? ????? d??? li???u v??o view
        databaseReference.child("CHAT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(chatKey).hasChild("messages")){
                    chatList.clear();
                    // v??ng l???p duy???t qua c??c tin nh???n trong ??o???n chat
                    for (DataSnapshot messagesSnapshot : snapshot.child(chatKey).child("messages").getChildren()){
                        String sendTime = messagesSnapshot.getKey();
                        if (!messagesSnapshot.hasChild("sender") || !messagesSnapshot.hasChild("msg")) continue;

                        String sender = messagesSnapshot.child("sender").getValue(String.class);
                        String msg = messagesSnapshot.child("msg").getValue(String.class);

                        Timestamp timestamp =new Timestamp(Long.parseLong(sendTime));
                        Date date = new Date(timestamp.getTime());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

                        Chat chat = new Chat(sender, msg, dateFormat.format(date) +" "+ timeFormat.format(date));
                        chatList.add(chat);

                        chatAdapter.updateData(chatList);
                        rvChatContent.scrollToPosition(chatList.size()-1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edtTypeText.getText().toString();
                if (!text.equals("")){
                    // l???y th???i gian hi???n t???i
                    String currentTimeStamps = String.valueOf(System.currentTimeMillis());
                    // l??u th???i gian tin nh???n cu???i c??ng l???i
                    LocalMemory.saveLastMessageTime(ChatActivity.this, chatKey);

                    databaseReference.child("CHAT").child(chatKey).child("members").child(localUser).setValue("");
                    databaseReference.child("CHAT").child(chatKey).child("members").child(otherUser).setValue("");

                    databaseReference.child("CHAT").child(chatKey).child("messages").child(currentTimeStamps).child("msg").setValue(text);
                    databaseReference.child("CHAT").child(chatKey).child("messages").child(currentTimeStamps).child("sender").setValue(localUser);

                    edtTypeText.setText("");
                    // g???i th??ng b??o ?????n user kh??c
                    getToken(text);
                }
            }
        });

        btnBack.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        localUser = LocalMemory.loadLocalUser(this);
//        databaseReference.child("USERS").child(localUser).child("isOnline").setValue(true);
        Log.d("life", "resume chat");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalMemory.saveLastMessageTime(ChatActivity.this, chatKey);
        Log.d("life", "pause chat");
    }

    @Override
    protected void onDestroy() {
//        if (!isBackClicked){
//            databaseReference.child("USERS").child(localUser).child("isOnline").setValue(false);
//        }
        Log.d("life", "destroy chat");
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("life", "stop chat");
    }
    public void getToken(String message){
        db.collection("Users")
                .whereEqualTo("username", otherUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("load token", document.getId() + " => " + document.getData());
                            // g???i th??ng b??o
                            String token = document.get("token", String.class);

                            JSONObject to = new JSONObject();
                            JSONObject data = new JSONObject();
                            try {
                                data.put("sender", localName); // sender trong th??ng b??o
                                data.put("message", message);
                                //data.put("receiver", otherUser);
                                data.put("sender_username", localUser); // !!
                                data.put("avatar", avatar);
                                data.put("chatKey", chatKey);

                                to.put("to", token);
                                to.put("data", data);

                                sendNotificatiton(to);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        }

                    }
                });

    }

    private void sendNotificatiton(JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ConstantData.NOTIFICATION_URL, to,
                response -> {
                    Log.d("notification", "send notification " + response);
                }, error -> {
            Log.d("notification", "send notification " + error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key="+ConstantData.SERVER_KEY);
                map.put("Content-type", "application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void init(){
        tvUsernameInChat = findViewById(R.id.tvUsernameInChat);
        tvStatus = findViewById(R.id.tvStatus);
        btnBack = findViewById(R.id.btnBack);
        imProfileInChat = findViewById(R.id.imProfileInChat);
        btnSend = findViewById(R.id.btnSend);
        edtTypeText = findViewById(R.id.edtTypeText);

        rvChatContent = findViewById(R.id.rvChatContent);
    }

    @Override
    public void onBackPressed() {
        Log.d("undo", "clicked");
        isBackClicked = true;
        if (from.equals("notification")){
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
        }
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                Log.d("btnback", "clicked");
                isBackClicked = true;
                if (from.equals("notification")){
                    startActivity(new Intent(ChatActivity.this, MainActivity.class));
                }
                finish();
                break;
        }
    }
}