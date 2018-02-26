package com.saumykukreti.learnforever.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.util.DateHandler;

import java.util.ArrayList;
import java.util.Date;

public class ReviseDatesAdapter extends RecyclerView.Adapter<ReviseDatesAdapter.ReviseDatesViewHolder> {

    private final ArrayList<Date> mListOfReminderDates;
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private int mCurrentPosition = 0;

    public ReviseDatesAdapter(Context context, ArrayList<Date> listOfDates) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListOfReminderDates = listOfDates;
        mContext = context;
    }

    @Override
    public ReviseDatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.revise_dates_item_view, parent, false);
        return new ReviseDatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviseDatesViewHolder holder, final int position) {
        Date date = mListOfReminderDates.get(position);
        holder.reviseDate.setText(DateHandler.convertDateToString(date));
    }

    @Override
    public int getItemCount() {
        return mListOfReminderDates.size();
    }

    @Override
    public void onViewRecycled(ReviseDatesViewHolder holder) {
        super.onViewRecycled(holder);
    }

    protected class ReviseDatesViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        TextView reviseDate;
        public ReviseDatesViewHolder(View itemView) {
            super(itemView);
            reviseDate = itemView.findViewById(R.id.text_revise_date);
            this.itemView = itemView;
        }
    }
}

