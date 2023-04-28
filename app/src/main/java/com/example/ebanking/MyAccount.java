package com.example.ebanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.ref.Reference;

public class MyAccount extends AppCompatActivity {
    Toolbar toolbar;
     ImageView profilePic;
    TextView nameTxtView, emailTxtView, actBalTxtView;
    Button addMoney;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String senderUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        toolbar = findViewById(R.id.toolbar);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        senderUid = auth.getUid();
        profilePic = findViewById(R.id.profilePic);
        nameTxtView = findViewById(R.id.userName);
        emailTxtView = findViewById(R.id.email);
        actBalTxtView = findViewById(R.id.actBal);
        addMoney = findViewById(R.id.addMoney);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // this will make the back button visible
            getSupportActionBar().setTitle("");
        }

        DatabaseReference userReference = database.getReference()
                .child("AppUser").child(senderUid);

        userReference.child("UserData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                Picasso.get().load(user.profilePic).into(profilePic);
                nameTxtView.setText(user.userName);
                emailTxtView.setText(user.email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userReference.child("AccountBalance")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double actBal= snapshot.getValue(Double.class);
                actBalTxtView.setText("₹"+actBal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    addMoney.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), AddMoney.class);
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

}