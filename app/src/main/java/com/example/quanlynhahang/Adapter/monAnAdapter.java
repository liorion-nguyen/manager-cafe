package com.example.quanlynhahang.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.quanlynhahang.Model.monAn;
import com.example.quanlynhahang.R;

import java.util.ArrayList;

public class monAnAdapter extends ArrayAdapter {
    Activity context;
    int resource;
    ArrayList<monAn> listMonAn;

    public monAnAdapter(Activity context, int resource, ArrayList<monAn> listMonAn){
        super(context,resource);
        this.context = context;
        this.resource = resource;
        this.listMonAn = listMonAn;
    }

    @Override
    public int getCount() {
        return listMonAn.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(resource,null);

        ImageView ivHinhAnhMinhHoa = customView.findViewById(R.id.ivHinhAnhMinhHoa);
        TextView tvTenMonAn = customView.findViewById(R.id.tvTenMonAn);
        TextView tvGiaMonAn = customView.findViewById(R.id.tvGiaMonAn);

        monAn ma = listMonAn.get(position);

        if (ma.getHinhAnhMinhHoa().trim().length() > 0){
            Glide.with(context.getBaseContext()).load(ma.getHinhAnhMinhHoa()).into(ivHinhAnhMinhHoa);
        }
        tvTenMonAn.setText(ma.getTenMonAn());
        tvGiaMonAn.setText((int)ma.getGiaMonAn() + "vnđ");

        return customView;
    }
}
