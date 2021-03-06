package com.saumykukreti.learnforever.dataManager;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;

import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.jobs.DeleteReminderJob;
import com.saumykukreti.learnforever.jobs.ReminderJob;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.util.AppDatabase;
import com.saumykukreti.learnforever.util.Converter;
import com.saumykukreti.learnforever.util.Utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by saumy on 12/3/2017.
 */
public class NoteDataController {

    static NoteDataController mNoteDataController = null;
    private final AppDatabase mDatabase;
    private final Context mContext;

    private NoteDataController(Context context) {
        mContext = context;
        mDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "learn_forever").allowMainThreadQueries().build();
    }

    public AppDatabase getDatabase() {
        return mDatabase;
    }

    public static NoteDataController getInstance(Context context) {
        if (mNoteDataController == null) {
            mNoteDataController = new NoteDataController(context);
            return mNoteDataController;
        } else {
            return mNoteDataController;
        }
    }

    public List<NoteTable> getAllNotes() {
        //Return all notes
        return mDatabase.noteDao().getAllNotes();
    }

    public List<NoteTable> getNoteWithId(long id) {
        //Return note with selected id
        return mDatabase.noteDao().getNoteWithId(id);
    }

    public List<NoteTable> getNoteWithIds(List<String> ids) {
        //Return note with selected id
        return mDatabase.noteDao().getNoteWithIds(ids);
    }

    public List<NoteTable> getNoteWithCategory(String category) {
        //Return notes with given category
        return mDatabase.noteDao().getNotesWithCategory(category);
    }

    public List<String> getListOfCategories() {
        //Return list of categories
        List<String> listOfCategories = mDatabase.noteDao().getAllCategories();

        HashSet<String> setOfCategories = new HashSet<>();

        //Making the list unique
        for (String category : listOfCategories) {
            if (category != null && !category.equalsIgnoreCase("")) {
                setOfCategories.add(category);
            }
        }

        listOfCategories = new ArrayList<>();
        listOfCategories.addAll(setOfCategories);
        return listOfCategories;
    }


    public List<String> getListOfCategoriesWithValue(String text) {
        //Return list of categories
        List<String> listOfCategories = mDatabase.noteDao().getCategoriesWithValue(text);

        HashSet<String> setOfCategories = new HashSet<>();

        //Making the list unique
        for (String category : listOfCategories) {
            if (category != null && !category.equalsIgnoreCase("")) {
                setOfCategories.add(category);
            }
        }

        listOfCategories = new ArrayList<>();
        listOfCategories.addAll(setOfCategories);
        return listOfCategories;
    }

    /**
     * Pass this method the following parameters to make a new note.
     * If null is passed in any parameter, the note is not created
     * @param category
     * @param noteTitle
     * @param contentInShort
     * @param content
     * @param timeStamp
     * @param learn
     * @return
     */
    public boolean newNote(String category, String noteTitle, String contentInShort, String content, String timeStamp, boolean learn) {
        //Validate data
        if(category==null || noteTitle == null || contentInShort == null || content == null || timeStamp == null){
            return false;
        }

        //Checking if content, cis timestamp, and notetitle are empty, if so return false
        if(content.isEmpty() && contentInShort.isEmpty() && noteTitle.isEmpty()){
            return false;
        }

        long noteId = mDatabase.noteDao().insertNote(new NoteTable(category, noteTitle, contentInShort, content, timeStamp, learn, "", "1"));
        if (learn) {
            LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new ReminderJob(mContext, noteId,null, null));
        }
        saveToSyncQueue(noteId, false);
        return true;
    }


    public void newNotes(List<NoteTable> notes) {
        mDatabase.noteDao().insertNote(notes);
    }

    /**
     * This method saves the note id that is created to be synced. If the note id passed needs to be deleted pass true as the
     * second parameter
     *
     * @param noteId toDelete - Pass true to delete
     */
    private void saveToSyncQueue(long noteId, boolean toDelete) {

        String listName = "";
        if (toDelete) {
            listName = Constants.LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS_TO_DELETE;
        } else {
            listName = Constants.LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS;
        }

        SharedPreferences preferences = Utility.getPreference(mContext);
        Set<String> setOfNoteIds = preferences.getStringSet(listName, null);

        if (setOfNoteIds == null) {
            setOfNoteIds = new HashSet<String>();
            setOfNoteIds.add(String.valueOf(noteId));
        } else {
            setOfNoteIds.add(String.valueOf(noteId));
        }

        preferences.edit().putStringSet(listName, setOfNoteIds).apply();
    }

    public boolean updateNote(NoteTable noteTable) {
        //Checking if the learn boolean has changed from previous value or not if so starting respective job
        List<NoteTable> previousNote = mDatabase.noteDao().getNoteWithId(noteTable.getId());
        if(previousNote.get(0).isLearn()!=noteTable.isLearn()){
            if (noteTable.isLearn()) {
                LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new ReminderJob(mContext, noteTable.getId(), null, null));
            } else {
                LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DeleteReminderJob(mContext, noteTable,false, null));
            }
        }

        //Updating the note in database
        mDatabase.noteDao().updateNote(noteTable);
        saveToSyncQueue(noteTable.getId(), false);
        return true;
    }

    public void updateNoteFromDatabase(NoteTable noteTable) {
        mDatabase.noteDao().updateNote(noteTable);
        saveToSyncQueue(noteTable.getId(), false);
    }

    /**
     *  This method is used to delete list of notes
     * @param notes - The list of notes that need to be deleted
     */
    public void deleteNotes(List<NoteTable> notes) {
        LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DeleteReminderJob(mContext, notes,null));
    }

    /**
     * This method is used to delete a single note
     * @param noteTable
     */
    public void deleteNote(NoteTable noteTable) {
        LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DeleteReminderJob(mContext, noteTable, true, null));
    }

    /**
     * Important - Call this method from database only, as this method does not check for any reminders. Also this note saves the updated/deleted note in sync queue
     * @param noteTable
     */
    public void deleteNoteFromDatabase(NoteTable noteTable) {
        //Delete reminder attached to this note
        mDatabase.noteDao().deleteNote(noteTable);
        saveToSyncQueue(noteTable.getId(), true);
    }

    public List<NoteTable> searchNoteWithString(String searchString) {
        searchString = '%' + searchString + '%';
        List<NoteTable> listOfNotes = mDatabase.noteDao().searchNoteWithString(searchString);

        if (listOfNotes.size() > 0) {
            return listOfNotes;
        } else {
            return new ArrayList<>();
        }
    }

    public List<NoteTable> searchNoteWithStringAndCategory(String searchString, String category) {
        searchString = '%' + searchString + '%';
        List<NoteTable> noteList = mDatabase.noteDao().searchNoteWithStringAndCategory(searchString, category);
        return noteList;
    }

    public void deleteAllRecords() {
        mDatabase.noteDao().deleteAllRecords();
    }

    public List<String> getNoteIdsWithCategory(String category) {
        //Get only the note ids and return
        List<NoteTable> listOfNotes = mDatabase.noteDao().getNotesWithCategory(category);
        return Converter.convertNoteListToStringList(listOfNotes);
    }
}








