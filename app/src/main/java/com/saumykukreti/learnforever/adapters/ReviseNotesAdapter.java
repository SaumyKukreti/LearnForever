package com.saumykukreti.learnforever.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.ReviseActivity;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.List;

/**
 * Created by saumy on 1/7/2018.
 */

public class ReviseNotesAdapter extends RecyclerView.Adapter<ReviseNotesAdapter.ReviseNotesViewHolder> {

    private final List<NoteTable> mListOfNotes;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;

    public ReviseNotesAdapter(Context context, List<NoteTable> listOfNotes) {
        mContext = context;
        mListOfNotes = listOfNotes;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ReviseNotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.home_fragment_recycler_view_note, parent, false);
        return new ReviseNotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviseNotesViewHolder holder, final int position) {
        final NoteTable note = mListOfNotes.get(position);

        //Setting views according to content available
        String noteTitle = "";
        String noteDescription = "";
        if (!note.getTitle().isEmpty() && !note.getContentInShort().isEmpty()) {
            //Display only title and content
            noteTitle = note.getTitle();
            noteDescription = note.getContentInShort();
        } else if (!note.getTitle().isEmpty() && note.getContentInShort().isEmpty()) {
            //Display title and content
            noteTitle = note.getTitle();
            noteDescription = note.getContent();
        } else if (note.getTitle().isEmpty() && !note.getContentInShort().isEmpty()) {
            //Display content in short and content
            noteTitle = note.getContentInShort();
            noteDescription = note.getContent();
        } else {
            //Display only content
            noteDescription = note.getContent();
        }

        if (noteTitle.equalsIgnoreCase("")) {
            // Show only content
            //Hiding line and title
            holder.noteTitle.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
            holder.noteContentInShort.setText(noteDescription);
        } else {
            //Show both the content and description
            holder.noteTitle.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
            holder.noteTitle.setText(noteTitle);
            holder.noteContentInShort.setText(noteDescription);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ReviseActivity.class);
                intent.putExtra(ReviseActivity.METADATA_POSITION, position);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mListOfNotes.size();
    }

    @Override
    public void onViewDetachedFromWindow(ReviseNotesViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public class ReviseNotesViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView noteTitle;
        TextView noteContentInShort;
        CardView noteCardView;
        ImageView line;

        public ReviseNotesViewHolder(View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.text_note_title);
            noteContentInShort = itemView.findViewById(R.id.text_note_content_in_short);
            noteCardView = itemView.findViewById(R.id.card_view_home_fragment_note);
            line = itemView.findViewById(R.id.line);
            this.itemView = itemView;
        }
    }
}
