package com.example.quanlychuoicuahangcaphe.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.quanlychuoicuahangcaphe.R;

import java.util.ArrayList;

public class CacAnhNhaHangAdapter extends ArrayAdapter {
    Activity context;
    int resourse;
    ArrayList<String> listCacHinhAnhNhaHang;

    public CacAnhNhaHangAdapter(Activity context, int resourse, ArrayList<String> listCacHinhAnhNhaHang){
        super(context,resourse);
        this.context = context;
        this.resourse = resourse;
        this.listCacHinhAnhNhaHang = listCacHinhAnhNhaHang;
    }

    @Override
    public int getCount() {
        return listCacHinhAnhNhaHang.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(resourse,null);

        ImageView ivAnhNhaHang = customView.findViewById(R.id.ivAnhNhaHang);
        String src = listCacHinhAnhNhaHang.get(position);
        Glide.with(context.getBaseContext()).load(src).into(ivAnhNhaHang);

        return customView;
    }
}
