package com.example.tdtuapp.Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.tdtuapp.LoginOrSignUp.LoginFragment;
import com.example.tdtuapp.LoginOrSignUp.SignUpFragment;

public class ViewPagerAdapterBottomDirection extends FragmentStatePagerAdapter {
    public ViewPagerAdapterBottomDirection(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TrainingEvaluationFragment();
            case 1:
                return new ChatListFragment();
            case 2:
                return new ProfileFragment();
            case 3:
                return new NotificationFragment();
            default:
                return new TrainingEvaluationFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        String title = "";
//        switch (position){
//            case 0:
//                title = "Điểm rèn luyện";
//                break;
//            case 1:
//                title = "Nhắn tin";
//                break;
//            case 2:
//                title = "Cá nhân";
//                break;
//            case 3:
//                title = "Thông tin";
//                break;
//        }
//        return title;
//    }
}
