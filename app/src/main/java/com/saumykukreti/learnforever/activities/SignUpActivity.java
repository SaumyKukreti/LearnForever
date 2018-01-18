package com.saumykukreti.learnforever.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.jobs.DataInitializerJob;

public class SignUpActivity extends Activity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        intialiseViews();
    }

    /**
     *  This method initailises all the views and sets on click listeners on them
     */
    private void intialiseViews() {

        mEmailEditText = findViewById(R.id.edit_text_email);
        mPasswordEditText = findViewById(R.id.edit_text_password);
        mConfirmPasswordEditText = findViewById(R.id.edit_text_confirm_password);

        findViewById(R.id.button_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validate fields
                boolean validationPassed = validateFields();
                if(validationPassed){
                    initiateSignUp();
                }else{
                    displayError();
                }
            }
        });
    }

    /**
     *  This method displays error if any field is missing or passwords do not match
     */
    private void displayError() {
        Toast.makeText(this, "Validation failed", Toast.LENGTH_SHORT).show();
        //TODO
    }

    /**
     *  This method validates the fields
     * @return
     */
    private boolean validateFields() {
        if(mEmailEditText.getText().length() == 0 || mPasswordEditText.getText().length() == 0 || mConfirmPasswordEditText.getText().length() == 0){
            return false;
        }

        if(!mPasswordEditText.getText().toString().contentEquals(mPasswordEditText.getText().toString())){
            //Passwords do not match
            return false;
        }
        return true;
    }

    private void initiateSignUp() {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(mEmailEditText.getText().toString(), mConfirmPasswordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user!=null) {
                                user.sendEmailVerification();
                            }
                            startActivity( new Intent(SignUpActivity.this, WaitingVerificationActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            //TODO - REMOVE THIS TOAST
                            Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}
