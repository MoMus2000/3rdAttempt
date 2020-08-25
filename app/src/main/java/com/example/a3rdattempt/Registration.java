package com.example.a3rdattempt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private EditText fullname;
    private EditText phone;
    private Button btnSignin;
    private Button btnSignup;
    private ProgressDialog mDialog;
    String userID;
    FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_registration);
        email = findViewById(R.id.EmReg);
        password = findViewById(R.id.PassReg);
        fullname = findViewById(R.id.fullName);
        phone = findViewById(R.id.Phone);
        btnSignin = findViewById(R.id.SigninReg);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        btnSignup= findViewById(R.id.SignUPReg);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mEmail = email.getText().toString().trim();
                String mPass = password.getText().toString().trim();
                final String FullName = fullname.getText().toString();
                final String PhoneNumber = phone.getText().toString();
                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(mPass)){
                    password.setError("Required Field");
                    return;
                }
                mDialog.setMessage("Creating new Account");
                mDialog.show();
                mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fname",FullName);
                            user.put("email",mEmail);
                            user.put("PhoneNumber",PhoneNumber);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"DONE",Toast.LENGTH_SHORT);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),Home.class));
                            Toast.makeText(getApplicationContext(),"Registration Complete & Logged in",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"There Seems to be a problem",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
