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

public class CacAnhCafeAdapter extends ArrayAdapter {
    Activity context;
    int resourse;
    ArrayList<String> listCacHinhAnhQuanCafe;

    public CacAnhCafeAdapter(Activity context, int resourse, ArrayList<String> listCacHinhAnhQuanCafe){
        super(context,resourse);
        this.context = context;
        this.resourse = resourse;
        this.listCacHinhAnhQuanCafe = listCacHinhAnhQuanCafe;
    }

    @Override
    public int getCount() {
        return listCacHinhAnhQuanCafe.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(resourse,null);

        ImageView ivAnhQuanCafe = customView.findViewById(R.id.ivAvatar);
        String src = listCacHinhAnhQuanCafe.get(position);
        Glide.with(context.getBaseContext()).load(src).into(ivAnhQuanCafe);

        return customView;
    }
}
