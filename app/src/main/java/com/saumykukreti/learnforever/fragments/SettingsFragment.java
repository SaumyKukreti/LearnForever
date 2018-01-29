package com.saumykukreti.learnforever.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.CategoryActivity;
import com.saumykukreti.learnforever.activities.ReviseActivity;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.jobs.DataSyncJob;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {
    private OnSettingsFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.updateActionBarForSettingsFragment();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsFragmentInteractionListener) {
            mListener = (OnSettingsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsFragmentInteractionListener");
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

        //Setting on clicks

        getView().findViewById(R.id.linear_settings_revise_intervals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviseIntervalsDialog();
            }
        });

        getView().findViewById(R.id.linear_settings_new_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewNoteDialog();
            }
        });
    }

    /**
     * This method shows a dialog that displays various new note options
     */
    private void showNewNoteDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_settings_new_note);

        final Switch titleSwitch = dialog.findViewById(R.id.switch_title);
        final Switch cisSwitch = dialog.findViewById(R.id.switch_cis);
        final Switch categorySwitch = dialog.findViewById(R.id.switch_category);

        //Setting selection based on earlier preferences
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);

        titleSwitch.setChecked(sharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_TITLE_SETTINGS, false));
        cisSwitch.setChecked(sharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_CIS_SETTINGS, false));
        categorySwitch.setChecked(sharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_CATEGORY_SETTINGS, false));

        titleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putBoolean(Constants.LEARN_FOREVER_PREFERENCE_TITLE_SETTINGS,titleSwitch.isChecked()).apply();
            }
        });

        cisSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putBoolean(Constants.LEARN_FOREVER_PREFERENCE_CIS_SETTINGS,cisSwitch.isChecked()).apply();
            }
        });

        categorySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putBoolean(Constants.LEARN_FOREVER_PREFERENCE_CATEGORY_SETTINGS,categorySwitch.isChecked()).apply();
            }
        });
        dialog.show();
    }

    /**
     * This method shows a dialog that displays various interval options
     */
    private void showReviseIntervalsDialog() {
//        Dialog dialog = new Dialog(getContext());
//        dialog.setContentView();


        //Setting on click listeners


    }

    public interface OnSettingsFragmentInteractionListener {
        void updateActionBarForSettingsFragment();
    }
}
