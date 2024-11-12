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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlychuoicuahangcaphe.Model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class LoginActicity extends AppCompatActivity {
    FloatingActionButton fabBack;
    EditText edtEmail, edtMatKhau;
    Button btnDangNhap;
    TextView tvQuenMatKhau;
    ImageView imgPasswordInputType;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
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
        fabBack = findViewById(R.id.fabBack);
    }

    private void addEvents() {
        btnDangNhap.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String matKhau = edtMatKhau.getText().toString();
            if(email.equals("") && matKhau.equals("")){
                Toast.makeText(this, "Vui lòng nhập Email và Mật Khẩu", Toast.LENGTH_SHORT).show();
            } else {
                myRef.getRef().get().addOnCompleteListener(task -> {
                    if(!task.isSuccessful()) {
                        Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean check = true;
                        Map<String, User> userList = (Map<String, User>) task.getResult().getValue();
                        for(Map.Entry<String, User> entry : userList.entrySet()) {
                            String id = entry.getKey();
                            Map<String, String> u = (Map<String, String>) entry.getValue();
                            if(u.get("email").equals(email)) {
                                check = false;
                                break;
                            }
                        }
                        if(!check) {
                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActicity.this).create();
                            alertDialog.setTitle("Thông báo");
                            alertDialog.setMessage("Đăng nhập thành công!");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                                Intent intent = new Intent(LoginActicity.this, AdminActivity.class);
                                startActivity(intent);
                            });
                            alertDialog.show();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActicity.this).create();
                            alertDialog.setTitle("Thông báo");
                            alertDialog.setMessage("Email hoặc Mật Khẩu chưa đúng!");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                                dialog.dismiss();
                            });
                            alertDialog.show();
                        }
                    }
                });
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

        fabBack.setOnClickListener(v -> {
            finish();
        });
    }
}