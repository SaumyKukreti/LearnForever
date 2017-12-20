package com.saumykukreti.learnforever.dataManager;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;

import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;
import com.saumykukreti.learnforever.util.AppDatabase;
import com.saumykukreti.learnforever.util.DateHandler;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by saumy on 12/20/2017.
 */

public class ReminderDataController {

    private static ReminderDataController mDataController;
    private final Context mContext;
    private final AppDatabase mDatabase;

    private ReminderDataController(Context context) {
        mContext = context;
        mDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "learnForever").allowMainThreadQueries().build();
    }

    public static ReminderDataController getInstance(Context context){

        if(mDataController == null){
            mDataController = new ReminderDataController(context);
            return mDataController;
        }else{
            return mDataController;
        }
    }

    public String getNotesForDate(Date date){
        List<ReminderTable> notesForDate = mDatabase.reminderDao().getNoteIdsForDate(DateHandler.convertDateToString(date));
        if(!notesForDate.isEmpty()) {
            ReminderTable reminderTable = notesForDate.get(0);
            return reminderTable.getNoteIds();
        }
        else {
            //No notes found
            return null;
        }
    }

    public void insertReminder(ReminderTable reminderTable) {
        mDatabase.reminderDao().insertReminder(reminderTable);
    }

    public void updateReminder(ReminderTable reminderTable) {
        mDatabase.reminderDao().updateReminder(reminderTable);
    }
}
