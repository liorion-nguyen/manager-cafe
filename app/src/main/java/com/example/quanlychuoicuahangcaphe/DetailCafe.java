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
import com.example.quanlychuoicuahangcaphe.Plugins.PhongToAnh;

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
    TextView tvName, tvAddress, tvEmail, tvPhone, tvOpenTime, tvDescription,textrating;
    Button btnShowImage,btnShowMenu,btnSuaQuanCafe,btnXoaAnhQuanCafe;
    ImageButton btnMail,btnPhone,btnSms,btnShowMap,btnTroLai;
    ImageView ivAvatar;
    RatingBar ratingBar;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference quanCafe = databaseReference.child("cafe");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    QuanCafe a = new QuanCafe();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cafe);
        // Ánh xạ
        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvOpenTime = findViewById(R.id.tvOpenTime);
        tvDescription = findViewById(R.id.tvDescription);
        btnTroLai = findViewById(R.id.imgBtnTroLai);
        btnShowImage = findViewById(R.id.btnShowImage);
        btnShowMenu = findViewById(R.id.btnShowMenu);
        btnSuaQuanCafe = findViewById(R.id.btnSuaQuanCafe);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnShowMap = findViewById(R.id.btnShowMap);
        btnXoaAnhQuanCafe = findViewById(R.id.btnXoaAnhQuanCafe);

        btnMail = findViewById(R.id.btnMail);
        btnPhone = findViewById(R.id.btnPhone);
        btnSms = findViewById(R.id.btnSms);
        ratingBar = findViewById(R.id.ratingBar);
        textrating = findViewById(R.id.textrating);



        //hiện dánh giá
        Intent intent = getIntent();
        Bundle data =  intent.getExtras();
        a = (QuanCafe) data.getSerializable("cafe");
        System.out.println(a.getListHinhAnh());

        Float rate = (float) Math.ceil(a.getTb() * 10) / 10;
        ratingBar.setRating(rate);
        textrating.setText(rate+"/5");

        // Phan code
        //showw map
        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCafe.this, MapsActivity.class);
                intent.putExtra("address", a.getAddress());// Lấy địa chỉ từ TextView
                intent.putExtra("ten_quan", a.getName()); // gan ten de hien thi ten quan
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
                b.setTitle("Xóa quán cafe");
                b.setMessage("Bạn có chắc là muốn xóa quán cafe không ?");
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
                                        StorageReference fileCanXoa = storageReference.child("anhQuanCafe")
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

                                StorageReference fileCanXoa = storageReference.child("avatar")
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

        tvName.setText(a.getName());
        tvAddress.setText("Địa chỉ : " + a.getAddress());
        tvEmail.setText("Email : " + a.getEmail());
        tvPhone.setText("Số điện thoại : " + a.getPhoneNumber());
        tvOpenTime.setText("Giờ mở cửa : " + a.getOpenTime());
        tvDescription.setText("Mô tả : " + a.getDescription());
        Glide.with(DetailCafe.this).load(a.getAvatar()).into(ivAvatar);

        // ham phong to anh
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCafe.this, PhongToAnh.class);
                Bundle data = new Bundle();
                // gan ghi trị anh co ten la anh
                data.putString("anh", a.getAvatar());
                intent.putExtras(data);
                startActivity(intent);
            }
        });


        btnShowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putSerializable("cafe",a);
                Intent xemAnhQuanCafe = new Intent(DetailCafe.this, AddImageCafe.class);
                xemAnhQuanCafe.putExtras(data);
                startActivityForResult(xemAnhQuanCafe,103);
            }
        });

        btnShowMenu.setOnClickListener(new View.OnClickListener() {
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

        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendEmail = new Intent( DetailCafe.this, com.example.quanlychuoicuahangcaphe.Plugins.SendEmail.class);
                Bundle data= new Bundle();
                data.putSerializable("cafe",a);
                sendEmail.putExtras(data);
                startActivityForResult(sendEmail, 105);
            }
        });

        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri phoneuri = Uri.parse("tel: " +a.getPhoneNumber());
                Intent goiDienIntent = new Intent(Intent.ACTION_DIAL,phoneuri); // chỉ chuyển sang màn hình gọi điện
                startActivity(goiDienIntent);
            }
        });

        btnSms.setOnClickListener(new View.OnClickListener() {
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
            tvName.setText(moi.getName());
            tvAddress.setText("Địa chỉ : " + moi.getAddress());
            tvEmail.setText("Email : " + moi.getEmail());
            tvPhone.setText("Số điện thoại : " + moi.getPhoneNumber());
            tvOpenTime.setText("Giờ mở cửa : " + moi.getOpenTime());
            tvDescription.setText("Mô tả : " + moi.getDescription());
            Glide.with(DetailCafe.this).load(moi.getAvatar()).into(ivAvatar);
        }
    }
}