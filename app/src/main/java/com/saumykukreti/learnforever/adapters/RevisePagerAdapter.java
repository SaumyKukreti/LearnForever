package com.saumykukreti.learnforever.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.List;

/**
 * Created by saumy on 12/30/2017.
 */

public class RevisePagerAdapter extends PagerAdapter {

    private final List<NoteTable> mListOfNotes;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private final RevisePagerAdapterListener mListener;

    public RevisePagerAdapter(Context context, List<NoteTable> listOfNotes, RevisePagerAdapterListener revisePagerAdapterListener) {
        mListener = revisePagerAdapterListener;
        mContext = context;
        mListOfNotes = listOfNotes;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mListOfNotes.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        NoteTable note = mListOfNotes.get(position);
        View view = mLayoutInflater.inflate(R.layout.revise_item_layout, container, false);

        //Setting values
        TextView titleTitleTextView = view.findViewById(R.id.text_note_title);
        TextView titleTextView = view.findViewById(R.id.text_title);
        TextView contentInShortTitle = view.findViewById(R.id.text_content_in_short_title);
        TextView contentInShort = view.findViewById(R.id.text_content_in_short);
        TextView categoryTitleTextView = view.findViewById(R.id.text_category_title);
        TextView categoryTextView = view.findViewById(R.id.text_category);
        TextView contentTitleTextView = view.findViewById(R.id.text_content_title);
        TextView contentTextView = view.findViewById(R.id.text_content);
        ImageView titleLine = view.findViewById(R.id.image_note_title);
        ImageView contentInShortLine = view.findViewById(R.id.image_note_cis);
        ImageView categoryLine = view.findViewById(R.id.image_note_category);
        ImageView contentLine = view.findViewById(R.id.image_note_content);

        if(note.getTitle()!=null && !note.getTitle().isEmpty()) {
            showViews(titleTitleTextView,titleTextView,titleLine);
            titleTextView.setText(note.getTitle());
        }
        else{
            hideViews(titleTitleTextView,titleTextView,titleLine);
        }

        if(note.getContentInShort()!=null && !note.getContentInShort().isEmpty()) {
            showViews(contentInShort,contentInShortLine,contentInShortTitle);
            contentInShort.setText(note.getContentInShort());
        }
        else{
            hideViews(contentInShort,contentInShortLine,contentInShortTitle);
        }

        if(note.getCategory()!=null && !note.getCategory().isEmpty()) {
            showViews(categoryTextView,categoryTitleTextView,categoryLine);
            categoryTextView.setText(note.getCategory());
        }
        else{
            hideViews(categoryTextView,categoryTitleTextView,categoryLine);
        }

         if(note.getContent()!=null && !note.getContent().isEmpty()) {
            showViews(contentTextView,contentTitleTextView,contentLine);
             contentTextView.setText(note.getContent());
        }
        else{
            hideViews(contentTextView,contentTitleTextView,contentLine);
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((CardView)object);
    }


    public interface RevisePagerAdapterListener{
        void noteClicked();
    }

    private void hideViews(View viewOne, View viewTwo, View viewThree){
        viewOne.setVisibility(View.GONE);
        viewTwo.setVisibility(View.GONE);
        viewThree.setVisibility(View.GONE);
    }

    private void showViews(View viewOne, View viewTwo, View viewThree){
        viewOne.setVisibility(View.VISIBLE);
        viewTwo.setVisibility(View.VISIBLE);
        viewThree.setVisibility(View.VISIBLE);
    }
}
