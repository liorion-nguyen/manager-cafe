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
    ImageView ivAvatar;
    Button btnChonAnh,btnHuyBo, btnXacNhan,btnChupanh;
    EditText edtName, edtPhoneNumber, edtEmail, edtDescription, edtAddress;
    TimePicker tpOpenTime, tpCloseTime;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference quanCafe = databaseReference.child("cafe");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_cafe);

        isConnected();
        // Ánh xạ
        ivAvatar = findViewById(R.id.ivAvatar);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        btnHuyBo = findViewById(R.id.btnHuyBo);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        edtName = findViewById(R.id.edtName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtEmail = findViewById(R.id.edtEmail);
        edtDescription = findViewById(R.id.edtDescription);
        edtAddress = findViewById(R.id.edtAddress);
        tpCloseTime = findViewById(R.id.tpCloseTime);
        tpOpenTime = findViewById(R.id.tpOpenTime);
        btnChupanh = findViewById(R.id.btnChupanh);

        // Lay itent
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        QuanCafe a = (QuanCafe) data.getSerializable("cafe");

        // Truyen du lieu
        Glide.with(UpdateCafe.this).load(a.getAvatar()).into(ivAvatar);
        edtName.setText(a.getName());
        edtAddress.setText(a.getAddress());
        edtEmail.setText(a.getEmail());
        edtPhoneNumber.setText(a.getPhoneNumber());
        edtDescription.setText(a.getDescription());
        int gioMoCua,phutMoCua,gioDongCua,phutDongCua;
        gioMoCua = Integer.parseInt(a.getOpenTime().substring(0,2));
        phutMoCua = Integer.parseInt(a.getOpenTime().substring(3,5));
        gioDongCua = Integer.parseInt(a.getOpenTime().substring(8,10));
        phutDongCua = Integer.parseInt(a.getOpenTime().substring(11,13));
        tpOpenTime.setHour(gioMoCua);
        tpOpenTime.setMinute(phutMoCua);
        tpCloseTime.setHour(gioDongCua);
        tpCloseTime.setMinute(phutDongCua);


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
                            Glide.with(UpdateCafe.this).load(photo).into(ivAvatar);

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
                        ivAvatar.setImageURI(o);
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
                String name = edtName.getText().toString();
                String address = edtAddress.getText().toString();
                String email = edtEmail.getText().toString();
                String phoneNumber = edtPhoneNumber.getText().toString();
                String description = edtDescription.getText().toString();
                String openTime = "";
                int gioMoCua = tpOpenTime.getHour();
                int phutMoCua = tpOpenTime.getMinute();
                int gioDongCua = tpCloseTime.getHour();
                int phutDongCua = tpCloseTime.getMinute();

                if (gioMoCua < 10){
                    openTime += "0"+gioMoCua+":";
                } else {
                    openTime += gioMoCua+":";
                }

                if (phutMoCua < 10){
                    openTime += "0"+phutMoCua + " - ";
                } else {
                    openTime += phutMoCua + " - ";
                }

                if (gioDongCua < 10){
                    openTime += "0" + gioDongCua + ":";
                } else {
                    openTime += gioDongCua + ":";
                }

                if (phutDongCua < 10){
                    openTime += "0" + phutDongCua;
                } else {
                    openTime += phutDongCua;
                }

                if (name.length() > 0 && address.length() > 0 && email.length() > 0 && phoneNumber.length() > 0 && description.length() > 0){
                    QuanCafe moi = a;
                    moi.setDescription(description);
                    moi.setAddress(address);
                    moi.setEmail(email);
                    moi.setOpenTime(openTime);
                    moi.setName(name);
                    moi.setPhoneNumber(phoneNumber);


                    // Đoạn sửa giá trị tên + email + giờ mở cửa + mô tả + email + số điện thoại
                    quanCafe.child(a.getId()).child("address").setValue(address);
                    quanCafe.child(a.getId()).child("email").setValue(email);
                    quanCafe.child(a.getId()).child("openTime").setValue(openTime);
                    quanCafe.child(a.getId()).child("description").setValue(description);
                    quanCafe.child(a.getId()).child("name").setValue(name);
                    quanCafe.child(a.getId()).child("phoneNumber").setValue(phoneNumber);

                    // Đoạn thêm ảnh vào storage + get link download nếu đã đổi ảnh / sửa ảnh
                    StorageReference avatar  =
                            storageReference.child("avatar").child(a.getId()).child( a.getId().toString() + ".jpg");

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) ivAvatar.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baoStream);
                    byte[] imgData = baoStream.toByteArray();
                    avatar.putBytes(imgData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            avatar.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String linkHinhAnh = uri.toString();
                                    moi.setAnhQuanCafe(linkHinhAnh);
                                    // Set value cho mon an
                                    quanCafe.child(a.getId().toString())
                                            .child("avatar").setValue(linkHinhAnh).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(UpdateCafe.this,
                                                                "Sửa quán cà phê thành công", Toast.LENGTH_SHORT).show();
                                                        Intent intent1 = new Intent();
                                                        Bundle data = new Bundle();
                                                        data.putSerializable("quanCafe",moi);
                                                        intent1.putExtras(data);
                                                        setResult(104,intent1);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(UpdateCafe.this,
                                                                "Sửa quán cà phê thất bại, lỗi : " + task.getException().toString(),
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