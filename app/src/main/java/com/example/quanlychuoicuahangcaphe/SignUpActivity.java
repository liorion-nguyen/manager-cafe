package com.example.quanlychuoicuahangcaphe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {
    EditText edtHoVaTen, edtEmail, edtMatKhau, edtXacNhanMatKhau;
    Button btnDangKy, btnHuy;
    ImageView imgPasswordInputType, imgRePasswordInputType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        addViews();
        addEvents();
    }

    private void addViews() {
        edtHoVaTen = findViewById(R.id.edtHoVaTen);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtXacNhanMatKhau = findViewById(R.id.edtXacNhanMatKhau);
        btnDangKy = findViewById(R.id.btnDangKy);
        btnHuy = findViewById(R.id.btnHuy);
        imgPasswordInputType = findViewById(R.id.imgPasswordInputType);
        imgRePasswordInputType = findViewById(R.id.imgRePasswordInputType);
    }

    private void addEvents() {
        btnDangKy.setOnClickListener(v -> {
            String HoVaTen = edtHoVaTen.getText().toString();
            String Email = edtEmail.getText().toString();
            String MatKhau = edtMatKhau.getText().toString();
            String XacNhanMatKhau = edtXacNhanMatKhau.getText().toString();
            AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
            alertDialog.setTitle("Thông báo");
            if(HoVaTen.equals("") || Email.equals("") || MatKhau.equals("") || XacNhanMatKhau.equals("")) {
                alertDialog.setMessage("Bạn vui lòng nhập đầy đủ thông tin");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                    dialog.dismiss();
                });
            } else {
                if(MatKhau.equals(XacNhanMatKhau)) {
                    alertDialog.setMessage("Đăng ký thành công!");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                        Intent resultIntent = getIntent();
                        resultIntent.putExtra("HoVaTen", HoVaTen);
                        resultIntent.putExtra("Email", Email);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                } else {
                    alertDialog.setMessage("Mật khẩu không khớp!");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                        dialog.dismiss();
                    });
                }
            }
            alertDialog.show();
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

        imgRePasswordInputType.setOnClickListener(v -> {
            if(edtXacNhanMatKhau.getInputType() == 129) {
                edtXacNhanMatKhau.setInputType(1);
                imgRePasswordInputType.setImageResource(R.drawable.baseline_visibility_off_24);
            } else {
                edtXacNhanMatKhau.setInputType(129);
                imgRePasswordInputType.setImageResource(R.drawable.baseline_visibility_24);
            }
        });

        btnHuy.setOnClickListener(v -> {
            Intent resultIntent = getIntent();
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        });

    }
}