package com.example.tdtuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.hash.ArgonHash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ChangePassActivity extends AppCompatActivity {
    EditText etUsername,etOldPass, etNewPass, etRepeatPass;
    TextView tvBack;
    Button btChangePass;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        etUsername = findViewById(R.id.etUsername);
        etOldPass = findViewById(R.id.etOldPass);
        etNewPass = findViewById(R.id.etNewPass);
        etRepeatPass = findViewById(R.id.etRepeatPass);
        tvBack = findViewById(R.id.tvBack);
        btChangePass = findViewById(R.id.btChangePass);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String oldPass = etOldPass.getText().toString();
                final String newPass = etNewPass.getText().toString();
                final String newPassRepeated = etRepeatPass.getText().toString();

                // rỗng
                if (username.isEmpty() || oldPass.isEmpty() || newPass.isEmpty() || newPassRepeated.isEmpty()){
                    showToast("Vui lòng nhập đầy đủ thông tin");
                } else if (newPass.length() < 8 || oldPass.length() < 8){
                    showToast("Mật khẩu quá ngắn");
                } else if (!newPass.equals(newPassRepeated)){
                    showToast("Xác nhận mật khẩu không đúng");
                }
                db.collection("Users")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().isEmpty()){
                                        showToast("Tên đăng nhập không tồn tại");
                                    } else { // có người dùng
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String hashPass = document.get("password", String.class);
                                            if (!ArgonHash.verify(oldPass, hashPass)){
                                                showToast("Mật khẩu không đúng");
                                            } else { // đúng mật khẩu
                                                db.collection("Users").document(document.getId())
                                                        .update("password", ArgonHash.hash(newPass))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                showToast("Đổi mật khẩu thành công");
                                                                finish();
                                                            }
                                                        });
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    showToast("Đổi mật khẩu thất bại");
                                }
                            }
                        });
                // tên đăng nhập hoặc mật khẩu không đúng
            }
        });
    }
    private void showToast(String text){
        Toast.makeText(ChangePassActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}