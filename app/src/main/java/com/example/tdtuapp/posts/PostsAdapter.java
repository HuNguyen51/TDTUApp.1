package com.example.tdtuapp.posts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tdtuapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private ArrayList<Posts> postList = new ArrayList<>();
    private Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PostsAdapter(ArrayList<Posts> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }
    public void updateData(ArrayList<Posts> postList){
        this.postList = postList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.train_eval_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posts post = postList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String username = post.getOwner();
        db.collection("Users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.get("avatar", String.class).isEmpty())
                                    Picasso.get().load(document.get("avatar", String.class)).into(holder.imAvatarCLB);
                                holder.tvNameCLB.setText(document.get("name", String.class));
                                break;
                            }
                        }
                    }
                });
        if (!post.getLinkImg().isEmpty())
            Picasso.get().load(post.getLinkImg()).into(holder.imPost);

//        holder.imComment.setOnClickListener();
//        holder.imLike.setOnClickListener();

        holder.tvCreateDate.setText(dateFormat.format(post.getCreate_date()));
        holder.tvFromDate.setText(dateFormat.format(post.getFrom()));
        holder.tvToDate.setText(dateFormat.format(post.getTo()));
        holder.tvField.setText(post.getField());
        holder.tvContent.setText(post.getContent());

//        holder.spAction
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imAvatarCLB;
        private ImageView imPost, imComment, imLike;
        private TextView tvNameCLB, tvCreateDate, tvFromDate, tvToDate, tvField, tvContent;
        private Spinner spAction;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imAvatarCLB = itemView.findViewById(R.id.imAvatarCLB); // load bằng owner
            imPost = itemView.findViewById(R.id.imPost);
            tvNameCLB = itemView.findViewById(R.id.tvNameCLB);  // load bằng owner
            tvCreateDate = itemView.findViewById(R.id.tvCreateDate);
            tvFromDate = itemView.findViewById(R.id.tvFromDate);
            tvToDate = itemView.findViewById(R.id.tvToDate);
            tvField = itemView.findViewById(R.id.tvField);
            tvContent = itemView.findViewById(R.id.tvContent);
            spAction = itemView.findViewById(R.id.spAction);
        }
    }
}
