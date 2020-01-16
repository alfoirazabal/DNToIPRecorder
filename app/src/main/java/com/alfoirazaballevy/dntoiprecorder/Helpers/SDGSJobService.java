package com.alfoirazaballevy.dntoiprecorder.Helpers;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alfoirazaballevy.dntoiprecorder.workers.ServerDataGetterService;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SDGSJobService extends JobService {

    public static final int JOB_ID = 1;
    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d(TAG, "Job started");
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(jobCancelled){
                    return;
                }
                ServiceConnection mConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) { }
                    @Override
                    public void onServiceDisconnected(ComponentName componentName) { }
                };
                Intent mIntent = new Intent(getApplicationContext(), ServerDataGetterService.class);
                bindService(mIntent, mConnection, BIND_AUTO_CREATE);
                unbindService(mConnection);
                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
