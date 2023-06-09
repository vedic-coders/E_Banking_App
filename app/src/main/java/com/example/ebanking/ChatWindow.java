package com.example.ebanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWindow extends AppCompatActivity {

    public static String receiverImg, senderImg;
    String receiverName, receiverEmail, receiverUid, senderUid, receiverStatus;
    CircleImageView profilePic;
    TextView receiverNameTopBar, payBtn;
    CardView sendBtn;
    EditText textMsg;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String senderRoom, receiverRoom;
    RecyclerView recyclerView;
    ArrayList<com.example.ebanking.MsgModel> messageArrayList;
    MessageAdapter messageAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // this will make the back button visible
        }

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        receiverName = getIntent().getStringExtra("userName");
        receiverEmail = getIntent().getStringExtra("email");
        receiverImg = getIntent().getStringExtra("receiverImg");
        receiverUid = getIntent().getStringExtra("uid");

        messageArrayList = new ArrayList<>();
        payBtn = findViewById(R.id.pay);
        sendBtn = findViewById(R.id.sendBtn);
        textMsg = findViewById(R.id.writeMsg);
        receiverNameTopBar = findViewById(R.id.receiverName);
        profilePic = findViewById(R.id.profilePic);

        recyclerView = findViewById(R.id.recyclerView); // Recycler view class

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        messageAdapter = new com.example.ebanking.MessageAdapter(ChatWindow.this, messageArrayList);
        recyclerView.setAdapter(messageAdapter);

        Picasso.get().load(receiverImg).into(profilePic); // for setting big image
        receiverNameTopBar.setText(receiverName);

        senderUid = auth.getUid();
        senderRoom = receiverUid; // Using other person's ID as Room
        receiverRoom = senderUid;

        DatabaseReference reference = database.getReference()
                .child("AppUser")
                .child(senderUid)
                .child("UserData");

        DatabaseReference chatReferenceSender = database.getReference()
                .child("AppUser")
                .child(senderUid)
                .child("MyChats")
                .child(senderRoom)
                .child("Messages");

        DatabaseReference chatReferenceReceiver = database.getReference()
                .child("AppUser")
                .child(receiverUid)
                .child("MyChats")
                .child(receiverRoom)
                .child("Messages");


        chatReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    com.example.ebanking.MsgModel messages = dataSnapshot.getValue(com.example.ebanking.MsgModel.class);
                    messageArrayList.add(messages);
                }
                linearLayoutManager.scrollToPosition(messageArrayList.size() - 1);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            // For getting Sender Photo
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilePic").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textMsg.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    textMsg.setError("Can't send empty message");
                    return;
                }

                textMsg.setText("");
                Date date = new Date();
                long timestamp = date.getTime();
                MsgModel messageModel = new MsgModel(message, senderUid, timestamp);
                chatReferenceSender.push().setValue(messageModel) // saving message in sender room
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                chatReferenceReceiver.push().setValue(messageModel)  // saving receiver in sender room
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // below code for storing lastMessageTimeStamp
                                                database.getReference()
                                                        .child("AppUser")
                                                        .child(senderUid)
                                                        .child("MyChats")
                                                        .child(senderRoom)
                                                        .child("LastMessageTimeStamp")
                                                        .setValue(timestamp)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                database.getReference()
                                                                        .child("AppUser")
                                                                        .child(receiverUid)
                                                                        .child("MyChats")
                                                                        .child(receiverRoom)
                                                                        .child("LastMessageTimeStamp")
                                                                        .setValue(timestamp)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                            }
                                                                        });
                                                            }
                                                        });

                                                // below code for storing Recent Activity Of Users Sending Messages
                                                database.getReference()
                                                        .child("AppUser")
                                                        .child(senderUid)
                                                        .child("RecentActivity")
                                                        .setValue(timestamp)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                // Might not be same for receiver
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });


            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to Profile Photo View Activity
                Intent intent = new Intent(getApplicationContext(), com.example.ebanking.UserDetails.class);
                intent.putExtra("User", receiverName);
                intent.putExtra("ProfilePicImg", receiverImg);
                intent.putExtra("Email", receiverEmail);
                intent.putExtra("Status", receiverStatus);
                startActivity(intent);
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to Profile Photo View Activity
                Intent intent = new Intent(getApplicationContext(), PayToUser.class);
                intent.putExtra("SenderUid", senderUid);
                intent.putExtra("ReceiverUid", receiverUid);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //if(itemId == android.R.id.home)  //this is for back button
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(ChatWindow.this, MyChatsActivity.class);
//        startActivity(intent);
        finish();
    }

    public void topBackPressed(View view)
    {
        onBackPressed();
    }
}