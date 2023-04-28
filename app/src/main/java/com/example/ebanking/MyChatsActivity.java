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
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MyChatsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;

    ArrayList<Users> userArrayList;
    String senderUid;
    ProgressBar progressBar;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // this will make the back button visible
        }
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        toolbar = findViewById(R.id.toolbar); // custom toolbar
        setSupportActionBar(toolbar);

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        senderUid = auth.getUid();

        DatabaseReference chatReference = database.getReference()
                .child("AppUser")
                .child(senderUid)
                .child("MyChats");
        DatabaseReference userReference = database.getReference()
                .child("AppUser");

        userArrayList = new ArrayList<>();

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                SortedMap<String, Long> mapReceiverTime = new TreeMap<>();
                SortedMap<Long, Users> mapTimeUser = new TreeMap<>(new Comparator<Long>() {
                    public int compare(Long a, Long b)
                    {
                        return b.compareTo(a);
                    }
                });

                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    String receiverKey = dataSnapshot.getKey();
                    Long timeStamp = dataSnapshot.child("LastMessageTimeStamp").getValue(Long.class);
                    if(timeStamp == null)
                    {
                        // Data changed but not completely, so not do anything right now
                        return;
                    }
                    // Storing the IDs of User with whom the Current User Has Done Chats
                    mapReceiverTime.put(receiverKey, timeStamp);
                }

                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userArrayList.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren())
                        {
                            String receiverKey = dataSnapshot.getKey();
                            if(mapReceiverTime.containsKey(receiverKey))
                            {
                                Long timeStamp = mapReceiverTime.get(receiverKey);
                                Users user = dataSnapshot.child("UserData").getValue(Users.class);
                                user.setTimeStamp(timeStamp);
                                mapTimeUser.put(timeStamp, user);
                            }

                        }

                        for (Map.Entry mapElement : mapTimeUser.entrySet()) {

                            // Finding the value
                            Users user = (Users) mapElement.getValue();
                            userArrayList.add(user);
                        }

                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
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


        adapter = new com.example.ebanking.UserAdapter(MyChatsActivity.this, userArrayList);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainUserRecyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.opt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.searchImg)
        {
            Intent intent = new Intent(MyChatsActivity.this, com.example.ebanking.Search.class);
            startActivity(intent);
        }
        else if(itemId == R.id.settings)
        {
            startActivity(new Intent(MyChatsActivity.this, com.example.ebanking.Setting.class));
        }
        else if(itemId == R.id.logout)
        {

                AlertDialog.Builder logout = new AlertDialog.Builder(MyChatsActivity.this);
                logout.setTitle("Logout?");
                logout.setIcon(R.drawable.baseline_logout_24);
                logout.setMessage("Are you sure you want to Logout?");

                // positive, negative and neutral here is related to positions only
                logout.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // logout
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MyChatsActivity.this, login.class);
                        startActivity(intent);
                        finish();
                    }
                });

                logout.setNegativeButton("No", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                logout.show(); // very IMPORTANT

        }
        else //if(itemId == android.R.id.home)  //this is for back button
        {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
