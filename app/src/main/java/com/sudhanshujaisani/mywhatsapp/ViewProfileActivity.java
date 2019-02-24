package com.sudhanshujaisani.mywhatsapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

public class ViewProfileActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textViewName,textViewStatus;
    Button sendReqBtn,declineReqBtn;
    DatabaseReference userRef,selfRef,notificationDbRef;
String friendshipState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        imageView=(ImageView)findViewById(R.id.view_profile_imageView);
        textViewName=(TextView)findViewById(R.id.view_profile_display_name);
        textViewStatus=(TextView)findViewById(R.id.view_profile_status);
        sendReqBtn=(Button)findViewById(R.id.view_profile_send_req_button);
        declineReqBtn=(Button)findViewById(R.id.view_profile_decline_req_button);
        friendshipState="notFriends";
        notificationDbRef=FirebaseDatabase.getInstance().getReference().child("notifications");

        final String userId=getIntent().getStringExtra("userId");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        final String selfId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        selfRef=FirebaseDatabase.getInstance().getReference().child("Users").child(selfId);
        userRef.keepSynced(true);
        selfRef.keepSynced(true);
        final DatabaseReference friendRequestDbRef=FirebaseDatabase.getInstance().getReference().child("Friend Req");
        final DatabaseReference friendsDbRef=FirebaseDatabase.getInstance().getReference().child("Freiends");
        friendRequestDbRef.keepSynced(true);
        friendsDbRef.keepSynced(true);
       userRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String name=dataSnapshot.child("name").getValue().toString();
               String status=dataSnapshot.child("status").getValue().toString();
               final String image=dataSnapshot.child("image").getValue().toString();

               textViewName.setText(name);
               textViewStatus.setText(status);
               if(!image.equals("noImage"));
               Picasso.with(ViewProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.images).into(imageView, new Callback() {
                   @Override
                   public void onSuccess() {

                   }

                   @Override
                   public void onError() {
                       Toast.makeText(ViewProfileActivity.this, "loading...", Toast.LENGTH_SHORT).show();
                       Picasso.with(ViewProfileActivity.this).load(image).placeholder(R.drawable.images).into(imageView);
                   }
               });

               friendRequestDbRef.child(selfId).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if(dataSnapshot.hasChild(userId)){
                           String requestType=dataSnapshot.child(userId).child("type").getValue().toString();
                           if(requestType.equals("recieved")){
                               friendshipState="reqRecieved";
                               sendReqBtn.setText("Accept Request");
                               declineReqBtn.setVisibility(View.VISIBLE);
                               declineReqBtn.setEnabled(true);
                           }
                           else if(requestType.equals("sent")){
                               friendshipState="reqSent";
                               sendReqBtn.setText("Cancel Request");
                           }

                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
               friendsDbRef.child(selfId).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if(dataSnapshot.hasChild(userId)){
                           friendshipState="friends";
                           sendReqBtn.setText("Unfriend");
                       }

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
       sendReqBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if(friendshipState.equals("notFriends")){
                   sendReqBtn.setEnabled(false);

               DatabaseReference selfToUser=friendRequestDbRef.child(selfId).child(userId).child("type");
               selfToUser.setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       DatabaseReference userToSelf=friendRequestDbRef.child(userId).child(selfId).child("type");
                       userToSelf.setValue("recieved").addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {

                               HashMap<String,String> notificationData=new HashMap<>();
                               notificationData.put("from",selfId);
                               notificationData.put("type","friend request");
                               notificationDbRef.child(userId).push().setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       Toast.makeText(ViewProfileActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                       friendshipState="reqSent";
                                       sendReqBtn.setEnabled(true);
                                       sendReqBtn.setText("Cancel Request");
                                   }
                               });
                           }
                       });

                   }
               });
           }
           else if(friendshipState.equals("reqSent")){
                   sendReqBtn.setEnabled(false);
                   DatabaseReference selfToUser=friendRequestDbRef.child(selfId).child(userId);
                   selfToUser.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           DatabaseReference userToSelf=friendRequestDbRef.child(userId).child(selfId);
                           userToSelf.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   Toast.makeText(ViewProfileActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                                   friendshipState="notFriends";
                                   sendReqBtn.setEnabled(true);
                                   sendReqBtn.setText("Send Request");
                               }
                           });
                       }
                   });
               }
           else  if(friendshipState.equals("reqRecieved")){
                   friendsDbRef.child(selfId).child(userId).setValue(new Date()).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           friendRequestDbRef.child(userId).child(selfId).setValue(new Date()).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {

                                   DatabaseReference selfToUser=friendRequestDbRef.child(selfId).child(userId);
                                   selfToUser.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           DatabaseReference userToSelf=friendRequestDbRef.child(userId).child(selfId);
                                           userToSelf.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                 //  Toast.makeText(ViewProfileActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                                                   friendshipState="friends";
                                                   sendReqBtn.setEnabled(true);
                                                   sendReqBtn.setText("Unfriend");
                                                   declineReqBtn.setVisibility(View.INVISIBLE);
                                                   declineReqBtn.setEnabled(false);
                                               }
                                           });
                                       }
                                   });

                               }
                           });
                       }
                   });
               }
               else if(friendshipState.equals("friends")){
                   friendsDbRef.child(selfId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           friendsDbRef.child(userId).child(selfId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   sendReqBtn.setText("Send Request");
                                   friendshipState="notFriends";
                               }
                           });
                       }
                   });
               }
           }
       });
    }
}
