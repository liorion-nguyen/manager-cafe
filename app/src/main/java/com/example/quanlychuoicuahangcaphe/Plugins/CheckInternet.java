package com.example.quanlychuoicuahangcaphe.Plugins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;

import com.example.quanlychuoicuahangcaphe.MainActivity;
import com.example.quanlychuoicuahangcaphe.R;

public class CheckInternet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_internet);
        isConnected();
    }
//  hàm check internet cho toàn app
    void isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                Intent intent = new Intent(CheckInternet.this, MainActivity.class);
                startActivity(intent);
            }

        }

        );
    }
}