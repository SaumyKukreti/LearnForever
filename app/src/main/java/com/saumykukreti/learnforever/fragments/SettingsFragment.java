package com.saumykukreti.learnforever.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.CustomIntervalActivity;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.jobs.LogoutJob;
import com.saumykukreti.learnforever.util.Converter;
import com.saumykukreti.learnforever.util.TextCreator;
import com.saumykukreti.learnforever.util.Utility;

public class SettingsFragment extends Fragment {
    public static final int LIST_CHANGED = 101;
    public static final int CANCELLED = 102;
    private OnSettingsFragmentInteractionListener mListener;
    private SharedPreferences mSharedPreferences;
    private int mSpeechRate;
    private TextView mSpeechRateText;
    private int mCurrentSpeechRate;

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

        setHasOptionsMenu(true);
        mSharedPreferences = getContext().getSharedPreferences(Constants.LEARN_FOREVER_PREFERENCE, Context.MODE_PRIVATE);

        //Initialising SpeechRate
        getCurrentSpeechRate();
        mCurrentSpeechRate = mSpeechRate;

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

        getView().findViewById(R.id.linear_settings_notification_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        getView().findViewById(R.id.linear_settings_reading_speed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReadingSpeedDialog();
            }
        });

        getView().findViewById(R.id.linear_settings_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
        getView().findViewById(R.id.linear_settings_layout_style).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLayoutStyleDialog();
            }
        });
        getView().findViewById(R.id.linear_settings_speech).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpeechSettingDialog();
            }
        });
        getView().findViewById(R.id.linear_settings_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sending email
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"saumykukreti1993@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Learn Forever Feedback");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Initialising values
        setValues();
    }

    /**
     *  This method shows a dialog to turn on or off reading in background
     */
    private void showSpeechSettingDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_speech_settings);

        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group_speech);
        if(mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_SPEECH_IN_BACKGROUND_PREFERENCE, false)){
            radioGroup.check(R.id.radio_on);
        }else{
            radioGroup.check(R.id.radio_off);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_on:
                        mSharedPreferences.edit().putBoolean(Constants.LEARN_FOREVER_PREFERENCE_SPEECH_IN_BACKGROUND_PREFERENCE, true).apply();
                        break;
                    case R.id.radio_off:
                        mSharedPreferences.edit().putBoolean(Constants.LEARN_FOREVER_PREFERENCE_SPEECH_IN_BACKGROUND_PREFERENCE, false).apply();
                        break;
                }
            }
        });

        setParams(dialog);

        dialog.show();
    }

    /**
     *  This method displays a dialog to choose a layout style
     */
    private void showLayoutStyleDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_settings_layout_settings);

        final RadioButton radioGrid = dialog.findViewById(R.id.radio_grid);
        final RadioButton radioList = dialog.findViewById(R.id.radio_list);
        final RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);

        //Set current choice
        String currentPreference = mSharedPreferences.getString(Constants.LEARN_FOREVER_PREFERENCE_LAYOUT_PREFERENCE,"");
        switch (currentPreference){
            case "grid":
                radioGroup.check(R.id.radio_grid);
                break;
            case "list":
                radioGroup.check(R.id.radio_list);
                break;

            default:
                radioGroup.check(R.id.radio_grid);
                break;
        }

        //Setting current selection
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_grid:
                        mSharedPreferences.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_LAYOUT_PREFERENCE,"grid").apply();
                        break;

                    case R.id.radio_list:
                        mSharedPreferences.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_LAYOUT_PREFERENCE,"list").apply();
                        break;
                }
                dialog.dismiss();
                Toast.makeText(getContext(), "Layout Settings Changed!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setValues();
            }
        });

        setParams(dialog);
        dialog.show();
    }

    /**
     *  This method shows a dialog that tells the user about me
     */
    private void showLogoutDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        dialog.setTitle("Logout");
        dialog.setMessage("Are you sure you want to logout?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Start logout process
                LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new LogoutJob(getContext(),null));
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     *  This method shows a dialog to change the reading speed
     */
    private void showReadingSpeedDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_settings_reading_speed);
        mSpeechRateText = (TextView)dialog.findViewById(R.id.text_speech_Rate);
        mSpeechRateText.setText(String.valueOf(mSpeechRate * 0.1));

        dialog.findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseSpeechRate();
            }
        });

        dialog.findViewById(R.id.button_substract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseSpeechRate();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setValues();
            }
        });

        setParams(dialog);
        dialog.show();
    }

    /**
     * This method decreases the rate of speech
     */
    private void decreaseSpeechRate() {
        getCurrentSpeechRate();
        mSpeechRate-=1;
        if(mSpeechRate==0){
            mSpeechRate = 1;
        }
        mSharedPreferences.edit().putInt(Constants.LEARN_FOREVER_PREFERENCE_SPEECH_RATE,mSpeechRate).apply();
        mSpeechRateText.setText(String.valueOf(mSpeechRate * 0.1));
    }

    /**
     *  This method gets the current speech rate
     */
    private void getCurrentSpeechRate() {
        mSpeechRate = mSharedPreferences.getInt(Constants.LEARN_FOREVER_PREFERENCE_SPEECH_RATE,10);
    }

    /**
     * This method increases the rate of speech
     */
    private void increaseSpeechRate() {
        getCurrentSpeechRate();
        mSpeechRate+=1;

        //Dont let the speech increase more than 10
        if(mSpeechRate>=100){
            mSpeechRate = 99;
        }
        mSharedPreferences.edit().putInt(Constants.LEARN_FOREVER_PREFERENCE_SPEECH_RATE,mSpeechRate).apply();
        mSpeechRateText.setText(String.valueOf(mSpeechRate * 0.1));
    }

    /**
     *  This method shows a time picker to choose a time for notification
     */
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Saving notification time
                mSharedPreferences.edit().putString(Constants.LEARN_FOREVER_PREFERENCE_NOTIFICATION_HOUR, String.valueOf(hourOfDay))
                .putString(Constants.LEARN_FOREVER_PREFERENCE_NOTIFICATION_MINUTE, String.valueOf(minute)).apply();

                //Re initialise notification
                Utility.setNotification(getContext());

                setValues();
                Toast.makeText(getContext(), "Time Changed!", Toast.LENGTH_SHORT).show();
            }
        },9,0,false);
        timePickerDialog.show();
    }

    /**
     * This method sets values in views
     */
    private void setValues() {
        String currentIntervalPreference = mSharedPreferences.getString(Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL,"");
        int[] currentInterval;
        switch (currentIntervalPreference){
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

        String currentLayoutPreference = mSharedPreferences.getString(Constants.LEARN_FOREVER_PREFERENCE_LAYOUT_PREFERENCE,"");
        String currentStyle = "Grid";
        switch (currentLayoutPreference){
            case "grid":
                currentStyle = "Grid";
                break;
            case "list":
                currentStyle = "List";
                break;
            default:
                currentStyle = "Grid";
                break;
        }

        TextView currentIntervalTV = getView().findViewById(R.id.text_two_revise_intervals);
        TextView currentFields = getView().findViewById(R.id.text_two_new_note);
        TextView currentTime = getView().findViewById(R.id.text_two_notification);
        TextView currentLayoutStyle = getView().findViewById(R.id.text_two_layout_style);

        currentIntervalTV.setText((TextCreator.getIntervalText(currentInterval)));
        currentLayoutStyle.setText(currentStyle);

        String currentFieldsString = TextCreator.getNoteSettingsText(mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_TITLE_SETTINGS, false),
                mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_CIS_SETTINGS, false),
                mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_CATEGORY_SETTINGS, false));

        //Checking if settings were changed or not, if yes then showing toast to the user
        if(!currentFields.getText().toString().isEmpty() &&
                !currentFields.getText().toString().equalsIgnoreCase(currentFieldsString))
            Toast.makeText(getContext(), "New note settings changed!", Toast.LENGTH_SHORT).show();

        currentFields.setText(currentFieldsString);

        //Setting current time
        int hour = Integer.parseInt(mSharedPreferences.getString(Constants.LEARN_FOREVER_PREFERENCE_NOTIFICATION_HOUR,"9"));
        int minute = Integer.parseInt(mSharedPreferences.getString(Constants.LEARN_FOREVER_PREFERENCE_NOTIFICATION_MINUTE,"0"));

        String hourString="";
        String minuteString="";

        if(hour<10)
            hourString = "0"+hour;
        else
            hourString = String.valueOf(hour);

        if(minute<10)
            minuteString = "0"+minute;
        else
            minuteString = String.valueOf(minute);

        currentTime.setText(hourString +":"+minuteString + "  (In 24 hours)");

        TextView currentRateOfSpeech = getView().findViewById(R.id.text_two_reading);

        //Speech rate
        if(mCurrentSpeechRate != mSpeechRate){
            mCurrentSpeechRate = mSpeechRate;
            Toast.makeText(getContext(), "Rate of speech changed!", Toast.LENGTH_SHORT).show();
        }
        currentRateOfSpeech.setText(String.valueOf(Utility.round(((float) (mSpeechRate * 0.1)),1)));

        TextView speechSetting = getView().findViewById(R.id.text_two_speech);
        //Setting speech setting text view
        if(mSharedPreferences.getBoolean(Constants.LEARN_FOREVER_PREFERENCE_SPEECH_IN_BACKGROUND_PREFERENCE, false)){
            speechSetting.setText("On");
        }else{
            speechSetting.setText("Off");
        }
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

        setParams(dialog);

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
                Toast.makeText(getContext(), "Revise Interval Changed!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setValues();
            }
        });

        setParams(dialog);
        dialog.show();
    }

    /**
     * This method sets the params to dialog
     * @param dialog
     */
    private void setParams(Dialog dialog) {
        //Setting dialog params
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.onWindowAttributesChanged(params);
    }

    private void openCustomIntervalActivity() {
        Intent intent = new Intent(getContext(), CustomIntervalActivity.class);
        startActivityForResult(intent,1020);
    }

    public interface OnSettingsFragmentInteractionListener {
        void updateActionBarForSettingsFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == LIST_CHANGED) {
            setValues();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Utility.showHelp(getContext(), getResources().getString(R.string.help_string_settings));
                return true;
        }
        return false;
    }
}

