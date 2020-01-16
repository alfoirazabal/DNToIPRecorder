package com.alfoirazaballevy.dntoiprecorder.workers;

import android.app.Service;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.alfoirazaballevy.dntoiprecorder.Helpers.NotificationsHandler;
import com.alfoirazaballevy.dntoiprecorder.R;
import com.alfoirazaballevy.dntoiprecorder.dbops.DBHelper;
import com.alfoirazaballevy.dntoiprecorder.domain.FullServerData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

import dntoiprecorderlib.DNToIPRecorderLib;

public class ServerDataGetterService extends Service {

    public static final String FILE_NAME_DOMAINS_DATA = "domains.txt";

    public static File FOLDER_WRITABLE_ANDROID_DATA;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        FOLDER_WRITABLE_ANDROID_DATA = new ContextWrapper(
                getApplicationContext()
        ).getExternalFilesDir("domainsData");
        String domainsData = "";
        String[] requestableDomainNames;

        FileReader reader = null;
        try {
            reader = new FileReader(
                    new File(FOLDER_WRITABLE_ANDROID_DATA, FILE_NAME_DOMAINS_DATA)
            );
            int character;
            while((character = reader.read()) != -1){
                domainsData += (char) character;
            }
            reader.close();
            requestableDomainNames = domainsData.split("\n");
            new DataRequester().execute(requestableDomainNames);
        } catch (FileNotFoundException e) {
            //Show error notification (or first time running the app notification)!
        } catch (IOException e) {
            //Show error notification
        }
        return null;
    }

    private class DataRequester extends AsyncTask<String[], Void, FullServerData[]> {

        @Override
        protected FullServerData[] doInBackground(String[]... requestableDomainNames) {
            FullServerData[] serversData = new FullServerData[requestableDomainNames[0].length];
            NotificationsHandler notsHand = new NotificationsHandler(getApplicationContext());
            for(int i = 0 ; i < serversData.length ; i++) {
                notsHand.displayBigNotification(
                        NotificationsHandler.NOTIF_ID_SERVER_DATA_GETTER,
                        R.mipmap.ic_launcher_foreground,
                        "DN To IP Recorder - By Alfo",
                        (i + 1) + "/" + serversData.length + "\nLocating '" +
                                requestableDomainNames[0][i] + "' IP",
                        1
                );
                FullServerData serverData;
                try {
                    serverData = FullServerData.convertFromServerData(
                            DNToIPRecorderLib.getServerData(requestableDomainNames[0][i])
                    );
                } catch (UnknownHostException e) {
                    serverData = new FullServerData(e, new Date(), requestableDomainNames[0][i]);
                }
                serversData[i] = serverData;
            }
            notsHand.removeNotification(1);
            return serversData;
        }

        @Override
        protected void onPostExecute(FullServerData[] fullServerData) {
            saveServersDataToDB(fullServerData);
        }
    }

    private void saveServersDataToDB(FullServerData[] finalServersData) {

        DBHelper dbHelper = new DBHelper(this);

        int nHostsNotFound = 0;

        for(FullServerData serverData : finalServersData) {
            if(serverData.hasError()){
                dbHelper.addUnrepeatedRegistration(
                        new Date(), serverData.getDnsName(), serverData.getError().getMessage()
                );
                nHostsNotFound++;
            }else{
                dbHelper.addUnrepeatedRegistration(
                        new Date(), serverData.getDnsName(), serverData.getIp()
                );
            }
        }

        NotificationsHandler notifsHandler = new NotificationsHandler(getApplicationContext());
        if(nHostsNotFound == 0){
            notifsHandler.displayBigNotification(
                    NotificationsHandler.NOTIF_ID_SERVER_DATA_COMPLETIONS,
                    R.mipmap.ic_launcher,
                    "DN To IP Recorder - By Alfo",
                    "OK: All Domain names had found and registered IPs",
                    2
            );
        }else{
            notifsHandler.displayBigNotification(
                    NotificationsHandler.NOTIF_ID_SERVER_DATA_COMPLETIONS,
                    R.mipmap.ic_launcher_error,
                    "DN To IP Recorder - By Alfo",
                    "WARNING: " + nHostsNotFound + " domain names had no IP or where not " +
                            "found!",
                    2
            );
        }

    }
}
