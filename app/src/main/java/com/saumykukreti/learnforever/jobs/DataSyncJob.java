package com.saumykukreti.learnforever.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by saumy on 12/17/2017.
 */

public class DataSyncJob extends Job {

    private static final int PRIORITY = 1;
    private static final String TAG = DataSyncJob.class.getSimpleName();
    private final Context mContext;
    private NoteDataController mDataController;
    private SharedPreferences mPreference;
    private String mUserId = "";

    public DataSyncJob(Context context, Params params) {
        super(new Params(PRIORITY).requireNetwork());
        mContext = context;

    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        //Check if there are any notes that are pending for sync
        mPreference = getApplicationContext().getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);

        mUserId = mPreference.getString(Constants.LEARN_FOREVER_PREFERENCE_USER_ID, "");

        if (!mUserId.isEmpty()) {
            Set<String> setOfNoteIds = mPreference.getStringSet(Constants.LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS, null);
            Set<String> setOfNoteIdsToDelete = mPreference.getStringSet(Constants.LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS_TO_DELETE, null);

            syncNotes(setOfNoteIds, false);
            syncNotes(setOfNoteIdsToDelete, true);
        } else {
            Log.e(TAG, "User id is empty");
        }
    }

    private void syncNotes(Set<String> setOfNoteIds, boolean toDelete) {
        List<String> listOfNoteIds = new ArrayList<>();

        if (setOfNoteIds != null && !setOfNoteIds.isEmpty()) {
            listOfNoteIds = new ArrayList<>(setOfNoteIds);
        }

        if (!listOfNoteIds.isEmpty()) {
            //Get account information
            mDataController = NoteDataController.getInstance(mContext);
            while (!listOfNoteIds.isEmpty()) {
                if (toDelete) {
                    deleteNoteWithId(listOfNoteIds.get(0));
                    setOfNoteIds.remove(listOfNoteIds.get(0));
                    saveToDeletePreference(setOfNoteIds);
                } else {
                    syncNoteWithId(listOfNoteIds.get(0));
                    setOfNoteIds.remove(listOfNoteIds.get(0));
                    saveToPreference(setOfNoteIds);
                }
                listOfNoteIds.remove(0);
            }

        } else {
            Log.e(TAG, "No notes to sync");
        }
    }

    private void deleteNoteWithId(String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(mUserId);


        myRef.child("Notes").child(id).removeValue();

    }

    private void saveToDeletePreference(Set<String> setOfNoteIds) {
        mPreference.edit().putStringSet(Constants.LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS_TO_DELETE, setOfNoteIds).commit();
    }

    private void saveToPreference(Set<String> setOfNoteIds) {
        mPreference.edit().putStringSet(Constants.LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS, setOfNoteIds).commit();
    }


    private void syncNoteWithId(String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(mUserId);


        List<NoteTable> note = mDataController.getNoteWithId(Long.parseLong(id));
        if (!note.isEmpty()) {
            myRef.child("Notes").child(String.valueOf(note.get(0).getId())).setValue(note.get(0));
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
