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
import android.widget.TextView;
import android.widget.Toast;

import com.example.tdtuapp.ChangePassActivity;
import com.example.tdtuapp.firestore.firestoreAPI;
import com.example.tdtuapp.LocalMemory.ConstantData;
import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.LoginSignUpActivity;
import com.example.tdtuapp.MainActivity;
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

public class LoginFragment extends Fragment {
    EditText etEmailLogin, etPasswordLogin;
    Button btLogin;
    TextView tvChangePass;
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
        tvChangePass = view.findViewById(R.id.tvChangePass);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etEmailLogin.getText().toString();
                final String password = etPasswordLogin.getText().toString();

                if (username.isEmpty()) showToast("Vui l??ng nh???p t??i kho???n");
                else if (password.isEmpty()) showToast("Vui l??ng nh???p m???t kh???u");
                else if (password.length() < 8) showToast("M???t kh???u qu?? ng???n");
                else {
                    // TODO
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Users")
                            .whereEqualTo("username", username)// l???y user c?? username n??y
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()){ // kh??ng c?? user
                                            showToast("T??n ????ng nh???p ho???c m???t kh???u kh??ng ????ng");
                                        }
                                        else { // c?? user
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("save local name", document.getId() + " => " + document.getData());
                                                String hashPass = document.get("password", String.class);
                                                if (!ArgonHash.verify(password, hashPass)){
                                                    showToast("T??n ????ng nh???p ho???c m???t kh???u kh??ng ????ng");
                                                } else if (document.get("isBan", Boolean.class)){
                                                    showToast("T??i kho???n c???a b???n ???? b??? ch???n");
                                                }
                                                else {
                                                    showToast("????ng nh???p th??nh c??ng");
                                                    LocalMemory.saveLocalUser(context, username);
                                                    LocalMemory.saveLocalName(context, document.get("name", String.class));
                                                    context.startActivity(new Intent(context, MainActivity.class));
                                                    getActivity().finish();
                                                }
                                                break;
                                            }

                                        }
                                    } else {
                                        Log.d("login", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        });

        tvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ChangePassActivity.class));
            }
        });
        return view;
    }
}