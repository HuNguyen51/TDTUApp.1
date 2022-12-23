package com.example.tdtuapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.LoginOrSignUp.ViewPagerAdapterLoginSignUp;
import com.google.android.material.tabs.TabLayout;

public class LoginSignUpActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        mTabLayout = findViewById(R.id.tabLayoutLoginSignUp);
        mViewPager = findViewById(R.id.viewPagerLoginSignUp);

        ViewPagerAdapterLoginSignUp viewPagerAdapterLoginSignUp = new ViewPagerAdapterLoginSignUp(
                getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapterLoginSignUp);

        mTabLayout.setupWithViewPager(mViewPager);

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
        // check nếu đã đăng nhập
        if (!LocalMemory.loadLocalUser(this).isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}