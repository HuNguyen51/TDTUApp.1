package com.example.tdtuapp.LoginOrSignUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tdtuapp.LocalMemory.ConstantData;
import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.LoginSignUpActivity;
import com.example.tdtuapp.MainActivity;
import com.example.tdtuapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {
    EditText etEmailLogin, etPasswordLogin;
    Button btLogin;
    Context context;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ConstantData.realTimeDatabaseUrl);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }
    private void showToast(String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        etEmailLogin = view.findViewById(R.id.etEmailLogin);
        etPasswordLogin = view.findViewById(R.id.etPasswordLogin);
        btLogin = (Button) view.findViewById(R.id.btLogin);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etEmailLogin.getText().toString();
                String password = etPasswordLogin.getText().toString();

                databaseReference.child("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // validate
                        if (username.isEmpty()) showToast("Vui lòng nhập tài khoản");
                        else if (!snapshot.hasChild(username)) showToast("Tên đăng nhập không tồn tại");
                        else if (password.isEmpty()) showToast("Vui lòng nhập mật khẩu");
                        else if (password.length() < 8) showToast("Mật khẩu quá ngắn");
                        else {
                            String actualPass = snapshot.child(username).child("password").getValue(String.class);
                            if (!actualPass.equals(password)) showToast("Sai mật khẩu");
                            else {
                                // TODO
                                if (!snapshot.child(username).child("token").getValue(String.class).isEmpty()){ //đăng nhập ở thiết bị khác
                                    showToast("Tài khoản đang được đăng nhập ở thiết bị khác");
                                } else {
                                    LocalMemory.saveLocalUser(context, username);
                                    context.startActivity(new Intent(context, MainActivity.class));
                                    getActivity().finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return view;
    }
}