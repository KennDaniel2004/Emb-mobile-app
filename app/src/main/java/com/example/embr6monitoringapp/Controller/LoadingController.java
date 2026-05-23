package com.example.embr6monitoringapp.Controller;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.embr6monitoringapp.Controller.GeneralinfoController;
import com.example.embr6monitoringapp.R;

public class LoadingController extends AppCompatActivity {

    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.loadingframe);

        animationView = findViewById(R.id.logoAnimation);

        animationView.playAnimation();

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(LoadingController.this, LoginController.class);
            startActivity(intent);
            finish();

        }, 10000);
    }
}