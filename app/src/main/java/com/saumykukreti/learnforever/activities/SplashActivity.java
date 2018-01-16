package com.saumykukreti.learnforever.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Space;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.constants.Constants;

public class SplashActivity extends Activity implements GoogleApiClient.ConnectionCallbacks{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Check if the user is already signed in, if yes take the user to the navigation drawer activity else take it to sign in activity

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preference = getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
                String userId = preference.getString(Constants.LEARN_FOREVER_PREFERENCE_USER_ID,"");

                if(!userId.isEmpty()){
                    startActivity(new Intent(SplashActivity.this,NavigationDrawerActivity.class));
                }
                else{
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                }

                SplashActivity.this.finish();
            }
        }, 500);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
