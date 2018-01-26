package com.saumykukreti.learnforever.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
    private ViewGroup mRootView;

    private int currentScene = 1;

    private final int SCENE_LOGIN = 1;
    private final int SCENE_SIGN_UP = 2;
    private final int SCENE_TIP = 3;
    private EditText mConfirmPasswordEditText;
    private String mCurrentEmailAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialiseViewsAndOnclicks();
        initialiseGoogleSignIn();
    }

    private void startTransition(int sceneName, @Nullable Transition transition, int duration) {
        if (mEmailEditText != null) {
            mCurrentEmailAddress = mEmailEditText.getText().toString();
        }
        currentScene = sceneName;

        Scene scene = null;
        if (sceneName == SCENE_LOGIN) {
            scene = Scene.getSceneForLayout(mRootView, R.layout.login_sign_in_layout, this);
        } else if (sceneName == SCENE_SIGN_UP) {
            scene = Scene.getSceneForLayout(mRootView, R.layout.login_sign_up_layout, this);
        } else if (sceneName == SCENE_TIP) {
            scene = Scene.getSceneForLayout(mRootView, R.layout.tip_layout, this);
        }

        if (transition == null) {
            //Default
            transition = new Fade();
            transition.setDuration(duration);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    initialiseViewsAndOnclicks();
                    if (mEmailEditText != null) mEmailEditText.setText(mCurrentEmailAddress);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
        }

        if (scene != null) {
            TransitionManager.go(scene, transition);
        } else {
            Log.e(TAG, "Scene is null");
        }
    }

    /**
     * This method shows the top layout
     */
    private void toggleTipLayout(boolean show) {

        if (show) {
            startTransition(SCENE_TIP, null, 500);
        } else {
            startTransition(SCENE_LOGIN, null, 500);
        }
    }

    private void initialiseViewsAndOnclicks() {
        //Setting on click listeners on sign in button
        mRootView = findViewById(R.id.frame_container);

        if (currentScene == SCENE_LOGIN) {
            mEmailEditText = findViewById(R.id.edit_text_email);
            mPasswordEditText = findViewById(R.id.edit_text_password);


            final Button signInButton = findViewById(R.id.scene_login_button_sign_in);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Initiate sign in using firebase auth
                    if (validateFields()) {
                        toggleTipLayout(true);
                        initiateFirebaseSignIn();
                    }
                }
            });

            mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    signInButton.performClick();
                    return false;
                }
            });

            //Setting on click listeners on sign up button
            findViewById(R.id.scene_login_button_sign_up).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTransition(SCENE_SIGN_UP, null, 500);
                }
            });
            findViewById(R.id.button_google_sign_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
        } else if (currentScene == SCENE_SIGN_UP) {

            mEmailEditText = findViewById(R.id.edit_text_email);
            mPasswordEditText = findViewById(R.id.edit_text_password);
            mConfirmPasswordEditText = findViewById(R.id.edit_text_confirm_password);

            findViewById(R.id.scene_signup_button_sign_up).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateFields()) {
                        initiateSignUp();
                    }
                }
            });
        } else if (currentScene == SCENE_TIP) {

        }
    }


    /**
     * This method tries to login using the credentials given
     */
    private void initiateFirebaseSignIn() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //Check if sign in is using firebase only or through google
        Task<AuthResult> authResult = null;
        if (mGoogleSignInTask != null) {
            //Through google sign in
            GoogleSignInAccount account = mGoogleSignInTask.getResult();
            AuthCredential googleCredentials = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            authResult = mAuth.signInWithCredential(googleCredentials);
        } else {
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
     * This method saves the sign in method in shared preference and starts data initializer job
     */
    private void startDataInitialiserJob() {

        //Saving the sign in method in preferences
        SharedPreferences preference = getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);

        if (mGoogleSignInTask == null) {
            //Firebase sign in
            preference.edit().putInt(Constants.LEARN_FOREVER_PREFERENCE_SIGN_IN_METHOD, Constants.SIGN_IN_METHOD_FIREBASE_SIGN_IN).apply();
        } else {
            //Google sign in
            preference.edit().putInt(Constants.LEARN_FOREVER_PREFERENCE_SIGN_IN_METHOD, Constants.SIGN_IN_METHOD_GOOGLE_SIGN_IN).apply();
        }

        //In case of google sign in, this field is always true
        if (mFireBaseUser.isEmailVerified()) {
            LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DataInitializerJob(this, null));
        } else {
            Toast.makeText(this, "Email verification pending", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method displays invalid credentials error message
     */
    private void showInvalidCredentialsError() {

        toggleTipLayout(false);

        //TODO - show a dialog box instead
        Toast.makeText(this, "Invalid Credentials, please try again", Toast.LENGTH_SHORT).show();
    }


    /**
     * This method does validation on fields email and password and return true if ok else return false
     *
     * @return
     */
    private boolean validateFields() {
        if (currentScene == SCENE_LOGIN) {
            if (mEmailEditText.getText().length() > 0 && mPasswordEditText.getText().length() > 5) {
                return true;
            } else {
                boolean emailFlag = false;
                boolean passwordFlag = false;

                //Showing the user what went wrong
                if (mEmailEditText.getText().length() == 0) {
                    mEmailEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.background_white_with_red_borders));
                    emailFlag = true;
                } else {
                    mEmailEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.background_white_with_rounded_corders));
                }
                if (mPasswordEditText.getText().length() < 6) {
                    mPasswordEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.background_white_with_red_borders));
                    passwordFlag = true;
                } else {
                    mPasswordEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.background_white_with_rounded_corders));
                }

                if (emailFlag && passwordFlag) {
                    Toast.makeText(this, "Please enter the email address and password to sign in or press the google sign in button to sign in with your google account!", Toast.LENGTH_SHORT).show();
                } else if (emailFlag) {
                    Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (passwordFlag) {
                    Toast.makeText(this, "The length of the password should be 6 or greater", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (currentScene == SCENE_SIGN_UP) {
            if (mEmailEditText.getText().length() > 0 &&
                    mPasswordEditText.getText().length() > 6 &&
                    (mPasswordEditText.getText().toString().equalsIgnoreCase(mConfirmPasswordEditText.getText().toString()))) {
                return true;
            } else {
                boolean emailFlag = false;
                boolean passwordFlag = false;
                boolean sameCheck = false;



                if (mEmailEditText.getText().length() == 0) {
                    mEmailEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.background_white_with_red_borders));
                    emailFlag = true;
                } else {
                    mEmailEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.background_white_with_rounded_corders));
                }
                if (mPasswordEditText.getText().length() < 6) {
                    mPasswordEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.background_white_with_red_borders));
                    passwordFlag = true;
                } else {
                    mPasswordEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.background_white_with_rounded_corders));
                    mConfirmPasswordEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.background_white_with_rounded_corders));
                }

                if(!mPasswordEditText.getText().toString().contentEquals(mConfirmPasswordEditText.getText().toString())){
                    sameCheck = true;
                }

                if (emailFlag && passwordFlag) {
                    Toast.makeText(this, "Please enter the email address and password to sign in or press the google sign in button to sign in with your google account!", Toast.LENGTH_SHORT).show();
                } else if (emailFlag) {
                    Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (passwordFlag) {
                    Toast.makeText(this, "The length of the password should be 6 or greater", Toast.LENGTH_SHORT).show();
                }
                else if(sameCheck){
                    Toast.makeText(this, "Password and confirm passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
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
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
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


    private void initiateSignUp() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                user.sendEmailVerification();
                            }
                            Intent intent = new Intent(LoginActivity.this, WaitingVerificationActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            //TODO - REMOVE THIS TOAST
                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (currentScene == SCENE_SIGN_UP) {
            startTransition(SCENE_LOGIN, null, 500);
        } else {
            super.onBackPressed();
        }
    }
}
