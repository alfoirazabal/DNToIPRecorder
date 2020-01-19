package com.alfoirazaballevy.dntoiprecorder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.alfoirazaballevy.dntoiprecorder.Helpers.DataExporter;
import com.alfoirazaballevy.dntoiprecorder.Helpers.NotificationsHandler;
import com.alfoirazaballevy.dntoiprecorder.Helpers.SDGSJobService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String FILE_NAME_DOMAINS_DATA = "domains.txt";
    public static File FOLDER_WRITABLE_ANDROID_DATA;

    private boolean mBounded;

    private EditText etxtDomains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manageInit();

        etxtDomains = findViewById(R.id.etxt_domains);

    }

    @Override
    public void onResume() {
        String domainsData = "";
        try {
            FileReader reader = new FileReader(
                    new File(FOLDER_WRITABLE_ANDROID_DATA, FILE_NAME_DOMAINS_DATA)
            );
            int character;
            while((character = reader.read()) != -1){
                domainsData += (char) character;
            }
            reader.close();
            etxtDomains.setText(domainsData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        saveDomainsTextData();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mBounded){
            unbindService(mConnection);
            mBounded = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.run_and_schedule_15mins:
                checkAvailScheduleAndRunJob(15);
                return true;
            case R.id.run_and_schedule_30mins:
                checkAvailScheduleAndRunJob(30);
                return true;
            case R.id.run_and_schedule_1hour:
                checkAvailScheduleAndRunJob(60);
                return true;
            case R.id.run_and_schedule_6hours:
                checkAvailScheduleAndRunJob(360);
                return true;
            case R.id.run_and_schedule_12hours:
                checkAvailScheduleAndRunJob(720);
                return true;
            case R.id.run_and_schedule_1day:
                checkAvailScheduleAndRunJob(1440);
                return true;
            case R.id.job_unschedule:
                cancelJob();
                Toast.makeText(
                        getApplicationContext(),
                        "Job Unscheduled!",
                        Toast.LENGTH_LONG
                ).show();
            case R.id.view_regs:

                return true;
            case R.id.export_regs:
                try{
                    DataExporter.export(getApplicationContext());
                    Toast.makeText(
                            getApplicationContext(),
                            "Data Saved!",
                            Toast.LENGTH_LONG
                    ).show();
                } catch(IOException e) {
                    Toast.makeText(
                            getApplicationContext(),
                            "IO Exception thrown while exporting the data.",
                            Toast.LENGTH_LONG
                    ).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveDomainsTextData() {
        String domainsData = etxtDomains.getText().toString();
        try {
            FileWriter writer = new FileWriter(
                    new File(FOLDER_WRITABLE_ANDROID_DATA, FILE_NAME_DOMAINS_DATA)
            );
            writer.write(domainsData);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) { }
        @Override
        public void onServiceDisconnected(ComponentName componentName) { }
    };

    private void manageInit() {
        FOLDER_WRITABLE_ANDROID_DATA = new ContextWrapper(
                getApplicationContext()
        ).getExternalFilesDir("domainsData");
        NotificationsHandler notifsHandler = new NotificationsHandler(getApplicationContext());
        notifsHandler.createChannels();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
       String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permissions " + Arrays.toString(permissions) + " granted!");
                    //OK
                } else {
                    Toast.makeText(
                            this,
                            "There will be a problem with the background services! " +
                                    "Please enable autostart and unlimited battery usage " +
                                    "in your phone next time.",
                            Toast.LENGTH_LONG
                    );
                }
                return;
            }
        }
    }

    public void checkAvailScheduleAndRunJob(int minutesScheduledFor) {
        if(!mBounded){
            mBounded = true;
            Toast.makeText(
                    getApplicationContext(),
                    "Scheduled for every " + minutesScheduledFor +
                            " minutes and running now",
                    Toast.LENGTH_LONG
            ).show();
            saveDomainsTextData();
            scheduleAndStartJob(minutesScheduledFor);
            mBounded = false;
        }else{
            Toast.makeText(
                    getApplicationContext(),
                    "Service already running (bounded)",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public void scheduleAndStartJob(int minutesScheduledFor) {
        ComponentName componentName = new ComponentName(this, SDGSJobService.class);
        JobInfo info = new JobInfo.Builder(SDGSJobService.JOB_ID, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(minutesScheduledFor * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        }else{
            Log.d(TAG, "Job scheduling failed!");
        }
    }

    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(SDGSJobService.JOB_ID);
        Log.d(TAG, "Job cancelled");
    }
}
