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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
    RelativeLayout toolbar;

    ImageView profilePic, searchImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats);

        toolbar = findViewById(R.id.toolbar);

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        toolbar = findViewById(R.id.toolbar); // custom toolbar

        profilePic = findViewById(R.id.profilePic);
        searchImg = findViewById(R.id.searchBtn);

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

        userReference.child(senderUid)
                .child("UserData")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if(user == null)
                {
                    return;
                }
                Picasso.get().load(user.profilePic).into(profilePic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                startActivity(intent);
            }
        });

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

}
