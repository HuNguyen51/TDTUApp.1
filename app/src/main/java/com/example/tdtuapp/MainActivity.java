package com.example.tdtuapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.tdtuapp.Home.ViewPagerAdapterBottomDirection;
import com.example.tdtuapp.LocalMemory.ConstantData;
import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.LoginOrSignUp.ViewPagerAdapterLoginSignUp;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ConstantData.realTimeDatabaseUrl);
    String localUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabLayout = findViewById(R.id.tabLayoutBottomDirection);
        mViewPager = findViewById(R.id.viewPagerBottomDirection);

        ViewPagerAdapterBottomDirection viewPagerAdapterBottomDirection = new ViewPagerAdapterBottomDirection(
                getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapterBottomDirection);
        // load mặc định đến trang 2
        mViewPager.setCurrentItem(2);

        mTabLayout.setupWithViewPager(mViewPager);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
    }


    @Override
    protected void onResume() {
        super.onResume();
        localUser = LocalMemory.loadLocalUser(this);
        databaseReference.child("USERS").child(localUser).child("isOnline").setValue(true);
        Log.d("life", "resume main");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("life", "pause main");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("life", "stop main");
        // databaseReference.child("USERS").child(localUser).child("isOnline").setValue(false);
    }

    @Override
    protected void onDestroy() {
        databaseReference.child("USERS").child(localUser).child("isOnline").setValue(false);
        Log.d("life", "destroy main");
        super.onDestroy();
    }
}