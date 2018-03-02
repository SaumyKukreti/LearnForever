package com.saumykukreti.learnforever.util;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.brodcastReceiver.NotificationBuilder;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
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

            //TODO - CHANGE THIS LOGIC, AS THIS WILL FAIL FOR NEW YEAR CASE
            if(lastRevisedCalendarDate.get(Calendar.DAY_OF_YEAR) >= currentCalendarDate.get(Calendar.DAY_OF_YEAR)){
                //This means the notes were revised today, hence showing reminders for today only
                ReminderTable notes = ReminderDataController.getInstance(context).getNotesForDate(new Date());
                if(notes!=null && notes.getNoteIds()!=null) {
                    return Converter.convertStringToList(notes.getNoteIds());
                }
                else{
                    return new ArrayList<String>();
                }
            }

            while (lastRevisedCalendarDate.get(Calendar.DAY_OF_YEAR) < currentCalendarDate.get(Calendar.DAY_OF_YEAR)){
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
            if(notes!=null && notes.getNoteIds()!=null && !notes.getNoteIds().isEmpty()) {
                return Converter.convertStringToList(notes.getNoteIds());
            }
            else {
                return new ArrayList<String>();
            }
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String saveImage(Context context, Bitmap image) {
        File pictureFile = getOutputMediaFile(context);
        if (pictureFile == null) {
            Log.e("Saving Image",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return "unsuccessful";
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Saving Image", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Saving Image", "Error accessing file: " + e.getMessage());
        }

        return pictureFile.getPath();
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(Context context){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName="ProfileImage.jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public static SharedPreferences getPreference(Context context){
        return context.getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE,Context.MODE_PRIVATE);
    }

    public static void saveStringInPreference(Context context, String key, String value){
        getPreference(context).edit().putString(key,value).apply();
    }

    public static String getStringFromPreference(Context context, String key){
        return getPreference(context).getString(key,"");
    }

    public static void saveBooleanInPreference(Context context, String key, boolean value){
        getPreference(context).edit().putBoolean(key,value).apply();
    }

    public static boolean getBooleanFromPreference(Context context, String key){
        return getPreference(context).getBoolean(key,false);
    }

    public static void saveIntInPreference(Context context, String key, int value){
        getPreference(context).edit().putInt(key,value).apply();
    }

    public static int getIntFromPreference(Context context, String key){
        return getPreference(context).getInt(key,-1);
    }

    /**
     *  This method shows a dialog shwoing help regarding the page
     */
    public static void showHelp(Context context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_help_layout);
        TextView helpText = dialog.findViewById(R.id.text_help);
        helpText.setText(message);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.onWindowAttributesChanged(params);
        dialog.show();
    }

    /**
     * This method rounds up float to required number of decimal places
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static List<NoteTable> sortList(List<NoteTable> listOfNotes, String filterMode){
        switch (filterMode){
            case Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_ALPHABETICALLY:
                AlphabeticComparater comp = new AlphabeticComparater();
                listOfNotes.sort(comp);
                return listOfNotes;

            case Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_OLD_FIRST:
                listOfNotes.sort(new Comparator<NoteTable>() {
                    @Override
                    public int compare(NoteTable note1, NoteTable note2) {
                        if(!note1.getDateOfCreation().isEmpty() && !note2.getDateOfCreation().isEmpty()){
                            if(DateHandler.convertStringToDate(note1.getDateOfCreation()).getTime()>DateHandler.convertStringToDate(note2.getDateOfCreation()).getTime()){
                                return 1;
                            }
                            else if(DateHandler.convertStringToDate(note1.getDateOfCreation()).getTime()<DateHandler.convertStringToDate(note2.getDateOfCreation()).getTime()){
                                return -1;
                            }
                            else return 0;
                        }
                        else{
                            return 0;
                        }
                    }
                });
                return listOfNotes;

            case Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_NEW_FIRST:
                listOfNotes.sort(new Comparator<NoteTable>() {
                    @Override
                    public int compare(NoteTable note1, NoteTable note2) {
                        if(!note1.getDateOfCreation().isEmpty() && !note2.getDateOfCreation().isEmpty()){
                            if(DateHandler.convertStringToDate(note1.getDateOfCreation()).getTime()>DateHandler.convertStringToDate(note2.getDateOfCreation()).getTime()){
                                return -1;
                            }
                            else if(DateHandler.convertStringToDate(note1.getDateOfCreation()).getTime()<DateHandler.convertStringToDate(note2.getDateOfCreation()).getTime()){
                                return 1;
                            }
                            else return 0;
                        }
                        else{
                            return 0;
                        }
                    }
                });
                return listOfNotes;
        }
        return listOfNotes;
    }

    private static class AlphabeticComparater implements Comparator<NoteTable> {

        @Override
        public int compare(NoteTable note1, NoteTable note2) {
            if(note1.getTitle()!=null && note2.getTitle()!=null && !note1.getTitle().isEmpty() && !note2.getTitle().isEmpty()){
                return note1.getTitle().compareTo(note2.getTitle());
            }else{
                return 0;
            }
        }
    }

    public static String getIntervalListString(ArrayList<String> listOfIntervals){
        StringBuffer str = new StringBuffer();
        for(String interval: listOfIntervals){
            str.append(interval).append(Constants.INTERVAL_STRING_SEPARATER);
        }
        if(str.length()>Constants.INTERVAL_STRING_SEPARATER.length()) {
            return str.substring(0, str.length() - Constants.INTERVAL_STRING_SEPARATER.length());
        }
        else{
            return "";
        }

    }
}
