package com.example.quanlychuoicuahangcaphe.Adapter;

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
import com.example.quanlychuoicuahangcaphe.Model.QuanCafe;

import com.example.quanlychuoicuahangcaphe.R;
import com.example.quanlychuoicuahangcaphe.DetailCafe;

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

        ImageView ivAnhNhaHang = customView.findViewById(R.id.ivAnhNhaHang);
        TextView tvTenNhaHang = customView.findViewById(R.id.tvTenNhaHang);
        TextView tvSoDienThoai = customView.findViewById(R.id.tvSoDienThoai);
        TextView tvGioMoCua = customView.findViewById(R.id.tvGioMoCua);
        TextView tvDiaChi = customView.findViewById(R.id.tvDiaChi);

        QuanCafe quanCafe = listQuanCafe.get(position);

        tvTenNhaHang.setText(quanCafe.getName());
        tvSoDienThoai.setText("Số điện thoại : " + quanCafe.getPhoneNumber());
        tvGioMoCua.setText(quanCafe.getOpenTime());
        tvDiaChi.setText(quanCafe.getAddress());

        if (quanCafe.getAnhQuanCafe() != null){
            if (quanCafe.getAnhQuanCafe().length() >0){
                Glide.with(context.getBaseContext()).load(quanCafe.getAnhQuanCafe()).into(ivAnhNhaHang);
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
