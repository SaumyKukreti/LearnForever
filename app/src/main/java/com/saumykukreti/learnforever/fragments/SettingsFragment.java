package com.saumykukreti.learnforever.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.CustomIntervalActivity;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.util.Converter;
import com.saumykukreti.learnforever.util.TextCreator;

public class SettingsFragment extends Fragment {
    public static final int LIST_CHANGED = 101;
    public static final int CANCELLED = 102;
    private OnSettingsFragmentInteractionListener mListener;
    private SharedPreferences mSharedPreferences;

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

        mSharedPreferences = getContext().getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);

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

        //Initialising values
        setValues();
    }

    /**
     * This method sets values in views
     */
    private void setValues() {
        String currentPreference = mSharedPreferences.getString(Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL,"");
        int[] currentInterval;
        switch (currentPreference){
            case "1":
                currentInterval = Constants.DAY_INTERVAL_ONE;
                break;
            case "2":
                currentInterval = Constants.DAY_INTERVAL_TWO;
                break;
            case "3":
                currentInterval = Constants.DAY_INTERVAL_THREE;
                break;
            case "custom":
                currentInterval = Converter.convertStringToIntArray(mSharedPreferences.getString(Constants.LEARN_FOREVER_PREFERENCE_CUSTOM_INTERVAL,""));
                break;
            default:
                currentInterval = Constants.DAY_INTERVAL_ONE;
                break;
        }

        TextView currentIntervalTV = getView().findViewById(R.id.text_two_revise_intervals);
        TextView currentFields = getView().findViewById(R.id.text_two_new_note);

        currentIntervalTV.setText((TextCreator.getIntervalText(currentInterval)));

        currentFields.setText(TextCreator.getNoteSettingsText(mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_TITLE_SETTINGS, false),
                mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_CIS_SETTINGS, false),
                mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_CATEGORY_SETTINGS, false)));
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
        titleSwitch.setChecked(mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_TITLE_SETTINGS, false));
        cisSwitch.setChecked(mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_CIS_SETTINGS, false));
        categorySwitch.setChecked(mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_CATEGORY_SETTINGS, false));

        titleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferences.edit().putBoolean(Constants.LEARN_FOREVER_PREFERENCE_TITLE_SETTINGS,titleSwitch.isChecked()).apply();
            }
        });

        cisSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferences.edit().putBoolean(Constants.LEARN_FOREVER_PREFERENCE_CIS_SETTINGS,cisSwitch.isChecked()).apply();
            }
        });

        categorySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferences.edit().putBoolean(Constants.LEARN_FOREVER_PREFERENCE_CATEGORY_SETTINGS,categorySwitch.isChecked()).apply();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setValues();
            }
        });
        dialog.show();
    }

    /**
     * This method shows a dialog that displays various interval options
     */
    private void showReviseIntervalsDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_settings_revise_interval);

        final RadioButton radioOne = dialog.findViewById(R.id.radio_one);
        final RadioButton radioTwo = dialog.findViewById(R.id.radio_two);
        final RadioButton radioThree = dialog.findViewById(R.id.radio_three);
        final RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);

        //Setting selection based on earlier preferences
        mSharedPreferences = getContext().getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);

        radioOne.setText(TextCreator.getIntervalText(Constants.DAY_INTERVAL_ONE));
        radioTwo.setText(TextCreator.getIntervalText(Constants.DAY_INTERVAL_TWO));
        radioThree.setText(TextCreator.getIntervalText(Constants.DAY_INTERVAL_THREE));

        //Set current choice
        String currentPreference = mSharedPreferences.getString(Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL,"");
        switch (currentPreference){
            case "1":
                radioGroup.check(R.id.radio_one);
                break;
            case "2":
                radioGroup.check(R.id.radio_two);
                break;
            case "3":
                radioGroup.check(R.id.radio_three);
                break;
            case "custom":
                radioGroup.check(R.id.radio_custom);
                break;
            default:
                radioGroup.check(R.id.radio_one);
                break;
        }

        //Setting current selection
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_one:
                        mSharedPreferences.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL,"1").apply();
                        break;

                    case R.id.radio_two:
                        mSharedPreferences.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL,"2").apply();
                        break;

                    case R.id.radio_three:
                        mSharedPreferences.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL,"3").apply();
                        break;

                    case R.id.radio_custom:
                        openCustomIntervalActivity();
                        break;
                }
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setValues();
            }
        });

        dialog.show();
    }

    private void openCustomIntervalActivity() {
        Intent intent = new Intent(getContext(), CustomIntervalActivity.class);
        startActivityForResult(intent,1020);
    }

    public interface OnSettingsFragmentInteractionListener {
        void updateActionBarForSettingsFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == LIST_CHANGED){
            setValues();
        }
    }
}
