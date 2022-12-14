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

                // r???ng
                if (username.isEmpty() || oldPass.isEmpty() || newPass.isEmpty() || newPassRepeated.isEmpty()){
                    showToast("Vui l??ng nh???p ?????y ????? th??ng tin");
                } else if (newPass.length() < 8 || oldPass.length() < 8){
                    showToast("M???t kh???u qu?? ng???n");
                } else if (!newPass.equals(newPassRepeated)){
                    showToast("X??c nh???n m???t kh???u kh??ng ????ng");
                }
                db.collection("Users")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().isEmpty()){
                                        showToast("T??n ????ng nh???p kh??ng t???n t???i");
                                    } else { // c?? ng?????i d??ng
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String hashPass = document.get("password", String.class);
                                            if (!ArgonHash.verify(oldPass, hashPass)){
                                                showToast("M???t kh???u kh??ng ????ng");
                                            } else { // ????ng m???t kh???u
                                                db.collection("Users").document(document.getId())
                                                        .update("password", ArgonHash.hash(newPass))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                showToast("?????i m???t kh???u th??nh c??ng");
                                                                finish();
                                                            }
                                                        });
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    showToast("?????i m???t kh???u th???t b???i");
                                }
                            }
                        });
                // t??n ????ng nh???p ho???c m???t kh???u kh??ng ????ng
            }
        });
    }
    private void showToast(String text){
        Toast.makeText(ChangePassActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}