package com.saumykukreti.learnforever.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.CategoryActivity;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {
    private OnCategoriesFragmentInteractionListener mListener;
    private List<String> mListOfCategories = new ArrayList<>();
    private ArrayAdapter<String> mArrayAdapter;
    private ListView mCategoriesListView;
    private EditText mSearchEditText;
    private NoteDataController mDataController;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();
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
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoriesFragmentInteractionListener) {
            mListener = (OnCategoriesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoriesFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.updateActionBarForCategoriesFragment();

        //Refreshing adapter
        getListOfCategories();

        if (!mListOfCategories.isEmpty()) {
            mArrayAdapter.notifyDataSetChanged();
            mCategoriesListView.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.no_categories_text).setVisibility(View.GONE);
        } else {
            mCategoriesListView.setVisibility(View.GONE);
            getView().findViewById(R.id.no_categories_text).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting list of categories
        getListOfCategories();

        initialiseSearchView();
        //Creating and setting an adapter for categories
        mArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.categories_fragment_item_layout, mListOfCategories);
        mCategoriesListView = getView().findViewById(R.id.category_fragment_categories_list_view);
        mCategoriesListView.setAdapter(mArrayAdapter);

        mCategoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra(CategoryActivity.METADATA_CATEGORY, mListOfCategories.get(i));
                startActivity(intent);
            }
        });
    }

    /**
     *  This method initialises search view
     */
    private void initialiseSearchView() {
        mSearchEditText = getView().findViewById(R.id.edit_search);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length()>1){
                    //Extracting all the categories and showing the list to the user
                    String searchString = "%"+charSequence+"%";
                    List<String> listOfCategories = mDataController.getListOfCategoriesWithValue(searchString);

                    //Changing the current string to list obtained from search
                    mListOfCategories.clear();
                    mListOfCategories.addAll(listOfCategories);
                    mArrayAdapter.notifyDataSetChanged();
                }
                else{
                    //Reset list to show all categories
                    getListOfCategories();
                    mArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void getListOfCategories() {
        mDataController = NoteDataController.getInstance(getActivity());
        List<String> listOfCategories = mDataController.getListOfCategories();
        mListOfCategories.clear();
        mListOfCategories.addAll(listOfCategories);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mListOfCategories.sort(null);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCategoriesFragmentInteractionListener {
        void updateActionBarForCategoriesFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Utility.showHelp(getContext(), getResources().getString(R.string.help_string_categories));
                return true;
        }
        return false;
    }
}
