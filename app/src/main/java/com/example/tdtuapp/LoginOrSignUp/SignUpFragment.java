package com.example.tdtuapp.LoginOrSignUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
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
import com.example.tdtuapp.firestore.firestoreAPI;
import com.example.tdtuapp.R;
import com.example.tdtuapp.hash.ArgonHash;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {
    EditText etUsernameSignUp, etEmailSignUp, etPasswordSignUp, etConfirmPasswordSignUp;
    Button btRegister;
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
        View view =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        etUsernameSignUp = view.findViewById(R.id.etUsernameSignUp);
        etEmailSignUp = view.findViewById(R.id.etEmailSignUp);
        etPasswordSignUp = view.findViewById(R.id.etPasswordSignUp);
        etConfirmPasswordSignUp = view.findViewById(R.id.etConfirmPasswordSignUp);
        btRegister = (Button)view.findViewById(R.id.btRegister);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsernameSignUp.getText().toString();
                String email = etEmailSignUp.getText().toString();
                String password = etPasswordSignUp.getText().toString();
                String password_confirm = etConfirmPasswordSignUp.getText().toString();
                // validate
                if (username.isEmpty()) showToast("Vui lòng nhập tên đăng nhập");
                else if (username.length() < 8) showToast("Tên đăng nhập quá ngắn");
                else if (email.isEmpty()) showToast("Vui lòng nhập email");
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) showToast("Định dạng email không đúng");
                else if (password.isEmpty()) showToast("Vui lòng nhập mật khẩu");
                else if (password.length() < 8) showToast("Mật khẩu quá ngắn");
                else if (password_confirm.isEmpty()) showToast("Vui lòng xác nhận mật khẩu");
                else if (!password_confirm.equals(password)) showToast("Xác nhận mật khẩu không đúng");
                else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Users")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("check user exists", "Lấy dữ liệu thành công");
                                        if (!task.getResult().isEmpty()){ // không có user
                                            showToast("Tên đăng nhập đã tồn tại");
                                        } else { // pass tất cả validate
                                            // TODO
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("username", username);
                                            user.put("name", username);
                                            user.put("password", ArgonHash.hash(password));
                                            user.put("email", email);
                                            user.put("avatar", "");
                                            user.put("role", "student");
                                            user.put("token", "");
                                            user.put("faculty", "");
                                            user.put("isBan", false);
                                            user.put("check", true);

                                            firestoreAPI.register(user); // firestore

                                            showToast("Đăng ký thành công");

                                            LocalMemory.saveLocalUser(context, username);
                                            LocalMemory.saveLocalName(context, username);
                                            context.startActivity(new Intent(context, MainActivity.class));
                                            getActivity().finish();
                                        }
                                    } else {
                                        Log.d("check user exists", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        });
        return view;
    }

}