package com.example.ebanking;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ebanking.MainActivity;
import com.example.ebanking.R;

public class PopUpTransactionActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_transaction);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.phone_pay_sound);
        mediaPlayer.start();

        showPopupScreen();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissPopupScreen();
                mediaPlayer.release();
            }
        },2000);

    }

    private void showPopupScreen() {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pop_animation);

        View popupView = findViewById(R.id.animationView);
        popupView.setVisibility(View.VISIBLE);

        popupView.startAnimation(animation);

    }

    private void dismissPopupScreen() {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pop_animation);


        animation.reset();


        View popupView = findViewById(R.id.animationView);
        popupView.setVisibility(View.GONE);

        popupView.startAnimation(animation);

        finish();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}