package com.example.ebanking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Register extends AppCompatActivity {

    TextView loginHere;
    EditText userName, email, password, password2;
    Button signupBtn;
    ImageView profilePic;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri;
    String imageUriStr;
   // ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        loginHere = findViewById(R.id.loginHere);
        userName = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.confirm_password);
        signupBtn = findViewById(R.id.signupBtn);
        profilePic = findViewById(R.id.profilePic);
      //  progressBar = findViewById(R.id.progressBar);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 10);
            }
        });

        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = email.getText().toString().trim();
                String userNameStr = userName.getText().toString().trim();
                String passwordStr = password.getText().toString().trim();
                String password2Str = password2.getText().toString().trim();


                if(TextUtils.isEmpty(emailStr) || TextUtils.isEmpty(userNameStr) || TextUtils.isEmpty(passwordStr) || TextUtils.isEmpty(password2Str))
                {
                    Toast.makeText(Register.this, "Valid Field Missing", Toast.LENGTH_SHORT).show();
                }
                else if(passwordStr.length() < 6)
                {
                    password.setError("Password must be at least 6 characters");
                }
                else if(!passwordStr.equals(password2Str))
                {
                    password2.setError("Password does not match");
                }
                else
                {
                   // progressBar.setVisibility(View.VISIBLE);
                    auth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference databaseReference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("upload").child(id);

                                if(imageUri != null)
                                {
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageUriStr = uri.toString();
                                                    }
                                                });
                                            }
                                            else {
                                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    imageUriStr = "https://firebasestorage.googleapis.com/v0/b/e-banking-21b68.appspot.com/o/userDefaultImage.png?alt=media&token=0edd4309-dc04-4a0a-8e32-be5284e6e8c2";
                                }
                                Users user = new Users(id, emailStr, userNameStr, passwordStr, imageUriStr);
                                databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Intent intent = new Intent(Register.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(Register.this, "Error in creating user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                         //   progressBar.setVisibility(View.GONE);
                        }

                    });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10)
        {
            if(data != null)
            {
                imageUri = data.getData();
                profilePic.setImageURI(imageUri);
            }
        }
    }
}