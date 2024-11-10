package com.example.quanlychuoicuahangcaphe;

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
import com.example.quanlychuoicuahangcaphe.Model.QuanCafe;
import com.example.quanlychuoicuahangcaphe.Plugins.CheckInternet;
import com.example.quanlychuoicuahangcaphe.Plugins.chupanh;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class UpdateCafe extends AppCompatActivity {
    ImageView ivAnhQuanCafe;
    Button btnChonAnh,btnHuyBo, btnXacNhan,btnChupanh;
    EditText edtTenQuanCafe, edtSoDienThoai, edtEmail, edtMoTaQuanCafe, edtDiaChiQuanCafe;
    TimePicker tpGioMoCua, tpGioDongCua;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference quanCafe = databaseReference.child("quanCafe");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_cafe);

        isConnected();
        // Ánh xạ
        ivAnhQuanCafe = findViewById(R.id.ivAnhQuanCafe);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        btnHuyBo = findViewById(R.id.btnHuyBo);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        edtTenQuanCafe = findViewById(R.id.edtTenQuanCafe);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);
        edtEmail = findViewById(R.id.edtEmail);
        edtMoTaQuanCafe = findViewById(R.id.edtMoTaQuanCafe);
        edtDiaChiQuanCafe = findViewById(R.id.edtDiaChiQuanCafe);
        tpGioDongCua = findViewById(R.id.tpGioDongCua);
        tpGioMoCua = findViewById(R.id.tpGioMoCua);
        btnChupanh = findViewById(R.id.btnChupanh);

        // Lay itent
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        QuanCafe a = (QuanCafe) data.getSerializable("cafe");

        // Truyen du lieu
        Glide.with(UpdateCafe.this).load(a.getAnhQuanCafe()).into(ivAnhQuanCafe);
        edtTenQuanCafe.setText(a.getName());
        edtDiaChiQuanCafe.setText(a.getAddress());
        edtEmail.setText(a.getEmail());
        edtSoDienThoai.setText(a.getPhoneNumber());
        edtMoTaQuanCafe.setText(a.getDescription());
        int gioMoCua,phutMoCua,gioDongCua,phutDongCua;
        gioMoCua = Integer.parseInt(a.getOpenTime().substring(0,2));
        phutMoCua = Integer.parseInt(a.getOpenTime().substring(3,5));
        gioDongCua = Integer.parseInt(a.getOpenTime().substring(8,10));
        phutDongCua = Integer.parseInt(a.getOpenTime().substring(11,13));
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
                            Glide.with(UpdateCafe.this).load(photo).into(ivAnhQuanCafe);

//                            Bitmap bitmap = BitmapFactory.decodeFile(photo);
//                            ivChonAnh.setImageBitmap(bitmap);
                        }
                    }
                }
        );

        btnChupanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(UpdateCafe.this, chupanh.class);
                ChupanhLaunch.launch(intent);
            }
        });

        ActivityResultLauncher chonAnhLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        ivAnhQuanCafe.setImageURI(o);
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
                String tenQuanCafe = edtTenQuanCafe.getText().toString();
                String diaChiQuanCafe = edtDiaChiQuanCafe.getText().toString();
                String email = edtEmail.getText().toString();
                String soDienThoai = edtSoDienThoai.getText().toString();
                String moTaQuanCafe = edtMoTaQuanCafe.getText().toString();
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

                if (tenQuanCafe.length() > 0 && diaChiQuanCafe.length() > 0 && email.length() > 0 && soDienThoai.length() > 0 && moTaQuanCafe.length() > 0){
                    QuanCafe moi = a;
                    moi.setDescription(moTaQuanCafe);
                    moi.setAddress(diaChiQuanCafe);
                    moi.setEmail(email);
                    moi.setOpenTime(gioHoatDong);
                    moi.setName(tenQuanCafe);
                    moi.setPhoneNumber(soDienThoai);


                    // Đoạn sửa giá trị tên + email + giờ mở cửa + mô tả + email + số điện thoại
                    quanCafe.child(a.getId()).child("address").setValue(diaChiQuanCafe);
                    quanCafe.child(a.getId()).child("email").setValue(email);
                    quanCafe.child(a.getId()).child("openTime").setValue(gioHoatDong);
                    quanCafe.child(a.getId()).child("description").setValue(moTaQuanCafe);
                    quanCafe.child(a.getId()).child("name").setValue(tenQuanCafe);
                    quanCafe.child(a.getId()).child("phoneNumber").setValue(soDienThoai);

                    // Đoạn thêm ảnh vào storage + get link download nếu đã đổi ảnh / sửa ảnh
                    StorageReference anhDaiDien  =
                            storageReference.child("anhDaiDien").child(a.getId()).child( a.getId().toString() + ".jpg");

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) ivAnhQuanCafe.getDrawable();
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
                                    moi.setAnhQuanCafe(linkHinhAnh);
                                    // Set value cho mon an
                                    quanCafe.child(a.getId().toString())
                                            .child("anhQuanCafe").setValue(linkHinhAnh).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(UpdateCafe.this,
                                                                "Sửa nhà hàng thành công", Toast.LENGTH_SHORT).show();
                                                        Intent intent1 = new Intent();
                                                        Bundle data = new Bundle();
                                                        data.putSerializable("quanCafe",moi);
                                                        intent1.putExtras(data);
                                                        setResult(104,intent1);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(UpdateCafe.this,
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
                    Toast.makeText(UpdateCafe.this, "Vui lòng nhập vào đầy đủ thông tin !", Toast.LENGTH_SHORT).show();
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
                                Intent intent = new Intent(UpdateCafe.this, CheckInternet.class);
                                startActivity(intent);
                            }
                        }

                );
    }
}