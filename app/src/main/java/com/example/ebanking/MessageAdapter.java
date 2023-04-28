package com.example.ebanking;

import static com.example.ebanking.ChatWindow.receiverImg;
import static com.example.ebanking.ChatWindow.senderImg;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ebanking.ChatWindow;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<com.example.ebanking.MsgModel> messageAdapterArrayList;
    int ITEM_SEND = 1, ITEM_RECEIVE = 2, MONEY_SENT = 3, MONEY_RECEIVED = 4;


    public MessageAdapter(ChatWindow context, ArrayList<com.example.ebanking.MsgModel> messageArrayList) {
        this.context = context;
        this.messageAdapterArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHolder(view);
        }
        else if(viewType == ITEM_RECEIVE){
            View view = LayoutInflater.from(context).inflate(R.layout.reciever_layout, parent, false);
            return new receiverViewHolder(view);
        }
        else if(viewType == MONEY_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.sent_money, parent, false);
            return new senderMoney(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.received_money, parent, false);
            return new receiverMoney(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MsgModel message = messageAdapterArrayList.get(position);
        
        if(holder.getClass().equals(senderViewHolder.class))
        {
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.msgTextView.setText(message.getMessage());
            viewHolder.timeStampTextView.setText(getTimeDate(message.timeStamp));
            Picasso.get().load(senderImg).into(viewHolder.circleImageView); //for setting sender image
        }
        else if(holder.getClass().equals(receiverViewHolder.class)){
            Log.d("DEBUGGING", "IN RECEIVERVIEWHOLDER");
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.msgTextView.setText(message.getMessage());
            viewHolder.timeStampTextView.setText(getTimeDate(message.timeStamp));
            Picasso.get().load(receiverImg).into(viewHolder.circleImageView); //for setting receiver image
            Log.d("DEBUGGING", "IN RECEIVERVIEWHOLDER222");

        }
        else if(holder.getClass().equals(senderMoney.class))
        {
            senderMoney viewHolder = (senderMoney) holder;
            viewHolder.msgTextView.setText("₹" + message.getAmount());
            viewHolder.timeStampTextView.setText(getTimeDate(message.timeStamp));
            Picasso.get().load(senderImg).into(viewHolder.circleImageView); //for setting sender image
        }
        else {
            Log.d("DEBUGGING", "IN RECEIVERMONEY");

            receiverMoney viewHolder = (receiverMoney) holder;
            viewHolder.msgTextView.setText("₹" + message.getAmount());
            viewHolder.timeStampTextView.setText(getTimeDate(message.timeStamp));
            Picasso.get().load(receiverImg).into(viewHolder.circleImageView); //for setting receiver image
            Log.d("DEBUGGING", "IN RECEIVERMONEY222");

        }
    }

    @Override
    public int getItemCount() {
        return messageAdapterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MsgModel message = messageAdapterArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId()))
        {
            if(TextUtils.isEmpty(message.getMessage()))
            {
                return MONEY_SENT;
            }
            return ITEM_SEND;
        }
        else {
            if(TextUtils.isEmpty(message.getMessage()))
            {
                return MONEY_RECEIVED;
            }
            return ITEM_RECEIVE;
        }
    }

    class senderViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView msgTextView, timeStampTextView;

        public senderViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.profilePicSender);
            msgTextView = itemView.findViewById(R.id.senderText);
            timeStampTextView = itemView.findViewById(R.id.senderTimeStamp);
        }
    }

    class receiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgTextView, timeStampTextView;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.profilePicReceiver);
            msgTextView = itemView.findViewById(R.id.receiverText);
            timeStampTextView = itemView.findViewById(R.id.receiverTimeStamp);
        }
    }

    class senderMoney extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView msgTextView, timeStampTextView;

        public senderMoney(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.profilePicSender);
            msgTextView = itemView.findViewById(R.id.senderMoney);
            timeStampTextView = itemView.findViewById(R.id.senderTimeStamp);
        }
    }
    class receiverMoney extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView msgTextView, timeStampTextView;

        public receiverMoney(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.profilePicReceiver);
            msgTextView = itemView.findViewById(R.id.receiverMoney);
            timeStampTextView = itemView.findViewById(R.id.receiverTimeStamp);
        }
    }

    public static String getTimeDate(long timestamp){
        try{
            Date netDate = (new Date(timestamp));
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }
}
