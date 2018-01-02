package com.saumykukreti.learnforever.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
        TextView titleTextView = view.findViewById(R.id.text_title);
        TextView contentInShort = view.findViewById(R.id.text_content_in_short);
        TextView contentTextView = view.findViewById(R.id.text_content);

        titleTextView.setText(note.getTitle());
        contentInShort.setText(note.getContentInShort());
        contentTextView.setText(note.getContent());
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
}
