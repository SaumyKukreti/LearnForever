package com.saumykukreti.learnforever.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saumykukreti.learnforever.LearnForeverApplication;
import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.CategoryActivity;
import com.saumykukreti.learnforever.activities.ReviseActivity;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.dataManager.ReminderDataController;
import com.saumykukreti.learnforever.jobs.DataSyncJob;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;
import com.saumykukreti.learnforever.modelClasses.dataTables.ReminderTable;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private final int METADATA_CATEGORIES_ACTIVITY_REQUEST_CODE = 10101;

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

        final NoteDataController datacontroller = NoteDataController.getInstance(getActivity());

        final TextView textview = view.findViewById(R.id.currentdatabase);

        Button insert = view.findViewById(R.id.buttton_insert_note);
        Button showall = view.findViewById(R.id.buttton_showall);
        Button update = view.findViewById(R.id.buttton_update);
        Button delete = view.findViewById(R.id.buttton_delete);
        Button categories = view.findViewById(R.id.buttton_categories);
        Button firebase = view.findViewById(R.id.insert_into_firebase);
        Button sync = view.findViewById(R.id.start_sync);
        Button showReminder = view.findViewById(R.id.show_reminder);
        Button revise = view.findViewById(R.id.buttton_revise);
        Button category = view.findViewById(R.id.button_category);

        final EditText noteToUpdate = view.findViewById(R.id.noteToUpdate);

        final EditText categoryedit = view.findViewById(R.id.categoryedit);
        final EditText titleedit = view.findViewById(R.id.titleedit);
        final EditText contentinshort = view.findViewById(R.id.cisedit);
        final EditText content = view.findViewById(R.id.contentedit);
        revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ReviseActivity.class));
            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), CategoryActivity.class), METADATA_CATEGORIES_ACTIVITY_REQUEST_CODE);
            }
        });
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!datacontroller.newNote(categoryedit.getText().toString(), titleedit.getText().toString(),
                        contentinshort.getText().toString(),
                        content.getText().toString(),
                        "timestamp",
                        true)) {

                    Toast.makeText(getContext(), "Note not Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Note Added", Toast.LENGTH_SHORT).show();
                }
            }
        });


        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<NoteTable> a = datacontroller.getAllNotes();
                String s = "";
                for (NoteTable x : a) {
                    s = s + x + "\n\n";

                }
                textview.setText(s);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (datacontroller.updateNote(Long.parseLong(noteToUpdate.getText().toString()),
                        categoryedit.getText().toString(),
                        titleedit.getText().toString(),
                        contentinshort.getText().toString(),
                        content.getText().toString(),
                        "timestamp",
                        true)) {
                    Toast.makeText(getContext(), "Note Updated", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "Note note updated", Toast.LENGTH_SHORT).show();

                }
            }
        });

        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> listOfCategories = datacontroller.getListOfCategories();

                String srrr = "";
                for (String str : listOfCategories){
                    srrr= srrr+ str;
                }

                textview.setText(srrr);
            }

        });


        firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();

                
                myRef.setValue("Hello, World!");
            }
        });

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LearnForeverApplication.getInstance().getJobManager().addJobInBackground(new DataSyncJob(getContext(),null));
            }
        });

        showReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ReminderTable> listOfData = ReminderDataController.getInstance(getContext()).getAllEntries();

                StringBuffer str = new StringBuffer();

                for (ReminderTable i : listOfData){
                    str.append(i);
                }

                textview.setText(str);

            }
        });
    }

    public interface OnSettingsFragmentInteractionListener {
        void updateActionBarForSettingsFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == METADATA_CATEGORIES_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                //Refreshing activity

            }
            //else do nothing
        }
    }
}
