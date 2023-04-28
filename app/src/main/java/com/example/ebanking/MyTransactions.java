package com.example.ebanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyTransactions extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    RecyclerView myTransactionsRecyclerView;
    MyTransactionsAdapter adapter;
    Map<String, Users> mapToUser;
    ArrayList<TransactionViewDetail> transactionArrayList;
    String senderUid;
    ProgressBar progressBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_transactions);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // this will make the back button visible
        }
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        myTransactionsRecyclerView = findViewById(R.id.myTransactionsRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        senderUid = auth.getUid();

        DatabaseReference transactionReference = database.getReference()
                .child("AppUser")
                .child(senderUid)
                .child("MyTransactions");
        DatabaseReference userReference = database.getReference()
                .child("AppUser");

        transactionArrayList = new ArrayList<>();
        mapToUser = new HashMap<String, Users>();

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("CRASHED1", "USERREFERENCE");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    Users user = dataSnapshot.child("UserData").getValue(Users.class);

                    mapToUser.put(key, user);
                }

                transactionReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("CRASHED2", "TRANSACTIONREFERENCE");
                        progressBar.setVisibility(View.VISIBLE);
                        transactionArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            TransactionModel transactionModel = dataSnapshot.getValue(TransactionModel.class);
                            String transactionSender = transactionModel.senderId, transactionReceiver = transactionModel.receiverId;
                            TransactionViewDetail detail = new TransactionViewDetail();
                            if (transactionSender.equals(transactionReceiver)) {// ADDED BY SELF
                                Users user = mapToUser.get(transactionSender);
                                detail.name = "Added to Account";
                                detail.email = user.email;
                                detail.profilePic = user.profilePic;
                                detail.amount = transactionModel.amount;
                                detail.timeStamp = transactionModel.timeStamp;
                                detail.sign = '+';
                            } else if (senderUid.equals(transactionReceiver)) { // RECEIVED MONEY
                                Users user = mapToUser.get(transactionSender);
                                detail.name = user.userName;
                                detail.email = user.email;
                                detail.profilePic = user.profilePic;
                                detail.amount = transactionModel.amount;
                                detail.timeStamp = transactionModel.timeStamp;
                                detail.sign = '+';
                            } else {   // SENT MONEY
                                Users user = mapToUser.get(transactionReceiver);
                                detail.name = user.userName;
                                detail.email = user.email;
                                detail.profilePic = user.profilePic;
                                detail.amount = transactionModel.amount;
                                detail.timeStamp = transactionModel.timeStamp;
                                detail.sign = '-';
                            }

                            transactionArrayList.add(detail);
                        }
                        Collections.reverse(transactionArrayList); //For showing transactions in latest to oldest order
                        progressBar.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new MyTransactionsAdapter(MyTransactions.this, transactionArrayList);
        myTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myTransactionsRecyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }


}