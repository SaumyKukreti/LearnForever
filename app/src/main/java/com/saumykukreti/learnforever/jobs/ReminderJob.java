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
import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;
import com.saumykukreti.learnforever.util.AppDatabase;
import com.saumykukreti.learnforever.util.DateHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by saumy on 12/17/2017.
 */

public class ReminderJob extends Job {

    private static final int PRIORITY = 1;
    private static final String TAG = DataSyncJob.class.getSimpleName();
    private final Context mContext;
    private final long mNoteId;
    private GoogleSignInAccount mAccount;
    private ReminderDataController mDataController;
    private SharedPreferences mPreference;

    public ReminderJob(Context context, long noteId, Params params) {
        super(new Params(PRIORITY).requireNetwork());
        mContext = context;
        mNoteId = noteId;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        mAccount = GoogleSignIn.getLastSignedInAccount(mContext);
        mDataController = ReminderDataController.getInstance(mContext);
        saveNoteIdInReminderTable(mNoteId);
    }

    /**
     *  This method saves the noteId in the ReminderTable, at frequent intervals
     * @param noteId
     */
    public void saveNoteIdInReminderTable(long noteId) {
        SharedPreferences preference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        String savedNoteString = preference.getString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, "");

        //Check if the note is already saved or not, if yes then ignore
        if(savedNoteString.length()>0){
            String[] savedNoteArray = savedNoteString.split(",");
            List<String> listOfNotes = Arrays.asList(savedNoteArray);

            if(!listOfNotes.contains(String.valueOf(noteId))){
                saveNoteIdInPreferenceAndUpdateReminderTable(noteId, savedNoteString);
            }
            //else ignore
        }else{
            saveNoteIdInPreferenceAndUpdateReminderTable(noteId, "");
        }
    }

    private void saveNoteIdInPreferenceAndUpdateReminderTable(long noteId, String savedNoteString) {
        //Saving data in preference
        //TODO - SAVE THIS PREFERENCE IN FIREBASE
        SharedPreferences preference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        if(savedNoteString.isEmpty()){
            preference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, String.valueOf(noteId)).apply();
        }
        else{
            preference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, savedNoteString+","+noteId).apply();
        }

        Date currentDate = new Date();

        for(int days : Constants.DAY_INTERVAL_ONE){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, days);
            Date date = cal.getTime();

            //Check if the date is in another year, if so break
            if(date.getYear()> currentDate.getYear()){
                break;
            }
            {
                //Else check if database already have a field with date required, if so get update the database row, else create a new one
                ReminderTable reminder = mDataController.getNotesForDate(date);

                if(reminder == null){
                    //Means there is not entry for this date

                    //Make a new entry in the database
                    ReminderTable reminderTable = new ReminderTable(DateHandler.convertDateToString(date),String.valueOf(noteId));
                    mDataController.insertReminder(reminderTable);
                }
                else{

                    //Else get the note list and add the note to that list
                    String reminderNotes = reminder.getNoteIds();
                    reminderNotes = reminderNotes + ","+noteId;
                    reminder.setNoteIds(reminderNotes);
                    mDataController.updateReminder(reminder);
                }
            }
        }

        syncReminderDataToFirebase();
    }

    void syncReminderDataToFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(mAccount.getId());

        SharedPreferences preference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        String savedNoteString = preference.getString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, "");

        myRef.child("Reminders").setValue(savedNoteString);
    }


    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
