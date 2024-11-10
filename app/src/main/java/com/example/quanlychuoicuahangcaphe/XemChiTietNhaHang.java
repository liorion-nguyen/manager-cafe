package com.example.quanlychuoicuahangcaphe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.quanlychuoicuahangcaphe.Plugins.MapsActivity;
import com.example.quanlychuoicuahangcaphe.Plugins.PhongtoAnh;

import com.example.quanlychuoicuahangcaphe.Model.NhaHang;
import com.example.quanlychuoicuahangcaphe.Model.monAn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class XemChiTietNhaHang extends AppCompatActivity {
    TextView tvTenNhaHang, tvDiaChiNhaHang, tvEmail, tvSoDienThoai, tvGioMoCua, tvMoTaNhaHang,textrating;
    Button btnTroLai,btnXemAnhNhaHang,btnXemThucDon,btnSuaNhaHang,btnXoaAnhNhaHang;
    ImageButton GuiEmail,phone,sms,btnShowMap;
    ImageView ivAnhNhaHang;
    RatingBar rating;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference nhaHang = databaseReference.child("nhaHang");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    NhaHang a = new NhaHang();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_chi_tiet_nha_hang);
        // Ánh xạ
        tvTenNhaHang = findViewById(R.id.tvTenNhaHang);
        tvDiaChiNhaHang = findViewById(R.id.tvDiaChiNhaHang);
        tvEmail = findViewById(R.id.tvEmail);
        tvSoDienThoai = findViewById(R.id.tvSoDienThoai);
        tvGioMoCua = findViewById(R.id.tvGioMoCua);
        tvMoTaNhaHang = findViewById(R.id.tvMoTaNhaHang);
        btnTroLai = findViewById(R.id.btnTroLai);
        btnXemAnhNhaHang = findViewById(R.id.btnXemAnhNhaHang);
        btnXemThucDon = findViewById(R.id.btnXemThucDon);
        btnSuaNhaHang = findViewById(R.id.btnSuaNhaHang);
        ivAnhNhaHang = findViewById(R.id.ivAnhNhaHang);
        btnShowMap = findViewById(R.id.btnShowMap);
        btnXoaAnhNhaHang = findViewById(R.id.btnXoaAnhNhaHang);

        GuiEmail = findViewById(R.id.mail);
        phone = findViewById(R.id.phone);
        sms= findViewById(R.id.sms);
        rating = findViewById(R.id.rating);
        textrating = findViewById(R.id.textrating);



        //hiện dánh giá
        Intent intent = getIntent();
        Bundle data =  intent.getExtras();
        a = (NhaHang) data.getSerializable("nhahang");

        Float rate = (float) Math.ceil(a.getTb() * 10) / 10;
        rating.setRating(rate);
        textrating.setText(rate+"/5");

        // Phan code

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(XemChiTietNhaHang.this, MapsActivity.class);
                intent.putExtra("nhahang_id", a.getId());
                startActivity(intent);
            }
        });
        btnTroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(100);
                finish();
            }
        });

        btnXoaAnhNhaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(XemChiTietNhaHang.this);
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
                        // Xoa trong storage + realtime
                        nhaHang.child(a.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                DataSnapshot data = snapshot.child("cacAnhNhaHang");
                                HashMap<String, String> id_anh = new HashMap<>();
                                id_anh = (HashMap<String, String>) data.getValue();
                                if (id_anh != null) {
                                    for (String key : id_anh.keySet()){
                                        StorageReference fileCanXoa = storageReference.child("anhNhaHang")
                                                .child(a.getId()).child(key+".jpg");
                                        fileCanXoa.delete();
                                    }
                                }

                                DataSnapshot data1 = snapshot.child("thucDon");
                                for (DataSnapshot data2 : data1.getChildren()){
                                    monAn ma = data2.getValue(monAn.class);
                                    if (ma != null) {
                                        StorageReference fileCanXoa = storageReference.child("anhMonAn")
                                                .child(a.getId()).child(ma.getId() +".jpg");
                                        fileCanXoa.delete();
                                    }
                                }

                                StorageReference fileCanXoa = storageReference.child("anhDaiDien")
                                        .child(a.getId()).child(a.getId()+".jpg");
                                fileCanXoa.delete();

                                nhaHang.child(a.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(XemChiTietNhaHang.this, "Xóa nhà hàng thành công"
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                });
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });
                b.create().show();
            }
        });



        tvTenNhaHang.setText(a.getTenNhaHang());
        tvDiaChiNhaHang.setText("Địa chỉ : " + a.getDiaChiNhaHang());
        tvEmail.setText("Email : " + a.getEmail());
        tvSoDienThoai.setText("Số điện thoại : " + a.getSoDienThoai());
        tvGioMoCua.setText("Giờ mở cửa : " + a.getGioMoCua());
        tvMoTaNhaHang.setText("Mô tả nhà hàng : " + a.getMoTaNhaHang());
        Glide.with(XemChiTietNhaHang.this).load(a.getAnhNhaHang()).into(ivAnhNhaHang);

        ivAnhNhaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(XemChiTietNhaHang.this, PhongtoAnh.class);
                Bundle data = new Bundle();
                data.putString("anh", a.getAnhNhaHang());
                intent.putExtras(data);
                startActivity(intent);
            }
        });


        btnXemAnhNhaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putSerializable("nhahang",a);
                Intent xemAnhNhaHang = new Intent(XemChiTietNhaHang.this, ThemAnhNhaHang.class);
                xemAnhNhaHang.putExtras(data);
                startActivityForResult(xemAnhNhaHang,103);
            }
        });

        btnXemThucDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putSerializable("nhahang",a);
                Intent xemThucDon = new Intent(XemChiTietNhaHang.this, XemThucDonActivity.class);
                xemThucDon.putExtras(data);
                startActivityForResult(xemThucDon,102);
            }
        });

        btnSuaNhaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent suaNhaHang = new Intent(XemChiTietNhaHang.this, SuaNhaHang.class);
                Bundle data = new Bundle();
                data.putSerializable("nhahang",a);
                suaNhaHang.putExtras(data);
                startActivityForResult(suaNhaHang,104);
            }
        });

        GuiEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent guiemail = new Intent( XemChiTietNhaHang.this, com.example.quanlychuoicuahangcaphe.Plugins.GuiEmail.class);
                Bundle data= new Bundle();
                data.putSerializable("nhahang",a);
                guiemail.putExtras(data);
                startActivityForResult(guiemail, 105);
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri phoneuri = Uri.parse("tel: " +a.getSoDienThoai());
                Intent goiDienIntent = new Intent(Intent.ACTION_DIAL,phoneuri); // chỉ chuyển sang màn hình gọi điện
                startActivity(goiDienIntent);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri smsuri = Uri.parse("smsto: " +a.getSoDienThoai());
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO,smsuri);
                startActivity(smsIntent);
            }
        });
    }



    // Doan set result code duh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == 102){
            Bundle dulieu = data.getExtras();
            NhaHang moi = (NhaHang) dulieu.getSerializable("nhahang");
            a.setListMonAn(moi.getListMonAn());
        }

        if (requestCode == 103 && resultCode == 103){
            Bundle dulieu = data.getExtras();
            NhaHang moi = (NhaHang) dulieu.getSerializable("nhahang");
            a.setListHinhAnh(moi.getListHinhAnh());
        }

        if (resultCode == 104 && requestCode == 104){
            Bundle dulieu = data.getExtras();
            NhaHang moi = (NhaHang) dulieu.getSerializable("nhahang");
            tvTenNhaHang.setText(moi.getTenNhaHang());
            tvDiaChiNhaHang.setText("Địa chỉ : " + moi.getDiaChiNhaHang());
            tvEmail.setText("Email : " + moi.getEmail());
            tvSoDienThoai.setText("Số điện thoại : " + moi.getSoDienThoai());
            tvGioMoCua.setText("Giờ mở cửa : " + moi.getGioMoCua());
            tvMoTaNhaHang.setText("Mô tả nhà hàng : " + moi.getMoTaNhaHang());
            Glide.with(XemChiTietNhaHang.this).load(moi.getAnhNhaHang()).into(ivAnhNhaHang);
        }
    }
}