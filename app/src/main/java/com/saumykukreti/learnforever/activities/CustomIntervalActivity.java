package com.saumykukreti.learnforever.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.fragments.SettingsFragment;
import com.saumykukreti.learnforever.util.Converter;
import com.saumykukreti.learnforever.util.Utility;

import java.util.ArrayList;

public class CustomIntervalActivity extends AppCompatActivity {

    private Button mGoBack;
    private Button mGoForward;
    private Button mDone;
    private ArrayList<Integer> mListOfDays;
    private int mIndex;
    private EditText mEditText;
    private TextView mCurrentListTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_interval);

        initialiseToolbar();

        mListOfDays = new ArrayList<Integer>();
        mIndex = 0;

        mCurrentListTV = findViewById(R.id.text_current_list);
        mEditText = findViewById(R.id.day_edit_text);
        mGoBack = findViewById(R.id.button_go_back);
        mGoForward = findViewById(R.id.button_go_forward);
        mDone = findViewById(R.id.button_done);

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mGoForward.performClick();
                return true;
            }
        });

        mGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListOfDays.size()!=0){
                    mIndex-=1;
                    mEditText.setText(mListOfDays.get(mIndex).toString());
                    mEditText.setSelection(mListOfDays.get(mIndex).toString().length());
                    mListOfDays.remove(mIndex);
                    showCurrentList();
                }
            }
        });
        mGoForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText.getText().toString().length()>0){
                    mListOfDays.add(Integer.valueOf(mEditText.getText().toString()));
                    mIndex +=1;

                    //Clearing the edit text
                    mEditText.setText("");
                    showCurrentList();
                }
            }
        });
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mListOfDays.isEmpty()){
                    //If so save the list in preference
                    String newInterval = Converter.convertIntegerListToString(mListOfDays);
                    String listOfIntervals = Utility.getStringFromPreference(CustomIntervalActivity.this, Constants.LEARN_FOREVER_PREFERENCE_LIST_OF_INTERVALS);

                    String[] intervals = listOfIntervals.split(Constants.INTERVAL_STRING_SEPARATER);
                    ArrayList<String> arrayListOfIntervals = new ArrayList<>();

                    for(String interval : intervals){
                        arrayListOfIntervals.add(interval);
                    }

                    if(!arrayListOfIntervals.contains(newInterval)){
                        //Add the new interval in the preference
                        arrayListOfIntervals.add(newInterval);
                        String listOfIntervalString = Utility.getIntervalListString(arrayListOfIntervals);
                        Utility.saveStringInPreference(CustomIntervalActivity.this, Constants.LEARN_FOREVER_PREFERENCE_LIST_OF_INTERVALS, listOfIntervalString);
                    }
                    setResult(SettingsFragment.LIST_CHANGED);
                }
                else{
                    setResult(SettingsFragment.CANCELLED);
                }
                finish();
            }
        });
    }

    /**
     * This method initialises the toolbar
     */
    private void initialiseToolbar() {
        getSupportActionBar().setTitle("Custom Interval");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showCurrentList() {
        StringBuffer stringBuffer = new StringBuffer();
        for(Integer i : mListOfDays){
            stringBuffer.append(i).append(", ");
        }
        mCurrentListTV.setText(stringBuffer.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
