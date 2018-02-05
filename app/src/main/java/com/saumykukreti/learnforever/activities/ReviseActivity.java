package com.saumykukreti.learnforever.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.adapters.RevisePagerAdapter;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;
import com.saumykukreti.learnforever.util.Converter;
import com.saumykukreti.learnforever.util.DateHandler;
import com.saumykukreti.learnforever.util.TextCreator;
import com.saumykukreti.learnforever.util.TextReader;
import com.saumykukreti.learnforever.util.Utility;

import java.util.Date;
import java.util.List;

public class ReviseActivity extends AppCompatActivity {

    public static final String METADATA_POSITION = "metadata_position";
    private List<NoteTable> mNoteList;
    private TextReader mTextReader;
    private boolean mIsSpeechOn = true;
    private boolean mIsSpeechIconVisible = false;
    private int mPageNumber;
    private boolean mTtsInitialised = false;
    private SharedPreferences mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
        mTextReader = new TextReader(this, getLifecycle());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getValuesFromPreference();
        initialiseViews();
        getNotesToRevise();
        if(mNoteList!=null && !mNoteList.isEmpty()) {
            getPosition();
            setViewPager();
            setPageNumber(mPageNumber);
            startReadingFirstNote();
        }else{
            //Show some error
        }
    }

    /**
     *  This method checks if any position has been sent, if not returns one
     */
    private void getPosition() {
        if(getIntent().hasExtra(METADATA_POSITION)){
            mPageNumber = getIntent().getIntExtra(METADATA_POSITION,0);
        }else{
            mPageNumber = 0;
        }
    }

    private void getValuesFromPreference() {
        mPreference = getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);
        mIsSpeechOn = mPreference.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_IS_SPEECH_ON, true);
    }

    /**
     *  This method initialises the views and sets on click listeners on them
     */
    private void initialiseViews() {
        LinearLayout settingLinearLayout = findViewById(R.id.linear_index_container);
        final ImageView noSpeechImage = findViewById(R.id.image_speech_off);
        final ImageView arrowImage = findViewById(R.id.image_up_arrow);

        //Setting speech icon
        if(mIsSpeechOn){
            noSpeechImage.setBackground(getResources().getDrawable(R.drawable.ic_volume_on_white));
        }
        else{
            noSpeechImage.setBackground(getResources().getDrawable(R.drawable.ic_volume_off_white));
        }

        settingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup container = findViewById(R.id.revise_activity_relative_layout);
                TransitionManager.beginDelayedTransition(container);

                if(!mIsSpeechIconVisible) {
                    mIsSpeechIconVisible= true;
                    noSpeechImage.setVisibility(View.VISIBLE);
                    arrowImage.animate().rotation(180);
                }else{
                    mIsSpeechIconVisible = false;
                    noSpeechImage.setVisibility(View.GONE);
                    arrowImage.animate().rotation(0);
                }

            }
        });

        noSpeechImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsSpeechOn){
                    //Turing volume/ speech off
                    mIsSpeechOn = false;
                    noSpeechImage.setBackground(getResources().getDrawable(R.drawable.ic_volume_off_white));
                    Toast.makeText(ReviseActivity.this, "Speech off", Toast.LENGTH_SHORT).show();

                    //Stop if currently reading
                    mTextReader.stopReading();
                }
                else{
                    mIsSpeechOn = true;
                    noSpeechImage.setBackground(getResources().getDrawable(R.drawable.ic_volume_on_white));
                    Toast.makeText(ReviseActivity.this, "Speech on", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *  This method sets the page number
     * @param pageNo
     */
    private void setPageNumber(int pageNo) {
        TextView pageNumberText = findViewById(R.id.text_view_index);
        pageNumberText.setText(pageNo+1+"/"+mNoteList.size());
    }

    /**
     *  This method starts reading the first note
     */
    private void startReadingFirstNote() {
        if(mPageNumber==0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mIsSpeechOn) {
                        mTextReader.readAloud(TextCreator.getNoteTextForReading(mNoteList.get(mPageNumber)));
                    } else {
                        Toast.makeText(ReviseActivity.this, "Speech is off!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000);
        }
        mTtsInitialised = true;
    }

    private void setViewPager() {
        ViewPager notesViewPager = findViewById(R.id.view_pager_notes);

        RevisePagerAdapter pagerAdapter = new RevisePagerAdapter(this, mNoteList, new RevisePagerAdapter.RevisePagerAdapterListener() {
            @Override
            public void noteClicked() {
                mTextReader.stopReading();
            }
        });
        notesViewPager.setAdapter(pagerAdapter);
        notesViewPager.setPageMargin(20);
        notesViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                setPageNumber(position);
                if(mIsSpeechOn) {
                    if(mTtsInitialised) {
                        mTextReader.readAloud(TextCreator.getNoteTextForReading(mNoteList.get(position)));
                    }
                    else{
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mIsSpeechOn) {
                                    mTextReader.readAloud(TextCreator.getNoteTextForReading(mNoteList.get(position)));
                                }
                            }
                        }, 1000);
                        mTtsInitialised=true;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(mPageNumber!=0){
            notesViewPager.setCurrentItem(mPageNumber);
        }

    }

    /**
     *  This method gets the notes that are to be revised today
     */
    private void getNotesToRevise() {
        List<String> notesToRemind = Utility.getNoteIdsToRemind(this);
        //Getting the notes
        mNoteList = NoteDataController.getInstance(this).getNoteWithIds(notesToRemind);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Save speech preference in shared preference
        mPreference.edit().putBoolean(Constants.LEARN_FOREVER_PREFERENCE_IS_SPEECH_ON, mIsSpeechOn).apply();
        mPreference.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_LAST_REVISE_DATE, DateHandler.convertDateToString(new Date())).apply();

    }
}
