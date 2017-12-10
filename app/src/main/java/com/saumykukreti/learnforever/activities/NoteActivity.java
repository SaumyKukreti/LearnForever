package com.saumykukreti.learnforever.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.dataManager.DataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final String METADATA_NOTE = "metadata_note";
    private boolean isNewNote = false;
    private EditText mNoteTitleEdit;
    private EditText mNoteConetentInShortEdit;
    private EditText mNoteContentEdit;
    private DataController mDataController;
    private NoteTable mNote;
    private Spinner mCategorySpinner;
    private List<String> mListOfCategories;
    private ImageView mAddCatgoryImage;
    private boolean mNewCategory;
    private EditText mNewCategoryEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mDataController = DataController.getInstance(this);

        getCategories();
        initialiseViews();

        if(getIntent().hasExtra(METADATA_NOTE)){
            setData((NoteTable)getIntent().getParcelableExtra(METADATA_NOTE));
        }
        else{
            isNewNote = true;
        }
    }

    /**
     *  This method initialises all the views that are needed across the activity
     */
    private void initialiseViews() {

        mAddCatgoryImage = findViewById(R.id.image_add_category);

        mNoteTitleEdit = findViewById(R.id.edit_note_title);
        mNoteConetentInShortEdit = findViewById(R.id.edit_note_content_in_short);
        mNoteContentEdit = findViewById(R.id.edit_note_content);
        mCategorySpinner = findViewById(R.id.categorySpinner);

        mNewCategoryEdit = findViewById(R.id.edit_category);

        mAddCatgoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mNewCategoryEdit.getVisibility()==View.VISIBLE){
                    mNewCategory = false;
                    mNewCategoryEdit.setVisibility(View.GONE);
                }
                else{
                    mNewCategory = true;
                    mNewCategoryEdit.setVisibility(View.VISIBLE);
                }
            }
        });

        //Adding default to the list
        mListOfCategories.add(0,"Default");

        //Setting spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mListOfCategories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mCategorySpinner.setAdapter(arrayAdapter);
    }

    /**
     *  This method sets the data to the views, in case of existing note
     * @param note
     */
    private void setData(NoteTable note) {
        mNote = note;
        mNoteTitleEdit.setText(note.getTitle());
        mNoteConetentInShortEdit.setText(note.getContentInShort());
        mNoteContentEdit.setText(note.getContent());

        //Get category position that corresponds with current category
        if(mNote.getCategory()!=null && !mNote.getCategory().equalsIgnoreCase("")){
            int position = getCategoryPosition(note.getCategory());
            if(position!=-1) {
                mCategorySpinner.setSelection(position);
            }
        }
    }

    /**
     *  This method gets the postion of category that corresponds with current category
     * @param currentCategory
     */
    private int getCategoryPosition(String currentCategory) {
        if(currentCategory.equalsIgnoreCase(""))
            return -1;

        for(int i =0 ; i<mListOfCategories.size();i++){
            String category = mListOfCategories.get(i);
            if(category.equalsIgnoreCase(currentCategory)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {
        //Saving note
        if(isNewNote){

            String category =getCategoryValue();

            //Create a new note
            mDataController.newNote(category,
                    mNoteTitleEdit.getText().toString(),
                    mNoteConetentInShortEdit.getText().toString(),
                    mNoteContentEdit.getText().toString(),
                    "timing",
                    true);
        }else{
            updateNoteValues();
            mDataController.updateNote(mNote);
        }
        super.onBackPressed();
    }

    /**
     *  This method gets the category value
     */
    private String getCategoryValue() {
        String category = "";

        //Check if the user wants a new category, if so use the value from edit text but if that is empty use the current spinner value
        if(mNewCategory){
            if(!mNewCategoryEdit.getText().toString().equalsIgnoreCase("")){
                category = mNewCategoryEdit.getText().toString();
            }
        }

        if(category.equalsIgnoreCase("")){
            if (mCategorySpinner.getSelectedItemPosition() != 0) {
                //Do not set anything for default
                category = mCategorySpinner.getSelectedItem().toString();
            }
        }

        return category;
    }

    /**
     *  This method updates the values of mNote with updated values
     */
    private void updateNoteValues() {
        String category =getCategoryValue();

        mNote.setCategory(category);
//        if(mCategorySpinner.getSelectedItemPosition()!=0){
//            //Do not set anything for default
//            mNote.setCategory(mCategorySpinner.getSelectedItem().toString());
//        }else{
//            mNote.setCategory("");
//        }
        mNote.setTitle(mNoteTitleEdit.getText().toString());
        mNote.setContentInShort(mNoteConetentInShortEdit.getText().toString());
        mNote.setContent(mNoteContentEdit.getText().toString());
    }

    /**
     *  This method gets the list of categories from the database
     */
    private void getCategories() {
        mListOfCategories = mDataController.getListOfCategories();
    }
}
