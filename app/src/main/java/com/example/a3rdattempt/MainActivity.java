package com.example.a3rdattempt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText Email;
    private EditText Password;
    private Button btnSignIn;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_main);
        Email = findViewById(R.id.email_login);
        Password = findViewById(R.id.pass_login);
        btnSignIn = findViewById(R.id.btnsignin);
        btnSignUp = findViewById(R.id.btnsignup);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEmail = Email.getText().toString().trim();
                String sPass = Password.getText().toString().trim();
                if(TextUtils.isEmpty(sEmail)){
                    Email.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(sPass)){
                    Password.setError("Required Field");
                    return;
                }
                mDialog.setMessage("Logging in");
                mDialog.show();
                mAuth.signInWithEmailAndPassword(sEmail,sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),Home.class));
                            Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"There Seems to be a problem",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Registration.class));
            }
        });
    }
}
