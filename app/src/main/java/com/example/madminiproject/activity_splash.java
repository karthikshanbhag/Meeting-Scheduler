package com.example.madminiproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class activity_splash extends AppCompatActivity {

    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoImageView = findViewById(R.id.logoImageView);

        // Load the animation from the XML file
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                logoImageView.setVisibility(ImageView.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, start the next activity or perform any other action
                // For example, you can start the MainActivity using an Intent
                Intent intent = new Intent(activity_splash.this, MainActivity.class);
                startActivity(intent);

                // Finish the splash activity to prevent going back to it
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Not needed in this case
            }
        });

        // Start the animation
        logoImageView.startAnimation(animation);
    }
}