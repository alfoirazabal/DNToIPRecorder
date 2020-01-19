package com.alfoirazaballevy.dntoiprecorder.listHelpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alfoirazaballevy.dntoiprecorder.R;

import java.util.ArrayList;

public class IPListAdapter extends RecyclerView.Adapter<IPListAdapter.MyHolder> {

    Context context;
    ArrayList<String> ips;

    public IPListAdapter(Context context, ArrayList<String> ips) {
        this.context = context;
        this.ips = ips;
    }

    @NonNull
    @Override
    public IPListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.domain_name_ips_list_item, parent, false);

        MyHolder myHolder = new MyHolder(view);
        return myHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull IPListAdapter.MyHolder holder, int position) {
        holder.txtIP.setText(ips.get(position));
    }

    @Override
    public int getItemCount() {
        return ips.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        TextView txtIP;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            txtIP = itemView.findViewById(R.id.txt_ip);
        }

    }
}
