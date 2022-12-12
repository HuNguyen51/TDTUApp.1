package com.example.tdtuapp.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.tdtuapp.Home.Chat.ChatActivity;
import com.example.tdtuapp.LocalMemory.ConstantData;
import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.MyApp.MyApplication;
import com.example.tdtuapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ConstantData.realTimeDatabaseUrl);

    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Log.d("notification", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> map = remoteMessage.getData();
            String sender = map.get("sender"); // username của local user
            String message = map.get("message");
            //String receiver = map.get("receiver"); // tên của user khác
            String avatar = map.get("avatar");
            String chatKey = map.get("chatKey");

            createNotification(sender, message, avatar, chatKey);
        }
    }
    private void createNotification(String sender, String message, String avatar, String chatKey){
        // tạo id cho chat
        int notifyID = LocalMemory.getNotifyID(this);
        Log.d("notify id", String.valueOf(notifyID));
        //
        Intent intent = new Intent( this , ChatActivity.class );
        intent.putExtra("name", sender);
        intent.putExtra("avatar", avatar);
        intent.putExtra("chatKey", chatKey);
        intent.putExtra("from", "notification");
        PendingIntent pendingIntent = PendingIntent.getActivity( this , 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( this, MyApplication.CHANNEL_ID)
                .setContentTitle(sender)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setSound(notificationSoundURI)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = notificationBuilder.build();
        //notification.flags = Notification.FLAG_AUTO_CANCEL;

        if (notificationManager != null){
            notificationManager.notify(new Random().nextInt(101), notification);
        }
    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onSendError(@NonNull String msgId, @NonNull Exception exception) {
        super.onSendError(msgId, exception);
    }

    @Override
    protected Intent getStartCommandIntent(Intent originalIntent) {
        return super.getStartCommandIntent(originalIntent);
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }

//    @Override
//    public void onNewToken(@NonNull String token) {
//        super.onNewToken(token);
//        updateToken(token);
//    }
//    private void updateToken(String token){
//        String localUser = LocalMemory.loadLocalUser(getApplicationContext());
//        Map<String, Object> map = new HashMap<>();
//        map.put("token", token);
//        databaseReference.child("USERS").child(localUser).updateChildren(map);
//    }
}
