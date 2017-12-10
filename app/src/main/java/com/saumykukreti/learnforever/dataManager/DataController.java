package com.saumykukreti.learnforever.dataManager;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.util.AppDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by saumy on 12/3/2017.
 */
public class DataController {

    static DataController mDataController = null;
    private final AppDatabase mDatabase;

    private DataController(Context context) {
        //TODO - REMOVE ALLOW MAIN THREAD QUERIES
        mDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "learnForever").allowMainThreadQueries().build();
    }

    public static DataController getInstance(Context context){
        if(mDataController == null){
            mDataController = new DataController(context);
            return mDataController;
        }else{
            return mDataController;
        }
    }

    public List<NoteTable> getAllNotes(){
        //Return all notes
        return mDatabase.noteDao().getAllNotes();
    }

    public List<NoteTable> getNoteWithId(long id){
        //Return note with selected id
        return mDatabase.noteDao().getNoteWithId(id);
    }

    public List<NoteTable> getNoteWithCategory(String category){
        //Return notes with given category
        return mDatabase.noteDao().getNotesWithCategory(category);
    }

    public List<String> getListOfCategories(){
        //Return list of categories
        List<String> listOfCategories = mDatabase.noteDao().getAllCategories();

        HashSet<String> setOfCategories = new HashSet<>();

        //Making the list unique
        for(String category : listOfCategories){
            if(!category.equalsIgnoreCase("")){
                setOfCategories.add(category);
            }
        }

        listOfCategories = new ArrayList<>();
        listOfCategories.addAll(setOfCategories);
        return listOfCategories;
    }

    public boolean newNote(String category, String noteTitle, String contentInShort, String content ,String timeStamp, boolean learn){
        //Validate data

        //Mandatory fields
        if(category==null || noteTitle==null || noteTitle.equalsIgnoreCase("") ||
                contentInShort==null || contentInShort.equalsIgnoreCase("") ||
                content==null || content.equalsIgnoreCase("")){
            //A mandatory field is null/blank, note cannot be added
            return false;
        }

        mDatabase.noteDao().insertNote(new NoteTable(category,noteTitle,contentInShort, content,timeStamp,learn));
        return true;
    }

    public boolean updateNote(NoteTable noteTable){
        mDatabase.noteDao().updateNote(noteTable);
        return true;
    }

    public boolean updateNote(long noteId, String category, String noteTitle, String contentInShort, String content ,String timeStamp, boolean learn){
        //Validate mandatory fields
        if(category==null || category.equalsIgnoreCase("") ||
                noteTitle==null || noteTitle.equalsIgnoreCase("") ||
                contentInShort==null || contentInShort.equalsIgnoreCase("") ||
                content==null || content.equalsIgnoreCase("")){
            //A mandatory field is null/blank, note cannot be added
            return false;
        }

        NoteTable note;

        //Check if noteid exists
        List<NoteTable> listOfNotes = getNoteWithId(noteId);
        if(listOfNotes.size()==0){
            return false;
        }else{
            note = listOfNotes.get(0);
        }

        note.setCategory(category);
        note.setTitle(noteTitle);
        note.setContentInShort(contentInShort);
        note.setContent(content);
        note.setTimeStamp(timeStamp);
        note.setLearn(learn);

        mDatabase.noteDao().updateNote(note);
        return true;
    }


    public void deleteNote(NoteTable noteTable){
        mDatabase.noteDao().deleteNote(noteTable);
    }

    public boolean deleteNote(long id){
        List<NoteTable> listOfNotes = getNoteWithId(id);

        if(listOfNotes.size()==0){
            return false;
        }
        else{
            mDatabase.noteDao().deleteNote(listOfNotes.get(0));
        }
        return true;
    }
}
