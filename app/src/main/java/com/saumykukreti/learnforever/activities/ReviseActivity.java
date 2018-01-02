package com.saumykukreti.learnforever.activities;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.adapters.RevisePagerAdapter;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;
import com.saumykukreti.learnforever.util.Converter;
import com.saumykukreti.learnforever.util.TextCreator;
import com.saumykukreti.learnforever.util.TextReader;

import java.util.Date;
import java.util.List;

public class ReviseActivity extends AppCompatActivity {

    private List<NoteTable> mNoteList;
    private int mCurrentPage=0;
    private TextReader mTextReader;
    private boolean mIsVolumeOn = true;
    private boolean mIsSpeechIconVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
        mTextReader = new TextReader(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialiseViews();
        getNotesToRevise();
        if(mNoteList!=null && !mNoteList.isEmpty()) {
            setViewPager();
            setPageNumber(0);
            startReadingFirstNote();
        }else{
            //Show some error
        }
    }

    /**
     *  This method initialises the views and sets on click listeners on them
     */
    private void initialiseViews() {
        LinearLayout settingLinearLayout = findViewById(R.id.linear_index_container);
        final ImageView noSpeechImage = findViewById(R.id.image_speech_off);
        final ImageView arrowImage = findViewById(R.id.image_up_arrow);

        settingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mIsSpeechIconVisible) {
                    mIsSpeechIconVisible= true;
                    noSpeechImage.setVisibility(View.VISIBLE);
                    arrowImage.setBackground(getResources().getDrawable(R.drawable.ic_arrow_down_white));
                }else{
                    mIsSpeechIconVisible = false;
                    noSpeechImage.setVisibility(View.GONE);
                    arrowImage.setBackground(getResources().getDrawable(R.drawable.ic_arrow_up_white));
                }
            }
        });

        noSpeechImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsVolumeOn){
                    //Turing volume/ speech off
                    mIsVolumeOn = false;
                    noSpeechImage.setBackground(getResources().getDrawable(R.drawable.ic_volume_off_white));
                    Toast.makeText(ReviseActivity.this, "Speech off", Toast.LENGTH_SHORT).show();

                    //Stop if currently reading
                    mTextReader.stopReading();
                }
                else{
                    mIsVolumeOn = true;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextReader.readAloud(TextCreator.getNoteText(mNoteList.get(0)));
            }
        },1000);
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
            public void onPageSelected(int position) {
                mCurrentPage = position;
                setPageNumber(position);
                if(mIsVolumeOn) {
                    mTextReader.readAloud(TextCreator.getNoteText(mNoteList.get(position)));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     *  This method gets the notes that are to be revised today
     */
    private void getNotesToRevise() {

        ReminderTable todaysReminder = ReminderDataController.getInstance(this).getNotesForDate(new Date());
        if(todaysReminder!=null){
            String notesIdsToRemind = todaysReminder.getNoteIds();
            List<String> notesToRemind = Converter.convertStringToList(notesIdsToRemind);

            //Getting the notes
            mNoteList = NoteDataController.getInstance(this).getNoteWithIds(notesToRemind);
        }
    }
}
