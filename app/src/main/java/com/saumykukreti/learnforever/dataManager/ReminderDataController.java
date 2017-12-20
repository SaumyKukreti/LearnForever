package com.saumykukreti.learnforever.dataManager;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.saumykukreti.learnforever.util.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    public String getNotesForToday(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("DD/mm/YY");
        String dateInString = dateFormat.format(date);

        String notesForDate = mDatabase.reminderDao().getNoteIdsForDate(dateInString);
        return notesForDate;

    }

}
