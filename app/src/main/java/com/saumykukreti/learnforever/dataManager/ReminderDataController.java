package com.saumykukreti.learnforever.dataManager;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;

import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;
import com.saumykukreti.learnforever.util.AppDatabase;

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
        List<ReminderTable> notesForDate = mDatabase.reminderDao().getNoteIdsForDate(convertDateToString(date));
        if(!notesForDate.isEmpty()) {
            ReminderTable reminderTable = notesForDate.get(0);
            return reminderTable.getNoteIds();
        }
        else {
            //No notes found
            return null;
        }
    }

    private String convertDateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YY");
        return dateFormat.format(date);
    }

    /**
     *  This method saves the noteId in the ReminderTable, at frequent intervals
     * @param noteId
     */
    public void saveNoteIdInReminderTable(long noteId) {
        //TODO - MOVE THIS CODE TO JOB

        SharedPreferences preference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        String savedNoteString = preference.getString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, "");

        //Check if the note is already saved or not, if yes then ignore
        if(savedNoteString.length()>0){
            String[] savedNoteArray = savedNoteString.split(",");
            List<String> listOfNotes = Arrays.asList(savedNoteArray);

            if(!listOfNotes.contains(String.valueOf(noteId))){
                saveNoteIdInPreferenceAndUpdateReminderTable(noteId, savedNoteString);
            }
            //else ignore
        }else{
            saveNoteIdInPreferenceAndUpdateReminderTable(noteId, "");
        }
    }

    private void saveNoteIdInPreferenceAndUpdateReminderTable(long noteId, String savedNoteString) {
        //Saving data in preference
        //TODO - SAVE THIS PREFERENCE IN FIREBASE
        SharedPreferences preference = mContext.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        if(savedNoteString.isEmpty()){
            preference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, String.valueOf(noteId)).apply();
        }
        else{
            preference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST, savedNoteString+","+noteId).apply();
        }

        Date currentDate = new Date();

        for(int days : Constants.DAY_INTERVAL_ONE){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, days);
            Date date = cal.getTime();

            //Check if the date is in another year, if so break
            if(date.getYear()> currentDate.getYear()){
                break;
            }
            {
                //Else check if database already have a field with date required, if so get update the database row, else create a new one
                String notesOnThatDate = mDataController.getNotesForDate(date);

                if(notesOnThatDate == null){
                    //Means there is not entry for this date

                    //Make a new entry in the database
                    ReminderTable reminderTable = new ReminderTable(convertDateToString(date),String.valueOf(noteId));
                    mDatabase.reminderDao().insertReminder(reminderTable);
                    syncReminderDataToFirebase();
                }
                else{

                    //Else get the note list and add the note to that list
                    notesOnThatDate = notesOnThatDate + ","+noteId;

                    ReminderTable reminderTable = new ReminderTable(convertDateToString(date),notesOnThatDate);
                    mDatabase.reminderDao().updateReminder(reminderTable);
                    syncReminderDataToFirebase();
                }
            }
        }
    }

    void syncReminderDataToFirebase(){

    }

}
