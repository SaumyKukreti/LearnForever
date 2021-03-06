package com.saumykukreti.learnforever.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;
import com.saumykukreti.learnforever.util.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saumy on 12/17/2017.
 */

public class DeleteReminderJob extends Job {

    private static final int PRIORITY = 1;
    private static final String TAG = DeleteReminderJob.class.getSimpleName();
    private final Context mContext;
    private final NoteTable mNote;
    private final boolean mDeleteNote;
    private final List<NoteTable> mListOfNotes;
    private ReminderDataController mReminderDataController;
    private SharedPreferences mPreference;
    private NoteDataController mNoteDataController;
    private String mUserId;

    public DeleteReminderJob(Context context, NoteTable note, boolean deleteNote, Params params) {
        super(new Params(PRIORITY).requireNetwork());
        mContext = context;
        mNote = note;
        mListOfNotes = null;
        mDeleteNote = deleteNote;
    }

    public DeleteReminderJob(Context context, List<NoteTable> notes, Params params) {
        super(new Params(PRIORITY).requireNetwork());
        mContext = context;
        mListOfNotes = notes;
        mNote = null;
        mDeleteNote = true;
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

        //Check if a single note has to be deleted or a list of notes has to be deleted
        if (mListOfNotes != null) {
            //This means that a list of notes needs to be deleted
            //Making a clone of mlistofnotes to avoid concurrent modification exception
            List<NoteTable> mListOfNotesClone = new ArrayList<>(mListOfNotes);
            //Iterating over the list and deleting note one by one
            for (NoteTable note : mListOfNotesClone) {
                if (note.isLearn()) {
                    //Delete the reminders from the reminder table and delete it from the list of reminders
                    deleteNoteWithLearningOn(note);
                } else {
                    //Just delete the note
                    mNoteDataController.deleteNoteFromDatabase(note);
                }
            }
        } else {
            //The note needs to be deleted
            if (mNote.isLearn()) {
                deleteNoteWithLearningOn(mNote);
            } else {
                if(mDeleteNote) {
                    //Directly delete the note
                    mNoteDataController.deleteNoteFromDatabase(mNote);
                }
                else{
                    //Delete reminder data first then and update the note
                    deleteNoteWithLearningOn(mNote);
                }
            }

        }
    }
    /**
     * This method deletes a note which has learning mode on
     *
     * @param note
     */
    private void deleteNoteWithLearningOn(NoteTable note) {
        //From preference list
        deleteNoteFromPreferenceList(note.getId());

        //Removing reminders
        deleteReminderDatesFromNoteAndDeleteEntriesFromReminderTable(note.getId());
    }


    /**
     * This method deletes the noteId from the list of notes to be reminded list
     *
     * @param noteId
     */
    private void deleteNoteFromPreferenceList(long noteId) {
        String savedNoteString = mPreference.getString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, "");

        if (!savedNoteString.isEmpty()) {
            List<String> savedNotesList = Converter.convertStringToList(savedNoteString);

            if (savedNotesList.contains(String.valueOf(noteId))) {
                //If so remove it from the list and save it
                savedNotesList.remove(String.valueOf(noteId));

                //Converting list to string
                String noteString = Converter.convertListToString(savedNotesList);

                //Saving the new list to preference
                mPreference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, noteString).apply();
                return;
            }
        }
    }

    /**
     * This method deletes the reminder data from note
     */
    private void deleteReminderDatesFromNoteAndDeleteEntriesFromReminderTable(long noteId) {
        List<NoteTable> noteList = mNoteDataController.getNoteWithId(noteId);

        if (noteList != null && !noteList.isEmpty()) {
            NoteTable note = noteList.get(0);

            deleteEntriesFromReminderTable(noteId, note.getReminderDates());

            //Check if the note is being deleted or updated
            //If the note is being deleted, delete the note else update the note
            if (mDeleteNote) {
                mNoteDataController.deleteNoteFromDatabase(note);
            } else {
                //Saving nothing
                note.setReminderDates("");
                mNoteDataController.updateNoteFromDatabase(note);
            }
        }
    }

    /**
     * This method deletes the reminders from all the dates
     *
     * @param noteId
     * @param reminderDates
     */
    private void deleteEntriesFromReminderTable(long noteId, String reminderDates) {

        if (reminderDates != null && !reminderDates.isEmpty()) {
            String[] reminders = reminderDates.split(",");

            for (String rem : reminders) {
                //Removing the note id from each reminder date
                ReminderTable reminderTable = mReminderDataController.getNotesForDate(rem);

                if (reminderTable != null) {
                    String noteIds = reminderTable.getNoteIds();

                    List<String> listOfNotes = Converter.convertStringToList(noteIds);

                    //Remove note to be deleted from note list
                    listOfNotes.remove(String.valueOf(noteId));

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

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
