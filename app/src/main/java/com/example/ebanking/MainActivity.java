package com.example.ebanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    TextView userName, email;
    ImageView profilePic;
    Users user;
    LinearLayout myAccount, myTransactions, settings, logout;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference userReference;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        profilePic = findViewById(R.id.profilePic);
        myAccount = findViewById(R.id.myAccount);
        myTransactions = findViewById(R.id.myTransactions);
        settings = findViewById(R.id.settings);
        logout = findViewById(R.id.logout);



        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userReference = database.getReference()
                .child("AppUser")
                .child(auth.getUid())
                .child("UserData");

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // this will make the back button visible
            getSupportActionBar().setTitle("");
        }
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);

                Picasso.get().load(user.profilePic).into(profilePic);
                userName.setText(user.userName);
                email.setText(user.email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyAccount.class);
                startActivity(intent);
            }
        });

        myTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyTransactions.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {

                    AlertDialog.Builder logoutDialog = new AlertDialog.Builder(MainActivity.this);
                    logoutDialog.setTitle("Logout?");
                    logoutDialog.setIcon(R.drawable.baseline_logout_24);
                    logoutDialog.setMessage("Are you sure you want to Logout?");

                    // positive, negative and neutral here is related to positions only
                    logoutDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // logout
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getApplicationContext(), login.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    });

                    logoutDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    logoutDialog.show(); // very IMPORTANT

                }

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