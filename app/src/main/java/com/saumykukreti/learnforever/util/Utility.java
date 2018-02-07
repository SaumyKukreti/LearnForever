package com.saumykukreti.learnforever.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.saumykukreti.learnforever.brodcastReceiver.NotificationBuilder;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by saumy on 2/1/2018.
 */

public class Utility {
    public static void setNotification(Context context) {
        SharedPreferences preference = context.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);

        int hour = Integer.parseInt(preference.getString(Constants.LEARN_FOREVER_PREFERENCE_NOTIFICATION_HOUR, "9"));
        int minute = Integer.parseInt(preference.getString(Constants.LEARN_FOREVER_PREFERENCE_NOTIFICATION_MINUTE, "0"));

        Date date = new Date();
        date.setHours(hour);
        date.setMinutes(minute);

        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        calendar.setTime(date);

        TimeZone timeZone = TimeZone.getDefault();
        calendar.setTimeZone(timeZone);

        Intent intent = new Intent(context.getApplicationContext(), NotificationBuilder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), Constants.NOTIFICATION_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     *  This note calculates all the notes that need to be revised from the last date the user revised to the current date
     * @param context
     * @return
     */
    public static List<String> getNoteIdsToRemind(Context context) {
        //First getting the date the user has revised last
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        String lastRevisedDateString = sharedPreferences.getString(Constants.LEARN_FOREVER_PREFERENCE_LAST_REVISE_DATE, "");

        //Getting the notes to revise between the current date and the last date
        if (!lastRevisedDateString.isEmpty()){
            //Now getting all the days between lastRevisedDate and current date

            Set<String> combinedNotesToRevise = new HashSet<>();

            Calendar lastRevisedCalendarDate = Calendar.getInstance();
            Calendar currentCalendarDate = Calendar.getInstance();
            lastRevisedCalendarDate.setTime(DateHandler.convertStringToDate(lastRevisedDateString));

            if(lastRevisedCalendarDate.get(Calendar.DAY_OF_YEAR) >= currentCalendarDate.get(Calendar.DAY_OF_YEAR)){
                //This means the notes were revised today, hence showing reminders for today only
                ReminderTable notes = ReminderDataController.getInstance(context).getNotesForDate(new Date());
                return Converter.convertStringToList(notes.getNoteIds());
            }

            while (lastRevisedCalendarDate.get(Calendar.DATE) < currentCalendarDate.get(Calendar.DATE)){
                lastRevisedCalendarDate.add(Calendar.DATE, 1);
                ReminderTable notes = ReminderDataController.getInstance(context).getNotesForDate(lastRevisedCalendarDate.getTime());
                if(notes!=null && notes.getNoteIds()!=null && !notes.getNoteIds().isEmpty()) {
                    List<String> notesToRemind = Converter.convertStringToList(notes.getNoteIds());
                    combinedNotesToRevise.addAll(notesToRemind);
                }
            }

            List<String> combinedNotesList = new ArrayList<>();
            combinedNotesList.addAll(combinedNotesToRevise);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                combinedNotesList.sort(null);
            }
            if(!combinedNotesList.isEmpty())
                Toast.makeText(context, "Showing notes from :"+lastRevisedDateString, Toast.LENGTH_SHORT).show();

            return combinedNotesList;
        }
        else{
            ReminderTable notes = ReminderDataController.getInstance(context).getNotesForDate(new Date());
            return Converter.convertStringToList(notes.getNoteIds());
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
