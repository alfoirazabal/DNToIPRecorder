package com.alfoirazaballevy.dntoiprecorder;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alfoirazaballevy.dntoiprecorder.dbops.DBHelper;
import com.alfoirazaballevy.dntoiprecorder.listHelpers.IPListAdapter;

import java.util.ArrayList;

public class DomainNamesIPs extends AppCompatActivity {

    private TextView txtDomainName;
    private RecyclerView lstIPs;

    private RecyclerView.LayoutManager layoutManager;
    private IPListAdapter ipListAdapter;

    private String domainName;

    private DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.domain_name_ips_list);

        txtDomainName = findViewById(R.id.txt_domain_name);
        lstIPs = findViewById(R.id.lst_ips);
        dbHelper = new DBHelper(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        this.domainName = bundle.getString("DOMAINNAME");

        txtDomainName.setText(this.domainName);

        ArrayList<String> ips = dbHelper.getIPsForDomainName(this.domainName);

        lstIPs.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstIPs.setLayoutManager(layoutManager);

        ipListAdapter = new IPListAdapter(this, ips);
        lstIPs.setAdapter(ipListAdapter);

    }

}
