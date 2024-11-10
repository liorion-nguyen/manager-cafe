package com.example.quanlychuoicuahangcaphe;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quanlychuoicuahangcaphe.Model.NhaHang;
import com.example.quanlychuoicuahangcaphe.Model.monAn;
import com.example.quanlychuoicuahangcaphe.Plugins.CheckInternet;

import com.example.quanlychuoicuahangcaphe.ThemAnhNhaHang;
import com.example.quanlychuoicuahangcaphe.Plugins.chupanh;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ThemMoiNhaHangActivity extends AppCompatActivity {
    ImageView imgAnhNhaHang;
    Button btnChonAnhNhaHang,btnHuyBo,btnXacNhan,btnThucDon,btnCacHinhAnhNhaHang,btnChupanh;
    EditText edtTenNhaHang, edtDiaChiNhaHang, edtEmail , edtSoDienThoai, edtMoTaNhaHang;
    TimePicker tpGioMoCua,tpGioDongCua;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference nhaHang = databaseReference.child("nhaHang");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    NhaHang moi = new NhaHang();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = nhaHang.push().getKey().toString();
        setContentView(R.layout.activity_them_moi_nha_hang);
        moi.setId(id);
        System.out.println(id);

        isConnected();
        // Ánh xạ
        imgAnhNhaHang = findViewById(R.id.imgAnhNhaHang);
        btnChonAnhNhaHang = findViewById(R.id.btnChonAnhNhaHang);
        btnHuyBo = findViewById(R.id.btnHuyBo);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        btnThucDon = findViewById(R.id.btnThucDon);
        btnCacHinhAnhNhaHang = findViewById(R.id.btnCacHinhAnhNhaHang);
        edtTenNhaHang = findViewById(R.id.edtTenNhaHang);
        edtDiaChiNhaHang = findViewById(R.id.edtDiaChiNhaHang);
        edtEmail = findViewById(R.id.edtEmail);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);
        edtMoTaNhaHang = findViewById(R.id.edtMoTaNhaHang);
        tpGioMoCua = findViewById(R.id.tpGioMoCua);
        tpGioDongCua = findViewById(R.id.tpGioDongCua);
        btnChupanh = findViewById(R.id.btnChupanh);

        ActivityResultLauncher ChupanhLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode()==110)
                        {
                            Intent intent = o.getData();
                            Bundle data = intent.getExtras();
                            String photo = data.getString("anh");
                            Glide.with(ThemMoiNhaHangActivity.this).load(photo).into(imgAnhNhaHang);

//                            Bitmap bitmap = BitmapFactory.decodeFile(photo);
//                            ivChonAnh.setImageBitmap(bitmap);
                        }
                    }
                }
        );

        btnChupanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ThemMoiNhaHangActivity.this, chupanh.class);
                ChupanhLaunch.launch(intent);
            }
        });

        // Chọn ảnh
        ActivityResultLauncher chonAnhLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        imgAnhNhaHang.setImageURI(o);
                    }
                }
        );

        btnChonAnhNhaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chonAnhLauncher.launch("image/*");
            }
        });

        btnHuyBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xóa trong storage + realtime.
                nhaHang.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot data = snapshot.child("cacAnhNhaHang");
                        HashMap<String, String> id_anh = new HashMap<>();
                        id_anh = (HashMap<String, String>) data.getValue();
                        if (id_anh != null) {
                            for (String key : id_anh.keySet()){
                                StorageReference fileCanXoa = storageReference.child("anhNhaHang")
                                        .child(id).child(key+".jpg");
                                fileCanXoa.delete();
                            }
                        }

                        DataSnapshot data1 = snapshot.child("thucDon");
                        for (DataSnapshot data2 : data1.getChildren()){
                            monAn ma = data2.getValue(monAn.class);
                            if (ma != null) {
                                StorageReference fileCanXoa = storageReference.child("anhMonAn")
                                        .child(id).child(ma.getId() +".jpg");
                                fileCanXoa.delete();
                            }
                        }

                        StorageReference fileCanXoa = storageReference.child("anhDaiDien")
                                .child(id+".jpg");
                        fileCanXoa.delete();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                nhaHang.child(id).removeValue();
                finish();
            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gioHoatDong = "";
                int gioMoCua = tpGioMoCua.getHour();
                int phutMoCua = tpGioMoCua.getMinute();
                int gioDongCua = tpGioDongCua.getHour();
                int phutDongCua = tpGioDongCua.getMinute();

                if (gioMoCua < 10){
                    gioHoatDong += "0"+gioMoCua+":";
                } else {
                    gioHoatDong += gioMoCua+":";
                }

                if (phutMoCua < 10){
                    gioHoatDong += "0"+phutMoCua + " - ";
                } else {
                    gioHoatDong += phutMoCua + " - ";
                }

                if (gioDongCua < 10){
                    gioHoatDong += "0" + gioDongCua + ":";
                } else {
                    gioHoatDong += gioDongCua + ":";
                }

                if (phutDongCua < 10){
                    gioHoatDong += "0" + phutDongCua;
                } else {
                    gioHoatDong += phutDongCua;
                }

                String tenNhaHang = edtTenNhaHang.getText().toString().trim();
                String diaChiNhaHang = edtDiaChiNhaHang.getText().toString().trim();
                String soDienThoai = edtSoDienThoai.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String moTaNhaHang = edtMoTaNhaHang.getText().toString().trim();
                if (tenNhaHang.length() > 0 && diaChiNhaHang.length() > 0 && soDienThoai.length() > 0 && email.length() > 0){
                    moi.setGioMoCua(gioHoatDong);
                    moi.setTenNhaHang(tenNhaHang);
                    moi.setDiaChiNhaHang(diaChiNhaHang);
                    moi.setSoDienThoai(soDienThoai);
                    moi.setEmail(email);
                    moi.setMoTaNhaHang(moTaNhaHang);
                    nhaHang.child(id).child("diaChiNhaHang").setValue(diaChiNhaHang);
                    nhaHang.child(id).child("email").setValue(email);
                    nhaHang.child(id).child("gioMoCua").setValue(gioHoatDong);
                    nhaHang.child(id).child("id").setValue(id);
                    nhaHang.child(id).child("moTaNhaHang").setValue(moTaNhaHang);
                    nhaHang.child(id).child("soDienThoai").setValue(soDienThoai);
                    nhaHang.child(id).child("tenNhaHang").setValue(tenNhaHang);

                    StorageReference anhDaiDien  =
                            storageReference.child("anhDaiDien").child(id).child(id + ".jpg");

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) imgAnhNhaHang.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baoStream);
                    byte[] imgData = baoStream.toByteArray();
                    anhDaiDien.putBytes(imgData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            anhDaiDien.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String linkAnhMonAn = uri.toString();
                                    nhaHang.child(id)
                                            .child("anhNhaHang").setValue(linkAnhMonAn).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(ThemMoiNhaHangActivity.this,
                                                                "Thêm cửa hàng thành công", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ThemMoiNhaHangActivity.this,
                                                                "Thêm cửa hàng thất bại, lỗi : " + task.getException().toString(),
                                                                Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    });

                } else {
                    Toast.makeText(ThemMoiNhaHangActivity.this, "Vui lòng nhập đầy đủ thông tin !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nút thêm các ảnh cho nhà hàng.
        btnCacHinhAnhNhaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent themMoiNhaHang = new Intent(ThemMoiNhaHangActivity.this, ThemAnhNhaHang.class);
                Bundle data = new Bundle();
                data.putSerializable("nhahang",moi);
                themMoiNhaHang.putExtras(data);
                // Doan nay phai them bundle de gui sang ben kia.
                startActivityForResult(themMoiNhaHang,130);
            }
        });

        // Nút xem thực đơn
        btnThucDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent themMoiNhaHang = new Intent(ThemMoiNhaHangActivity.this, XemThucDonActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("nhahang",moi);
                themMoiNhaHang.putExtras(data);
                // Doan nay phai them bundle de gui sang ben kia.
                startActivityForResult(themMoiNhaHang,129);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 130 && resultCode == 103){
            Bundle dulieu = data.getExtras();
            NhaHang a = (NhaHang) dulieu.getSerializable("nhahang");
            moi.setListHinhAnh(a.getListHinhAnh());
        }

        if (requestCode == 129 && resultCode == 102){
            Bundle dulieu = data.getExtras();
            NhaHang a = (NhaHang) dulieu.getSerializable("nhahang");
            moi.setListMonAn(a.getListMonAn());
        }
    }
    void isConnected() {
        ConnectivityManager cm
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        cm.registerNetworkCallback
                (
                        builder.build(),
                        new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onLost(Network network) {
                                Intent intent = new Intent(ThemMoiNhaHangActivity.this, CheckInternet.class);
                                startActivity(intent);
                            }
                        }

                );
    }
}