package com.sudhanshujaisani.mywhatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class MyAccountActivity extends AppCompatActivity {

CircleImageView circleImageView;
TextView textViewDisplayName,textViewStatus;
FirebaseDatabase firebaseDatabase;
FirebaseAuth firebaseAuth;
DatabaseReference databaseReference;
String name,status,uid,imageUri;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        textViewDisplayName=(TextView)findViewById(R.id.textViewDisplayName);
        textViewStatus=(TextView)findViewById(R.id.textViewStatus);
        circleImageView=(CircleImageView)findViewById(R.id.circleImageView);
firebaseAuth=FirebaseAuth.getInstance();
uid=firebaseAuth.getCurrentUser().getUid();

firebaseDatabase=FirebaseDatabase.getInstance();
databaseReference=firebaseDatabase.getReference().child("Users").child(uid);
databaseReference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         name=dataSnapshot.child("name").getValue().toString();
         status=dataSnapshot.child("status").getValue().toString();
         imageUri=dataSnapshot.child("image").getValue().toString();
        textViewDisplayName.setText(name);
        textViewStatus.setText(status);
if(!imageUri.equals("noImage"))
        Picasso.with(MyAccountActivity.this).load(imageUri).placeholder(R.drawable.images).into(circleImageView);

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});

   textViewDisplayName.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           final EditText editText=new EditText(MyAccountActivity.this);
           editText.setText(name);
           final AlertDialog.Builder builder=new AlertDialog.Builder(MyAccountActivity.this);
           builder.setTitle("Update display name");
           builder.setView(editText);
           builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   Task task = databaseReference.child("name").setValue(editText.getText().toString());
                   task.addOnCompleteListener(new OnCompleteListener() {
                       @Override
                       public void onComplete(@NonNull Task task) {
                           Toast.makeText(MyAccountActivity.this, "Display Name Updated", Toast.LENGTH_SHORT).show();
                       }
                   });

               }
           });

           builder.show();

       }
   });
        textViewStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText=new EditText(MyAccountActivity.this);
                editText.setText(status);
                final AlertDialog.Builder builder=new AlertDialog.Builder(MyAccountActivity.this);
                builder.setTitle("Update status");
                builder.setView(editText);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Task task = databaseReference.child("status").setValue(editText.getText().toString());
                        task.addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Toast.makeText(MyAccountActivity.this, "Status Updated", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                builder.show();

                    }
                });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
               Intent newIntent= Intent.createChooser(intent,"Select display picture from...");
                startActivityForResult(newIntent,1001);
            }
        });
            }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Toast.makeText(MyAccountActivity.this, "here..", Toast.LENGTH_SHORT).show();
            Uri uri = data.getData();

            Bitmap thumbBitmap = null;
            try {
                thumbBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_SHORT).show();
            }

            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
            byte[] bytes=baos.toByteArray();
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            final StorageReference thumbImageReference=firebaseStorage.getReference().child("Profile Pictures").child("Thumb Images").child(uid+".jpg");
            thumbImageReference.putBytes(bytes).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(task.isSuccessful())
                        return thumbImageReference.getDownloadUrl();
                    else
                        throw task.getException();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    String thumbUri=task.getResult().toString();
                    Task task2=databaseReference.child("thumb").setValue(thumbUri);
                    task2.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(MyAccountActivity.this, "Thumb Updated", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
                circleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                circleImageView.setImageURI(uri);

                final StorageReference storageReference = firebaseStorage.getReference().child("Profile Pictures").child(uid + ".jpg");

                storageReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (task.isSuccessful())
                            return storageReference.getDownloadUrl();
                        else throw task.getException();
                    }

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MyAccountActivity.this, "Storage Updated", Toast.LENGTH_SHORT).show();
                            String imgUri = task.getResult().toString();
                            Task task2 = databaseReference.child("image").setValue(imgUri);
                            task2.addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                        Toast.makeText(MyAccountActivity.this, "DB Updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });


            }
        }
    }



