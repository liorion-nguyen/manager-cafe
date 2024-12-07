package com.example.quanlychuoicuahangcafe.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.quanlychuoicuahangcafe.Model.QuanCafe;

import com.example.quanlychuoicuahangcafe.R;
import com.example.quanlychuoicuahangcafe.DetailCafe;

import java.util.ArrayList;

public class QuanCafeAdapter extends ArrayAdapter implements Filterable {
    Activity context;
    int resource;
    ArrayList<QuanCafe> listQuanCafe, listQuanCafeBackup, ListQuanCafeFilter;


    public QuanCafeAdapter(Activity context, int resource, ArrayList<QuanCafe> listQuanCafe){
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.listQuanCafe = this.listQuanCafeBackup = listQuanCafe;
    }

    @Override
    public int getCount() {
        return listQuanCafe.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(resource, null);

        ImageView ivAvatar = customView.findViewById(R.id.ivAvatar);
        TextView tvName = customView.findViewById(R.id.tvName);
        TextView tvPhoneNumber = customView.findViewById(R.id.tvPhoneNumber);
        TextView tvOpenTime = customView.findViewById(R.id.tvOpenTime);
        TextView tvAddress = customView.findViewById(R.id.tvAddress);

        QuanCafe quanCafe = listQuanCafe.get(position);

        tvName.setText(quanCafe.getName());
        tvPhoneNumber.setText("Số điện thoại : " + quanCafe.getPhoneNumber());
        tvOpenTime.setText(quanCafe.getOpenTime());
        tvAddress.setText(quanCafe.getAddress());

        if (quanCafe.getAvatar() != null){
            if (quanCafe.getAvatar().length() >0){
                Glide.with(context.getBaseContext()).load(quanCafe.getAvatar()).into(ivAvatar);
            }
        }

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putSerializable("cafe", quanCafe);
                Intent xemchitiet = new Intent(context, DetailCafe.class);
                xemchitiet.putExtras(data);
                context.startActivityForResult(xemchitiet, 100);
            }
        });

        return customView;
    }

    //get filter

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase().trim();
                if (query.length()<1)
                {
                    ListQuanCafeFilter = listQuanCafeBackup;
                }else {
                    ListQuanCafeFilter = new ArrayList<>();
                    for (QuanCafe nh : listQuanCafeBackup)
                    {
                        if(nh.getName().toLowerCase().contains(query)
                        || nh.getAddress().toLowerCase().contains(query))
                        {
                            ListQuanCafeFilter.add(nh);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values= ListQuanCafeFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listQuanCafe = (ArrayList<QuanCafe>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
