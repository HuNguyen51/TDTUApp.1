package com.example.tdtuapp.Home;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tdtuapp.LocalMemory.ConstantData;
import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.LoginSignUpActivity;
import com.example.tdtuapp.R;
import com.example.tdtuapp.firestore.firestoreAPI;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileFragment extends Fragment {
    Button btnLogout;
    Context context;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ConstantData.realTimeDatabaseUrl);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String localUser = LocalMemory.loadLocalUser(context);
                // set token về rỗng
                firestoreAPI.updateToken(localUser, "");
                // xóa thông báo
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                // set thành offline
//                databaseReference.child("USERS").child(localUser).child("isOnline").setValue(false);
                // xóa người dùng khỏi local máy
                LocalMemory.saveLocalUser(context, "");
                // di chuyển đến màn hình đăng nhập
                context.startActivity(new Intent(context, LoginSignUpActivity.class));
                getActivity().finishAffinity();
            }
        });
        return view;
    }
}