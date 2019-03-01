package com.sudhanshujaisani.mywhatsapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
FirebaseRecyclerAdapter<Friends,ChatUserHolder> firebaseRecyclerAdapter;
DatabaseReference databaseReference;
    RecyclerView recyclerView;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_chat, container, false);
         recyclerView=(RecyclerView)view.findViewById(R.id.chatRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Friends").child(uid);
databaseReference.keepSynced(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query=databaseReference.limitToLast(50);
        FirebaseRecyclerOptions<Friends> options=new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query,Friends.class).build();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Friends, ChatUserHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatUserHolder holder, int position, @NonNull Friends model) {
                CardView cardView=holder.cardView1;
                final CircleImageView circleImageView=(CircleImageView)cardView.findViewById(R.id.user_detail_image);
                final TextView displayName=(TextView)cardView.findViewById(R.id.user_detail_display_name);
                final TextView displayStatus=(TextView)cardView.findViewById(R.id.user_detail_status);
                final CircleImageView onlineIcon=(CircleImageView)cardView.findViewById(R.id.user_detail_online_icon_imageView);


                String userId=getRef(position).getKey();
                DatabaseReference userRef=FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                userRef.keepSynced(true);
                        userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name=dataSnapshot.child("name").getValue().toString();
                        String status=dataSnapshot.child("status").getValue().toString();
                        String image=dataSnapshot.child("thumb").getValue().toString();
                        String online=dataSnapshot.child("online").getValue().toString();


                        if(!image.equals("noImage"))
                        Picasso.with(getContext()).load(image).placeholder(R.drawable.images).into(circleImageView);
                        else
                            circleImageView.setImageResource(R.drawable.images);
                        displayName.setText(name);
                        displayStatus.setText(status);
                        if(online.equals("true"))
                            onlineIcon.setVisibility(View.VISIBLE);
                        else
                            onlineIcon.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
                );
            }

            @NonNull
            @Override
            public ChatUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               CardView cardView=(CardView)LayoutInflater.from(parent.getContext()).inflate(R.layout.user_details,parent,false);
                return new ChatUserHolder(cardView);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatUserHolder extends RecyclerView.ViewHolder{
CardView cardView1;
    public ChatUserHolder(View itemView) {
        super(itemView);
        cardView1=(CardView)itemView;
    }
}
}
