package com.example.ebanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PayToUser extends AppCompatActivity {

    Users sender, receiver;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String senderUid, receiverUid, senderRoom, receiverRoom;
    ImageView profilePic;
    TextView userName, email, pay;
    EditText amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_to_user);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        senderUid = getIntent().getStringExtra("SenderUid");
        receiverUid = getIntent().getStringExtra("ReceiverUid");
        profilePic = findViewById(R.id.profilePic);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        amount = findViewById(R.id.amount);
        pay = findViewById(R.id.pay);

        senderRoom = receiverUid;
        receiverRoom = senderUid;

        DatabaseReference senderReference = database.getReference()
                .child("AppUser")
                .child(senderUid);

        DatabaseReference receiverReference = database.getReference()
                .child("AppUser")
                .child(receiverUid);

        receiverReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users receiverData = snapshot.child("UserData").getValue(Users.class);
                Picasso.get().load(receiverData.profilePic).into(profilePic);
                userName.setText(receiverData.userName);
                email.setText(receiverData.email);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = amount.getText().toString();
                if(TextUtils.isEmpty(money))
                {
                    Toast.makeText(PayToUser.this, "Payment must be at least ₹1", Toast.LENGTH_SHORT).show();
                    return;
                }

                double moneyDouble = Double.parseDouble(money);
                Date date = new Date();
                long timestamp = date.getTime();
                MsgModel messageModel = new MsgModel("", senderUid, timestamp);
                messageModel.setAmount(moneyDouble);
                senderReference // In chats
                        .child("MyChats")
                        .child(senderRoom)
                        .child("Messages")
                        .push().setValue(messageModel) // saving transaction message in sender room
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                receiverReference
                                        .child("MyChats")
                                        .child(receiverRoom)
                                        .child("Messages")
                                        .push().setValue(messageModel)  // saving transaction message in receiver room
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

                                            }
                                        });
                            }
                        });


                TransactionModel transactionModel = new TransactionModel(moneyDouble, senderUid, timestamp);
                senderReference // In MyTransactions
                        .child("MyTransactions")
                        .push().setValue(transactionModel) // saving transaction message in sender room
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                receiverReference
                                        .child("MyTransactions")
                                        .push().setValue(transactionModel)  // saving transaction message in receiver room
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                finish();

                                            }
                                        });
                            }
                        });

            }
        });
    }
}