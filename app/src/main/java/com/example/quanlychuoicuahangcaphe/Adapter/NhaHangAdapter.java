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
import com.example.quanlychuoicuahangcaphe.Model.NhaHang;

import com.example.quanlychuoicuahangcaphe.R;
import com.example.quanlychuoicuahangcaphe.XemChiTietNhaHang;

import java.util.ArrayList;

public class NhaHangAdapter extends ArrayAdapter implements Filterable {
    Activity context;
    int resource;
    ArrayList<NhaHang> listNhaHang,listNhaHangBackup,ListNhaHangFilter;

    public NhaHangAdapter(Activity context, int resource, ArrayList<NhaHang> listNhaHang){
        super(context,resource);
        this.context = context;
        this.resource = resource;
        this.listNhaHang = this.listNhaHangBackup =listNhaHang;
    }

    @Override
    public int getCount() {
        return listNhaHang.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(resource,null);

        ImageView ivAnhNhaHang = customView.findViewById(R.id.ivAnhNhaHang);
        TextView tvTenNhaHang = customView.findViewById(R.id.tvTenNhaHang);
        TextView tvSoDienThoai = customView.findViewById(R.id.tvSoDienThoai);
        TextView tvGioMoCua = customView.findViewById(R.id.tvGioMoCua);
        TextView tvDiaChi = customView.findViewById(R.id.tvDiaChi);

        NhaHang nhaHang = listNhaHang.get(position);

        tvTenNhaHang.setText(nhaHang.getTenNhaHang());
        tvSoDienThoai.setText("Số điện thoại : " + nhaHang.getSoDienThoai());
        tvGioMoCua.setText(nhaHang.getGioMoCua());
        tvDiaChi.setText(nhaHang.getDiaChiNhaHang());

        if (nhaHang.getAnhNhaHang() != null){
            if (nhaHang.getAnhNhaHang().length() >0){
                Glide.with(context.getBaseContext()).load(nhaHang.getAnhNhaHang()).into(ivAnhNhaHang);
            }
        }

        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putSerializable("nhahang",nhaHang);
                Intent xemchitiet = new Intent(context, XemChiTietNhaHang.class);
                xemchitiet.putExtras(data);
                context.startActivityForResult(xemchitiet,100);
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
                    ListNhaHangFilter = listNhaHangBackup;
                }else {
                    ListNhaHangFilter = new ArrayList<>();
                    for (NhaHang nh : listNhaHangBackup)
                    {
                        if(nh.getTenNhaHang().toLowerCase().contains(query)
                        || nh.getDiaChiNhaHang().toLowerCase().contains(query))
                        {
                            ListNhaHangFilter.add(nh);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values= ListNhaHangFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listNhaHang = (ArrayList<NhaHang>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
