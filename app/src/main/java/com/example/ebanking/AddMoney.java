package com.example.ebanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class AddMoney extends AppCompatActivity {

    Users user;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String senderUid, receiverUid;

    TextView pay;
    EditText amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        senderUid = auth.getUid();
        receiverUid = senderUid;
        amount = findViewById(R.id.amountTxtView);
        pay = findViewById(R.id.addMoneyBtn);

        DatabaseReference senderReference = database.getReference()
                .child("AppUser")
                .child(senderUid);


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = amount.getText().toString();
                if (TextUtils.isEmpty(money)) {
                    Toast.makeText(getApplicationContext(), "Payment must be at least ₹1", Toast.LENGTH_SHORT).show();
                    return;
                }

                double moneyDouble = Double.parseDouble(money);
                Date date = new Date();
                long timestamp = date.getTime();

                senderReference
                        .child("AccountBalance")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double senderActBal = snapshot.getValue(double.class);


                                TransactionModel transactionModel = new TransactionModel(moneyDouble, senderUid, receiverUid, timestamp);
                                senderReference // In MyTransactions
                                        .child("MyTransactions")
                                        .push().setValue(transactionModel) // saving transaction message in sender room
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // Adding Money To Account
                                                database.getReference()
                                                        .child("AppUser").child(senderUid)
                                                        .child("AccountBalance")
                                                        .setValue(senderActBal + moneyDouble).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Intent intent = new Intent(getApplicationContext(), PopUpTransactionActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                                // Adding Money To Account



                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }


        });


    }
}