package com.saumykukreti.learnforever.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.adapters.ReviseDatesAdapter;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.util.DateHandler;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by saumy on 2/26/2018.
 */

public class ReviseDatesDialog extends Dialog{

    private final NoteTable mNote;
    private final Context mContext;
    private ArrayList<Date> mListOfReminderDates;

    public ReviseDatesDialog(@NonNull Context context, NoteTable note) {
        super(context);
        mNote = note;
        mContext = context;
    }

    public void show(){
        this.setContentView(R.layout.dialog_revise_dates);
        setParams();
        getContent();
        setContent();
        super.show();
    }

    /**
     * This method sets the content into the views
     */
    private void setContent() {
        if(!mListOfReminderDates.isEmpty()){
            RecyclerView reviseDatesRecyclerView = findViewById(R.id.recycler_view_revise_dates);
            ReviseDatesAdapter adapter = new ReviseDatesAdapter(mContext, mListOfReminderDates);

            reviseDatesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            reviseDatesRecyclerView.setAdapter(adapter);
        }
        else{
            Toast.makeText(mContext, "No reminder dates remaining!", Toast.LENGTH_SHORT).show();
            this.dismiss();
        }
    }

    /**
     * This method gets the revise dates
     */
    private void getContent() {
        String reminderDatesString = mNote.getReminderDates();
        String[] reminderDatesArray = reminderDatesString.split(",");

        ArrayList<Date> listOfAllReminderDates = new ArrayList<>();

        for(String reminderDate : reminderDatesArray){
            //Converting reminder date into actal date and adding it to a list
            listOfAllReminderDates.add(DateHandler.convertStringToDate(reminderDate));
        }

        //Sorting the list
        listOfAllReminderDates.sort(null);

        mListOfReminderDates = new ArrayList<>();
        Date currentDate = new Date();

        //Removing the days that have passed
        for(Date date : listOfAllReminderDates){
            if(date.getTime() > currentDate.getTime()){
                mListOfReminderDates.add(date);
            }
        }
    }

    /**
     * This method sets params on the dialog
     */
    private void setParams() {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        this.onWindowAttributesChanged(params);
    }
}
