package com.saumykukreti.learnforever.modelClasses.dataTables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by saumy on 12/20/2017.
 */

@Entity
public class ReminderTable {

    @NonNull
    @PrimaryKey
    private long mId;

    @ColumnInfo(name = "date")
    private String mDate;

    @ColumnInfo(name = "noteIds")
    private String mNoteIds;

    public ReminderTable(){
        //Empty constructor for firebase
    }

    private ReminderTable(@NonNull long id, String date, String noteIds) {
        mId = id;
        mDate = date;
        mNoteIds = noteIds;
    }

    @NonNull
    private long getId() {
        return mId;
    }

    private void setId(@NonNull long id) {
        mId = id;
    }

    private String getDate() {
        return mDate;
    }

    private void setDate(String date) {
        mDate = date;
    }

    private String getNoteIds() {
        return mNoteIds;
    }

    private void setNoteIds(String noteIds) {
        mNoteIds = noteIds;
    }
}
