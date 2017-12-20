package com.saumykukreti.learnforever.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;

import java.util.List;

/**
 * Created by saumy on 12/20/2017.
 */

@Dao
public interface ReminderDao {

    @Query("SELECT * FROM ReminderTable WHERE date = :date")
    List<ReminderTable> getNoteIdsForDate(String date);
}
