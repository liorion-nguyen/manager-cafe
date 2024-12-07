package com.example.quanlychuoicuahangcafe.Plugins;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import com.bumptech.glide.Glide;
import com.example.quanlychuoicuahangcafe.R;


public class chupanh extends AppCompatActivity {
    ImageView imgData;
    Button takePicture,TroLai;
    String currentPhotoPath;



    ActivityResultLauncher takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode()==RESULT_OK)
                    {
                        Glide.with(chupanh.this).load(currentPhotoPath).into(imgData);
//                        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
//                        imgData.setImageBitmap(bitmap);
                    }

                }
            }
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chupanh);

        isConnected();
        imgData = findViewById(R.id.imgData);
        takePicture = findViewById(R.id.takePicture);
        TroLai = findViewById(R.id.TroLai);

        TroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPhotoPath != null)
                {
                    Intent intent = new Intent();
                    Bundle data= new Bundle();
                    data.putSerializable("anh",currentPhotoPath);
                    intent.putExtras(data);
                    setResult(110,intent);
                    finish();
                }
                finish();
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "photo";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    //tao anh tam thoi
                    File imageFile = File.createTempFile(filename,".jpg",storageDirectory);
                    currentPhotoPath = imageFile.getAbsolutePath();
                    Uri imgUri = FileProvider.getUriForFile(chupanh.this,"com.example.quanlychuoicuahangcafe.fileprovider",imageFile);
                    Intent takePictuerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictuerIntent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
                    takePictureLauncher.launch(takePictuerIntent);
                } catch (Exception e)
                {
                    Log.d("Loi chup anh",e.toString());
                    Toast.makeText(chupanh.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // kiểm tra kết nối mạng
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
                                Intent intent = new Intent(chupanh.this, CheckInternet.class);
                                startActivity(intent);
                            }
                        }

                );
    }
}