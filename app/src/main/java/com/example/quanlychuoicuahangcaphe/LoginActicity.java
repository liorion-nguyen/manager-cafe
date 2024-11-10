package com.example.quanlychuoicuahangcaphe;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActicity extends AppCompatActivity {
    EditText edtEmail, edtMatKhau;
    Button btnDangNhap;
    TextView tvQuenMatKhau;
    ImageView imgPasswordInputType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acticity);
        addViews();
        addEvents();
    }

    private void addViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        tvQuenMatKhau = findViewById(R.id.tvQuenMatKhau);
        imgPasswordInputType = findViewById(R.id.imgPasswordInputType);
    }

    private void addEvents() {
        btnDangNhap.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String matKhau = edtMatKhau.getText().toString();
            if(email.equals("") && matKhau.equals("")){
                Toast.makeText(this, "Vui lòng nhập Email và Mật Khẩu", Toast.LENGTH_SHORT).show();
            } else {
                if(email.equals("admin") && matKhau.equals("admin")) {
                    Intent intent = new Intent(LoginActicity.this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Email hoặc Mật Khẩu chưa đúng!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgPasswordInputType.setOnClickListener(v -> {
            if(edtMatKhau.getInputType() == 129) {
                edtMatKhau.setInputType(1);
                imgPasswordInputType.setImageResource(R.drawable.baseline_visibility_off_24);
            } else {
                edtMatKhau.setInputType(129);
                imgPasswordInputType.setImageResource(R.drawable.baseline_visibility_24);
            }
        });

        tvQuenMatKhau.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActicity.this, QuenMatKhauActivity.class);
            startActivity(intent);
        });
    }
}