package com.example.tdtuapp.Home;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tdtuapp.EditProfileActivity;
import com.example.tdtuapp.LocalMemory.ConstantData;
import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.example.tdtuapp.LoginSignUpActivity;
import com.example.tdtuapp.R;
import com.example.tdtuapp.firestore.firestoreAPI;
import com.example.tdtuapp.posts.Posts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private ImageView btnLogout;
    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ConstantData.realTimeDatabaseUrl);
    private CircleImageView imAvatar;
    private TextView tvName, tvBio, tvStudentID, tvFaculty, tvEmail, tvUsername;
    private Button btnEditProfile;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imAvatar = view.findViewById(R.id.imAvatar);
        tvName = view.findViewById(R.id.tvName);
        tvBio = view.findViewById(R.id.tvBio);
        tvStudentID = view.findViewById(R.id.tvStudentID);
        tvFaculty = view.findViewById(R.id.tvFaculty);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvUsername = view.findViewById(R.id.tvUsername);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chuyển intent qua chỉnh sửa thông tin
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String localUser = LocalMemory.loadLocalUser(context);
                // set token về rỗng
                firestoreAPI.updateToken(localUser, "");
                // xóa thông báo
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                // set thành offline
                databaseReference.child("USERS_ONLINE").child(localUser).setValue(false);
                // xóa người dùng khỏi local máy
                LocalMemory.saveLocalUser(context, "");
                // di chuyển đến màn hình đăng nhập
                context.startActivity(new Intent(context, LoginSignUpActivity.class));
                getActivity().finishAffinity();
            }
        });

        loadInfo();
        return view;
    }
    private void loadInfo(){
        String localUser = LocalMemory.loadLocalUser(context);
        db.collection("Users")
                .whereEqualTo("username", localUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Picasso.get().load(document.get("avatar", String.class)).into(imAvatar);
                                tvName.setText(document.get("name", String.class));
                                tvFaculty.setText(document.get("faculty", String.class));
                                tvEmail.setText(document.get("email", String.class));
                                tvUsername.setText(document.get("username", String.class));
                                String studentID = "Câu lạc bộ";
                                if (document.get("student_id", String.class) != null){
                                    studentID = document.get("student_id", String.class);
                                }
                                tvStudentID.setText(studentID);

                                String bio = document.get("bio", String.class);
                                if (bio.isEmpty()){
                                    tvBio.setVisibility(View.GONE);
                                } else {
                                    tvBio.setVisibility(View.VISIBLE);
                                    tvBio.setText(bio);
                                }
                                break;
                            }
                        }
                    }
                });
    }
}