package com.example.quanlychuoicuahangcaphe;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quanlychuoicuahangcaphe.Adapter.CacAnhCafeAdapter;

import com.example.quanlychuoicuahangcaphe.Model.QuanCafe;
import com.example.quanlychuoicuahangcaphe.Plugins.CheckInternet;
import com.example.quanlychuoicuahangcaphe.Plugins.PhongtoAnh;
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

public class AddImageCafe extends AppCompatActivity {
    ImageView ivChonAnh;
    Button btnChonAnh, btnThemAnh, btnTroLai, btnChupanh;
    ListView lvCacAnhQuanCafe;
    ArrayList<String> CacAnhQuanCafe = new ArrayList<>();
    CacAnhCafeAdapter cacAnhCafeAdapter;

    // Firebase + Storage
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference quanCafe = databaseReference.child("cafe");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    QuanCafe new_nh = new QuanCafe();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image_cafe);

        isConnected();
        btnChonAnh = findViewById(R.id.btnChonAnh);
        btnThemAnh = findViewById(R.id.btnThemAnh);
        btnTroLai = findViewById(R.id.btnTroLai);
        lvCacAnhQuanCafe = findViewById(R.id.lvCacAnhCafe);
        ivChonAnh = findViewById(R.id.ivChonAnh);
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
                            Glide.with(AddImageCafe.this).load(photo).into(ivChonAnh);
                        }
                    }
                });

        btnChupanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddImageCafe.this, chupanh.class);
                ChupanhLaunch.launch(intent);
            }
        });

        CacAnhQuanCafe = new ArrayList<>();
        cacAnhCafeAdapter = new CacAnhCafeAdapter(AddImageCafe.this, R.layout.lv_images_cafe, CacAnhQuanCafe);
        lvCacAnhQuanCafe.setAdapter(cacAnhCafeAdapter);

        ActivityResultLauncher chonAnhLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        ivChonAnh.setImageURI(o);
                    }
                });

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        QuanCafe a = (QuanCafe) data.getSerializable("cafe");
        new_nh = a;
        if (a.getListHinhAnh() != null) {
            for (String src : a.getListHinhAnh()) {
                CacAnhQuanCafe.add(src);
            }
        }
        cacAnhCafeAdapter.notifyDataSetChanged();
        // phong to
        lvCacAnhQuanCafe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddImageCafe.this, PhongtoAnh.class);
                Bundle data = new Bundle();
                data.putString("anh", CacAnhQuanCafe.get(position));
                intent.putExtras(data);
                startActivity(intent);
            }
        });

        lvCacAnhQuanCafe.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder b = new AlertDialog.Builder(AddImageCafe.this);
                b.setTitle("Xóa ảnh cafe");
                b.setMessage("Bạn có chắc là muốn xóa ảnh khỏi các ảnh mô tả cafe ?");
                b.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                System.out.println(position);
                b.setPositiveButton("Chắc chắn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CacAnhQuanCafe.remove(position);
                        a.setListHinhAnh(CacAnhQuanCafe);
                        new_nh.setListHinhAnh(CacAnhQuanCafe);
                        cacAnhCafeAdapter.notifyDataSetChanged();

                        // Đoạn get key của ảnh + xóa ảnh.
                        quanCafe.child(a.getId()).child("listHinhAnh").addValueEventListener(new ValueEventListener() {
                            int dem = 0;

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    System.out.println(dem + " vi tri thu i");
                                    if (dem == position) {
                                        // Đoạn xóa
                                        System.out.println(data.getKey());
                                        StorageReference fileCanXoa = storageReference.child("anhQuanCafe")
                                                .child(a.getId()).child(data.getKey() + ".jpg");
                                        fileCanXoa.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                quanCafe.child(a.getId().toString()).child("listHinhAnh")
                                                        .child(data.getKey()).removeValue();
                                                Toast.makeText(AddImageCafe.this, "Xóa ảnh thành công",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddImageCafe.this, "Xóa ảnh thất bại !",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        dem++;
                                        break;
                                    } else
                                        dem++;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });
                b.create().show();
                return false;
            }
        });

        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chonAnhLauncher.launch("image/*");
            }
        });

        btnThemAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) ivChonAnh.getDrawable();
                if (bitmapDrawable != null) {
                    String id = quanCafe.child(a.getId()).child("listHinhAnh").push().getKey();
                    StorageReference anhCafe = storageReference.child("anhQuanCafe").child(a.getId()).child(id + ".jpg");
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baoStream);
                    byte[] imgData = baoStream.toByteArray();
                    anhCafe.putBytes(imgData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            anhCafe.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String link = uri.toString();
                                    CacAnhQuanCafe.add(link);
                                    a.setListHinhAnh(CacAnhQuanCafe);
                                    new_nh.setListHinhAnh(CacAnhQuanCafe);
                                    ivChonAnh.setImageResource(R.drawable.img);
                                    cacAnhCafeAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddImageCafe.this, "Lỗi khi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(AddImageCafe.this, "Vui lòng chọn ảnh trước khi thêm!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnTroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                Bundle data = new Bundle();
                new_nh.setListHinhAnh(CacAnhQuanCafe);
                a.setListHinhAnh(CacAnhQuanCafe);
                data.putSerializable("cafe", a);
                intent1.putExtras(data);
                setResult(103, intent1);
                finish();
            }
        });
    }

    void isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        cm.registerNetworkCallback(
                builder.build(),
                new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onLost(Network network) {
                        Intent intent = new Intent(AddImageCafe.this, CheckInternet.class);
                        startActivity(intent);
                    }
                }

        );
    }
}