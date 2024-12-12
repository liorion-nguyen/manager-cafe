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
import android.util.Log;

import com.example.quanlychuoicuahangcaphe.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Kiểm tra kết nối mạng
        checkInternetConnection();

        // Khởi tạo Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MapsActivity", "Không thể khởi tạo Map Fragment");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;

        // Lấy thông tin từ Intent
        String diaChi = getIntent().getStringExtra("address");
        String tenQuan = getIntent().getStringExtra("ten_quan");

        if (diaChi != null && !diaChi.isEmpty()) {
            // Hiển thị vị trí từ địa chỉ
            LatLng latLng = convertAddressToLatLng(diaChi);
            if (latLng != null) {
                addMarkerAndMoveCamera(latLng, tenQuan);
            } else {
                Log.e("MapsActivity", "Không thể chuyển đổi địa chỉ thành LatLng");
            }
        } else {
            Log.e("MapsActivity", "Không có thông tin địa chỉ");
        }
    }

    private LatLng convertAddressToLatLng(String address) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            Log.e("MapsActivity", "Lỗi khi chuyển đổi địa chỉ: " + e.getMessage());
        }
        return null;
    }

    private void addMarkerAndMoveCamera(LatLng latLng, String nameQuan) {
        if (gMap != null) {
            gMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí của quán " + nameQuan));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(@NonNull Network network) {
                startActivity(new Intent(MapsActivity.this, CheckInternet.class));
                finish();
            }
        });
    }

}
