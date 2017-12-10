package com.saumykukreti.learnforever.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.activities.NoteActivity;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import java.util.List;

/**
 * Created by saumy on 12/9/2017.
 */

public class HomeFragmentNotesRecyclerViewAdapter extends RecyclerView.Adapter<HomeFragmentNotesRecyclerViewAdapter.HomeFragmentNotesViewHolder>{

    private Context mContext;
    private List<NoteTable> mNoteList;

    public HomeFragmentNotesRecyclerViewAdapter(Context context, List<NoteTable> noteList) {
        mContext = context;
        mNoteList = noteList;
    }

    @Override
    public HomeFragmentNotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new HomeFragmentNotesViewHolder(layoutInflater.inflate(R.layout.home_fragment_recycler_view_note, parent,false));
    }

    @Override
    public void onBindViewHolder(HomeFragmentNotesViewHolder holder, int position) {
        final NoteTable note = mNoteList.get(position);

        holder.noteTitle.setText(note.getTitle());
        holder.noteContentInShort.setText(note.getContentInShort());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NoteActivity.class);
                intent.putExtra(NoteActivity.METADATA_NOTE, note);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    class HomeFragmentNotesViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView noteTitle;
        TextView noteContentInShort;

        public HomeFragmentNotesViewHolder(View itemView) {
            super(itemView);
            noteTitle= itemView.findViewById(R.id.text_note_title);
            noteContentInShort= itemView.findViewById(R.id.text_note_content_in_short);
            this.itemView=itemView;
        }
    }




}
