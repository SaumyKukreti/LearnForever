package com.saumykukreti.learnforever.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.adapters.ReviseNotesAdapter;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;
import com.saumykukreti.learnforever.util.Converter;
import com.saumykukreti.learnforever.util.Utility;

import java.util.Date;
import java.util.List;

public class ReviseFragment extends Fragment {
    private OnReviseFragmentInteractionListener mListener;

    @Override
    public void onResume() {
        super.onResume();
        mListener.updateActionBarForReviseFragment();
    }

    public ReviseFragment() {
        // Required empty public constructor
    }

    public static ReviseFragment newInstance() {
        ReviseFragment fragment = new ReviseFragment();
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
        return inflater.inflate(R.layout.fragment_revise, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_notes);
        List<NoteTable> noteList = null;
        List<String> notesToRemind = Utility.getNoteIdsToRemind(getContext());
        //Getting the notes
        noteList = NoteDataController.getInstance(getContext()).getNoteWithIds(notesToRemind);

        if (noteList != null && !noteList.isEmpty()) {
            ReviseNotesAdapter adapter = new ReviseNotesAdapter(getContext(), noteList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            view.findViewById(R.id.text_no_notes).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReviseFragmentInteractionListener) {
            mListener = (OnReviseFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnReviseFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnReviseFragmentInteractionListener {
        void updateActionBarForReviseFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Utility.showHelp(getContext(), getResources().getString(R.string.help_string_revise));
                return true;
        }
        return false;
    }
}
