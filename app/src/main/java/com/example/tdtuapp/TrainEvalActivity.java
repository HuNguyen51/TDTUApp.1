package com.example.tdtuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.tdtuapp.posts.Posts;
import com.example.tdtuapp.posts.PostsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TrainEvalActivity extends AppCompatActivity{
    private RecyclerView rvTrainEval;
    private PostsAdapter adapter;
    private ArrayList<Posts> postList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView btnBack;

    private String field = "Học thuật";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_eval);
        Intent intent = getIntent();
        field = intent.getStringExtra("field");

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        rvTrainEval = findViewById(R.id.rvTrainEval);
        postList = new ArrayList<>();
        adapter = new PostsAdapter(postList, this);

        rvTrainEval.setLayoutManager(new LinearLayoutManager(this));
        rvTrainEval.setAdapter(adapter);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db.collection("Posts")
                .whereEqualTo("field", field)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) { // load các bài viết
                                Posts post = new Posts(
                                        document.get("owner", String.class),
                                        document.getId(),
                                        document.get("linkImg", String.class),
                                        document.get("content", String.class),
                                        document.get("field", String.class),
                                        document.get("create_date", Long.class),
                                        document.get("from", Long.class),
                                        document.get("to", Long.class)
                                );
                                postList.add(post);
                            }
                            adapter.updateData(postList);
                        }
                    }
                });
    }
}