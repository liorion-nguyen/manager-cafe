package com.example.quanlychuoicuahangcaphe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quanlychuoicuahangcaphe.Adapter.monAnAdapter;
import com.example.quanlychuoicuahangcaphe.Model.QuanCafe;
import com.example.quanlychuoicuahangcaphe.Model.monAn;
import com.example.quanlychuoicuahangcaphe.Plugins.CheckInternet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class XemThucDonActivity extends AppCompatActivity {
    Button btnTroLai, btnThemMonAn;
    ListView lvMonAn;
    ArrayList<monAn> listMonAn;
    com.example.quanlychuoicuahangcaphe.Adapter.monAnAdapter monAnAdapter;
    QuanCafe b = new QuanCafe();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference quanCafe = databaseReference.child("quanCafe");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_thuc_don);

        isConnected();
        btnTroLai = findViewById(R.id.btnTroLai);
        btnThemMonAn = findViewById(R.id.btnThemMonAn);
        lvMonAn = findViewById(R.id.lvMonAn);
        listMonAn = new ArrayList<>();
        monAnAdapter = new monAnAdapter(XemThucDonActivity.this,R.layout.lv_item_mon_an,listMonAn);
        lvMonAn.setAdapter(monAnAdapter);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        QuanCafe a = (QuanCafe) data.getSerializable("cafe");
        if (a.getListMonAn() != null) {
            for (monAn ma : a.getListMonAn()){
                listMonAn.add(ma);
            }
        }
        monAnAdapter.notifyDataSetChanged();
        b = a;

        lvMonAn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent quanCafe = new Intent(XemThucDonActivity.this, XemMonAn.class);
                Bundle data = new Bundle();
                data.putSerializable("cafe",b);
                data.putSerializable("monan",b.getListMonAn().get(i));
                quanCafe.putExtras(data);
                startActivityForResult(quanCafe,104);
            }
        });

        btnTroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                Bundle data = new Bundle();
                data.putSerializable("cafe",b);
                intent1.putExtras(data);
                setResult(102,intent1);
                finish();
            }
        });

        btnThemMonAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent themMonAn = new Intent(XemThucDonActivity.this, ThemMoiMonAnActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("cafe", b);
                themMonAn.putExtras(data);
                startActivityForResult(themMonAn,101);
            }
        });
    }

    // Đoạn bắt các hoạt động
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 101){
            Bundle dulieu = data.getExtras();
            monAn moi = (monAn) dulieu.getSerializable("monan");
            ArrayList<monAn> listmonan = new ArrayList<>();
            listmonan.addAll(listMonAn);
            listMonAn.clear();
            listMonAn.add(moi);
            listMonAn.addAll(listmonan);
            b.setListMonAn(listMonAn);
            monAnAdapter.notifyDataSetChanged();
        }

        if (requestCode == 104 && resultCode == 104){
            Bundle dulieu = data.getExtras();
            ArrayList<monAn> listmonan = (ArrayList<monAn>) dulieu.getSerializable("listmonan");
            b.setListMonAn(listmonan);
            listMonAn.clear();
            listMonAn.addAll(listmonan);
            monAnAdapter.notifyDataSetChanged();
        }

        if (requestCode == 104 && resultCode == 106){
            Bundle dulieu = data.getExtras();
            ArrayList<monAn> listmonan =  new ArrayList<>();
            monAn moi = (monAn) dulieu.getSerializable("monan");
            for (monAn monan : listMonAn) {
                if (monan.getId().equals(moi.getId())){
                    // Xóa ở storage
                    StorageReference fileCanXoa = storageReference.child("anhMonAn")
                            .child(b.getId()).child(moi.getId().toString()+".jpg");
                    fileCanXoa.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            quanCafe.child(b.getId().toString()).child("thucDon").child(moi.getId()).removeValue();
                            Toast.makeText(XemThucDonActivity.this, "Xóa món ăn thành công"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(XemThucDonActivity.this, "Xảy ra lỗi khi xóa file !"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    listmonan.add(monan);
                }
            }
            b.setListMonAn(listmonan);
            listMonAn.clear();
            listMonAn.addAll(listmonan);
            monAnAdapter.notifyDataSetChanged();
        }
    }
    void isConnected() {
        ConnectivityManager cm
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        cm.registerNetworkCallback
                (builder.build(), new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onLost(Network network) {
                        Intent intent = new Intent(XemThucDonActivity.this, CheckInternet.class);
                        startActivity(intent);
                    }
                }
                );
    }
}