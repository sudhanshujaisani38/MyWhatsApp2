package com.sudhanshujaisani.mywhatsapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> messagesList;
String selfUid,recieverUid;
String senderImageURL,recieverImageURL;

public static final int SENT_MESSAGE=1;
public  static final int RECIVED_MESSAGE=2;
    public MessageAdapter(List<Messages> messagesList,String recieverImage) {
        this.messagesList = messagesList;
        selfUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        recieverImageURL=recieverImage;

    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==RECIVED_MESSAGE)
        view=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_recieved,parent,false);
        else
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_sent,parent,false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Messages messages=messagesList.get(position);
        if(!messagesList.get(position).getFrom().equals(selfUid)){
            Picasso.with(holder.itemView.getContext()).load(recieverImageURL).into(holder.circleImageView);
        }
        if(messages.getType().equals("text")) {
            holder.textView.setText(messages.getMessage());
            holder.imageView.setVisibility(View.INVISIBLE);
        }
        else if(messages.getType().equals("image")){
            holder.textView.setText("");
            holder.textView.setVisibility(View.INVISIBLE);
            Picasso.with(holder.itemView.getContext()).load(messages.getMessage()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView circleImageView;
        public TextView textView;
        public ImageView imageView;


        public MessageViewHolder(View itemView) {
            super(itemView);
            circleImageView=(CircleImageView) itemView.findViewById(R.id.circleImageMessageBody);
            textView=(TextView)itemView.findViewById(R.id.textViewMessage);
            imageView=(ImageView)itemView.findViewById(R.id.imageViewMesage);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesList.get(position).getFrom().equals(selfUid)){
            return SENT_MESSAGE;
        }
        else
        return RECIVED_MESSAGE;
    }
}
