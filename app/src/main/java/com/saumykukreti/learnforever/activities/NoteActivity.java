package com.saumykukreti.learnforever.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.dialog.IntervalDialog;
import com.saumykukreti.learnforever.dialog.ReviseDatesDialog;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.util.DateHandler;
import com.saumykukreti.learnforever.util.TextCreator;
import com.saumykukreti.learnforever.util.TextReader;
import com.saumykukreti.learnforever.util.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    //Intent constants
    public static final String METADATA_NOTE = "metadata_note";
    public static final String METADATA_FROM_WIDGET = "metadata_from_widget";
    public static final String METADATA_CATEGORY = "metadata_category";
    private static final int REQ_CODE_SPEECH_INPUT = 1001;
    private static final String TAG = NoteActivity.class.getSimpleName();

    private boolean isNewNote = false;
    private EditText mNoteTitleEdit;
    private EditText mNoteConetentInShortEdit;
    private EditText mNoteContentEdit;
    private NoteDataController mDataController;
    private NoteTable mNote;
    private Spinner mCategorySpinner;
    private List<String> mListOfCategories = new ArrayList<>();
    private boolean mNewCategory;
    private EditText mNewCategoryEdit;
    private Switch mLearnSwitch;
    private boolean mLearnState = false;
    private AutoCompleteTextView mCategoryAutoComplete;
    private ArrayAdapter<String> mAutoCompleteAdapter;
    private TextReader mTextReader;
    private SharedPreferences mSharedPreferences;
    private IntervalDialog mIntervalDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mDataController = NoteDataController.getInstance(this);
        mTextReader = new TextReader(this, getLifecycle());
        mSharedPreferences = getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);

        initialiseToolbar();
        initialiseViews();

        if (getIntent().hasExtra(METADATA_NOTE)) {
            //This means that it is an existing note and its data needs to be shown
            setData((NoteTable) getIntent().getParcelableExtra(METADATA_NOTE));
        } else {
            isNewNote = true;
        }

        if (isNewNote) {
            setVisibilityOfViewsBasedOnPreference();
            setCategoryValue();
        }
        //else make visible contents only those items that is of note

        setCategoryAutoComplete();

        //Hiding keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (getIntent().hasExtra(METADATA_FROM_WIDGET) && getIntent().getBooleanExtra(METADATA_FROM_WIDGET, false)) {
            //From widget, start dictation
            promptSpeechInput();
        }

        if (getIntent().getAction() != null &&
                getIntent().getAction().equals(Intent.ACTION_SEND) &&
                getIntent().getType() != null &&
                getIntent().getType().equals("text/plain")) {

            handleSendText(getIntent());
        }

    }

    /**
     * This method pre populates category value. This method is used when new note is being created from within category
     */
    private void setCategoryValue() {
        if(getIntent().hasExtra(METADATA_CATEGORY)){
            //Setting the visibility of category visible irrespective of preference and setting the value
            showCategoryViews();
            ((AutoCompleteTextView)findViewById(R.id.autocomplete_category)).setText(getIntent().getStringExtra(METADATA_CATEGORY));
        }
    }

    /**
     * This method handles the text sent by other applications
     *
     * @param intent - The received intent
     */
    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null && !sharedText.isEmpty()) {
            mNoteContentEdit.setText(sharedText);
        }
    }

    /**
     * This method sets the visibility of views based on preference settings
     */
    private void setVisibilityOfViewsBasedOnPreference() {
        if (mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_TITLE_SETTINGS, false)) {
            showTitleViews();
        }
        if (mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_CIS_SETTINGS, false)) {
            showCISViews();
        }
        if (mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_CATEGORY_SETTINGS, false)) {
            showCategoryViews();
        }
    }

    /**
     * Show all views related with category
     */
    private void showCategoryViews() {
        //Showing the fields
        findViewById(R.id.note_category).setVisibility(View.VISIBLE);
        findViewById(R.id.category_line).setVisibility(View.VISIBLE);
        findViewById(R.id.autocomplete_category).setVisibility(View.VISIBLE);
    }

    /**
     * Show all views related with cis
     */
    private void showCISViews() {
        //Showing the fields
        findViewById(R.id.note_cis).setVisibility(View.VISIBLE);
        findViewById(R.id.cis_line).setVisibility(View.VISIBLE);
        findViewById(R.id.edit_note_content_in_short).setVisibility(View.VISIBLE);

    }

    /**
     * Show all views related with title
     */
    private void showTitleViews() {
        //Showing the fields
        findViewById(R.id.note_title).setVisibility(View.VISIBLE);
        findViewById(R.id.title_line).setVisibility(View.VISIBLE);
        findViewById(R.id.edit_note_title).setVisibility(View.VISIBLE);

    }

    private void setCategoryAutoComplete() {
        mCategoryAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!mCategoryAutoComplete.isPerformingCompletion()) {
                    if (editable.length() > 0) {
                        setDateInCategoryAutoComplete(editable.toString());
                    } else {
                        setDateInCategoryAutoComplete("");
                    }
                }
            }
        });
    }

    private void setDateInCategoryAutoComplete(String value) {
        if (value.isEmpty()) {
            //Show all categories
            //Refreshing category list
            getAllCategories();
        } else {
            getCategoryWithValue(value);
        }

        //Check if adapter already made
        if (mCategoryAutoComplete.getAdapter() == null) {
            //Create a new adapter
            mAutoCompleteAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, mListOfCategories);
            mCategoryAutoComplete.setAdapter(mAutoCompleteAdapter);
        } else {
            mAutoCompleteAdapter.notifyDataSetChanged();
        }

        if (mListOfCategories != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCategoryAutoComplete.showDropDown();
                }
            }, 200);
        }

    }

    /**
     * This method intialises the toolbar for this activity
     */
    private void initialiseToolbar() {
        Toolbar toolbar = findViewById(R.id.note_activity_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        }
    }


    /**
     * This method initialises all the views that are needed across the activity and set on click listeners
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initialiseViews() {

        mNoteTitleEdit = findViewById(R.id.edit_note_title);
        mNoteConetentInShortEdit = findViewById(R.id.edit_note_content_in_short);
        mNoteContentEdit = findViewById(R.id.edit_note_content);
        mLearnSwitch = findViewById(R.id.learn_switch);
        mCategoryAutoComplete = findViewById(R.id.autocomplete_category);
        mNewCategoryEdit = findViewById(R.id.edit_category);

        mLearnSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mLearnState) {
                        //Is on currently, turning off

                        AlertDialog.Builder dialog = new AlertDialog.Builder(NoteActivity.this);

                        dialog.setTitle("Turning learn off will delete all reminders. Do you want to continue?");
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Delete selected notes
                                mLearnState = false;
                                mLearnSwitch.performClick();
                            }
                        });

                        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Do nothing
                            }
                        });

                        dialog.show();
                    } else {
                        mLearnState = true;
                        mLearnSwitch.performClick();
                    }
                }
                return true;
            }
        });

        mCategoryAutoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateInCategoryAutoComplete("");
            }
        });

        mCategoryAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setDateInCategoryAutoComplete("");
                }
            }
        });

    }

    /**
     * This method sets the data to the views, in case of existing note
     *
     * @param note
     */
    private void setData(NoteTable note) {
        if (note != null) {
            mNote = note;
            if (!note.getTitle().isEmpty()) {
                showTitleViews();
                mNoteTitleEdit.setText(note.getTitle());
            }

            if (!note.getContentInShort().isEmpty()) {
                showCISViews();
                mNoteConetentInShortEdit.setText(note.getContentInShort());
            }

            if (!note.getCategory().isEmpty()) {
                showCategoryViews();
                mCategoryAutoComplete.setText(note.getCategory());
            }

            mNoteContentEdit.setText(note.getContent());
            mLearnSwitch.setChecked(note.isLearn());
            mLearnState = note.isLearn();
        } else {
            isNewNote = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (!isNewNote) {
            menuInflater.inflate(R.menu.menu_for_note_activity, menu);
        } else {
            menuInflater.inflate(R.menu.menu_for_note_activity_new_note, menu);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.note_activity_delete:
                //Ask for deletion
                askForConfirmationAndDeleteNote();
                break;
            case R.id.note_activity_show_all_views:
                //Ask for deletion
                showCategoryViews();
                showCISViews();
                showTitleViews();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.note_activity_dictate:
                promptSpeechInput();
                break;
            case R.id.note_activity_show_revise_dates:
                openReviseDatesDialog();
                break;

            case R.id.note_activity_speak:
                //Checking if the note is currently being read, if so stop the reading else start it
                if (mTextReader.isReading()) {
                    mTextReader.stopReading();
                } else {
                    mTextReader.readAloud(TextCreator.getNoteTextForReading(mNote));
                }
                break;
            case R.id.action_help:
                Utility.showHelp(this, getResources().getString(R.string.help_string_note_activity));
                return true;
            case R.id.note_activity_change_interval:
                //Show revise interval dialog
                mIntervalDialog = new IntervalDialog(this);
                mIntervalDialog.show();
                return true;
        }
        return true;
    }

    /**
     * This method shows the upcoming dates of revision
     */
    private void openReviseDatesDialog() {
        ReviseDatesDialog reviseDatesDialog = new ReviseDatesDialog(this, mNote);
        reviseDatesDialog.show();
    }

    /**
     * This method
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now now");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Log.e(TAG, "Dictation not initialised");
        }
    }

    /**
     * This method gets the postion of category that corresponds with current category
     *
     * @param currentCategory
     */
    private int getCategoryPosition(String currentCategory) {
        if (currentCategory.equalsIgnoreCase(""))
            return -1;

        for (int i = 0; i < mListOfCategories.size(); i++) {
            String category = mListOfCategories.get(i);
            if (category.equalsIgnoreCase(currentCategory)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {
        //Saving note
        if (isNewNote) {
            String category = getCategoryValue();

            //Create a new note
            mDataController.newNote(category,
                    mNoteTitleEdit.getText().toString(),
                    mNoteConetentInShortEdit.getText().toString(),
                    mNoteContentEdit.getText().toString(),
                    DateHandler.convertDateToString(new Date()),
                    mLearnState);
        } else {
            updateNoteValues();
            mDataController.updateNote(mNote);
        }
        super.onBackPressed();
    }


    private void askForConfirmationAndDeleteNote() {

        //Ask for confirmation
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Are you sure you want to delete the selected notes");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Delete selected notes
                mDataController.deleteNote(mNote);
                NoteActivity.this.finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do nothing
            }
        });

        dialog.show();
    }

    /**
     * This method gets the category value
     */
    private String getCategoryValue() {
        return mCategoryAutoComplete.getText().toString();
    }

    /**
     * This method updates the values of mNote with updated values
     */
    private void updateNoteValues() {
        String category = getCategoryValue();

        mNote.setCategory(category);
        mNote.setTitle(mNoteTitleEdit.getText().toString());
        mNote.setContentInShort(mNoteConetentInShortEdit.getText().toString());
        mNote.setContent(mNoteContentEdit.getText().toString());
        mNote.setLearn(mLearnState);
    }

    /**
     * This method gets the list of categories from the database
     */
    private void getAllCategories() {
        mListOfCategories = mDataController.getListOfCategories();
    }

    /**
     * This method gets the list of categories from the database
     */
    private void getCategoryWithValue(String text) {
        List<String> listOfCategories = mDataController.getListOfCategoriesWithValue(text);

        mListOfCategories.clear();
        mListOfCategories.addAll(listOfCategories);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mNoteContentEdit.setText(mNoteContentEdit.getText() +" "+ result.get(0));
        }
        else if(resultCode == Constants.RESULT_LIST_CHANGED){
            mIntervalDialog.dataSetChanged();
        }
    }
}
