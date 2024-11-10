package com.example.quanlychuoicuahangcaphe;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlychuoicuahangcaphe.Plugins.GuiEmail;

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
            AlertDialog alertDialog = new AlertDialog.Builder(QuenMatKhauActivity.this).create();
            alertDialog.setTitle("Thông báo");
            alertDialog.setMessage("Bạn có chắc chắn muốn gửi mật khẩu mới không?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Có", (dialog, which) -> {
                sendMatKhauMoi();
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Không", (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog.show();
        });

        btnHuy.setOnClickListener(v -> {
            finish();
        });

    }

    public void sendMatKhauMoi() {
        String toEmail = edtEmail.getText().toString().trim();
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
                    content += tmp_char.charAt(index);
                }

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

                AlertDialog alertDialog_GuiEmail = new AlertDialog.Builder(QuenMatKhauActivity.this).create();
                alertDialog_GuiEmail.setTitle("Thông báo");
                alertDialog_GuiEmail.setMessage("Email đã được gửi tới " + toEmail);
                alertDialog_GuiEmail.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
                alertDialog_GuiEmail.show();
                //clear noi dung
                edtEmail.setText("");

//                Toast.makeText(QuenMatKhauActivity.this, "Email đã được gửi tới"+toEmail, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.d("Lỗi gửi Email", e.toString());
                Toast.makeText(QuenMatKhauActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}