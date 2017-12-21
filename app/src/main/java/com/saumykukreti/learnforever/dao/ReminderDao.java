package com.saumykukreti.learnforever.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;

import java.util.List;

/**
 * Created by saumy on 12/20/2017.
 */

@Dao
public interface ReminderDao {

    @Query("SELECT * FROM ReminderTable WHERE date = :date")
    List<ReminderTable> getNoteIdsForDate(String date);

    @Query("SELECT * FROM ReminderTable")
    List<ReminderTable> getAllEntries();


    @Insert
    long insertReminder(ReminderTable reminderTable);

    @Update
    void updateReminder(ReminderTable reminderTable);
}
