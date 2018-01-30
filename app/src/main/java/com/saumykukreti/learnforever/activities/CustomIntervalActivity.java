package com.saumykukreti.learnforever.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.fragments.SettingsFragment;
import com.saumykukreti.learnforever.util.Converter;

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
                return false;
            }
        });

        mGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListOfDays.size()!=0){
                    mIndex-=1;
                    mEditText.setText(mListOfDays.get(mIndex).toString());
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
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL,"custom").apply();
                    sharedPreferences.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_CUSTOM_INTERVAL, Converter.convertIntegerListToString(mListOfDays)).apply();
                    setResult(SettingsFragment.LIST_CHANGED);
                }
                else{
                    setResult(SettingsFragment.CANCELLED);
                }
                finish();
            }
        });
    }

    private void showCurrentList() {
        StringBuffer stringBuffer = new StringBuffer();
        for(Integer i : mListOfDays){
            stringBuffer.append(i).append(", ");
        }
        mCurrentListTV.setText(stringBuffer.toString());
    }
}
