package com.saumykukreti.learnforever.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.events.InitializationCompleteEvent;
import com.saumykukreti.learnforever.jobs.DataInitializerJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class LoginActivity extends Activity {

    private static final int RC_SIGN_IN = 1010;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private GoogleSignInClient mGoogleSignInClient;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Task<GoogleSignInAccount> mGoogleSignInTask;
    private boolean mCreateAccount;
    private FirebaseUser mFireBaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialiseViews();
        initialiseGoogleSignIn();
    }

    /**
     * This method initialises all the views in the activity
     */
    private void initialiseViews() {

        mEmailEditText = findViewById(R.id.edit_text_email);
        mPasswordEditText = findViewById(R.id.edit_text_password);

        //Setting on click listeners on sign in button
        findViewById(R.id.button_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initiate sign in using firebase auth
                boolean validationPassed = validateFields();

                if(validationPassed){
                    initiateFirebaseSignIn();
                }
                else{
                    displayRequiredFilds();
                }
            }
        });

        //Setting on click listeners on sign up button
        findViewById(R.id.button_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    /**
     *  This method changes the drawable of the field that the user left incomplete
     */
    private void displayRequiredFilds() {
        Toast.makeText(this, "Validation failed", Toast.LENGTH_SHORT).show();

        if(mEmailEditText.getText().length()==0){
            //TODO
        }

        if(mPasswordEditText.getText().length()==0){
            //TODO
        }
    }

    /**
     *  This method tries to login using the credentials given
     */
    private void initiateFirebaseSignIn() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //Check if sign in is using firebase only or through google
        Task<AuthResult> authResult = null;
        if(mGoogleSignInTask!=null){
            //Through google sign in
            GoogleSignInAccount account = mGoogleSignInTask.getResult();
            AuthCredential googleCredentials = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            authResult = mAuth.signInWithCredential(googleCredentials);
        }else{
            authResult = mAuth.signInWithEmailAndPassword(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
        }

        //On completion listener
        authResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            mFireBaseUser = mAuth.getCurrentUser();

                            //TODO - REMOVE THIS
                            Toast.makeText(LoginActivity.this, "Login complete", Toast.LENGTH_SHORT).show();

                            //Sign in complete, initiate login procedure
                            startDataInitialiserJob();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            // Show appropriate error
                            showInvalidCredentialsError();
                        }
                    }
                });
    }

    /**
     *  This method saves the sign in method in shared preference and starts data initializer job
     */
    private void startDataInitialiserJob() {

        //Saving the sign in method in preferences
        SharedPreferences preference = getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);

        if(mGoogleSignInTask==null){
            //Firebase sign in
            preference.edit().putInt(Constants.LEARN_FOREVER_PREFERENCE_SIGN_IN_METHOD, Constants.SIGN_IN_METHOD_FIREBASE_SIGN_IN).apply();
        }
        else{
            //Google sign in
            preference.edit().putInt(Constants.LEARN_FOREVER_PREFERENCE_SIGN_IN_METHOD, Constants.SIGN_IN_METHOD_GOOGLE_SIGN_IN).apply();
        }

        //In case of google sign in, this field is always true
        if(mFireBaseUser.isEmailVerified()) {
            LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DataInitializerJob(this, null));
        }else{
            Toast.makeText(this, "Email verification pending", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  This method displays invalid credentials error message
     */
    private void showInvalidCredentialsError() {
        //TODO - show a dialog box instead
        Toast.makeText(this, "Invalid Credentials, please try again", Toast.LENGTH_SHORT).show();
    }


    /**
     *  This method does validation on fields email and password and return true if ok else return false
     * @return
     */
    private boolean validateFields() {
        if(mEmailEditText.getText().length()>0 && mPasswordEditText.getText().length()>0){
            return true;
        }
        return false;
    }

    /**
     * This method initialises google sign
     */
    private void initialiseGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.button_google_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }


    // Start google sign in
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            mGoogleSignInTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            initiateFirebaseSignIn();
        }
    }

    @Subscribe
    public void onMessageEvent(InitializationCompleteEvent event) {
        startActivity(new Intent(this, NavigationDrawerActivity.class));
        this.finish();
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
}
