package com.alfoirazaballevy.dntoiprecorder.dbops;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alfoirazaballevy.dntoiprecorder.DomainNamesListActivity;
import com.alfoirazaballevy.dntoiprecorder.domain.FullServerData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class DBHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    private static final String DATABASE_NAME = "dndata.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_REGS = "_registrations";

    private static final String TABLE_REGS_ID = "id";
    private static final String TABLE_REGS_DATETIME = "datetime";
    private static final String TABLE_REGS_DOMAIN = "domain";
    private static final String TABLE_REGS_IP = "ip";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final String LOG_TAG = "DBHelper";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryTable = "CREATE TABLE " + TABLE_REGS + "(";
        queryTable += getInsStr(TABLE_REGS_ID, "INTEGER PRIMARY KEY", false);
        queryTable += getInsStr(TABLE_REGS_DATETIME, "TEXT", false);
        queryTable += getInsStr(TABLE_REGS_DOMAIN, "TEXT", false);
        queryTable += getInsStr(TABLE_REGS_IP, "TEXT", true);
        queryTable += ");";
        db.execSQL(queryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    public long addUnrepeatedRegistration(Date dateTime, String domain, String ip) {
        db = this.getWritableDatabase();
        long returnable;
        long foundRegistrationTableIndex = ipAndDomainTableIndex(domain, ip);
        if(foundRegistrationTableIndex == -1){
            ContentValues values = new ContentValues();
            values.put(TABLE_REGS_DATETIME, SIMPLE_DATE_FORMAT.format(dateTime));
            values.put(TABLE_REGS_DOMAIN, domain);
            values.put(TABLE_REGS_IP, ip);
            returnable = db.insert(TABLE_REGS, null, values);
        }else{
            Log.d(LOG_TAG, "Repeated Registration found for DN: '" +
                    domain + "' with IP: " + ip + ". Overriding DATETIME");
            overrideDateTimeOfRegistration(foundRegistrationTableIndex, dateTime);
            returnable = foundRegistrationTableIndex;
        }
        return returnable;
    }

    private long ipAndDomainTableIndex(String domain, String ip) {
        int tableIndex = -1;
        db = this.getReadableDatabase();
        String[] columns = new String[]{TABLE_REGS_ID};
        String whereClause = TABLE_REGS_DOMAIN + " = ? AND " + TABLE_REGS_IP + " = ?";
        String[] whereArgs = new String[]{domain, ip};
        Cursor cursor = db.query(
                TABLE_REGS,
                columns,
                whereClause, whereArgs, null, null, null, null
        );
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int iId = cursor.getColumnIndex(columns[0]);
            Log.d(LOG_TAG, "iId for domain and ip: " + domain + " - " + ip + ": " + iId);
            tableIndex = cursor.getInt(iId);
        }
        return tableIndex;
    }

    private void overrideDateTimeOfRegistration(long registrationId, Date dateTime) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TABLE_REGS_DATETIME, SIMPLE_DATE_FORMAT.format(dateTime));
        db.update(
                TABLE_REGS, cv, TABLE_REGS_ID + " = " + registrationId, null
        );
    }

    public ArrayList<String> getIPsForDomainName(String domainName) {
        db = this.getReadableDatabase();
        String[] columns = new String[]{TABLE_REGS_IP};
        String whereClause = TABLE_REGS_DOMAIN + " = ?";
        String[] whereArgs = new String[]{domainName};
        Cursor cursor = db.query(
                TABLE_REGS,
                columns,
                whereClause, whereArgs, null, null, null, null
        );
        ArrayList<String> ips = new ArrayList<>();
        int iIp = cursor.getColumnIndex(columns[0]);
        for(cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            ips.add(cursor.getString(iIp));
        }

        return ips;
    }

    public void deleteRegistration(long l) {
        db = this.getWritableDatabase();
        db.delete(TABLE_REGS, TABLE_REGS_ID + " = " + l, null);
    }

    public ArrayList<FullServerData> getAllServerData(){
        db = this.getReadableDatabase();
        String[] columns = new String[]{
                TABLE_REGS_ID,
                TABLE_REGS_DATETIME,
                TABLE_REGS_DOMAIN,
                TABLE_REGS_IP
        };
        Cursor cursor = db.query(
                TABLE_REGS,
                columns,
                null, null, null,
                null, null, null
        );
        int iId = cursor.getColumnIndex(columns[0]);
        int iDateTime = cursor.getColumnIndex(columns[1]);
        int iDomain = cursor.getColumnIndex(columns[2]);
        int iIp = cursor.getColumnIndex(columns[3]);

        ArrayList<FullServerData> fullServerData = new ArrayList<>();

        //Cursor iteration IN REVERSE ORDER
        for(cursor.moveToLast() ; !cursor.isBeforeFirst() ; cursor.moveToPrevious()) {
            int id = cursor.getInt(iId);
            String dateTime = cursor.getString(iDateTime);
            String domain = cursor.getString(iDomain);
            String ip = cursor.getString(iIp);
            FullServerData fsd = new FullServerData(id, dateTime, domain, ip);
            fullServerData.add(fsd);
        }

        return fullServerData;
    }

    public ArrayList<DomainNamesListActivity.DomainNamesAndAppearances> getListOfDNsAndTimesRequested() {
        db = this.getReadableDatabase();
        String[] columns = new String[]{
                TABLE_REGS_DOMAIN
        };
        Cursor cursor = db.query(TABLE_REGS, columns,
                null, null, null, null, null);
        int iDomain = cursor.getColumnIndex(columns[0]);

        HashMap<String, Integer> dnsAndTimesRequested = new HashMap<>();

        for(cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            String domainName = cursor.getString(iDomain);
            if(dnsAndTimesRequested.containsKey(domainName)){
                dnsAndTimesRequested.put(domainName, dnsAndTimesRequested.get(domainName) + 1);
            }else{
                dnsAndTimesRequested.put(domainName, 1);
            }
        }

        ArrayList<DomainNamesListActivity.DomainNamesAndAppearances> domainNamesAndAppearances =
                new ArrayList<>();

        Iterator<String> itDNSs =
                dnsAndTimesRequested.keySet().iterator();
        while(itDNSs.hasNext()) {
            String domainName = itDNSs.next();
            int appearances = dnsAndTimesRequested.get(domainName);
            domainNamesAndAppearances.add(
                    new DomainNamesListActivity.DomainNamesAndAppearances(domainName, appearances)
            );
        }

        return domainNamesAndAppearances;
    }

    //Get Insertion String
    private String getInsStr(String key, String insertionData, boolean lastOne){
        String returnable = key + " " + insertionData;
        if(!lastOne){
            returnable += ", ";
        }
        return returnable;
    }

}
