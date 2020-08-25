package com.example.a3rdattempt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class Post extends AppCompatActivity {
    private ImageView newPostImage;
    private EditText newPostText;
    private Button newPostButton;
    private Uri postImageUri = null;
    private ProgressDialog mDialog;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private Bitmap compressedFileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        newPostImage = findViewById(R.id.PostImage);
        newPostText = findViewById(R.id.postText);
        newPostButton = findViewById(R.id.Postings);
        mDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(Post.this);
            }
        });
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String desc = newPostText.getText().toString();
                if(!TextUtils.isEmpty(desc)&& postImageUri != null){
                    mDialog.setMessage("Publishing");
                    mDialog.show();
//                    final String RandomName;
//                    RandomName = random();
                    final String RandomName = UUID.randomUUID().toString();
                    StorageReference filePath = storageReference.child("PostImages").child(RandomName+".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                            final String downloadUri = task.getResult().toString();

                            if(task.isSuccessful()){
//                                String downloadUri = storageReference.getDownloadUrl().toString();
//                                File file = new File(postImageUri.getPath());
//                                compressedFileImage = new Compressor(Post.this).compressToFile(file);
                                File newThumbFile = new File(postImageUri.getPath());
                                try {

                                    compressedFileImage = new Compressor(Post.this)
                                            .setMaxHeight(100)
                                            .setQuality(1)
                                            .setMaxWidth(100)
                                            .compressToBitmap(newThumbFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedFileImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();
                                UploadTask uploadTask = storageReference.child("PostImages/thumbs")
                                        .child(RandomName + ".jpg").putBytes(thumbData);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        String thumbUri = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                        Map<String,Object> postMap = new HashMap<>();
                                        postMap.put("Image_url", downloadUri);
                                        postMap.put("imageThumb",thumbUri);
                                        postMap.put("desc",desc);
                                        postMap.put("user_id",currentUserId);
                                        postMap.put("TimeStamp", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(Post.this, "Post was added", Toast.LENGTH_LONG).show();
                                                    Intent mainIntent = new Intent(Post.this, Home.class);
                                                    startActivity(mainIntent);
                                                    finish();


                                                } else {


                                                }
                                                mDialog.dismiss();


                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //Error handling

                                    }
                                });


                            } else {
                                mDialog.dismiss();


                            }

                        }
                    });


                }

            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}
