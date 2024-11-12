package com.example.quanlychuoicuahangcaphe;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlychuoicuahangcaphe.Model.User;
import com.example.quanlychuoicuahangcaphe.Plugins.CheckInternet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;

public class SignUpActivity extends AppCompatActivity {
    EditText edtHoVaTen, edtEmail, edtMatKhau, edtXacNhanMatKhau;
    Button btnDangKy;
    FloatingActionButton fabBack;
    ImageView imgPasswordInputType, imgRePasswordInputType;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        isConnected();
        addViews();
        addEvents();
    }

    private void addViews() {
        edtHoVaTen = findViewById(R.id.edtHoVaTen);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtXacNhanMatKhau = findViewById(R.id.edtXacNhanMatKhau);
        btnDangKy = findViewById(R.id.btnDangKy);
        fabBack = findViewById(R.id.fabBack);
        imgPasswordInputType = findViewById(R.id.imgPasswordInputType);
        imgRePasswordInputType = findViewById(R.id.imgRePasswordInputType);
    }

    private void addEvents() {
        btnDangKy.setOnClickListener(v -> {
            String HoVaTen = edtHoVaTen.getText().toString();
            String Email = edtEmail.getText().toString();
            String MatKhau = edtMatKhau.getText().toString();
            String XacNhanMatKhau = edtXacNhanMatKhau.getText().toString();

            if(HoVaTen.equals("") || Email.equals("") || MatKhau.equals("") || XacNhanMatKhau.equals("")) {
                AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                alertDialog.setTitle("Thông báo");
                alertDialog.setMessage("Bạn vui lòng nhập đầy đủ thông tin");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                    dialog.dismiss();
                });
                alertDialog.show();
            } else {
                if(MatKhau.equals(XacNhanMatKhau)) {
                    myRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            }
                            else {
                                boolean check = true;
                                Map<String, User> userList = (Map<String, User>) task.getResult().getValue();
                                for(Map.Entry<String, User> entry : userList.entrySet()) {
                                    String id = entry.getKey();
                                    Map<String, String> u = (Map<String, String>) entry.getValue();
                                    if(u.get("email").equals(Email)) {
                                        check = false;
                                        break;
                                    }
                                }
                                if(!check) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                                    alertDialog.setTitle("Thông báo");
                                    alertDialog.setMessage("Email đã tồn tại!");
                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                                        dialog.dismiss();
                                    });
                                    alertDialog.show();
                                } else {
                                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                                    alertDialog.setTitle("Thông báo");
                                    alertDialog.setMessage("Đăng ký thành công!");
                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                                        String id = myRef.push().getKey();
                                        User user = new User(id, HoVaTen, Email, MatKhau, "", "");
                                        user.setId(id);

                                        myRef.child(id).setValue(user);

                                        Intent resultIntent = getIntent();
                //                        resultIntent.putExtra("HoVaTen", HoVaTen);
                //                        resultIntent.putExtra("Email", Email);
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    });
                                    alertDialog.show();
                                }
                            }
                        }
                    });
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                    alertDialog.setTitle("Thông báo");
                    alertDialog.setMessage("Mật khẩu không khớp!");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    alertDialog.show();
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

        imgRePasswordInputType.setOnClickListener(v -> {
            if(edtXacNhanMatKhau.getInputType() == 129) {
                edtXacNhanMatKhau.setInputType(1);
                imgRePasswordInputType.setImageResource(R.drawable.baseline_visibility_off_24);
            } else {
                edtXacNhanMatKhau.setInputType(129);
                imgRePasswordInputType.setImageResource(R.drawable.baseline_visibility_24);
            }
        });

        fabBack.setOnClickListener(v -> {
            Intent resultIntent = getIntent();
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        });

    }

    void isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        cm.registerNetworkCallback(
                builder.build(),
                new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onLost(Network network) {
                        Intent intent = new Intent(SignUpActivity.this, CheckInternet.class);
                        startActivity(intent);
                    }
                });
    }
}