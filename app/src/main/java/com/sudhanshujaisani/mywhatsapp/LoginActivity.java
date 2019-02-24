package com.sudhanshujaisani.mywhatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
EditText editTextLogin,editTextPass;
Button button;
FirebaseAuth firebaseAuth;
DatabaseReference userDbRef;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar=(Toolbar)findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);

        editTextLogin=(EditText)findViewById(R.id.editTextLoginEmail);
        editTextPass=(EditText)findViewById(R.id.editTextLoginPass);
        button=(Button)findViewById(R.id.buttonLogin);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Signing in");
        progressDialog.setMessage("Please wait while we sign you in..");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth=FirebaseAuth.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String email=editTextLogin.getText().toString();
                String password=editTextPass.getText().toString();
                Task task=firebaseAuth.signInWithEmailAndPassword(email,password);
               task.addOnCompleteListener(new OnCompleteListener() {
                   @Override
                   public void onComplete(@NonNull Task task1) {
                       if(task1.isSuccessful()){
                           progressDialog.dismiss();

                           String uid=firebaseAuth.getCurrentUser().getUid();
                           String deviceToken=FirebaseInstanceId.getInstance().getToken();
                           userDbRef=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                           userDbRef.child("token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {

                                   Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   startActivity(intent);
                                   finish();
                               }
                           });

                       }

                       else{progressDialog.hide();

                           Toast.makeText(LoginActivity.this, "Please check the entries and try again.", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
        });

    }
}
