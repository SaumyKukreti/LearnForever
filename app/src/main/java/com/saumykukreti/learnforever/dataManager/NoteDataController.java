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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by saumy on 12/3/2017.
 */
public class NoteDataController {

    static NoteDataController mDataController = null;
    private final AppDatabase mDatabase;
    private final Context mContext;

    private NoteDataController(Context context) {
        //TODO - REMOVE ALLOW MAIN THREAD QUERIES
        mContext = context;
        mDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "learnForever").allowMainThreadQueries().build();
    }

    public AppDatabase getDatabase() {
        return mDatabase;
    }

    public static NoteDataController getInstance(Context context) {
        if (mDataController == null) {
            mDataController = new NoteDataController(context);
            return mDataController;
        } else {
            return mDataController;
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

    public boolean newNote(String category, String noteTitle, String contentInShort, String content, String timeStamp, boolean learn) {
        //Validate data

        //Mandatory fields
        if (content == null || content.equalsIgnoreCase("")) {
            //A mandatory field is null/blank, note cannot be added
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

        SharedPreferences preference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        Set<String> setOfNoteIds = preference.getStringSet(listName, null);

        if (setOfNoteIds == null) {
            setOfNoteIds = new HashSet<String>();
            setOfNoteIds.add(String.valueOf(noteId));
        } else {
            setOfNoteIds.add(String.valueOf(noteId));
        }

        preference.edit().putStringSet(listName, setOfNoteIds).apply();
    }

    public boolean updateNote(NoteTable noteTable) {
        mDatabase.noteDao().updateNote(noteTable);
        saveToSyncQueue(noteTable.getId(), false);
        if (noteTable.isLearn()) {
            LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new ReminderJob(mContext, noteTable.getId(), null, null));
        } else {
            LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DeleteReminderJob(mContext, noteTable.getId(),false, null));
        }

        return true;
    }

    public void updateNoteInDatabseOnly(NoteTable noteTable) {
        mDatabase.noteDao().updateNote(noteTable);
        saveToSyncQueue(noteTable.getId(), false);
    }

    public boolean updateNote(long noteId, String category, String noteTitle, String contentInShort, String content, String timeStamp, boolean learn) {
        //Validate mandatory fields
        if (category == null || category.equalsIgnoreCase("") ||
                noteTitle == null || noteTitle.equalsIgnoreCase("") ||
                contentInShort == null || contentInShort.equalsIgnoreCase("") ||
                content == null || content.equalsIgnoreCase("")) {
            //A mandatory field is null/blank, note cannot be added
            return false;
        }

        NoteTable note;

        //Check if noteid exists
        List<NoteTable> listOfNotes = getNoteWithId(noteId);
        if (listOfNotes.size() == 0) {
            return false;
        } else {
            note = listOfNotes.get(0);
        }

        note.setCategory(category);
        note.setTitle(noteTitle);
        note.setContentInShort(contentInShort);
        note.setContent(content);
        note.setDateOfCreation(timeStamp);
        note.setLearn(learn);

        mDatabase.noteDao().updateNote(note);
        saveToSyncQueue(note.getId(), false);
        if (note.isLearn()) {
            LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new ReminderJob(mContext, note.getId(),null, null));
        } else {
            LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DeleteReminderJob(mContext, note.getId(),false, null));
        }

        return true;
    }

    public void deleteNotes(List<NoteTable> notes) {
        for(NoteTable note : notes){
            //Check if note was saved, if yes then delete its reminders as well else just delete the note
            if(note.isLearn()) {
                LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DeleteReminderJob(mContext, note.getId(), true, null));
            }
            else{
                deleteNoteFromDatabase(note);
            }
        }
    }


    public void deleteNote(NoteTable noteTable) {
        //Check if note was saved, if yes then delete its reminders as well else just delete the note
        if(noteTable.isLearn()) {
            //Delete reminder attached to this note
            LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DeleteReminderJob(mContext, noteTable.getId(), true, null));
        }else{
            deleteNoteFromDatabase(noteTable);
        }
    }

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
}








