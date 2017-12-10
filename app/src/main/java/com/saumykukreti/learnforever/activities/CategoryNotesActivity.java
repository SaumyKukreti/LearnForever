package com.saumykukreti.learnforever.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.adapters.HomeFragmentNotesRecyclerViewAdapter;
import com.saumykukreti.learnforever.dataManager.DataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.List;

public class CategoryNotesActivity extends AppCompatActivity {

    public static final String METADATA_CATEGORY = "metadata_category";
    private static final String TAG = CategoryNotesActivity.class.getSimpleName();
    private List<NoteTable> mNoteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_notes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseNotesAdapter();
    }

    private void initialiseNotesAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_category_notes);
        getCategories();
        HomeFragmentNotesRecyclerViewAdapter homeFragmentNotesRecyclerViewAdapter = new HomeFragmentNotesRecyclerViewAdapter(this, mNoteList);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(homeFragmentNotesRecyclerViewAdapter);
    }

    /**
     *  This method gets the note list of a particular category
     */
    private void getCategories() {

        DataController dataController = DataController.getInstance(this);

        if(getIntent().hasExtra(METADATA_CATEGORY)){
            mNoteList = dataController.getNoteWithCategory(getIntent().getStringExtra(METADATA_CATEGORY));
            if(mNoteList.size()==0){
                Log.e(TAG, "No notes found for the selected category");
            }
        }
        else{
            Log.e(TAG, "No category found in the intent");
        }
    }
}
