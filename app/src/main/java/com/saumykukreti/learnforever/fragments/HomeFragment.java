package com.saumykukreti.learnforever.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.ReviseActivity;
import com.saumykukreti.learnforever.adapters.HomeFragmentNotesRecyclerViewAdapter;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.jobs.DataSyncJob;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.util.Converter;
import com.saumykukreti.learnforever.util.TextCreator;
import com.saumykukreti.learnforever.util.TextReader;
import com.saumykukreti.learnforever.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
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
    private LinearLayout mSearchContainer;
    private RecyclerView mRecyclerView;
    private String mCurrentLayoutManager;
    private boolean mIsInForeGround = true;
    private int mCurrentFilter = 0;

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
        mTextReader = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialiseViews();
        if (mCategory != null && !mCategory.isEmpty()) {
            //Means this activity shows notes of a particular category

            //Update mNotesList and initialise adapter
            mAllNotes = datacontroller.getNoteWithCategory(mCategory);

            initialiseNotesAdapter(false);

            //Not for home fragment but the list we are shown when in categories (tap one)
            setLiveDataForCategory();
        } else {
            //Modify fragment to show all notes
            initialiseNotesAdapter(true);

            //Initialise live data on list
            setLiveData();
        }


        //Initialise search view
        initialiseSearchView();
    }

    /**
     * Apply default filter
     */
    private void applyDefaultFilter() {
        String filterOption = Utility.getStringFromPreference(getContext(), Constants.LEARN_FOREVER_PREFERENCE_DEFAULT_FILTER);
        switch (filterOption) {
            case Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_ALPHABETICALLY:
                mCurrentFilter = R.id.radio_alphabetically;
                Utility.sortList(mAllNotes, Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_ALPHABETICALLY);
                break;
            case Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_NEW_FIRST:
                mCurrentFilter = R.id.radio_recent_first;
                Utility.sortList(mAllNotes, Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_NEW_FIRST);
                break;
            case Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_OLD_FIRST:
                mCurrentFilter = R.id.radio_old_first;
                Utility.sortList(mAllNotes, Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_OLD_FIRST);
                break;
            default:
                mCurrentFilter = R.id.radio_recent_first;
                Utility.sortList(mAllNotes, Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_NEW_FIRST);
                break;
        }
        mHomeFragmentNotesRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * This method intialises all the views that are used across the fragment
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
            }
        });
    }


    private void setLiveData() {
        LiveData<List<NoteTable>> livenotes = datacontroller.getDatabase().noteDao().getLiveAllNotes();
        livenotes.observe(this, new Observer<List<NoteTable>>() {
            @Override
            public void onChanged(@Nullable List<NoteTable> noteTables) {
                mHomeFragmentNotesRecyclerViewAdapter.setNoteTableList(noteTables);
                applyDefaultFilter();
                syncData();
            }
        });
    }

    /**
     * This method sets text watcher on the search view and
     */
    private void initialiseSearchView() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 2) {
                    //Boolean to keep track if the current list is updated or not to avoid unnecessary database calls
                    mNoteListUpdated = true;
                    if (mCategory != null && !mCategory.isEmpty()) {
                        mAllNotes = datacontroller.searchNoteWithStringAndCategory(charSequence.toString(), mCategory);
                    } else {
                        mAllNotes = datacontroller.searchNoteWithString(charSequence.toString());
                    }
                    initialiseNotesAdapter(false);
                } else {
                    //Show all notes
                    if (mNoteListUpdated) {
                        mNoteListUpdated = false;
                        if (mCategory != null && !mCategory.isEmpty()) {
                            mAllNotes = datacontroller.getNoteWithCategory(mCategory);
                        } else {
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
     * This method starts a job that sync data to firebase
     */
    private void syncData() {
        LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DataSyncJob(getContext(), null));
    }

    /**
     * Thie method
     *
     * @param turnOn - Pass true to show cancel button and vice versa
     */
    private void toggleCancelFabVisibility(boolean turnOn) {
        FloatingActionButton cancelFab = getView().findViewById(R.id.fab_cancel);
        if (turnOn) {
            cancelFab.setVisibility(View.VISIBLE);
        } else {
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
     * This method is used to disable selection mode
     */
    private void cancelAction() {
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
        if (updateNoteList) {
            mAllNotes = datacontroller.getAllNotes();
        }
        mRecyclerView = getView().findViewById(R.id.recycler_notes);
        mRecyclerView.removeAllViews();
        mHomeFragmentNotesRecyclerViewAdapter = new HomeFragmentNotesRecyclerViewAdapter(getActivity(), mAllNotes, new HomeFragmentAdapterInteractionListener() {
            @Override
            public void toggleSelectionMode(boolean on) {
                setSelectionMode(on);
            }
        });

        String layoutStyle = Utility.getStringFromPreference(getContext(), Constants.LEARN_FOREVER_PREFERENCE_LAYOUT_PREFERENCE);

        if (layoutStyle.equalsIgnoreCase("list")) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mCurrentLayoutManager = "list";
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            mCurrentLayoutManager = "grid";
        }
        mRecyclerView.setAdapter(mHomeFragmentNotesRecyclerViewAdapter);

        final ViewGroup fragment_container = getView().findViewById(R.id.home_container);
        mSearchContainer = getView().findViewById(R.id.search_container);
        final Fade fade = new Fade();
        fade.setDuration(200);
        fade.removeTarget(mRecyclerView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRecyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    //If velocity is positive, the user is scolling down and vice versa
                    TransitionManager.beginDelayedTransition(fragment_container, fade);
                    if (velocityY > 3000) {
                        //Scrolling down
                        mSearchContainer.setVisibility(View.GONE);
                    } else if ((velocityY < -3000)) {
                        //Scolling up
                        mSearchContainer.setVisibility(View.VISIBLE);
                    }
                    return false;
                }
            });
        }
    }

    /**
     * This method controls the fab button in the navigation drawer activity
     *
     * @param turnOn - Pass true to show fab and vice versa
     */
    public void toggleFabVisiblity(boolean turnOn) {
        //When selection mode is on turn off fab visibility and vice versa
        mListener.toggleFabVisibility(turnOn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_delete:
                handleDeleteButtonPress();
                return true;
            case R.id.home_read:
                readSelectedNotes();
                return true;
            case R.id.home_send:
                sendSelectedNotes();
                return true;
            case R.id.action_help:
                if (mIsInForeGround) {
                    Utility.showHelp(getContext(), getResources().getString(R.string.help_string_home));
                    return true;
                }
                break;
            case R.id.home_cancel:
                if (mSelectionModeOn) {
                    cancelAction();
                } else {
                    Toast.makeText(getContext(), "Nothing to cancel!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.home_search:
                if (mSearchContainer.getVisibility() == View.VISIBLE) {
                    Toast.makeText(getContext(), "Search is already visible!", Toast.LENGTH_SHORT).show();
                } else {
                    mSearchContainer.setVisibility(View.VISIBLE);
                }
                return true;

            case R.id.home_revise:
                handleReviseButtonPress();
                break;
            case R.id.home_filter:
                showFilteringOptions();
                break;
        }
        return false;
    }

    /**
     * This method shows filtering options
     */
    private void showFilteringOptions() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_filtering_options);

        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);

        if (mCurrentFilter != 0) {
            radioGroup.check(mCurrentFilter);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_alphabetically:
                        mCurrentFilter = R.id.radio_alphabetically;
                        Utility.sortList(mAllNotes, Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_ALPHABETICALLY);
                        mHomeFragmentNotesRecyclerViewAdapter.notifyDataSetChanged();
                        Utility.saveStringInPreference(getContext(), Constants.LEARN_FOREVER_PREFERENCE_DEFAULT_FILTER, Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_ALPHABETICALLY);
                        dialog.dismiss();
                        break;
                    case R.id.radio_recent_first:
                        mCurrentFilter = R.id.radio_recent_first;
                        Utility.sortList(mAllNotes, Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_NEW_FIRST);
                        Utility.saveStringInPreference(getContext(), Constants.LEARN_FOREVER_PREFERENCE_DEFAULT_FILTER, Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_NEW_FIRST);
                        mHomeFragmentNotesRecyclerViewAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        break;
                    case R.id.radio_old_first:
                        mCurrentFilter = R.id.radio_old_first;
                        Utility.sortList(mAllNotes, Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_OLD_FIRST);
                        Utility.saveStringInPreference(getContext(), Constants.LEARN_FOREVER_PREFERENCE_DEFAULT_FILTER, Constants.LEARN_FOREVER_PREFERENCE_FILTER_SETTING_OLD_FIRST);
                        mHomeFragmentNotesRecyclerViewAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        break;
                }
                Toast.makeText(getContext(), "Filter applied", Toast.LENGTH_SHORT).show();
            }
        });
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.onWindowAttributesChanged(params);
        dialog.show();
    }

    /**
     * This method handles the revise button
     */
    private void handleReviseButtonPress() {
        if (mSelectionModeOn) {
            List<NoteTable> selectedNoteList = mHomeFragmentNotesRecyclerViewAdapter.getSelectedList();
            if (!selectedNoteList.isEmpty()) {
                Intent intent = new Intent(getContext(), ReviseActivity.class);
                intent.putStringArrayListExtra(ReviseActivity.METADATA_NOTES_TO_REVISE, (ArrayList<String>) Converter.convertNoteListToStringList(selectedNoteList));
                startActivity(intent);
                cancelAction();
            } else {
                Toast.makeText(getContext(), "Select some notes first!", Toast.LENGTH_SHORT).show();
            }
        } else {
            setSelectionMode(true);
        }
    }


    private void sendSelectedNotes() {
        if (mSelectionModeOn) {
            List<NoteTable> selectedNoteList = mHomeFragmentNotesRecyclerViewAdapter.getSelectedList();
            if (!selectedNoteList.isEmpty()) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                if (selectedNoteList.size() == 1 && selectedNoteList.get(0).getTitle() != null && !selectedNoteList.get(0).getTitle().isEmpty()) {
                    //Setting the subject the title of the note if only one note is being sent and title is available
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, selectedNoteList.get(0).getTitle());
                } else {
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Notes");
                }

                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, TextCreator.getNoteTextForSending(selectedNoteList));
                startActivity(Intent.createChooser(sharingIntent, "Send notes via"));
            }
        } else {
            setSelectionMode(true);
        }
    }

    /**
     * This method handles the case when delete button is pressed
     */
    private void handleDeleteButtonPress() {
        if (mSelectionModeOn) {
            //This means that already some notes have been selected and those have to be deleted
            List<NoteTable> selectedNoteList = mHomeFragmentNotesRecyclerViewAdapter.getSelectedList();
            if (!selectedNoteList.isEmpty()) {
                askForConfirmationAndDeleteNote(selectedNoteList);
            }
        } else {
            setSelectionMode(true);
        }
    }

    /**
     * This method controls the visibility of buttons
     *
     * @param turnOn
     */
    public void setSelectionMode(boolean turnOn) {
        mSelectionModeOn = turnOn;

        if (turnOn) {
            //Notifying the adapter
            mHomeFragmentNotesRecyclerViewAdapter.toggleSelectionMode(true);

            //Changing the visisblity of fabs
            toggleFabVisiblity(false);
            toggleCancelFabVisibility(true);
        } else {
            //Notifying the adapter
            mHomeFragmentNotesRecyclerViewAdapter.toggleSelectionMode(false);

            //Changing the visisblity of fabs
            toggleFabVisiblity(true);
            toggleCancelFabVisibility(false);
        }
    }

    /**
     * This method shows a dialog asking the user to delete the current note or not
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

    public boolean getSelectionMode() {
        return mSelectionModeOn;
    }

    public void onNavigationFabLongClick() {
    }


    private void readSelectedNotes() {
        //Check if some notes are selected
        if (mSelectionModeOn) {
            List<NoteTable> listOfSelectedNotes = mHomeFragmentNotesRecyclerViewAdapter.getSelectedList();
            mTextReader.readAloud(TextCreator.getNoteTextForReading(listOfSelectedNotes));
        } else {
            setSelectionMode(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTextReader = null;
    }

    /**
     * Call this method when the layout of home fragment needs to be refreshed
     */
    public void refreshLayout() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mIsInForeGround = true;
        //Checking if layout settings were changed or not if so refreshing the layout
        String layoutPreference = Utility.getStringFromPreference(getContext(), Constants.LEARN_FOREVER_PREFERENCE_LAYOUT_PREFERENCE);

        if (!layoutPreference.equalsIgnoreCase(mCurrentLayoutManager)) {
            //Setting must have changed, thus refreshing layout
            initialiseNotesAdapter(false);
        }
    }

    /**
     * Calling this method to let home fragment know that it is not in foreground
     */
    public void goneInBackgroung() {
        mIsInForeGround = false;
        mRecyclerView.setVisibility(View.GONE);
    }
}