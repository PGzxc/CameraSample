package com.example.camerasample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.camerasample.utils.FileProviderUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_PERMISSION_CODE_TAKE_PHOTO = 0x110;
    private static final int REQUEST_CODE_TAKE_PHOTO = 0x111;
    private static final int REQ_ALBUM_CHOOSE = 0x112;
    private String mCurrentPhotoPath;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
    }

    private void initView() {
        imgView = findViewById(R.id.img_view);
    }

    private void setListener() {
        findViewById(R.id.btn_takePhoto).setOnClickListener(view -> {
            takePhotoNoCompress();
        });
        findViewById(R.id.btn_choosePhoto).setOnClickListener(view -> {
            choosePhoto();
        });
    }

    /**
     * 从相册选择照片
     */
    private void choosePhoto() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_ALBUM_CHOOSE);
    }

    /**
     * 通过摄像头拍照
     */
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
                    .format(new Date()) + ".png";
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            mCurrentPhotoPath = file.getAbsolutePath();
            Uri fileUri = FileProviderUtils.getUriFromFile(this, file);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
        }

    }

    private void takePhotoNoCompress() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSION_CODE_TAKE_PHOTO);
        } else {
            takePhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION_CODE_TAKE_PHOTO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    imgView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(MainActivity.this, "取消拍照", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            case REQ_ALBUM_CHOOSE:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(MainActivity.this, "点击取消从相册选择", Toast.LENGTH_LONG).show();
                    return;
                } else if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    imgView.setImageURI(uri);
                }
                break;
        }

    }
}
