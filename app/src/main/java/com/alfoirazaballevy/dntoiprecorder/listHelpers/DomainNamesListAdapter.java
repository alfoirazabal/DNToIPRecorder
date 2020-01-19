package com.alfoirazaballevy.dntoiprecorder.listHelpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alfoirazaballevy.dntoiprecorder.DomainNamesIPs;
import com.alfoirazaballevy.dntoiprecorder.DomainNamesListActivity;
import com.alfoirazaballevy.dntoiprecorder.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DomainNamesListAdapter extends RecyclerView.Adapter<DomainNamesListAdapter.MyHolder> {

    Context context;
    ArrayList<DomainNamesListActivity.DomainNamesAndAppearances> domainNamesAndAppearances;

    public DomainNamesListAdapter(
            Context context,
            ArrayList<DomainNamesListActivity.DomainNamesAndAppearances> domainNamesAndAppearances
    ) {
        this.context = context;
        this.domainNamesAndAppearances = domainNamesAndAppearances;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.domain_names_list_item, parent, false);
        MyHolder myHolder = new MyHolder(view);

        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {

        final DomainNamesListActivity.DomainNamesAndAppearances domainNameAndAppearance =
                domainNamesAndAppearances.get(position);

        myHolder.txtDomainName.setText(String.valueOf(
                domainNameAndAppearance.domainName
        ));
        myHolder.txtAppearances.setText(String.valueOf(
                domainNameAndAppearance.appearances
        ));

        myHolder.layoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DomainNamesIPs.class);
                intent.putExtra("DOMAINNAME", domainNameAndAppearance.domainName);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return domainNamesAndAppearances.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        TextView txtDomainName;
        TextView txtAppearances;
        ConstraintLayout layoutContainer;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            this.txtDomainName = itemView.findViewById(R.id.txt_domain_name);
            this.txtAppearances = itemView.findViewById(R.id.txt_appearances);
            this.layoutContainer = itemView.findViewById(R.id.layout_container);

        }

    }

}
