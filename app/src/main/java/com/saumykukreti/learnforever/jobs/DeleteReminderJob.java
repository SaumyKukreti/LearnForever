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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by saumy on 12/17/2017.
 */

public class DeleteReminderJob extends Job {

    private static final int PRIORITY = 1;
    private static final String TAG = DataSyncJob.class.getSimpleName();
    private final Context mContext;
    private final long mNoteId;
    private final boolean mDeleteNote;
    private ReminderDataController mReminderDataController;
    private SharedPreferences mPreference;
    private NoteDataController mNoteDataController;
    private String mUserId;

    public DeleteReminderJob(Context context, long noteId, boolean deleteNote, Params params) {
        super(new Params(PRIORITY).requireNetwork());
        mContext = context;
        mNoteId = noteId;
        mDeleteNote = deleteNote;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        mPreference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        mReminderDataController = ReminderDataController.getInstance(mContext);
        mNoteDataController = NoteDataController.getInstance(mContext);

        mUserId = mPreference.getString(Constants.LEARN_FOREVER_PREFERENCE_USER_ID, "");

        if (!mUserId.isEmpty()) {
            if (deleteNoteFromPreferenceList(mNoteId)) {
                //Means the note was previously saved and all reminder data must be removed
                deleteReminderDatesFromNoteAndDeleteEntriesFromReminderTable();
            }
        } else {
            Log.e(TAG, "User id is empty");
        }
    }

    /**
     * This method deletes the reminder data from note
     */
    private void deleteReminderDatesFromNoteAndDeleteEntriesFromReminderTable() {
        List<NoteTable> noteList = mNoteDataController.getNoteWithId(mNoteId);

        if (noteList != null && !noteList.isEmpty()) {
            NoteTable note = noteList.get(0);

            deleteEntriesFromReminderTable(note.getReminderDates());

            //Check if the note is being deleted or updated
            //If the note is being deleted, delete the note else update the note
            if (mDeleteNote) {
                mNoteDataController.deleteNoteFromDatabase(note);
            } else {
                //Saving nothing
                note.setReminderDates("");
                mNoteDataController.updateNoteInDatabseOnly(note);
            }
        }
    }

    /**
     * This method deletes the reminders from all the dates
     *
     * @param reminderDates
     */
    private void deleteEntriesFromReminderTable(String reminderDates) {

        if (reminderDates != null && !reminderDates.isEmpty()) {
            String[] reminders = reminderDates.split(",");

            for (String rem : reminders) {
                //Removing the note id from each reminder date
                ReminderTable reminderTable = mReminderDataController.getNotesForDate(rem);

                if (reminderTable != null) {
                    String noteIds = reminderTable.getNoteIds();

                    List<String> listOfNotes = Converter.convertStringToList(noteIds);

                    //Remove note to be deleted from note list
                    listOfNotes.remove(String.valueOf(mNoteId));

                    if (Converter.convertListToString(listOfNotes).equalsIgnoreCase("")) {
                        //The note was the only id that was stored, hence deleting the entry
                        mReminderDataController.deleteReminder(reminderTable);
                    } else {
                        reminderTable.setNoteIds(Converter.convertListToString(listOfNotes));
                        mReminderDataController.updateReminder(reminderTable);
                    }
                }
            }
        }
    }

    /**
     * This method deletes the noteId from the list of notes to be reminded list
     *
     * @param noteId
     */
    private boolean deleteNoteFromPreferenceList(long noteId) {
        String savedNoteString = mPreference.getString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, "");

        if (!savedNoteString.isEmpty()) {
            List<String> savedNotesList = Converter.convertStringToList(savedNoteString);

            if (savedNotesList.contains(String.valueOf(noteId))) {
                //If so remove it from the list and save it
                savedNotesList.remove(String.valueOf(noteId));

                //Converting list to string
                String noteString = Converter.convertListToString(savedNotesList);

                mPreference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, noteString).apply();
                syncReminderDataToFirebase();

                return true;
            }
        }
        return false;
    }

    void syncReminderDataToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(mUserId);

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
