package com.example.ebanking;

import android.content.Intent;
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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder> {
    MyChatsActivity myChatsActivity;
    ArrayList<Users> userArrayList;

    public UserAdapter(MyChatsActivity myChatsActivity, ArrayList<Users> userArrayList) {
        this.myChatsActivity = myChatsActivity;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myChatsActivity).inflate(R.layout.user_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewHolder holder, int position) {
        Users user = userArrayList.get(position);
        holder.userName.setText(user.userName); // setting user name on recycler view
        holder.email.setText(user.email);  // CHANGE -- setting status on email
        Picasso.get().load(user.profilePic).into(holder.profilePic);
        holder.timeStamp.setText(getTimeDate(user.timeStamp));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myChatsActivity, com.example.ebanking.ChatWindow.class);
                intent.putExtra("userName", user.getUserName());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("receiverImg", user.getProfilePic());
                intent.putExtra("uid", user.getUserId());
                myChatsActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePic;
        TextView userName, email, timeStamp;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePic);
            userName = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.email);
            timeStamp = itemView.findViewById(R.id.timeStamp);

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
