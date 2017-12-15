package com.saumykukreti.learnforever.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.NoteActivity;
import com.saumykukreti.learnforever.fragments.HomeFragment;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saumy on 12/9/2017.
 */

public class HomeFragmentNotesRecyclerViewAdapter extends RecyclerView.Adapter<HomeFragmentNotesRecyclerViewAdapter.HomeFragmentNotesViewHolder>{

    private final HomeFragment.HomeFragmentAdapterInteractionListener mListener;
    private Context mContext;
    private List<NoteTable> mNoteList;
    private boolean mSelectionMode;
    private List<NoteTable> mSelectedNoteList = new ArrayList<>();

    public HomeFragmentNotesRecyclerViewAdapter(Context context, List<NoteTable> noteList, HomeFragment.HomeFragmentAdapterInteractionListener homeFragmentAdapterInterationListener) {
        mContext = context;
        mNoteList = noteList;
        mListener = homeFragmentAdapterInterationListener;
    }

    @Override
    public HomeFragmentNotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new HomeFragmentNotesViewHolder(layoutInflater.inflate(R.layout.home_fragment_recycler_view_note, parent,false));
    }

    @Override
    public void onBindViewHolder(final HomeFragmentNotesViewHolder holder, int position) {
        final NoteTable note = mNoteList.get(position);

        holder.noteTitle.setText(note.getTitle());
        holder.noteContentInShort.setText(note.getContentInShort());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSelectionMode){

                    //Check if item is in selected list if so delete the item else add the item, alter its background accordingly
                    if(mSelectedNoteList.contains(note)){
                        mSelectedNoteList.remove(note);
                        holder.noteCardView.setCardBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                    }
                    else{
                        mSelectedNoteList.add(note);
                        holder.noteCardView.setCardBackgroundColor(mContext.getResources().getColor(android.R.color.darker_gray));
                    }
                }else {
                    Intent intent = new Intent(mContext, NoteActivity.class);
                    intent.putExtra(NoteActivity.METADATA_NOTE, note);
                    mContext.startActivity(intent);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Start selection mode
                Toast.makeText(mContext, "Selection mode on", Toast.LENGTH_SHORT).show();
                mSelectionMode = true;
                mListener.toggleSelectionMode(true);
                mSelectedNoteList.add(note);
                holder.noteCardView.setCardBackgroundColor(mContext.getResources().getColor(android.R.color.darker_gray));
                return true;
            }
        });
    }

    public List<NoteTable> getSelectedList(){
        if(mSelectionMode) {
            return mSelectedNoteList;
        }
        else{
            //Return empty list
            return new ArrayList<>();
        }
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    class HomeFragmentNotesViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView noteTitle;
        TextView noteContentInShort;
        CardView noteCardView;

        public HomeFragmentNotesViewHolder(View itemView) {
            super(itemView);
            noteTitle= itemView.findViewById(R.id.text_note_title);
            noteContentInShort= itemView.findViewById(R.id.text_note_content_in_short);
            noteCardView = itemView.findViewById(R.id.card_view_home_fragment_note);
            this.itemView=itemView;
        }
    }


    public void toggleSelectionMode(boolean on){
        mSelectionMode = on;

        //Reset adapter if selection mode turned off
        if(!on){
            this.notifyDataSetChanged();
        }
    }


}
