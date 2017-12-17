package com.saumykukreti.learnforever.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.saumykukreti.learnforever.constants.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by saumy on 12/17/2017.
 */

public class DataSyncJob extends Job {

    private static final int PRIORITY = 1;
    private static final String TAG = DataSyncJob.class.getSimpleName();

    public DataSyncJob(Params params) {
        super(new Params(PRIORITY).requireNetwork());
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        //Check if there are any notes that are pending for sync
        SharedPreferences preference = getApplicationContext().getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        Set<String> setOfNoteIds = preference.getStringSet(Constants.LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS, null);

        List<String> listOfNoteIds = new ArrayList<>();

        if(setOfNoteIds!=null && !setOfNoteIds.isEmpty()){
            listOfNoteIds = new ArrayList<>(setOfNoteIds);
        }

        if(!listOfNoteIds.isEmpty()){
            while(!listOfNoteIds.isEmpty()){
                syncNoteWithId(listOfNoteIds.get(0));
                listOfNoteIds.remove(0);
            }
        }
        else{
            Log.e(TAG, "No notes to sync");
        }
    }

    private void syncNoteWithId(String id){
        //Sync here
    }


    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
