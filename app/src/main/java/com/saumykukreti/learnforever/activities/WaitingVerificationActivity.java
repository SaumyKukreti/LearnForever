package com.saumykukreti.learnforever.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.events.InitializationCompleteEvent;
import com.saumykukreti.learnforever.jobs.DataInitializerJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WaitingVerificationActivity extends Activity {


    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_verification);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        initialiseViews();
    }

    /**
     *  This method initialises all the views
     */
    private void initialiseViews() {
        Button resendEmailButton = findViewById(R.id.button_resend_email);
        Button continueButton = findViewById(R.id.button_continue);

        resendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.sendEmailVerification();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.reload();
                //Wait for 5 seconds to load and check
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mUser.isEmailVerified()){
                            LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DataInitializerJob(WaitingVerificationActivity.this, null));
                        }
                        else{
                            //User still not verified
                            Toast.makeText(WaitingVerificationActivity.this, "Verification not complete, please open the link given on the email!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 5000);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(InitializationCompleteEvent event) {
        startActivity(new Intent(this, NavigationDrawerActivity.class));
        this.finish();
    }
}
