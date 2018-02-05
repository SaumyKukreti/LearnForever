package com.saumykukreti.learnforever.brodcastReceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.ReviseActivity;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;
import com.saumykukreti.learnforever.util.Utility;

import java.util.Date;
import java.util.List;

/**
 * Created by saumy on 12/27/2017.
 */

public class NotificationBuilder extends BroadcastReceiver {

    private static final String TAG = BroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Inside broadcast receiver!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        //Checking if there are any notes to revise, if so, show a notification else ignore
        List<String> noteIdsToRemind = Utility.getNoteIdsToRemind(context);
        if (!noteIdsToRemind.isEmpty()) {
            //This means we have some notes to revise today

            //Here we make a notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent reviseIntent = new Intent(context, ReviseActivity.class);
            reviseIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, Constants.NOTIFICATION_REQUEST_CODE, reviseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.learn_forever_icon)
                    .setContentTitle("Time to revise some notes")
                    .setContentText("Tap to start revising now")
                    .setAutoCancel(true);

            notificationManager.notify(Constants.NOTIFICATION_REQUEST_CODE, builder.build());
        }
    }
}
