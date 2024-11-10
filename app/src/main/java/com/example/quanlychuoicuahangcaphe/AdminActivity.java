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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.quanlychuoicuahangcaphe.Adapter.NhaHangAdapter;

import com.example.quanlychuoicuahangcaphe.Model.NhaHang;
import com.example.quanlychuoicuahangcaphe.Model.monAn;
import com.example.quanlychuoicuahangcaphe.Plugins.CheckInternet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AdminActivity extends AppCompatActivity {
    FloatingActionButton fabThemNhaHang;
    ListView lvNhaHang;
    ArrayList<NhaHang> listNhaHang;
    NhaHangAdapter nhaHangAdapter;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference nhaHang = databaseReference.child("nhaHang");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Ánh xạ
        fabThemNhaHang = findViewById(R.id.fabThemNhaHang);
        lvNhaHang = findViewById(R.id.lvNhaHang);
        listNhaHang = new ArrayList<>();
        nhaHangAdapter = new NhaHangAdapter(AdminActivity.this,R.layout.lv_item_nha_hang,listNhaHang);
        lvNhaHang.setAdapter(nhaHangAdapter);

        fabThemNhaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent themMoiNhaHang = new Intent(AdminActivity.this, ThemMoiNhaHangActivity.class);
                startActivity(themMoiNhaHang);
            }
        });
        isConnected();
        docDuLieu();
    }

    //tao menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =  getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem searchBar = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) searchBar.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                nhaHangAdapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                nhaHangAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // kiểm tra kết nối mạng
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
                                Intent intent = new Intent(AdminActivity.this, CheckInternet.class);
                                startActivity(intent);
                            }
                        }

                );
    }

    // Bắt kết quả của các activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        nhaHangAdapter.notifyDataSetChanged();
    }

    // Đọc dữ liệu từ FireBase rồi lưu vào list nhà hàng
    private void docDuLieu(){
        nhaHang.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNhaHang.clear();
                for (DataSnapshot data: snapshot.getChildren()){
                    NhaHang nh = data.getValue(NhaHang.class);
                    DataSnapshot data4 = data.child("danhGia");
                    float sumda=0;
                    float arrda=0;

                    for (DataSnapshot a : data4.getChildren()) {
                        sumda+=Float.parseFloat(a.getValue().toString());
                    }
                    if (data4.getChildrenCount() == 0) {
                        arrda = 0;
                    } else arrda = sumda / data4.getChildrenCount();
                    nh.setTb(arrda);

                    ArrayList<monAn> listMonAn = new ArrayList<>();
                    DataSnapshot data1 = data.child("thucDon");

                    for (DataSnapshot data2: data1.getChildren()){
                        monAn ma = data2.getValue(monAn.class);
                        if (ma != null){
                            listMonAn.add(ma);
                        }
                    }
                    DataSnapshot dataCacAnhNhaHang = data.child("cacAnhNhaHang");
                    ArrayList<String> hinhanhs = new ArrayList<>();
                    for (DataSnapshot data3 : dataCacAnhNhaHang.getChildren()){
                        hinhanhs.add(data3.getValue().toString());
                    }

                    nh.setListHinhAnh(hinhanhs);
                    nh.setListMonAn(listMonAn);
                    if (nh != null){
                        listNhaHang.add(nh);
                    }
                }
                nhaHangAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}