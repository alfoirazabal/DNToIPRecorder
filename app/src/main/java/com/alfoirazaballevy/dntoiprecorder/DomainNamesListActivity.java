package com.alfoirazaballevy.dntoiprecorder;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alfoirazaballevy.dntoiprecorder.dbops.DBHelper;
import com.alfoirazaballevy.dntoiprecorder.listHelpers.DomainNamesListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DomainNamesListActivity extends AppCompatActivity {

    private DBHelper dbHelper = new DBHelper(this);

    private RecyclerView lstDomains;
    private RecyclerView.LayoutManager layoutManager;
    private DomainNamesListAdapter domainNamesListAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.domain_names_list);

        ArrayList<DomainNamesAndAppearances> domainNamesAndAppearances =
                dbHelper.getListOfDNsAndTimesRequested();
        Collections.sort(domainNamesAndAppearances);

        lstDomains = findViewById(R.id.lst_domains);
        lstDomains.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstDomains.setLayoutManager(layoutManager);

        domainNamesListAdapter = new DomainNamesListAdapter(
                this, domainNamesAndAppearances
        );
        lstDomains.setAdapter(domainNamesListAdapter);

    }

    public static class DomainNamesAndAppearances implements Comparable<DomainNamesAndAppearances> {

        public String domainName;
        public int appearances;

        public DomainNamesAndAppearances(String domainName, int appearances) {
            this.domainName = domainName;
            this.appearances = appearances;
        }

        @Override
        public int compareTo(DomainNamesAndAppearances domainNamesAndAppearances) {
            return domainNamesAndAppearances.appearances - this.appearances;
        }
    }

}

