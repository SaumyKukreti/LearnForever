package com.saumykukreti.learnforever.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.List;

/**
 * Created by saumy on 12/8/2017.
 */

@Dao
public interface NoteDao {

    @Query("SELECT * FROM NoteTable")
    List<NoteTable> getAllNotes();

    @Query("SELECT category FROM NoteTable")
    List<String> getAllCategories();

    @Query("SELECT title FROM NoteTable")
    List<String> getAllTitles();

    @Query("SELECT * FROM NoteTable WHERE mId = :id")
    List<NoteTable> getNoteWithId(long id);

    @Query("SELECT * FROM NOTETABLE WHERE title LIKE :searchString OR CONTENT LIKE :searchString OR contentInShort LIKE :searchString")
    List<NoteTable> searchNoteWithString(String searchString);

    @Query("SELECT * FROM notetable WHERE category = :category")
    List<NoteTable> getNotesWithCategory(String category);

    //onConflict = OnConflictStrategy.REPLACE
    @Insert
    long insertNote(NoteTable noteTable);

    @Insert
    void insertNote(List<NoteTable> noteTables);

    @Update
    void updateNote(NoteTable noteTable);

    @Delete
    void deleteNote(NoteTable noteTable);

    @Delete
    void deleteNotes(List<NoteTable> noteTables);
}
