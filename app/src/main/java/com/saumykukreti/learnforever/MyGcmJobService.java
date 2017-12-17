package com.saumykukreti.learnforever;

/**
 * Created by saumy on 12/17/2017.
 */

import android.support.annotation.NonNull;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;

/**
 * Created by yboyar on 3/20/16.
 */
public class MyGcmJobService extends GcmJobSchedulerService {
    @NonNull
    @Override
    protected JobManager getJobManager() {
        return LearnForeverApplication.getInstance().getJobManager();
    }
}
