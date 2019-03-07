package com.sudhanshujaisani.mywhatsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
EditText messageContentEditText;
ImageButton sendButton,sendImageButton;
DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
RecyclerView recyclerViewMessages;
ArrayList<Messages>messagesArrayList=new ArrayList<>();
MessageAdapter messageAdapter;
SwipeRefreshLayout swipeRefreshLayout;
public static  final  int ITEMS_PER_PAGE=10;
    String selfUid,friendUid;
int pageNo=1;
int itemPos=0;
    private String lastMsgKey;
    private String prevKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        selfUid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        String name=getIntent().getStringExtra("user_Name");
        String image=getIntent().getStringExtra("image");
         friendUid=getIntent().getStringExtra("userId");
        messageAdapter=new MessageAdapter(messagesArrayList,image);
        messageContentEditText=(EditText)findViewById(R.id.editText_message_content);
        sendButton=(ImageButton)findViewById(R.id.button_send);
        sendImageButton=(ImageButton)findViewById(R.id.send_image_button);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        recyclerViewMessages=(RecyclerView)findViewById(R.id.recycler_view_for_messages);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_chat_activity);
        setSupportActionBar(toolbar);

        recyclerViewMessages.setHasFixedSize(true);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        View view= LayoutInflater.from(this).inflate(R.layout.layout_chat_activity_custom_bar,null,false);
        actionBar.setCustomView(view);

        TextView textViewName=(TextView)findViewById(R.id.textView_name_chat_activity);
        TextView textViewLastSeen=(TextView)findViewById(R.id.textView_lastSeen);
        CircleImageView circleImageView=(CircleImageView) findViewById(R.id.image_toolbar);

        textViewName.setText(name);
        Picasso.with(this).load(image).placeholder(R.drawable.images).into(circleImageView);

        rootRef.child("Chat").child(selfUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(selfUid)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", "false");
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + selfUid + "/" + friendUid, chatAddMap);
                    chatUserMap.put("Chat/" + friendUid + "/" + selfUid, chatAddMap);

                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("ChatLog", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        loadMessages();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo++;
                itemPos=0;
                loadMoreMessages();
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageContent=messageContentEditText.getText().toString();
                if(!TextUtils.isEmpty(messageContent)){

                    DatabaseReference dbRef=rootRef.child("messages").child(selfUid).child(friendUid).push();
                    String pushId=dbRef.getKey();
                    Map messageMap=new HashMap();
                    messageMap.put("message",messageContent);
                    messageMap.put("seen",false);
                    messageMap.put("type","text");
                    messageMap.put("time",ServerValue.TIMESTAMP);
                    messageMap.put("from",selfUid);

                    Map messageUserMap=new HashMap();
                    messageUserMap.put("messages/"+selfUid+"/"+friendUid+"/"+pushId,messageMap);
                    messageUserMap.put("messages/"+friendUid+"/"+selfUid+"/"+pushId,messageMap);

                    rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("ChatLog", databaseError.getMessage().toString());
                            }
                        }
                    });
                    messageContentEditText.setText("");

                }
            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                Intent newIntent= Intent.createChooser(intent,"Select picture from...");
                startActivityForResult(newIntent,345);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==345&&resultCode==RESULT_OK){
            Uri uri=data.getData();
            DatabaseReference dbRef=rootRef.child("messages").child(selfUid).child(friendUid).push();
            final String pushId=dbRef.getKey();


            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Message  Images").child(pushId + ".jpg");

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
                        Toast.makeText(ChatActivity.this, "Storage Updated", Toast.LENGTH_SHORT).show();
                        String imgUri = task.getResult().toString();

                        Map messageMap=new HashMap();
                        messageMap.put("message",imgUri);
                        messageMap.put("seen",false);
                        messageMap.put("type","image");
                        messageMap.put("time",ServerValue.TIMESTAMP);
                        messageMap.put("from",selfUid);

                        Map messageUserMap=new HashMap();
                        messageUserMap.put("messages/"+selfUid+"/"+friendUid+"/"+pushId,messageMap);
                        messageUserMap.put("messages/"+friendUid+"/"+selfUid+"/"+pushId,messageMap);

                        rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.d("ChatLog", databaseError.getMessage().toString());
                                }
                            }
                        });

                    }

                }
            });



        }
    }

    private void loadMoreMessages() {
        DatabaseReference messageRef=rootRef.child("messages").child(selfUid).child(friendUid);
        Query query=messageRef.orderByKey().endAt(lastMsgKey).limitToLast(pageNo*ITEMS_PER_PAGE);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message=dataSnapshot.getValue(Messages.class);
                String messageKey=dataSnapshot.getKey();
                if (!prevKey.equals(messageKey)){
                messagesArrayList.add(itemPos++,message);
                }
                else{
                    prevKey=lastMsgKey;
                }
                if(itemPos==1){
                    lastMsgKey=messageKey;
                    prevKey=messageKey;
                }

                messageAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadMessages(){

        DatabaseReference messageRef=rootRef.child("messages").child(selfUid).child(friendUid);
        Query query=messageRef.limitToLast(pageNo*ITEMS_PER_PAGE);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message=dataSnapshot.getValue(Messages.class);
                itemPos++;

                if(itemPos==1){
                    lastMsgKey=dataSnapshot.getKey();
                    prevKey=dataSnapshot.getKey();
                }
                messagesArrayList.add(message);
                messageAdapter.notifyDataSetChanged();
                recyclerViewMessages.scrollToPosition(messagesArrayList.size()-1);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {



            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
