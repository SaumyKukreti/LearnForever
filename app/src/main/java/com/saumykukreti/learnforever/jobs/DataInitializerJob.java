
package com.saumykukreti.learnforever.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.events.InitializationCompleteEvent;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.util.DateHandler;
import com.saumykukreti.learnforever.util.TextCreator;
import com.saumykukreti.learnforever.util.Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by saumy on 12/17/2017.
 */

public class DataInitializerJob extends Job {

    private static final int PRIORITY = 1;
    private static final String TAG = DataInitializerJob.class.getSimpleName();
    private final Context mContext;
    private NoteDataController mDataController;
    private SharedPreferences mPreference;

    public DataInitializerJob(Context context, Params params) {
        super(new Params(PRIORITY).requireNetwork());
        mContext = context;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        mDataController = NoteDataController.getInstance(mContext);

        //Getting id
        intialisePreferenceSettings();

        //Initialising alarm
        Utility.setNotification(mContext);

        String userId = "";
        FirebaseAuth user = FirebaseAuth.getInstance();
        userId = (user!=null) ? user.getUid() : "";

        if(userId!=null && !userId.isEmpty()) {
            //Saving the user id in preference
            Utility.saveStringInPreference(mContext,Constants.LEARN_FOREVER_PREFERENCE_USER_ID, userId);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(userId);
            myRef.child("Notes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<NoteTable> listOfNotes = new ArrayList<>();
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            NoteTable note = data.getValue(NoteTable.class);
                            listOfNotes.add(note);
                        }
                        mDataController.newNotes(listOfNotes);
                    }
                    EventBus.getDefault().post(new InitializationCompleteEvent());

                    //Initialisation of note table complete, initialising reminder table in the background
                    if (!listOfNotes.isEmpty()) {
                        LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new ReminderJob(mContext, listOfNotes, null));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        else{
            //TODO - Handle this
        }
    }

    private void intialisePreferenceSettings() {
        //Initialising preference values, by default setting all the fields to true
        Utility.saveBooleanInPreference(mContext,Constants.LEARN_FOREVER_PREFERENCE_TITLE_SETTINGS, true);
        Utility.saveBooleanInPreference(mContext,Constants.LEARN_FOREVER_PREFERENCE_CIS_SETTINGS, true);
        Utility.saveBooleanInPreference(mContext,Constants.LEARN_FOREVER_PREFERENCE_CATEGORY_SETTINGS, true);
        Utility.saveIntInPreference(mContext,Constants.LEARN_FOREVER_PREFERENCE_SPEECH_RATE, 10);
        Utility.saveStringInPreference(mContext,Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL, TextCreator.getIntervalText(Constants.DAY_INTERVAL_ONE));
        Utility.saveStringInPreference(mContext,Constants.LEARN_FOREVER_PREFERENCE_LAST_REVISE_DATE, DateHandler.convertDateToString(new Date()));
        String intervalOne = Arrays.toString(Constants.DAY_INTERVAL_ONE).substring(1,Arrays.toString(Constants.DAY_INTERVAL_ONE).length()-1);
        String intervalTwo = Arrays.toString(Constants.DAY_INTERVAL_TWO).substring(1,Arrays.toString(Constants.DAY_INTERVAL_TWO).length()-1);
        String intervalThree = Arrays.toString(Constants.DAY_INTERVAL_THREE).substring(1,Arrays.toString(Constants.DAY_INTERVAL_THREE).length()-1);
        Utility.saveStringInPreference(mContext,Constants.LEARN_FOREVER_PREFERENCE_LIST_OF_INTERVALS,intervalOne+Constants.INTERVAL_STRING_SEPARATER+
                        intervalTwo +Constants.INTERVAL_STRING_SEPARATER+intervalThree);
    }


    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
