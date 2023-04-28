package com.example.ebanking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView myChats, myTransactions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myChats = findViewById(R.id.myChats);
        myTransactions = findViewById(R.id.myTransactions);

        myChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyChatsActivity.class);
                startActivity(intent);
            }
        });

        myTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "My Transactions Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}