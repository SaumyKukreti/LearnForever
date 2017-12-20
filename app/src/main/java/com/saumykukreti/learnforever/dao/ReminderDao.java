package com.saumykukreti.learnforever.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

/**
 * Created by saumy on 12/20/2017.
 */

@Dao
public interface ReminderDao {

    @Query("SELECT * FROM REMINDERTABLE WHERE date = :date")
    String getNoteIdsForDate(String date);
}
