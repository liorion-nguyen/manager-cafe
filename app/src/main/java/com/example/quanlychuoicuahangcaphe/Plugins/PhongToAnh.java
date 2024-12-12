package com.example.quanlychuoicuahangcaphe.Plugins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.quanlychuoicuahangcaphe.R;

public class PhongToAnh extends AppCompatActivity {
    ImageView imgAnh;
    Button TroLai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phongto_anh);

        imgAnh = findViewById(R.id.imgAnh);
        TroLai = findViewById(R.id.TroLai);

        Intent intent = getIntent();
        Bundle  data = intent.getExtras();

        assert data != null;
        String anh = data.getString("anh");

        Glide.with(PhongToAnh.this).load(anh).into(imgAnh);


        TroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}