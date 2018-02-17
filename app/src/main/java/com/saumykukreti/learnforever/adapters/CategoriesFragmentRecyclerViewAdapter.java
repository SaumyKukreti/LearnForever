package com.saumykukreti.learnforever.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.TextView;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.CategoryActivity;
import com.saumykukreti.learnforever.activities.ReviseActivity;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saumy on 2/13/2018.
 */

public class CategoriesFragmentRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesFragmentRecyclerViewAdapter.CategoriesViewHolder> {

    private final ArrayList<String> mListOfCategories;
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private int mCurrentPosition = 0;

    public CategoriesFragmentRecyclerViewAdapter(Context context, ArrayList<String> listOfCategories) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListOfCategories = listOfCategories;
        mContext = context;
    }

    @Override
    public CategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.categories_fragment_item_layout, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoriesViewHolder holder, final int position) {

        holder.categoryName.setText(mListOfCategories.get(position));

        holder.categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CategoryActivity.class);
                intent.putExtra(CategoryActivity.METADATA_CATEGORY, mListOfCategories.get(position));
                mContext.startActivity(intent);
            }
        });

        holder.categoryName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Sending the user directly to revise all the notes belonging to that category
                Intent intent = new Intent(mContext, ReviseActivity.class);
                List<String> listOfNotes = NoteDataController.getInstance(mContext).getNoteIdsWithCategory(mListOfCategories.get(position));
                intent.putStringArrayListExtra(ReviseActivity.METADATA_NOTES_TO_REVISE, (ArrayList<String>) listOfNotes);
                mContext.startActivity(intent);
                return true;
            }
        });

        if(mCurrentPosition <= position) {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.item_animation_from_bottom));
        }else{
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.item_animation_fall_down));
        }

        mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mListOfCategories.size();
    }

    @Override
    public void onViewRecycled(CategoriesViewHolder holder) {
        super.onViewRecycled(holder);
    }

    protected class CategoriesViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        TextView categoryName;
        public CategoriesViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.text_category);
            this.itemView = itemView;
        }
    }
}
