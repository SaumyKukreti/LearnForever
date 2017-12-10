package com.saumykukreti.learnforever.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.adapters.HomeFragmentNotesRecyclerViewAdapter;
import com.saumykukreti.learnforever.dataManager.DataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.List;

public class HomeFragment extends Fragment {
    private OnHomeFragmentInteractionListener mListener;
    private DataController datacontroller;
    private List<NoteTable> mAllNotes;
    private HomeFragmentNotesRecyclerViewAdapter mHomeFragmentNotesRecyclerViewAdapter;
    private boolean mNoteListUpdated;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Get arguments here
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoriesFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        datacontroller = DataController.getInstance(getActivity());

        //Initialise search view
        initialiseSearchView();
    }

    /**
     *  This method sets text watcher on the search view and
     */
    private void initialiseSearchView() {
        EditText searchEdit = getView().findViewById(R.id.edit_search);


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>2){
                    //Boolean to keep track if the current list is updated or not to avoid unnecessary database calls
                    mNoteListUpdated = true;
                    mAllNotes = datacontroller.searchNoteWithString(charSequence.toString());
                    initialiseNotesAdapter(false);
                }
                else{
                    //Show all notes
                    if(mNoteListUpdated) {
                        mNoteListUpdated = false;
                        mAllNotes = datacontroller.getAllNotes();
                        initialiseNotesAdapter(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        searchEdit.addTextChangedListener(textWatcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        initialiseNotesAdapter(true);
    }

    /**
     *  This method initialises the recycler view with notes
     */
    private void initialiseNotesAdapter(boolean updateNoteList) {
        if(updateNoteList){
            mAllNotes = datacontroller.getAllNotes();
        }
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_notes);
        recyclerView.removeAllViews();
        mHomeFragmentNotesRecyclerViewAdapter = new HomeFragmentNotesRecyclerViewAdapter(getActivity(), mAllNotes);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(mHomeFragmentNotesRecyclerViewAdapter);
    }

    public interface OnHomeFragmentInteractionListener {

    }
}
