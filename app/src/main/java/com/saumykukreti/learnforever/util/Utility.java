package com.saumykukreti.learnforever.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.saumykukreti.learnforever.brodcastReceiver.NotificationBuilder;
import com.saumykukreti.learnforever.constants.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
}
