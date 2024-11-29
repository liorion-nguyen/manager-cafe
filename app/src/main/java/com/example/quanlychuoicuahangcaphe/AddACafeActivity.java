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
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddACafeActivity extends AppCompatActivity {
    ImageView imgAnhCafe;
    Button btnChonAnhCafe, btnCancel, btnConfirm, btnMenu, btnCacHinhAnhCafe, btnChupanh;
    EditText edtName, edtAddress, edtEmail, edtPhone, edtDescription;
    TimePicker tpOpenTime, tpCloseTime;
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
        System.out.println(newCafe.getListHinhAnh());

        isConnected();
        imgAnhCafe = findViewById(R.id.imgAnhCafe);
        btnChonAnhCafe = findViewById(R.id.btnChonAnhCafe);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnMenu = findViewById(R.id.btnMenu);
        btnCacHinhAnhCafe = findViewById(R.id.btnCacHinhAnhCafe);
        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtDescription = findViewById(R.id.edtDescription);
        tpOpenTime = findViewById(R.id.tpOpenTime);
        tpCloseTime = findViewById(R.id.tpCloseTime);
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cafeRef.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot data = snapshot.child("listHinhAnh");
                        HashMap<String, String> id_anh = (HashMap<String, String>) data.getValue();
                        if (id_anh != null) {
                            for (String key : id_anh.keySet()) {
                                StorageReference fileCanXoa = storageReference.child("avatar")
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

                        StorageReference fileCanXoa = storageReference.child("avatar")
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

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> listHinhAnh = newCafe.getListHinhAnh(); // Lấy danh sách hình ảnh
                String openTime = "";
                int gioOpen = tpOpenTime.getHour();
                int phutOpen = tpOpenTime.getMinute();
                int gioClose = tpCloseTime.getHour();
                int phutClose = tpCloseTime.getMinute();

                // Format opening hours
                if (gioOpen < 10) {
                    openTime += "0" + gioOpen + ":";
                } else {
                    openTime += gioOpen + ":";
                }

                if (phutOpen < 10) {
                    openTime += "0" + phutOpen + " - ";
                } else {
                    openTime += phutOpen + " - ";
                }

                if (gioClose < 10) {
                    openTime += "0" + gioClose + ":";
                } else {
                    openTime += gioClose + ":";
                }

                if (phutClose < 10) {
                    openTime += "0" + phutClose;
                } else {
                    openTime += phutClose;
                }

                // Get cafe details
                String name = edtName.getText().toString().trim();
                String address = edtAddress.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();

                // Check if all fields are filled
                if (name.length() > 0 && address.length() > 0 && phone.length() > 0 && email.length() > 0) {
                    // Set cafe data
                    newCafe.setOpenTime(openTime);
                    newCafe.setName(name);
                    newCafe.setAddress(address);
                    newCafe.setPhoneNumber(phone);
                    newCafe.setEmail(email);
                    newCafe.setDescription(description);

                    // Update Firebase database
                    cafeRef.child(id).child("address").setValue(address);
                    cafeRef.child(id).child("email").setValue(email);
                    cafeRef.child(id).child("openTime").setValue(openTime);
                    cafeRef.child(id).child("id").setValue(id);
                    cafeRef.child(id).child("description").setValue(description);
                    cafeRef.child(id).child("phoneNumber").setValue(phone);
                    cafeRef.child(id).child("name").setValue(name);
                    cafeRef.child(id).child("listImages").setValue(listHinhAnh); // Thêm danh sách hình ảnh

                    // Upload avatar image
                    StorageReference avatar = storageReference.child("avatar").child(id).child(id + ".jpg");
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) imgAnhCafe.getDrawable();
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
                                    String linkAnhCafe = uri.toString();
                                    cafeRef.child(id).child("avatar").setValue(linkAnhCafe).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AddACafeActivity.this, "Thêm quán café thành công!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(AddACafeActivity.this, "Thêm quán café thất bại, lỗi: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
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
        btnMenu.setOnClickListener(new View.OnClickListener() {
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