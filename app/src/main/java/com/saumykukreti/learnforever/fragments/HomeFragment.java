package com.saumykukreti.learnforever.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.NavigationDrawerActivity;
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
    private boolean mSelectionModeOn;

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
        setHasOptionsMenu(true);
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
        mListener.updateActionBarForHomeFragment();
        initialiseNotesAdapter(true);
    }

    private void toggleCancelFabVisibility(boolean turnOn){
        FloatingActionButton cancelFab = getView().findViewById(R.id.fab_cancel);
        if(turnOn) {
            cancelFab.setVisibility(View.VISIBLE);
        }
        else{
            cancelFab.setVisibility(View.GONE);
        }
        cancelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAction();
            }
        });
    }

    private void cancelAction(){
        mSelectionModeOn = false;
        mHomeFragmentNotesRecyclerViewAdapter.toggleSelectionMode(false);
        initialiseNotesAdapter(false);
        toggleFabVisiblity(false);
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
        mHomeFragmentNotesRecyclerViewAdapter = new HomeFragmentNotesRecyclerViewAdapter(getActivity(), mAllNotes, new HomeFragmentAdapterInterationListener() {
            @Override
            public void toggleSelectionMode(boolean on) {
                mSelectionModeOn = on;
                toggleFabVisiblity(true);

            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(mHomeFragmentNotesRecyclerViewAdapter);
    }

    public void toggleFabVisiblity(boolean turnOffFab){
        //When selection mode is on turn off fab visibility and vice versa
        mListener.toggleFabVisibility(!turnOffFab);
        toggleCancelFabVisibility(turnOffFab);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home_delete:
                Toast.makeText(getContext(), "Delete home", Toast.LENGTH_SHORT).show();
                if(mSelectionModeOn){
                    //This means that already some notes have been selected and those have to be deleted
                    List<NoteTable> selectedNoteList = mHomeFragmentNotesRecyclerViewAdapter.getSelectedList();
                    if(!selectedNoteList.isEmpty()){
                        askForConfirmationAndDeleteNote(selectedNoteList);
                    }
                }
                else{
                    mSelectionModeOn = true;
                    //This means selection mode is off, turn it on
                    mHomeFragmentNotesRecyclerViewAdapter.toggleSelectionMode(true);
                    toggleFabVisiblity(true);
                }
                return true;
            case R.id.home_send:
                Toast.makeText(getContext(), "Send home", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.home_cancel:
                cancelAction();
        }
        return true;
    }

    private void askForConfirmationAndDeleteNote(final List<NoteTable> selectedNoteList) {

        //Ask for confirmation
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        dialog.setTitle("Are you sure you want to delete the selected notes");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Delete selected notes
                datacontroller.deleteNotes(selectedNoteList);

                //Refresh data list
                initialiseNotesAdapter(true);
                //After deleting turn it off
                mHomeFragmentNotesRecyclerViewAdapter.toggleSelectionMode(false);
                toggleFabVisiblity(false);
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Refresh data list
                initialiseNotesAdapter(true);
                //After deleting turn it off
                mHomeFragmentNotesRecyclerViewAdapter.toggleSelectionMode(false);
                toggleFabVisiblity(false);
            }
        });

        dialog.show();
    }

    public interface OnHomeFragmentInteractionListener {
        void updateActionBarForHomeFragment();
        void toggleFabVisibility(boolean on);
    }


    public interface HomeFragmentAdapterInterationListener{
        void toggleSelectionMode(boolean on);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.toggleFabVisibility(true);
    }
}
