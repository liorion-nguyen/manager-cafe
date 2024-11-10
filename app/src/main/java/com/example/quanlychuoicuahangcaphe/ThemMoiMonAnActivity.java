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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quanlychuoicuahangcaphe.Plugins.CheckInternet;
import com.example.quanlychuoicuahangcaphe.Model.NhaHang;
import com.example.quanlychuoicuahangcaphe.Plugins.chupanh;
import com.example.quanlychuoicuahangcaphe.Model.monAn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ThemMoiMonAnActivity extends AppCompatActivity {

    EditText edtTenMonAn, edtMoTaMonAn, edtGiaMonAn;
    ImageView ivAnhMonAn;
    Button btnHuyBo, btnXacNhan,btnChonAnh,btnChupanh;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference nhaHang = databaseReference.child("nhaHang");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_moi_mon_an);

        isConnected();
        // Ánh xạ
        edtTenMonAn = findViewById(R.id.edtTenMonAn);
        edtMoTaMonAn = findViewById(R.id.edtMoTaMonAn);
        edtGiaMonAn = findViewById(R.id.edtGiaMonAn);
        ivAnhMonAn = findViewById(R.id.ivAnhMonAn);
        btnHuyBo = findViewById(R.id.btnTroLai);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        btnChonAnh = findViewById(R.id.btnChonAnh);
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
                            Glide.with(ThemMoiMonAnActivity.this).load(photo).into(ivAnhMonAn);

                        }
                    }
                }
        );

        btnChupanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ThemMoiMonAnActivity.this, chupanh.class);
                ChupanhLaunch.launch(intent);
            }
        });

        // Code

        ActivityResultLauncher chonAnhLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        ivAnhMonAn.setImageURI(o);
                    }
                }
        );

        btnHuyBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenMonAn = edtTenMonAn.getText().toString();
                String moTaMonAn = edtMoTaMonAn.getText().toString();
                float giaMonAn = 0;
                if (edtGiaMonAn.getText().toString().length() > 0){
                    giaMonAn = Float.parseFloat(edtGiaMonAn.getText().toString());
                }
                if (tenMonAn.length() > 0 && moTaMonAn.length() > 0 && giaMonAn > 0){
                    Intent intent = getIntent();
                    Bundle data = intent.getExtras();
                    NhaHang a = (NhaHang) data.getSerializable("nhahang");
                    String idmonan = nhaHang.child(a.getId()).child("thucDon").push().getKey().toString();
                    monAn ma = new monAn(idmonan,tenMonAn,"",giaMonAn,moTaMonAn);
                    StorageReference anhMonAn  =
                            storageReference.child("anhMonAn").child(a.getId()).child(idmonan + ".jpg");

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) ivAnhMonAn.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baoStream);
                    byte[] imgData = baoStream.toByteArray();
                    anhMonAn.putBytes(imgData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            anhMonAn.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String linkAnhMonAn = uri.toString();
                                    ma.setHinhAnhMinhHoa(linkAnhMonAn);
                                    // Set value cho mon an
                                    nhaHang.child(a.getId().toString())
                                            .child("thucDon").child(idmonan).setValue(ma).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(ThemMoiMonAnActivity.this,
                                                                "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                                                        Bundle data = new Bundle();
                                                        data.putSerializable("monan",ma);
                                                        Intent result = new Intent();
                                                        result.putExtras(data);
                                                        setResult(101,result);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ThemMoiMonAnActivity.this,
                                                                "Thêm món ăn thất bại, lỗi : " + task.getException().toString(),
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
                    Toast.makeText(ThemMoiMonAnActivity.this,
                            "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chonAnhLauncher.launch("image/*");
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
                                Intent intent = new Intent(ThemMoiMonAnActivity.this, CheckInternet.class);
                                startActivity(intent);
                            }
                        }

                );
    }
}