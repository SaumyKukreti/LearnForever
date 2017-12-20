
package com.saumykukreti.learnforever.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saumykukreti.learnforever.dataManager.NoteDataController;
import com.saumykukreti.learnforever.events.InitializationCompleteEvent;
import com.saumykukreti.learnforever.modelClasses.dataTables.NoteTable;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saumy on 12/17/2017.
 */

public class DataInitializerJob extends Job {

    private static final int PRIORITY = 1;
    private static final String TAG = DataSyncJob.class.getSimpleName();
    private final Context mContext;
    private GoogleSignInAccount mAccount;
    private NoteDataController mDataController;
    private SharedPreferences mPreference;

    public DataInitializerJob(Context context, Params params) {
        super(new Params(PRIORITY).requireNetwork());
        mContext = context;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        mAccount = GoogleSignIn.getLastSignedInAccount(mContext);
        mDataController = NoteDataController.getInstance(mContext);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(mAccount.getId());
        myRef.child("Notes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()>0) {
                    List<NoteTable> listOfNotes = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        listOfNotes.add(data.getValue(NoteTable.class));
                    }
                    mDataController.newNotes(listOfNotes);
                    EventBus.getDefault().post(new InitializationCompleteEvent());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
