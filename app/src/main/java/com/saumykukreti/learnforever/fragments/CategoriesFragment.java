package com.saumykukreti.learnforever.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.CategoryActivity;
import com.saumykukreti.learnforever.dataManager.NoteDataController;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {
    private OnCategoriesFragmentInteractionListener mListener;
    private List<String> mListOfCategories = new ArrayList<>();
    private ArrayAdapter<String> mArrayAdapter;
    private ListView mCategoriesListView;

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

        //Creating and setting an adapter for categories
        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mListOfCategories);

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

    private void getListOfCategories() {
        NoteDataController dataController = NoteDataController.getInstance(getActivity());
        List<String> listOfCategories = dataController.getListOfCategories();
        mListOfCategories.clear();
        mListOfCategories.addAll(listOfCategories);

        //TODO - If no categories are found show appropriate error message

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCategoriesFragmentInteractionListener {
        void updateActionBarForCategoriesFragment();
    }
}
