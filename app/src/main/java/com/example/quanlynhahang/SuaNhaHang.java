package com.example.quanlynhahang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.example.quanlynhahang.Model.NhaHang;
import com.example.quanlynhahang.Plugins.CheckInternet;
import com.example.quanlynhahang.Plugins.chupanh;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SuaNhaHang extends AppCompatActivity {
    ImageView ivAnhNhaHang;
    Button btnChonAnh,btnHuyBo, btnXacNhan,btnChupanh;
    EditText edtTenNhaHang, edtSoDienThoai, edtEmail, edtMoTaNhaHang, edtDiaChiNhaHang;
    TimePicker tpGioMoCua, tpGioDongCua;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference nhaHang = databaseReference.child("nhaHang");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_nha_hang);

        isConnected();
        // Ánh xạ
        ivAnhNhaHang = findViewById(R.id.ivAnhNhaHang);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        btnHuyBo = findViewById(R.id.btnHuyBo);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        edtTenNhaHang = findViewById(R.id.edtTenNhaHang);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);
        edtEmail = findViewById(R.id.edtEmail);
        edtMoTaNhaHang = findViewById(R.id.edtMoTaNhaHang);
        edtDiaChiNhaHang = findViewById(R.id.edtDiaChiNhaHang);
        tpGioDongCua = findViewById(R.id.tpGioDongCua);
        tpGioMoCua = findViewById(R.id.tpGioMoCua);
        btnChupanh = findViewById(R.id.btnChupanh);

        // Lay itent
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        NhaHang a = (NhaHang) data.getSerializable("nhahang");

        // Truyen du lieu
        Glide.with(SuaNhaHang.this).load(a.getAnhNhaHang()).into(ivAnhNhaHang);
        edtTenNhaHang.setText(a.getTenNhaHang());
        edtDiaChiNhaHang.setText(a.getDiaChiNhaHang());
        edtEmail.setText(a.getEmail());
        edtSoDienThoai.setText(a.getSoDienThoai());
        edtMoTaNhaHang.setText(a.getMoTaNhaHang());
        int gioMoCua,phutMoCua,gioDongCua,phutDongCua;
        gioMoCua = Integer.parseInt(a.getGioMoCua().substring(0,2));
        phutMoCua = Integer.parseInt(a.getGioMoCua().substring(3,5));
        gioDongCua = Integer.parseInt(a.getGioMoCua().substring(8,10));
        phutDongCua = Integer.parseInt(a.getGioMoCua().substring(11,13));
        tpGioMoCua.setHour(gioMoCua);
        tpGioMoCua.setMinute(phutMoCua);
        tpGioDongCua.setHour(gioDongCua);
        tpGioDongCua.setMinute(phutDongCua);


        // Phần code
        // btn hủy bỏ
        btnHuyBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                            Glide.with(SuaNhaHang.this).load(photo).into(ivAnhNhaHang);

//                            Bitmap bitmap = BitmapFactory.decodeFile(photo);
//                            ivChonAnh.setImageBitmap(bitmap);
                        }
                    }
                }
        );

        btnChupanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SuaNhaHang.this, chupanh.class);
                ChupanhLaunch.launch(intent);
            }
        });

        ActivityResultLauncher chonAnhLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        ivAnhNhaHang.setImageURI(o);
                    }
                }
        );

        // btn chọn ảnh
        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chonAnhLauncher.launch("image/*");
            }
        });

        // btn xác nhận
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenNhaHang = edtTenNhaHang.getText().toString();
                String diaChiNhaHang = edtDiaChiNhaHang.getText().toString();
                String email = edtEmail.getText().toString();
                String soDienThoai = edtSoDienThoai.getText().toString();
                String moTaNhaHang = edtMoTaNhaHang.getText().toString();
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

                if (tenNhaHang.length() > 0 && diaChiNhaHang.length() > 0 && email.length() > 0 && soDienThoai.length() > 0 && moTaNhaHang.length() > 0){
                    NhaHang moi = a;
                    moi.setMoTaNhaHang(moTaNhaHang);
                    moi.setDiaChiNhaHang(diaChiNhaHang);
                    moi.setEmail(email);
                    moi.setGioMoCua(gioHoatDong);
                    moi.setTenNhaHang(tenNhaHang);
                    moi.setSoDienThoai(soDienThoai);


                    // Đoạn sửa giá trị tên + email + giờ mở cửa + mô tả + email + số điện thoại
                    nhaHang.child(a.getId()).child("diaChiNhaHang").setValue(diaChiNhaHang);
                    nhaHang.child(a.getId()).child("email").setValue(email);
                    nhaHang.child(a.getId()).child("gioMoCua").setValue(gioHoatDong);
                    nhaHang.child(a.getId()).child("moTaNhaHang").setValue(moTaNhaHang);
                    nhaHang.child(a.getId()).child("tenNhaHang").setValue(tenNhaHang);
                    nhaHang.child(a.getId()).child("soDienThoai").setValue(soDienThoai);

                    // Đoạn thêm ảnh vào storage + get link download nếu đã đổi ảnh / sửa ảnh
                    StorageReference anhDaiDien  =
                            storageReference.child("anhDaiDien").child(a.getId()).child( a.getId().toString() + ".jpg");

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) ivAnhNhaHang.getDrawable();
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
                                    String linkHinhAnh = uri.toString();
                                    moi.setAnhNhaHang(linkHinhAnh);
                                    // Set value cho mon an
                                    nhaHang.child(a.getId().toString())
                                            .child("anhNhaHang").setValue(linkHinhAnh).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(SuaNhaHang.this,
                                                                "Sửa nhà hàng thành công", Toast.LENGTH_SHORT).show();
                                                        Intent intent1 = new Intent();
                                                        Bundle data = new Bundle();
                                                        data.putSerializable("nhahang",moi);
                                                        intent1.putExtras(data);
                                                        setResult(104,intent1);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SuaNhaHang.this,
                                                                "Sửa nhà hàng thất bại, lỗi : " + task.getException().toString(),
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
                    Toast.makeText(SuaNhaHang.this, "Vui lòng nhập vào đầy đủ thông tin !", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                                Intent intent = new Intent(SuaNhaHang.this, CheckInternet.class);
                                startActivity(intent);
                            }
                        }

                );
    }
}