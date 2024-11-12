package com.example.quanlychuoicuahangcaphe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlychuoicuahangcaphe.Model.User;
import com.example.quanlychuoicuahangcaphe.Plugins.SendEmail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class QuenMatKhauActivity extends AppCompatActivity {
    EditText edtEmail;
    Button btnQuenMatKhau, btnHuy;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quen_mat_khau);
        addViews();
        addEvents();
    }

    private void addViews() {
        edtEmail = findViewById(R.id.edtEmail);
        btnQuenMatKhau = findViewById(R.id.btnQuenMatKhau);
        btnHuy = findViewById(R.id.btnHuy);
    }

    private void addEvents() {
        btnQuenMatKhau.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            myRef.getRef().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        boolean check = true;
                        String id = "";
                        Map<String, User> userList = (Map<String, User>) task.getResult().getValue();
                        for(Map.Entry<String, User> entry : userList.entrySet()) {
                            id = entry.getKey();
                            Map<String, String> u = (Map<String, String>) entry.getValue();
                            if(u.get("email").equals(email)) {
                                check = false;
                                break;
                            }
                        }
                        if(!check) {
                            AlertDialog alertDialog = new AlertDialog.Builder(QuenMatKhauActivity.this).create();
                            alertDialog.setTitle("Thông báo");
                            alertDialog.setMessage("Bạn có chắc chắn muốn gửi mật khẩu mới không?");
                            String finalId = id;
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Có", (dialog, which) -> {
                                String newPassword = sendMatKhauMoi();
                                if(!newPassword.equals("")) {
                                    myRef.child(finalId).child("password").setValue(newPassword);
                                }
                            });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Không", (dialog, which) -> {
                                dialog.dismiss();
                            });
                            alertDialog.show();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(QuenMatKhauActivity.this).create();
                            alertDialog.setTitle("Thông báo");
                            alertDialog.setMessage("Email không tồn tại!");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                                dialog.dismiss();
                            });
                            alertDialog.show();
                        }
                    }
                }
            });
        });

        btnHuy.setOnClickListener(v -> {
            finish();
        });

    }

    public String sendMatKhauMoi() {
        String toEmail = edtEmail.getText().toString().trim();
        String newPassword = "";
        if(toEmail.equals("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(QuenMatKhauActivity.this).create();
            alertDialog.setTitle("Thông báo");
            alertDialog.setMessage("Vui lòng nhập Email!");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                dialog.dismiss();
            });
        } else {
            try {
                String fromEmail = "phanxuanvinh4592@@gmail.com";
                String emailPassword = "qdvp qaii wtcr tfzy";
                String subject = "QUÊN MẬT KHẨU";

                String content = "Mật khẩu mới của bạn là: ";

                String tmp_char = "abcdefghijklmnopqrstuvwxyz0123456789";
                for (int i = 0; i < 8; i++) {
                    int index = (int) (Math.random() * tmp_char.length());
                    newPassword += tmp_char.charAt(index);
                }

                content += newPassword;

                String host = "smtp.gmail.com";
                Properties properties = System.getProperties();
                properties.put("mail.smtp.host", host);
                properties.put("mail.smtp.port", "465"); // cổng
                properties.put("mail.smtp.ssl.enable", "true"); //Chứng chỉ bảo mật
                properties.put("mail.smtp.auth", "true"); //xác thực

                Session session = Session.getInstance(properties, new Authenticator() { // ss cua mail
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, emailPassword);
                    }
                });
                // gui noi dung thu
                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(content);
                Thread emailThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Transport.send(mimeMessage);

                        } catch (Exception e) {
                            Log.d("Lỗi Thread Email", e.toString());
                            Toast.makeText(QuenMatKhauActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                emailThread.start();

                AlertDialog alertDialog_SendEmail = new AlertDialog.Builder(QuenMatKhauActivity.this).create();
                alertDialog_SendEmail.setTitle("Thông báo");
                alertDialog_SendEmail.setMessage("Email đã được gửi tới " + toEmail);
                alertDialog_SendEmail.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
                alertDialog_SendEmail.show();
                //clear noi dung
                edtEmail.setText("");

//                Toast.makeText(QuenMatKhauActivity.this, "Email đã được gửi tới"+toEmail, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.d("Lỗi gửi Email", e.toString());
                Toast.makeText(QuenMatKhauActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        return newPassword;
    }
}