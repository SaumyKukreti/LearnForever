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
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;
import com.saumykukreti.learnforever.util.Converter;
import com.saumykukreti.learnforever.util.DateHandler;
import com.saumykukreti.learnforever.util.Utility;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by saumy on 12/17/2017.
 */

public class ReminderJob extends Job {

    private static final int PRIORITY = 1;
    private static final String TAG = ReminderJob.class.getSimpleName();
    private final Context mContext;
    private long mNoteId = 0;
    private String mDateOfCreation = null;
    private List<NoteTable> mListOfNotes = null;
    private ReminderDataController mReminderDataController;
    private NoteDataController mNoteDataController;
    private SharedPreferences mPreference;
    private String mUserId;

    /**
     * Constructor
     *
     * @param context
     * @param noteId
     * @param dateOfCreation - Pass date of creation in dd/mm/yy format during data initialisation only else pass null
     * @param params
     */
    public ReminderJob(Context context, long noteId, String dateOfCreation, Params params) {
        super(new Params(PRIORITY).requireNetwork());
        mContext = context;
        mNoteId = noteId;
        mDateOfCreation = dateOfCreation;
    }

    public ReminderJob(Context context, List<NoteTable> listOfNotes, Params params) {
        super(new Params(PRIORITY).requireNetwork());
        mContext = context;
        mListOfNotes = listOfNotes;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        //Initialise variables
        mReminderDataController = ReminderDataController.getInstance(mContext);
        mNoteDataController = NoteDataController.getInstance(mContext);


        //Check if list of notes is null or not
        if(mListOfNotes!=null){
            for(NoteTable note : mListOfNotes){
                mNoteId = note.getId();
                mDateOfCreation = note.getDateOfCreation();
                if(note.isLearn()) {
                    saveNoteIdInReminderTable();
                }
            }
        }
        else{
            saveNoteIdInReminderTable();
        }
    }

    /**
     *  This method saves the noteId in the ReminderTable, at frequent intervals
     */
    public void saveNoteIdInReminderTable() {
        mPreference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences preference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        String savedNoteString = preference.getString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, "");

        mUserId = mPreference.getString(Constants.LEARN_FOREVER_PREFERENCE_USER_ID, "");

        if(!mUserId.isEmpty()) {
            //Check if the note is already saved or not, if yes then ignore
            if (savedNoteString.length() > 0) {
                List<String> listOfNotes = Converter.convertStringToList(savedNoteString);

                if (!listOfNotes.contains(String.valueOf(mNoteId))) {
                    saveNoteIdInPreferenceAndUpdateReminderTable(mNoteId, savedNoteString);
                }
                //else ignore
            } else {
                //Else, its a new list
                saveNoteIdInPreferenceAndUpdateReminderTable(mNoteId, "");
            }
        }
        else{
            Log.e(TAG, "User id is empty");
        }
    }

    private void saveNoteIdInPreferenceAndUpdateReminderTable(long noteId, String savedNoteString) {
        //Saving data in preference
        SharedPreferences preference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        if(savedNoteString.isEmpty()){
            preference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, String.valueOf(noteId)).apply();
        }
        else{
            preference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, savedNoteString+","+noteId).apply();
        }
        Date currentDate;
        if(mDateOfCreation!=null){
            currentDate = DateHandler.convertStringToDate(mDateOfCreation);
        }
        else {
             currentDate = new Date();
        }

        String reminderDates = "";

        String currentIntervalPreference = preference.getString(Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL, "");
        int[] currentInterval = Converter.convertStringToIntArray(currentIntervalPreference);

        for(int days : currentInterval){
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.DATE, days);
            Date date = cal.getTime();

            reminderDates = reminderDates + DateHandler.convertDateToString(date)+",";

            {
                //Else check if database already have a field with date required, if so get update the database row, else create a new one
                ReminderTable reminder = mReminderDataController.getNotesForDate(date);

                if(reminder == null){
                    //Means there is not entry for this date

                    //Make a new entry in the database
                    ReminderTable reminderTable = new ReminderTable(DateHandler.convertDateToString(date),String.valueOf(noteId));
                    mReminderDataController.insertReminder(reminderTable);
                }
                else{

                    //Else get the note list and add the note to that list
                    String reminderNotes = reminder.getNoteIds();
                    reminderNotes = reminderNotes + ","+noteId;
                    reminder.setNoteIds(reminderNotes);
                    mReminderDataController.updateReminder(reminder);
                }
            }
        }

        saveReminderDatesToNote(noteId, reminderDates);
        syncReminderDataToFirebase();
    }

    private void saveReminderDatesToNote(long noteId, String reminderDates) {
        List<NoteTable> listOfNotes = mNoteDataController.getNoteWithId(noteId);

        if(listOfNotes!=null && !listOfNotes.isEmpty()){
            NoteTable noteTable = listOfNotes.get(0);

            noteTable.setReminderDates(reminderDates);
            mNoteDataController.updateNoteInDatabseOnly(noteTable);
        }
    }

    void syncReminderDataToFirebase(){
        if(Utility.isNetworkAvailable(mContext)) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(mUserId);

            SharedPreferences preference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
            String savedNoteString = preference.getString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, "");

            myRef.child("Reminders").setValue(savedNoteString);
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
