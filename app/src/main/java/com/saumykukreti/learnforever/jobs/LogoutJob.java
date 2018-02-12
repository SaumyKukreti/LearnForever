package com.saumykukreti.learnforever.jobs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.firebase.auth.FirebaseAuth;
import com.saumykukreti.learnforever.activities.LoginActivity;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.events.InitializationCompleteEvent;
import com.saumykukreti.learnforever.events.LogoutCompleteEvent;
import com.saumykukreti.learnforever.util.Utility;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by saumy on 2/10/2018.
 */

public class LogoutJob extends Job {

    private final Context mContext;
    private SharedPreferences mPreference;
    private ReminderDataController mReminderDataController;
    private NoteDataController mNoteDataController;

    public LogoutJob(Context context, Params params) {
        super(new Params(1).setRequiresNetwork(true));
        mContext = context;
    }

    private void logoutFromFirebase() {
        FirebaseAuth.getInstance().signOut();

    }


    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        mPreference = getApplicationContext().getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        mReminderDataController = ReminderDataController.getInstance(mContext);
        mNoteDataController = NoteDataController.getInstance(mContext);
        //mUserId = mPreference.getString(Constants.LEARN_FOREVER_PREFERENCE_USER_ID, "");

        //Initiate logout only if network is available
        if(Utility.isNetworkAvailable(mContext)){
            logoutFromFirebase();

            //Clear preferences
            mPreference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST,"").apply();

            //Clearing user id
            mPreference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_USER_ID,"").apply();

            //Clear note database
            mReminderDataController.deleteAllRecords();

            //Clear reminder database
            mNoteDataController.deleteAllRecords();

            //Tell back the starting activity to open login activity
            EventBus.getDefault().post(new LogoutCompleteEvent());

        }
        else{
            Toast.makeText(mContext, "Logout needs network access!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
