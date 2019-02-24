package com.sudhanshujaisani.mywhatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Toolbar toolbar =(Toolbar)findViewById(R.id.users_toolbar);
        setSupportActionBar(toolbar);
         databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");


    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query=databaseReference.limitToLast(50);
        FirebaseRecyclerOptions<Users> options=new FirebaseRecyclerOptions.Builder<Users>()
                                                                                    .setQuery(query,Users.class).build();

         firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                CardView view= (CardView) LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.user_details,parent,false);
                ;
                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull final Users model) {
                CardView view=holder.cardView;
                final ImageView imageView=(ImageView)view.findViewById(R.id.user_detail_image);
                TextView textViewName=(TextView)view.findViewById(R.id.user_detail_display_name);
                TextView textViewStatus=(TextView)view.findViewById(R.id.user_detail_status);

                textViewName.setText(model.getName());
                textViewStatus.setText(model.getStatus());
                if(model.getImage()!="noImage")
                Picasso.with(UsersActivity.this).load(model.getThumb()).placeholder(R.drawable.images).into(imageView);
                else
                    imageView.setImageResource(R.drawable.images);

                final String userId=getRef(position).getKey();
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(UsersActivity.this);

                        ImageView imageView1=new ImageView(UsersActivity.this);
                        imageView1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        imageView1.setMaxWidth(400);
                        imageView1.setMaxHeight(400);
                        Picasso.with(UsersActivity.this).load(model.getImage()).placeholder(R.drawable.images).into(imageView1);
                        builder.setView(imageView1).show();
                    }
                });
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(UsersActivity.this,ViewProfileActivity.class);
                        intent.putExtra("userId",userId);
                        startActivity(intent);
                    }
                });
            }
        };
         firebaseRecyclerAdapter.startListening();
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
CardView cardView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            cardView=(CardView) itemView;
        }

    }
}
