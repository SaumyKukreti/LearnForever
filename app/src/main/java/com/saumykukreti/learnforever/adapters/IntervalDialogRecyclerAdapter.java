package com.saumykukreti.learnforever.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.constants.Constants;
import com.saumykukreti.learnforever.dialog.IntervalDialog;
import com.saumykukreti.learnforever.util.Utility;

import java.util.ArrayList;

/**
 * Created by saumy on 12/9/2017.
 */

public class IntervalDialogRecyclerAdapter extends RecyclerView.Adapter<IntervalDialogRecyclerAdapter.IntervalDialogViewHolder>{


    private final Context mContext;
    private final ArrayList<String> mListOfIntervals;
    private final IntervalDialog.intervalDialogListener mListener;
    private final String mCurrentInterval;

    public IntervalDialogRecyclerAdapter(Context context, ArrayList<String> arrayListOfIntervals, IntervalDialog.intervalDialogListener intervalDialogListener) {
        mContext = context;
        mListOfIntervals = arrayListOfIntervals;
        mListener = intervalDialogListener;
        mCurrentInterval = Utility.getStringFromPreference(context, Constants.LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL);
    }

    @Override
    public IntervalDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new IntervalDialogViewHolder(layoutInflater.inflate(R.layout.interval_dialog_item_layout, parent,false));
    }

    @Override
    public void onBindViewHolder(final IntervalDialogViewHolder holder, final int position) {
        String interval = mListOfIntervals.get(position);
        holder.intervalText.setText(interval);

        holder.intervalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.intervalClicked(position);
            }
        });

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.intervalClicked(position);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletionConfirmationDialog(position);
            }
        });


        if(mCurrentInterval.equalsIgnoreCase(interval)){
            holder.radioButton.setChecked(true);
        }
    }

    private void showDeletionConfirmationDialog(final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Do you wish to delete this interval?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.intervalDeleted(position);
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return mListOfIntervals.size();
    }

    class IntervalDialogViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView intervalText;
        RadioButton radioButton;
        ImageView deleteButton;

        public IntervalDialogViewHolder(View itemView) {
            super(itemView);
            intervalText = itemView.findViewById(R.id.text_interval);
            radioButton = itemView.findViewById(R.id.radio_interval);
            deleteButton = itemView.findViewById(R.id.image_delete_interval);
            this.itemView=itemView;
        }
    }

}
