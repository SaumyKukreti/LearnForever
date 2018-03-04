package com.saumykukreti.learnforever.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.CustomIntervalActivity;
import com.saumykukreti.learnforever.adapters.IntervalDialogRecyclerAdapter;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.util.Utility;

import java.util.ArrayList;

/**
 * Created by saumy on 2/21/2018.
 */

public class IntervalDialog extends Dialog {
    private final Context mContext;
    private ArrayList<String> mArrayListOfIntervals = new ArrayList<>();
    private IntervalDialogRecyclerAdapter mAdapter;

    public IntervalDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void show(){
        this.setContentView(R.layout.dialog_settings_revise_interval);
        setParams(this);
        setClickListeners();
        getDataFromPreference();
        setDataInViews();
        super.show();
    }

    /**
     * This method gets data from preference
     */
    private void getDataFromPreference() {
        String listOfIntervals = Utility.getStringFromPreference(mContext, Constants.LEARN_FOREVER_PREFERENCE_LIST_OF_INTERVALS);
        getIntervalListFromPreferenceString(listOfIntervals);
    }

    /**
     * This method spearates all the intervals into individual intervals and store them in a member variable
     * @param listOfIntervals
     */
    private void getIntervalListFromPreferenceString(String listOfIntervals) {
        String[] intervals = listOfIntervals.split(Constants.INTERVAL_STRING_SEPARATER);
        mArrayListOfIntervals.clear();
        for(String interval : intervals){
            mArrayListOfIntervals.add(interval);
        }
    }

    /**
     * This method sets the data received from preference on the view
     */
    private void setDataInViews() {
        RecyclerView intervalRecyclerView = this.findViewById(R.id.intervals_recycler_view);
        intervalRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mAdapter = new IntervalDialogRecyclerAdapter(mContext, mArrayListOfIntervals, new intervalDialogListener() {
            @Override
            public void intervalClicked(int position) {
                //Save selection into preference
                Utility.saveStringInPreference(mContext,Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL, mArrayListOfIntervals.get(position));
                IntervalDialog.this.dismiss();
                return;
            }

            @Override
            public void intervalDeleted(int position) {
                handleDeletion(position);
            }
        });
        intervalRecyclerView.setAdapter(mAdapter);
    }

    private void handleDeletion(int position) {
        mArrayListOfIntervals.remove(mArrayListOfIntervals.get(position));
        mAdapter.notifyDataSetChanged();
        StringBuffer str = new StringBuffer();
        for(String interval: mArrayListOfIntervals){
            str.append(interval).append(Constants.INTERVAL_STRING_SEPARATER);
        }
        if(str.length()>Constants.INTERVAL_STRING_SEPARATER.length()) {
            str.substring(0, str.length() - Constants.INTERVAL_STRING_SEPARATER.length());
            Utility.saveStringInPreference(mContext, Constants.LEARN_FOREVER_PREFERENCE_LIST_OF_INTERVALS, str.toString());
        }
    }

    /**
     *  This method sets click listeners on the views
     */
    private void setClickListeners() {
        //Handling click of add new category
        this.findViewById(R.id.linear_add_new_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomIntervalActivity();
            }
        });
    }

    /**
     * This method sets the params to dialog
     * @param dialog
     */
    private void setParams(Dialog dialog) {
        //Setting dialog params
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.onWindowAttributesChanged(params);
    }

    private void openCustomIntervalActivity() {
        Intent intent = new Intent(getContext(), CustomIntervalActivity.class);
        ((Activity)mContext).startActivityForResult(intent,1020);
    }

    public void dataSetChanged() {
        getDataFromPreference();
        Toast.makeText(mContext, "Interval Added", Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }

    public interface intervalDialogListener{
        void intervalClicked(int position);
        void intervalDeleted(int position);
    }

}
