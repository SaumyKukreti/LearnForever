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
import com.saumykukreti.learnforever.activities.CategoryNotesActivity;
import com.saumykukreti.learnforever.dataManager.DataController;

import java.util.List;

public class CategoriesFragment extends Fragment {
    private OnCategoriesFragmentInteractionListener mListener;

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
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DataController dataController = DataController.getInstance(getActivity());

        //Getting list of categories
        final List<String> listOfCategories = dataController.getListOfCategories();

        //Creating and setting an adapter for categories

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,listOfCategories);

        ListView categoriesListView = getView().findViewById(R.id.category_fragment_categories_list_view);
        categoriesListView.setAdapter(arrayAdapter);

        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), CategoryNotesActivity.class);
                intent.putExtra(CategoryNotesActivity.METADATA_CATEGORY,listOfCategories.get(i));
                startActivity(intent);
            }
        });
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
