package com.saumykukreti.learnforever.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        //Set notes in notes Recycler view
        initialiseNotesAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        initialiseNotesAdapter();
    }

    /**
     *  This method initialises the recycler view with notes
     */
    private void initialiseNotesAdapter() {
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_notes);
        mAllNotes = datacontroller.getAllNotes();
        mHomeFragmentNotesRecyclerViewAdapter = new HomeFragmentNotesRecyclerViewAdapter(getActivity(), mAllNotes);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setAdapter(mHomeFragmentNotesRecyclerViewAdapter);
    }

    public interface OnHomeFragmentInteractionListener {

    }
}
