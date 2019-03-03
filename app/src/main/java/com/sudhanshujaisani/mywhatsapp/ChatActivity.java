package com.sudhanshujaisani.mywhatsapp;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
EditText messageContentEditText;
ImageButton sendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String name=getIntent().getStringExtra("user_Name");
        String image=getIntent().getStringExtra("image");

        messageContentEditText=(EditText)findViewById(R.id.editText_message_content);
        sendButton=(ImageButton)findViewById(R.id.button_send);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_chat_activity);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.layout_chat_activity_custom_bar,null);
//                LayoutInflater.from(this).inflate(R.layout.layout_chat_activity_custom_bar,null,false);
        actionBar.setCustomView(view);
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
//        View view=actionBar.getCustomView();
//        TextView textViewName=(TextView)findViewById(R.id.textView_name_chat_activity);
//        TextView textViewLastSeen=(TextView)findViewById(R.id.textView_lastSeen);
//        CircleImageView circleImageView=(CircleImageView) findViewById(R.id.image_toolbar);
//
//        textViewName.setText("sajks");
//        Picasso.with(this).load(image).placeholder(R.drawable.images).into(circleImageView);
//
    }
}
