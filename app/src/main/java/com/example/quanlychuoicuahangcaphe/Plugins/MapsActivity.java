package com.example.quanlychuoicuahangcaphe.Plugins;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.util.Log;

import com.example.quanlychuoicuahangcaphe.Model.QuanCafe;
import com.example.quanlychuoicuahangcaphe.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap gMap;
    FrameLayout map;
    private DatabaseReference nhaHangRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        isConnected();

        map = findViewById(R.id.map);

        // Lấy địa chỉ từ Intent
        String diaChi = getIntent().getStringExtra("dia_chi");

        if (diaChi != null && !diaChi.isEmpty()) {
            // Khởi tạo Map Fragment
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(googleMap -> {
                    this.gMap = googleMap;

                    // Chuyển địa chỉ thành LatLng và hiển thị trên bản đồ
                    LatLng latLng = chuyenDiaChiThanhLatLng(diaChi);
                    if (latLng != null) {
                        gMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí của quán"));
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } else {
                        Log.e("MapsActivity", "Không thể chuyển đổi địa chỉ thành LatLng");
                    }
                });
            } else {
                Log.e("MapsActivity", "Không thể khởi tạo Map Fragment");
            }
        } else {
            Log.e("MapsActivity", "Địa chỉ không hợp lệ");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;

        // Lấy ID nhà hàng từ Intent
        String nhahangId = getIntent().getStringExtra("nhahang_id");

        // Thực hiện truy vấn đến Firebase để lấy địa chỉ theo ID
        nhaHangRef.child(nhahangId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    QuanCafe quanCafe = dataSnapshot.getValue(QuanCafe.class);

                    if (quanCafe != null) {
                       LatLng quanCafeLatLng = chuyenDiaChiThanhLatLng(quanCafe.address);
                        // Thêm đánh dấu cho địa điểm đích
                        gMap.addMarker(new MarkerOptions().position(quanCafeLatLng).title("Quán cà phê"));

                        // Di chuyển camera đến địa điểm đích
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quanCafeLatLng, 15));
                    }
                } else {
                    Log.e("MapsActivity", "Không tìm thấy nhà hàng với ID đã cho");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi từ Firebase
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
            }
        });
    }

    private LatLng chuyenDiaChiThanhLatLng(String diaChi) {
        Geocoder geocoder = new Geocoder(this);
        try {
            // Chuyển địa chỉ thành tọa độ LatLng
            Address address = geocoder.getFromLocationName(diaChi, 1).get(0);
            if (address != null) {
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException | IndexOutOfBoundsException e) {
            Log.e("MapsActivity", "Lỗi khi chuyển đổi địa chỉ: " + e.getMessage());
        }
        return null;
    }

    private void isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(@NonNull Network network) {
                Intent intent = new Intent(MapsActivity.this, CheckInternet.class);
                startActivity(intent);
            }
        });
    }
}
