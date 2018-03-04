package com.saumykukreti.learnforever.modelClasses.dataTables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by saumy on 12/20/2017.
 */

@Entity
public class ReminderTable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long mId;

    @ColumnInfo(name = "date")
    private String mDate;

    @ColumnInfo(name = "noteIds")
    private String mNoteIds;

    public ReminderTable(){
        //Empty constructor for firebase
    }

    public ReminderTable(String date, String noteIds) {
        mDate = date;
        mNoteIds = noteIds;
    }

    @NonNull
    public long getId() {
        return mId;
    }

    public void setId(@NonNull long id) {
        mId = id;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getNoteIds() {
        return mNoteIds;
    }

    public void setNoteIds(String noteIds) {
        mNoteIds = noteIds;
    }

    @Override
    public String toString() {
        StringBuilder value = new StringBuilder();
        value.append("UID = "+mId).append("\n");
        value.append("Date = "+mDate).append("\n");
        value.append("NoteIds = "+mNoteIds).append("\n\n\n");
        return value.toString();
    }
}
