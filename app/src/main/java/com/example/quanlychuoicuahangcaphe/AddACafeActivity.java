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
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quanlychuoicuahangcaphe.Model.QuanCafe;
import com.example.quanlychuoicuahangcaphe.Model.monAn;
import com.example.quanlychuoicuahangcaphe.Plugins.CheckInternet;
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

import java.util.HashMap;
import java.util.Map;

public class AddACafeActivity extends AppCompatActivity {
    ImageView imgAnhCafe;
    Button btnChonAnhCafe, btnHuyBo, btnXacNhan, btnThucDon, btnCacHinhAnhCafe, btnChupanh;
    EditText edtTenCafe, edtDiaChiCafe, edtEmailCafe, edtSoDienThoaiCafe, edtMoTaCafe;
    TimePicker tpGioMoCua, tpGioDongCua;
    DatabaseReference cafeRef = FirebaseDatabase.getInstance().getReference("cafe");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    QuanCafe newCafe = new QuanCafe();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = cafeRef.push().getKey();
        setContentView(R.layout.activity_add_cafe);
        newCafe.setId(id);
        System.out.println(id);

        isConnected();
        imgAnhCafe = findViewById(R.id.imgAnhCafe);
        btnChonAnhCafe = findViewById(R.id.btnChonAnhCafe);
        btnHuyBo = findViewById(R.id.btnHuyBo);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        btnThucDon = findViewById(R.id.btnThucDon);
        btnCacHinhAnhCafe = findViewById(R.id.btnCacHinhAnhCafe);
        edtTenCafe = findViewById(R.id.edtTenQuanCaFe);
        edtDiaChiCafe = findViewById(R.id.edtDiaChiQuanCaFe);
        edtEmailCafe = findViewById(R.id.edtEmailQuanCaFe);
        edtSoDienThoaiCafe = findViewById(R.id.edtSoDienThoaiQuanCaFe);
        edtMoTaCafe = findViewById(R.id.edtMoTaQuanCaFe);
        tpGioMoCua = findViewById(R.id.tpGioMoCua);
        tpGioDongCua = findViewById(R.id.tpGioDongCua);
        btnChupanh = findViewById(R.id.btnChupanh);

        ActivityResultLauncher ChupanhLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == 110) {
                            Intent intent = o.getData();
                            Bundle data = intent.getExtras();
                            String photo = data.getString("anh");
                            Glide.with(AddACafeActivity.this).load(photo).into(imgAnhCafe);
                        }
                    }
                });

        btnChupanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddACafeActivity.this, chupanh.class);
                ChupanhLaunch.launch(intent);
            }
        });

        // Chọn ảnh
        ActivityResultLauncher chonAnhLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        imgAnhCafe.setImageURI(o);
                    }
                });

        btnChonAnhCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chonAnhLauncher.launch("image/*");
            }
        });

        btnHuyBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cafeRef.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot data = snapshot.child("listHinhAnh");
                        HashMap<String, String> id_anh = (HashMap<String, String>) data.getValue();
                        if (id_anh != null) {
                            for (String key : id_anh.keySet()) {
                                StorageReference fileCanXoa = storageReference.child("anhDaiDien")
                                        .child(id).child(key + ".jpg");
                                fileCanXoa.delete();
                            }
                        }

                        DataSnapshot data1 = snapshot.child("thucDon");
                        for (DataSnapshot data2 : data1.getChildren()) {
                            monAn ma = data2.getValue(monAn.class);
                            if (ma != null) {
                                StorageReference fileCanXoa = storageReference.child("anhMonAn")
                                        .child(id).child(ma.getId() + ".jpg");
                                fileCanXoa.delete();
                            }
                        }

                        StorageReference fileCanXoa = storageReference.child("anhDaiDien")
                                .child(id + ".jpg");
                        fileCanXoa.delete();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                cafeRef.child(id).removeValue();
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

                if (gioMoCua < 10) {
                    gioHoatDong += "0" + gioMoCua + ":";
                } else {
                    gioHoatDong += gioMoCua + ":";
                }

                if (phutMoCua < 10) {
                    gioHoatDong += "0" + phutMoCua + " - ";
                } else {
                    gioHoatDong += phutMoCua + " - ";
                }

                if (gioDongCua < 10) {
                    gioHoatDong += "0" + gioDongCua + ":";
                } else {
                    gioHoatDong += gioDongCua + ":";
                }

                if (phutDongCua < 10) {
                    gioHoatDong += "0" + phutDongCua;
                } else {
                    gioHoatDong += phutDongCua;
                }

                String tenCafe = edtTenCafe.getText().toString().trim();
                String diaChiCafe = edtDiaChiCafe.getText().toString().trim();
                String soDienThoaiCafe = edtSoDienThoaiCafe.getText().toString().trim();
                String emailCafe = edtEmailCafe.getText().toString().trim();
                String moTaCafe = edtMoTaCafe.getText().toString().trim();

                if (tenCafe.length() > 0 && diaChiCafe.length() > 0 && soDienThoaiCafe.length() > 0
                        && emailCafe.length() > 0) {

                    Map<String, String> newCafe = new HashMap<>();
                    newCafe.put("openTime", gioHoatDong);
                    newCafe.put("name", tenCafe);
                    newCafe.put("address", diaChiCafe);
                    newCafe.put("phoneNumber", soDienThoaiCafe);
                    newCafe.put("email", emailCafe);
                    newCafe.put("description", moTaCafe);
                    newCafe.put("id", id);
                    System.out.println(newCafe);

                    cafeRef.child(id).setValue(newCafe).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddACafeActivity.this, "Thêm quán café thành công!", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Log.e("FirebaseError", "Lỗi ghi địa chỉ", task.getException());
                            }
                        }
                    });
                    Intent intent = new Intent(AddACafeActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddACafeActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nút thêm các ảnh cho quán café.
        btnCacHinhAnhCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent themMoiCafe = new Intent(AddACafeActivity.this, AddImageCafe.class);
                Bundle data = new Bundle();
                data.putSerializable("cafe", newCafe);
                themMoiCafe.putExtras(data);
                startActivityForResult(themMoiCafe, 130);
            }
        });

        // Nút xem thực đơn
        btnThucDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent xemThucDon = new Intent(AddACafeActivity.this, XemThucDonActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("cafe", newCafe);
                xemThucDon.putExtras(data);
                startActivityForResult(xemThucDon, 129);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 130 && resultCode == 103) {
            Bundle dulieu = data.getExtras();
            QuanCafe a = (QuanCafe) dulieu.getSerializable("cafe");
            newCafe.setListHinhAnh(a.getListHinhAnh());
        }

        if (requestCode == 129 && resultCode == 102) {
            Bundle dulieu = data.getExtras();
            QuanCafe a = (QuanCafe) dulieu.getSerializable("cafe");
            newCafe.setListMonAn(a.getListMonAn());
        }
    }

    void isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        cm.registerNetworkCallback(
                builder.build(),
                new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onLost(Network network) {
                        Intent intent = new Intent(AddACafeActivity.this, CheckInternet.class);
                        startActivity(intent);
                    }
                });
    }
}