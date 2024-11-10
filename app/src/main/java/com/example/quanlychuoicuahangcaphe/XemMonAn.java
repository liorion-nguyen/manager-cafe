package com.example.quanlychuoicuahangcaphe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quanlychuoicuahangcaphe.Model.NhaHang;
import com.example.quanlychuoicuahangcaphe.Model.monAn;
import com.example.quanlychuoicuahangcaphe.Plugins.PhongtoAnh;

import java.util.ArrayList;

public class XemMonAn extends AppCompatActivity {

    TextView tvTenMonAn, tvMoTaMonAn, tvGiaMonAn;
    ImageView ivAnhMonAn;
    Button btnSuaMonAn, btnXoaMonAn, btnTroLai;
    NhaHang a = new NhaHang();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_mon_an);

        // Anh xa
        tvTenMonAn = findViewById(R.id.tvTenMonAn);
        tvMoTaMonAn = findViewById(R.id.tvMoTaMonAn);
        tvGiaMonAn = findViewById(R.id.tvGiaMonAn);
        ivAnhMonAn = findViewById(R.id.ivAnhMonAn);
        btnSuaMonAn = findViewById(R.id.btnSuaMonAn);
        btnXoaMonAn = findViewById(R.id.btnXoaMonAn);
        btnTroLai = findViewById(R.id.btnTroLai);

        // Doan code
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        monAn ma = (monAn) data.getSerializable("monan");
        a = (NhaHang) data.getSerializable("nhahang");
        tvTenMonAn.setText(ma.getTenMonAn());
        tvMoTaMonAn.setText("Mô tả món ăn : " + ma.getMoTaMonAn());
        tvGiaMonAn.setText("Giá món ăn : " + (int) ma.getGiaMonAn() + "vnđ");
        Glide.with(XemMonAn.this).load(ma.getHinhAnhMinhHoa()).into(ivAnhMonAn);

        ivAnhMonAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(XemMonAn.this, PhongtoAnh.class);
                Bundle data = new Bundle();
                data.putString("anh", ma.getHinhAnhMinhHoa());
                intent.putExtras(data);
                startActivity(intent);
            }
        });

        btnTroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dulieu = new Intent();
                Bundle data = new Bundle();
                data.putSerializable("listmonan",a.getListMonAn());
                dulieu.putExtras(data);
                setResult(104,dulieu);
                finish();
            }
        });

        btnSuaMonAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent xemMonAn = new Intent(XemMonAn.this, SuaMonAnActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("monan",ma);
                data.putSerializable("nhahang",a);
                xemMonAn.putExtras(data);
                startActivityForResult(xemMonAn,105);
            }
        });

        btnXoaMonAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(XemMonAn.this);
                b.setTitle("Xóa món ăn");
                b.setMessage("Bạn có chắc là muốn xóa món ăn khỏi thực đơn không ?");
                b.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                b.setPositiveButton("Chắc chắn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent dulieu = new Intent();
                        Bundle data = new Bundle();
                        data.putSerializable("monan",ma);
                        dulieu.putExtras(data);
                        setResult(106, dulieu);
                        finish();
                    }
                });
                b.create().show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 105 && requestCode == 105){
            Bundle dulieu = data.getExtras();
            monAn moi = (monAn) dulieu.getSerializable("monan");
            ArrayList<monAn> listMonAnMoi = new ArrayList<>();
            for (monAn monan : a.getListMonAn()){
                if (monan.getId().equals(moi.getId())){
                    listMonAnMoi.add(moi);
                } else {
                    listMonAnMoi.add(monan);
                }
            }
            a.setListMonAn(listMonAnMoi);
            tvTenMonAn.setText(moi.getTenMonAn());
            tvMoTaMonAn.setText("Mô tả món ăn : " + moi.getTenMonAn());
            tvGiaMonAn.setText("Giá món ăn : " + (int)moi.getGiaMonAn() + "vnđ");
            Glide.with(XemMonAn.this).load(moi.getHinhAnhMinhHoa()).into(ivAnhMonAn);
        }
    }
}