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

import com.example.quanlychuoicuahangcaphe.Model.QuanCafe;
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

public class DetailCafe extends AppCompatActivity {
    TextView tvTenQuanCafe, tvDiaChiQuanCafe, tvEmail, tvSoDienThoai, tvGioMoCua, tvMoTaQuanCafe,textrating;
    Button btnTroLai,btnXemAnhQuanCafe,btnXemThucDon,btnSuaQuanCafe,btnXoaAnhQuanCafe;
    ImageButton GuiEmail,phone,sms,btnShowMap;
    ImageView ivAnhQuanCafe;
    RatingBar rating;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference quanCafe = databaseReference.child("quanCafe");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    QuanCafe a = new QuanCafe();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cafe);
        // Ánh xạ
        tvTenQuanCafe = findViewById(R.id.tvTenQuanCafe);
        tvDiaChiQuanCafe = findViewById(R.id.tvDiaChiQuanCafe);
        tvEmail = findViewById(R.id.tvEmail);
        tvSoDienThoai = findViewById(R.id.tvSoDienThoai);
        tvGioMoCua = findViewById(R.id.tvGioMoCua);
        tvMoTaQuanCafe = findViewById(R.id.tvMoTaQuanCafe);
        btnTroLai = findViewById(R.id.btnTroLai);
        btnXemAnhQuanCafe = findViewById(R.id.btnXemAnhQuanCafe);
        btnXemThucDon = findViewById(R.id.btnXemThucDon);
        btnSuaQuanCafe = findViewById(R.id.btnSuaQuanCafe);
        ivAnhQuanCafe = findViewById(R.id.ivAnhQuanCafe);
        btnShowMap = findViewById(R.id.btnShowMap);
        btnXoaAnhQuanCafe = findViewById(R.id.btnXoaAnhQuanCafe);

        GuiEmail = findViewById(R.id.mail);
        phone = findViewById(R.id.phone);
        sms= findViewById(R.id.sms);
        rating = findViewById(R.id.rating);
        textrating = findViewById(R.id.textrating);



        //hiện dánh giá
        Intent intent = getIntent();
        Bundle data =  intent.getExtras();
        a = (QuanCafe) data.getSerializable("cafe");

        Float rate = (float) Math.ceil(a.getTb() * 10) / 10;
        rating.setRating(rate);
        textrating.setText(rate+"/5");

        // Phan code

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCafe.this, MapsActivity.class);
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

        btnXoaAnhQuanCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(DetailCafe.this);
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
                        quanCafe.child(a.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                DataSnapshot data = snapshot.child("listHinhAnh");
                                HashMap<String, String> id_anh = new HashMap<>();
                                id_anh = (HashMap<String, String>) data.getValue();
                                if (id_anh != null) {
                                    for (String key : id_anh.keySet()){
                                        StorageReference fileCanXoa = storageReference.child("anhNhaHang")
                                                .child(a.getId()).child(key+".jpg");
                                        fileCanXoa.delete();
                                    }
                                }

                                DataSnapshot data1 = snapshot.child("listMonAn");
                                for (DataSnapshot data2 : data1.getChildren()){
                                    monAn ma = data2.getValue(monAn.class);
                                    if (ma != null) {
                                        StorageReference fileCanXoa = storageReference.child("anhMonAn")
                                                .child(a.getId()).child(ma.getId() +".jpg");
                                        fileCanXoa.delete();
                                    }
                                }

                                StorageReference fileCanXoa = storageReference.child("anhQuanCafe")
                                        .child(a.getId()).child(a.getId()+".jpg");
                                fileCanXoa.delete();

                                quanCafe.child(a.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(DetailCafe.this, "Xóa quán cafe thành công"
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



        tvTenQuanCafe.setText(a.getName());
        tvDiaChiQuanCafe.setText("Địa chỉ : " + a.getAddress());
        tvEmail.setText("Email : " + a.getEmail());
        tvSoDienThoai.setText("Số điện thoại : " + a.getPhoneNumber());
        tvGioMoCua.setText("Giờ mở cửa : " + a.getOpenTime());
        tvMoTaQuanCafe.setText("Mô tả nhà hàng : " + a.getDescription());
        Glide.with(DetailCafe.this).load(a.getAnhQuanCafe()).into(ivAnhQuanCafe);

        ivAnhQuanCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCafe.this, PhongtoAnh.class);
                Bundle data = new Bundle();
                data.putString("anh", a.getAnhQuanCafe());
                intent.putExtras(data);
                startActivity(intent);
            }
        });


        btnXemAnhQuanCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putSerializable("cafe",a);
                Intent xemAnhQuanCafe = new Intent(DetailCafe.this, AddImageCafe.class);
                xemAnhQuanCafe.putExtras(data);
                startActivityForResult(xemAnhQuanCafe,103);
            }
        });

        btnXemThucDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putSerializable("cafe",a);
                Intent xemThucDon = new Intent(DetailCafe.this, XemThucDonActivity.class);
                xemThucDon.putExtras(data);
                startActivityForResult(xemThucDon,102);
            }
        });

        btnSuaQuanCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent suaQuanCafe = new Intent(DetailCafe.this, UpdateCafe.class);
                Bundle data = new Bundle();
                data.putSerializable("cafe",a);
                suaQuanCafe.putExtras(data);
                startActivityForResult(suaQuanCafe,104);
            }
        });

        GuiEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent guiemail = new Intent( DetailCafe.this, com.example.quanlychuoicuahangcaphe.Plugins.GuiEmail.class);
                Bundle data= new Bundle();
                data.putSerializable("cafe",a);
                guiemail.putExtras(data);
                startActivityForResult(guiemail, 105);
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri phoneuri = Uri.parse("tel: " +a.getPhoneNumber());
                Intent goiDienIntent = new Intent(Intent.ACTION_DIAL,phoneuri); // chỉ chuyển sang màn hình gọi điện
                startActivity(goiDienIntent);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri smsuri = Uri.parse("smsto: " +a.getPhoneNumber());
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
            QuanCafe moi = (QuanCafe) dulieu.getSerializable("cafe");
            a.setListMonAn(moi.getListMonAn());
        }

        if (requestCode == 103 && resultCode == 103){
            Bundle dulieu = data.getExtras();
            QuanCafe moi = (QuanCafe) dulieu.getSerializable("cafe");
            a.setListHinhAnh(moi.getListHinhAnh());
        }

        if (resultCode == 104 && requestCode == 104){
            Bundle dulieu = data.getExtras();
            QuanCafe moi = (QuanCafe) dulieu.getSerializable("cafe");
            tvTenQuanCafe.setText(moi.getName());
            tvDiaChiQuanCafe.setText("Địa chỉ : " + moi.getAddress());
            tvEmail.setText("Email : " + moi.getEmail());
            tvSoDienThoai.setText("Số điện thoại : " + moi.getPhoneNumber());
            tvGioMoCua.setText("Giờ mở cửa : " + moi.getOpenTime());
            tvMoTaQuanCafe.setText("Mô tả nhà hàng : " + moi.getDescription());
            Glide.with(DetailCafe.this).load(moi.getAnhQuanCafe()).into(ivAnhQuanCafe);
        }
    }
}