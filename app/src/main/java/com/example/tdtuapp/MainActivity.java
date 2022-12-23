package com.example.tdtuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tdtuapp.Home.ViewPagerAdapterBottomDirection;
import com.example.tdtuapp.LocalMemory.ConstantData;
import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.LoginOrSignUp.ViewPagerAdapterLoginSignUp;
import com.example.tdtuapp.firestore.firestoreAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ConstantData.realTimeDatabaseUrl);
    String localUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localUser = LocalMemory.loadLocalUser(this);
        // update token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    String token = task.getResult();
                    firestoreAPI.updateToken(localUser, token);
                } else {
                    Log.d("get token failed", "Fetching FCM registration token failed", task.getException());
                }
            }
        });

        mTabLayout = findViewById(R.id.tabLayoutBottomDirection);
        mViewPager = findViewById(R.id.viewPagerBottomDirection);

        ViewPagerAdapterBottomDirection viewPagerAdapterBottomDirection = new ViewPagerAdapterBottomDirection(
                getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapterBottomDirection);
        // load mặc định đến trang 2
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.icon_run);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_comment);
        mTabLayout.getTabAt(2).setIcon(R.drawable.user);
        mTabLayout.getTabAt(3).setIcon(R.drawable.icon_info);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
    }


    @Override
    protected void onResume() {
        super.onResume();
        localUser = LocalMemory.loadLocalUser(this);
//        databaseReference.child("USERS").child(localUser).child("isOnline").setValue(true);
        databaseReference.child("USERS_ONLINE").child(localUser).setValue(true);
        Log.d("life", "resume main");


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("life", "pause main");
    }

    @Override
    protected void onStop() {
//        databaseReference.child("USERS_ONLINE").child(localUser).setValue(false);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        databaseReference.child("USERS_ONLINE").child(localUser).setValue(false);
        super.onDestroy();
    }
}