package com.democracy;

import com.democracy.helper.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
	 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
 
    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
 
        mContext = getApplicationContext();
        
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
            	SharedPreferences prefs = mContext.getSharedPreferences(
						"com.democracy", Context.MODE_PRIVATE);
            	
            	String token = prefs.getString(
						Constants.TOKEN_SP_KEY, null);
            	
            	if(token == null) {
            		Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            		startActivity(i);
            	} else {
            		Intent i = new Intent(SplashActivity.this, MainActivity.class);
            		startActivity(i);
            	}
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}