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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.quanlychuoicuahangcaphe.Adapter.QuanCafeAdapter;

import com.example.quanlychuoicuahangcaphe.Model.QuanCafe;
import com.example.quanlychuoicuahangcaphe.Model.monAn;
import com.example.quanlychuoicuahangcaphe.Plugins.CheckInternet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {
    FloatingActionButton fabThemQuanCafe;
    ListView lvQuanCafe;
    ArrayList<QuanCafe> listQuanCafe;
    QuanCafeAdapter quanCafeAdapter;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference quanCafe = databaseReference.child("cafe");
    EditText editSeach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Ánh xạ
        fabThemQuanCafe = findViewById(R.id.fabThemQuanCafe);
        lvQuanCafe = findViewById(R.id.lvQuanCafe);
        listQuanCafe = new ArrayList<>();
        quanCafeAdapter = new QuanCafeAdapter(AdminActivity.this, R.layout.lv_item_nha_hang, listQuanCafe);
        lvQuanCafe.setAdapter(quanCafeAdapter);
        editSeach = findViewById(R.id.edtSearch);

        //ham tim kiem thong tin quan
        editSeach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                quanCafeAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // ham them quan cafe
        fabThemQuanCafe.setOnClickListener(view -> {
            Intent addACafe = new Intent(AdminActivity.this, AddACafeActivity.class);
            startActivity(addACafe);
        });
        isConnected();
        docDuLieu();

        // Initialize BottomNavigationView after setContentView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {Intent intent;
          switch (Objects.requireNonNull(item.getTitle()).toString()) {
              case "Profile":
                  intent = new Intent(AdminActivity.this, ProfileActivity.class);
                  startActivity(intent);
                  return true;
              case "Settings":
                  intent = new Intent(AdminActivity.this, SettingActivity.class);
                  startActivity(intent);
                  return true;
              default:
                  return false;
          }
        });
    }

    // tao menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem searchBar = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) searchBar.getActionView();
        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                quanCafeAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                quanCafeAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // kiểm tra kết nối mạng
    void isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        cm.registerNetworkCallback(
                builder.build(),
                new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onLost(@NonNull Network network) {
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
        quanCafeAdapter.notifyDataSetChanged();
    }

    // Đọc dữ liệu từ FireBase rồi lưu vào list nhà hàng
    private void docDuLieu() {
        quanCafe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listQuanCafe.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    QuanCafe cuahang = data.getValue(QuanCafe.class);
                    DataSnapshot data4 = data.child("danhGia");
                    float sumda = 0;
                    float arrDa;
                    for (DataSnapshot a : data4.getChildren()) {
                        sumda += Float.parseFloat(Objects.requireNonNull(a.getValue()).toString());
                    }
                    if (data4.getChildrenCount() == 0) {
                        arrDa = 0;
                    } else
                        arrDa = sumda / data4.getChildrenCount();
                    assert cuahang != null;
                    cuahang.setTb(arrDa);

                    ArrayList<monAn> listMonAn = new ArrayList<>();
                    DataSnapshot data1 = data.child("thucDon");

                    for (DataSnapshot data2 : data1.getChildren()) {
                        monAn ma = data2.getValue(monAn.class);
                        if (ma != null) {
                            listMonAn.add(ma);
                        }
                    }
                    DataSnapshot dataCacAnhQuanCafe = data.child("listImages");
                    ArrayList<String> hinhanhs = new ArrayList<>();
                    for (DataSnapshot data3 : dataCacAnhQuanCafe.getChildren()) {
                        hinhanhs.add(Objects.requireNonNull(data3.getValue()).toString());
                    }

                    cuahang.setListHinhAnh(hinhanhs);
                    cuahang.setListMonAn(listMonAn);
                    listQuanCafe.add(cuahang);
                }
                quanCafeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}