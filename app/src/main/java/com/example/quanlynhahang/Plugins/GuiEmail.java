package com.example.quanlynhahang.Plugins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlynhahang.Model.NhaHang;
import com.example.quanlynhahang.R;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//    mdyv jwpn wunh vkov : mã pass app email

public class GuiEmail extends AppCompatActivity {
    TextView edtEmailTo,TenNH;
    EditText edtContent,edtSubject;
    Button btnSend,btnTroLai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui_email);

        isConnected();
        // Lay itent
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        NhaHang a = (NhaHang) data.getSerializable("nhahang");

        String email = a.getEmail();
        String name = a.getTenNhaHang();

        edtEmailTo = findViewById(R.id.edtEmailTo);
        edtContent = findViewById(R.id.edtContent);
        edtSubject = findViewById(R.id.edtSubject);
        btnSend = findViewById(R.id.btnSend);
        btnTroLai = findViewById(R.id.btnTroLai);
        TenNH = findViewById(R.id.tenNH);

        edtEmailTo.setText(email);
        TenNH.setText(name);

        btnTroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fromEmail = "plvanhieu@gmail.com";
                    String emailPassword = "mdyv jwpn wunh vkov";
                    String toEmail = edtEmailTo.getText().toString().trim();
                    String subject = edtSubject.getText().toString().trim();
                    String content = edtContent.getText().toString().trim();
                    String host = "smtp.gmail.com";
                    Properties properties = System.getProperties();
                    properties.put("mail.smtp.host",host);
                    properties.put("mail.smtp.port","465"); // cổng
                    properties.put("mail.smtp.ssl.enable","true"); //Chứng chỉ bảo mật
                    properties.put("mail.smtp.auth","true"); //xác thực

                    Session session = Session.getInstance(properties, new Authenticator() { // ss cua mail
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(fromEmail,emailPassword);
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

                            }catch (Exception e)
                            {
                                Log.d("Lỗi Thread Email",e.toString());
                                Toast.makeText(GuiEmail.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    emailThread.start();
                    //clear noi dung
                    edtContent.setText("");
                    edtEmailTo.setText("");
                    edtSubject.setText("");

                    Toast.makeText(GuiEmail.this, "Email đã được gửi tới"+toEmail, Toast.LENGTH_SHORT).show();
                }catch (Exception e)
                {
                    Log.d("Lỗi gửi Email",e.toString());
                    Toast.makeText(GuiEmail.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void isConnected() {
        ConnectivityManager cm
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        cm.registerNetworkCallback
                (
                        builder.build(),
                        new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onLost(Network network) {
                                Intent intent = new Intent(GuiEmail.this, CheckInternet.class);
                                startActivity(intent);
                            }
                        }

                );
    }
}