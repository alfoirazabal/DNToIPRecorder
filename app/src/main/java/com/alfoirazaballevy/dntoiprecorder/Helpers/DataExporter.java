package com.alfoirazaballevy.dntoiprecorder.Helpers;

import android.content.Context;
import android.content.ContextWrapper;

import com.alfoirazaballevy.dntoiprecorder.dbops.DBHelper;
import com.alfoirazaballevy.dntoiprecorder.domain.FullServerData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class DataExporter {

    public static void export(Context context) throws IOException {

        DBHelper dbHelper = new DBHelper(context);
        ArrayList<FullServerData> fullServersData = dbHelper.getAllServerData();

        ContextWrapper cw = new ContextWrapper(context);

        File fout = new File(cw.getExternalFilesDir("/"), "serverData.txt");
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(FullServerData fullServerData : fullServersData) {
            bw.write(fullServerData.toString());
            bw.newLine();
        }
        bw.close();

    }

}
