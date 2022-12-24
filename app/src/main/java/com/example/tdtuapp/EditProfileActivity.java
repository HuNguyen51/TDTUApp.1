package com.example.tdtuapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tdtuapp.LocalMemory.LocalMemory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ImageView btnSaveInfo, btnBack;
    CircleImageView imAvatar;
    TextView action_edit_avatar;
    EditText etName, etStudentID, etBio, etEmail,etFaculty;
    String role = "";
    String id = "";
    String avtSrc = "";
    String localImg = "";
    Spinner spFaculty;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://tdtu-app-ab935.appspot.com");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
        spFaculty = findViewById(R.id.spFaculty);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.faculty_arrays, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spFaculty.setAdapter(adapter);
        spFaculty.setOnItemSelectedListener(this);
        imAvatar = findViewById(R.id.imAvatar);
        action_edit_avatar = findViewById(R.id.action_edit_avatar);
        etName = findViewById(R.id.etName);
        etStudentID = findViewById(R.id.etStudentID);
        etBio = findViewById(R.id.etBio);
        etEmail = findViewById(R.id.etEmail);
        etFaculty = findViewById(R.id.etFaculty);
        String localUser = LocalMemory.loadLocalUser(this);
        db.collection("Users") // load thông tin vào trong để người dùng không cần nhập
                .whereEqualTo("username", localUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                                avtSrc = document.get("avatar", String.class);
                                Picasso.get().load(avtSrc).into(imAvatar);
                                etName.setText(document.get("name", String.class));
                                etFaculty.setText(document.get("faculty", String.class));
                                etEmail.setText(document.get("email", String.class));

                                role = document.get("role", String.class);
                                if (!role.equals("student")){
                                    etStudentID.setEnabled(false);
                                } else {
                                    etStudentID.setEnabled(true);
                                    String studentID = "";
                                    if (document.get("student_id", String.class) != null){
                                        studentID = document.get("student_id", String.class);
                                    }
                                    etStudentID.setText(studentID);
                                }

                                String bio = "";
                                if (document.get("bio", String.class) != null) bio = document.get("bio", String.class);
                                etBio.setText(bio);
                                break;
                            }
                        }
                    }
                });
        action_edit_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        btnSaveInfo = findViewById(R.id.btnSaveInfo);
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (localImg.equals("")){
                    updateUser();
                } else {
                    updateUser(localImg);
                }
                finish();
            }
        });
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode == RESULT_OK && data!=null ){
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
            cursor.moveToFirst();
            int colIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);

            String picturePath = cursor.getString(colIndex);
            cursor.close();

            Log.d("choose image", picturePath);

            localImg = picturePath;
            imAvatar.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }
    public void updateUser(String picturePath){
        Uri file = Uri.fromFile(new File(picturePath));
        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child(file.getLastPathSegment());
        UploadTask uploadTask = imagesRef.putFile(file, metadata);

        // Register observers to listen for when the download is done or if it fails
        uploadTask
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Log.d("on progress", "Upload is " + progress + "% done");
                    }
                })
                .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("progress pause", "Upload is paused");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d("result", "upload không thành công");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                    Log.d("download path", uri.toString());
                                avtSrc = uri.toString();
                                updateUser();
                            }
                        });
                    }
                });
    }
    public void updateUser(){
        Map<String, Object> info = new HashMap<>();
        info.put("avatar", avtSrc);
        info.put("name", etName.getText().toString());
        info.put("faculty", etFaculty.getText().toString());
        info.put("email", etEmail.getText().toString());
        info.put("bio", etBio.getText().toString());
        if (role.equals("student")){
            info.put("student_id", etStudentID.getText().toString());
        }
        db.collection("Users").document(id)
                .update(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String faculty = parent.getSelectedItem().toString();
        etFaculty.setText(faculty);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}