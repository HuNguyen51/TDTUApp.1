package com.example.tdtuapp.LoginOrSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.tdtuapp.LoginOrSignUp.LoginFragment;
import com.example.tdtuapp.LoginOrSignUp.SignUpFragment;

public class ViewPagerAdapterLoginSignUp extends FragmentStatePagerAdapter {
    public ViewPagerAdapterLoginSignUp(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new LoginFragment();
            case 1:
                return new SignUpFragment();
            default:
                return new LoginFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Đăng nhập";
                break;
            case 1:
                title = "Đăng ký";
                break;
        }
        return title;
    }
}
