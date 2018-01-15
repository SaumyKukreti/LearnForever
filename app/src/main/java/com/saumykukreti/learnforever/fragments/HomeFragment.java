package com.saumykukreti.learnforever.fragments;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.NavigationDrawerActivity;
import com.saumykukreti.learnforever.adapters.HomeFragmentNotesRecyclerViewAdapter;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.jobs.DataSyncJob;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.util.TextCreator;
import com.saumykukreti.learnforever.util.TextReader;

import java.util.List;

public class HomeFragment extends Fragment{
    private OnHomeFragmentInteractionListener mListener;
    private NoteDataController datacontroller;
    private List<NoteTable> mAllNotes;
    private HomeFragmentNotesRecyclerViewAdapter mHomeFragmentNotesRecyclerViewAdapter;
    private boolean mNoteListUpdated;
    private boolean mSelectionModeOn;
    private TextReader mTextReader;

    private static final String METADATA_CATEGORY = "metadata_category";
    private String mCategory;
    private EditText mSearchEdit;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String categoryName) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(METADATA_CATEGORY, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        datacontroller = NoteDataController.getInstance(getActivity());
        if (getArguments() != null) {
            mCategory = getArguments().getString(METADATA_CATEGORY);
        }

        //Hiding the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        mTextReader = new TextReader(getContext().getApplicationContext(), getLifecycle());
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
        mTextReader =null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialiseViews();
        if(mCategory!=null && !mCategory.isEmpty()){
            //Means this activity shows notes of a particular category

            //Update mNotesList and initialise adapter
            mAllNotes = datacontroller.getNoteWithCategory(mCategory);

            initialiseNotesAdapter(false);

            //Hide search view
            setLiveDataForCategory();
        }
        else{
            //Modify fragment to show all notes
            initialiseNotesAdapter(true);

            //Initialise live data on list
            setLiveData();
        }


        //Initialise search view
        initialiseSearchView();

    }

    /**
     *  This method intialises all the views that are used across the fragment
     */
    private void initialiseViews() {
        mSearchEdit = getView().findViewById(R.id.edit_search);

    }

    private void setLiveDataForCategory() {
        LiveData<List<NoteTable>> livenotes = datacontroller.getDatabase().noteDao().getLiveAllNotesWithCategory(mCategory);
        livenotes.observe(this, new Observer<List<NoteTable>>() {
            @Override
            public void onChanged(@Nullable List<NoteTable> noteTables) {
                mHomeFragmentNotesRecyclerViewAdapter.setNoteTableList(noteTables);
                mHomeFragmentNotesRecyclerViewAdapter.notifyDataSetChanged();
                syncData();
            }});
    }


    private void setLiveData() {
        LiveData<List<NoteTable>> livenotes = datacontroller.getDatabase().noteDao().getLiveAllNotes();
        livenotes.observe(this, new Observer<List<NoteTable>>() {
            @Override
            public void onChanged(@Nullable List<NoteTable> noteTables) {
                mHomeFragmentNotesRecyclerViewAdapter.setNoteTableList(noteTables);
                mHomeFragmentNotesRecyclerViewAdapter.notifyDataSetChanged();
                syncData();
            }});
    }

    /**
     *  This method sets text watcher on the search view and
     */
    private void initialiseSearchView() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>2){
                    //Boolean to keep track if the current list is updated or not to avoid unnecessary database calls
                    mNoteListUpdated = true;
                    if(mCategory!=null && !mCategory.isEmpty()){
                        mAllNotes = datacontroller.searchNoteWithStringAndCategory(charSequence.toString(), mCategory);
                    }
                    else {
                        mAllNotes = datacontroller.searchNoteWithString(charSequence.toString());
                    }
                    initialiseNotesAdapter(false);
                }
                else{
                    //Show all notes
                    if(mNoteListUpdated) {
                        mNoteListUpdated = false;
                        if(mCategory!=null && !mCategory.isEmpty()){
                            mAllNotes = datacontroller.getNoteWithCategory(mCategory);
                        }
                        else {
                            mAllNotes = datacontroller.getAllNotes();
                        }
                        initialiseNotesAdapter(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        mSearchEdit.addTextChangedListener(textWatcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        syncData();
        mListener.updateActionBarForHomeFragment();
    }

    /**
     *  This method starts a job that sync data to firebase
     */
    private void syncData() {
        LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DataSyncJob(getContext(),null));
    }

    /**
     * Thie method
     *
     * @param turnOn - Pass true to show cancel button and vice versa
     */
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

    /**
     *  This method is used to disable selection mode
     */
    private void cancelAction(){
        mTextReader.stopReading();
        setSelectionMode(false);
        mHomeFragmentNotesRecyclerViewAdapter.clearSelectedList();
        mHomeFragmentNotesRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * This method initialises the recycler view with notes
     *
     * @param updateNoteList - Pass true when you want the list data to be refreshed else pass false
     */
    public void initialiseNotesAdapter(boolean updateNoteList) {
        if(updateNoteList){
            mAllNotes = datacontroller.getAllNotes();
        }
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_notes);
        recyclerView.removeAllViews();
        mHomeFragmentNotesRecyclerViewAdapter = new HomeFragmentNotesRecyclerViewAdapter(getActivity(), mAllNotes, new HomeFragmentAdapterInteractionListener() {
            @Override
            public void toggleSelectionMode(boolean on) {
                setSelectionMode(on);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(mHomeFragmentNotesRecyclerViewAdapter);


        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSearchEdit.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        final Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSearchEdit.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    //If velocity is positive, the user is scolling down and vice versa
                    ViewGroup view = getView().findViewById(R.id.fragment_linear_container);
                    TransitionManager.beginDelayedTransition(view);
                    if(velocityY>0){
                        //Scrolling down
                        mSearchEdit.startAnimation(animation);

                    }else{
                        //Scolling up
                        mSearchEdit.startAnimation(animation2);
                    }
                    return false;
                }
            });
        }
    }

    /**
     *  This method controls the fab button in the navigation drawer activity
     *
     * @param turnOn - Pass true to show fab and vice versa
     */
    public void toggleFabVisiblity(boolean turnOn){
        //When selection mode is on turn off fab visibility and vice versa
        mListener.toggleFabVisibility(turnOn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home_delete:
                handleDeleteButtonPress();
                return true;
            case R.id.home_read:
                readSelectedNotes();
                return true;
            case R.id.home_send:
                Toast.makeText(getContext(), "Send home", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.home_cancel:
                cancelAction();
                return true;
            case R.id.home_add_to_category:
                return true;
        }
        return true;
    }

    /**
     *  This method handles the case when delete button is pressed
     */
    private void handleDeleteButtonPress() {
        if(mSelectionModeOn){
            //This means that already some notes have been selected and those have to be deleted
            List<NoteTable> selectedNoteList = mHomeFragmentNotesRecyclerViewAdapter.getSelectedList();
            if(!selectedNoteList.isEmpty()){
                askForConfirmationAndDeleteNote(selectedNoteList);
            }
        }
        else{
            setSelectionMode(true);
        }
    }

    /**
     *  This method controls the visibility of buttons
     * @param turnOn
     */
    public void setSelectionMode(boolean turnOn){
        mSelectionModeOn = turnOn;

        if(turnOn){
            //Notifying the adapter
            mHomeFragmentNotesRecyclerViewAdapter.toggleSelectionMode(true);

            //Changing the visisblity of fabs
            toggleFabVisiblity(false);
            toggleCancelFabVisibility(true);
        }
        else{
            //Notifying the adapter
            mHomeFragmentNotesRecyclerViewAdapter.toggleSelectionMode(false);

            //Changing the visisblity of fabs
            toggleFabVisiblity(true);
            toggleCancelFabVisibility(false);
        }
    }

    /**
     *  This method shows a dialog asking the user to delete the current note or not
     *
     * @param selectedNoteList
     */
    private void askForConfirmationAndDeleteNote(final List<NoteTable> selectedNoteList) {
        //Ask for confirmation
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        dialog.setTitle("Are you sure you want to delete the selected notes");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Delete selected notes
                datacontroller.deleteNotes(selectedNoteList);
                setSelectionMode(false);
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelAction();
            }
        });
        dialog.show();
    }

    public interface OnHomeFragmentInteractionListener {
        void updateActionBarForHomeFragment();
        void toggleFabVisibility(boolean on);
    }


    public interface HomeFragmentAdapterInteractionListener {
        void toggleSelectionMode(boolean on);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.toggleFabVisibility(true);
    }

    public boolean getSelectionMode(){
        return mSelectionModeOn;
    }

    public void onNavigationFabLongClick(){
    }


    private void readSelectedNotes() {
        //Check if some notes are selected
        if(mSelectionModeOn){
            List<NoteTable> listOfSelectedNotes = mHomeFragmentNotesRecyclerViewAdapter.getSelectedList();
            mTextReader.readAloud(TextCreator.getNoteText(listOfSelectedNotes));
        }
        else{
            setSelectionMode(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTextReader = null;
    }
}