package com.alfoirazaballevy.dntoiprecorder.listHelpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        myHolder.txtDomainName.setText(String.valueOf(
                domainNamesAndAppearances.get(position).domainName
        ));
        myHolder.txtAppearances.setText(String.valueOf(
                domainNamesAndAppearances.get(position).appearances
        ));

    }

    @Override
    public int getItemCount() {
        return domainNamesAndAppearances.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        TextView txtDomainName;
        TextView txtAppearances;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            this.txtDomainName = itemView.findViewById(R.id.txt_domain_name);
            this.txtAppearances = itemView.findViewById(R.id.txt_appearances);

        }

    }

}
