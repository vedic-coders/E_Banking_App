package com.example.ebanking;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyTransactionsAdapter extends RecyclerView.Adapter<MyTransactionsAdapter.viewHolder>{


    MyTransactions myTransactions;
    ArrayList<TransactionViewDetail> transactionArrayList;
    public MyTransactionsAdapter(MyTransactions myTransactions, ArrayList<TransactionViewDetail> transactionArrayList) {
        this.myTransactions = myTransactions;
        this.transactionArrayList = transactionArrayList;
    }

    @NonNull
    @Override
    public MyTransactionsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myTransactions).inflate(R.layout.transaction_view,parent,false);
        return new MyTransactionsAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTransactionsAdapter.viewHolder holder, int position) {
        TransactionViewDetail detail = transactionArrayList.get(position);
        holder.userName.setText(detail.name);
        holder.email.setText(detail.email);  // CHANGE -- setting status on email
        Picasso.get().load(detail.profilePic).into(holder.profilePic);
        holder.timeStamp.setText(getTimeDate(detail.timeStamp));
        holder.amount.setText(detail.sign + "₹" +detail.amount);
        if(detail.sign == '+')
        {
            // Green
            holder.amount.setTextColor(Color.parseColor("#019508"));
        }
        else
        {
            // Red
            holder.amount.setTextColor(Color.parseColor("#EA0505"));
        }

    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePic;
        TextView userName, email, timeStamp, amount;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePic);
            userName = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.email);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            amount = itemView.findViewById(R.id.amount);

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
