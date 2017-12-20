package com.saumykukreti.learnforever.util;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.saumykukreti.learnforever.dao.NoteDao;
import com.saumykukreti.learnforever.dao.ReminderDao;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;

/**
 * Created by saumy on 12/8/2017.
 */

@Database(entities = {NoteTable.class, ReminderTable.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
    public abstract ReminderDao reminderDao();
}