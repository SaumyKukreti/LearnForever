package com.saumykukreti.learnforever.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saumykukreti.learnforever.R;

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
}
