package com.saumykukreti.learnforever.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    //Intent constants
    public static final String METADATA_NOTE = "metadata_note";

    private boolean isNewNote = false;
    private EditText mNoteTitleEdit;
    private EditText mNoteConetentInShortEdit;
    private EditText mNoteContentEdit;
    private NoteDataController mDataController;
    private NoteTable mNote;
    private Spinner mCategorySpinner;
    private List<String> mListOfCategories;
    private boolean mNewCategory;
    private EditText mNewCategoryEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mDataController = NoteDataController.getInstance(this);

        getCategories();
        initialiseViews();
        setCategorySpinner();

        if (getIntent().hasExtra(METADATA_NOTE)) {
            //This means that it is an existing note and its data needs to be shown
            setData((NoteTable) getIntent().getParcelableExtra(METADATA_NOTE));
        } else {
            isNewNote = true;
        }
    }

    /**
     * This method initialises all the views that are needed across the activity and set on click listeners
     */
    private void initialiseViews() {

        ImageView addCatgoryImage = findViewById(R.id.image_add_category);
        mNoteTitleEdit = findViewById(R.id.edit_note_title);
        mNoteConetentInShortEdit = findViewById(R.id.edit_note_content_in_short);
        mNoteContentEdit = findViewById(R.id.edit_note_content);
        mCategorySpinner = findViewById(R.id.categorySpinner);

        mNewCategoryEdit = findViewById(R.id.edit_category);

        addCatgoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNewCategoryEdit.getVisibility() == View.VISIBLE) {
                    mNewCategory = false;
                    mNewCategoryEdit.setVisibility(View.GONE);
                } else {
                    mNewCategory = true;
                    mNewCategoryEdit.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * This method initialises the category spinner
     */
    private void setCategorySpinner() {
        //Setting spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.note_activity_category_text_layout, mListOfCategories);
        arrayAdapter.setDropDownViewResource(R.layout.note_activity_category_list_text_layout);
        mCategorySpinner.setAdapter(arrayAdapter);
    }

    /**
     * This method sets the data to the views, in case of existing note
     *
     * @param note
     */
    private void setData(NoteTable note) {
        if (note != null) {
            mNote = note;
            mNoteTitleEdit.setText(note.getTitle());
            mNoteConetentInShortEdit.setText(note.getContentInShort());
            mNoteContentEdit.setText(note.getContent());

            //Get category position that corresponds with current category
            if (mNote.getCategory() != null && !mNote.getCategory().equalsIgnoreCase("")) {
                int position = getCategoryPosition(note.getCategory());
                if (position != -1) {
                    mCategorySpinner.setSelection(position);
                }
            }
        } else {
            isNewNote = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (!isNewNote) {
            menuInflater.inflate(R.menu.menu_for_note_activity, menu);
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
        }
        return true;
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
                    "timing",
                    true);
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
        String category = "";

        //Check if the user wants a new category, if so use the value from edit text but if that is empty use the current spinner value
        if (mNewCategory) {
            if (!mNewCategoryEdit.getText().toString().equalsIgnoreCase("")) {
                category = mNewCategoryEdit.getText().toString();
            }
        }

        //Getting value from spinner
        if (category.equalsIgnoreCase("")) {
            if (mCategorySpinner.getSelectedItemPosition() != 0) {
                //Do not set anything for default
                category = mCategorySpinner.getSelectedItem().toString();
            }
        }
        return category;
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
    }

    /**
     * This method gets the list of categories from the database
     */
    private void getCategories() {
        mListOfCategories = mDataController.getListOfCategories();

        //Adding default to the list
        mListOfCategories.add(0, "Default");

    }
}
