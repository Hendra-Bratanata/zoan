package com.zoan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QR_activity extends AppCompatActivity implements View.OnClickListener, ZXingScannerView.ResultHandler {
    Button button_reset;
    TextView tvQrcode;
    FrameLayout flCamera;
    private ZXingScannerView mScannerView;
    private boolean isCapture =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        button_reset =  findViewById(R.id.button_reset);
        flCamera = findViewById(R.id.frame_layout_camera);
        tvQrcode = findViewById(R.id.text_view_qr_code_value);
        initScannerView();
        initDefaultView();
        button_reset.setOnClickListener(this);
    }

    private void initDefaultView() {
     tvQrcode.setText("QR CODE VALUES");
     button_reset.setVisibility(View.GONE);


    }

    private void initScannerView() {
        mScannerView = new ZXingScannerView(this);
        mScannerView.setAutoFocus(true);
        mScannerView.setResultHandler(this);
        flCamera.addView(mScannerView);
    }

    @Override
    protected void onStart() {
        mScannerView.startCamera();
        doRequestPermission();
        super.onStart();
    }

    private void doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            initScannerView();
        }
    }

    @Override
    protected void onPause() {
        mScannerView.stopCamera();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_reset) {
            mScannerView.resumeCameraPreview(this);
            initDefaultView();
        }

    }

    @Override
    public void handleResult(Result result) {

       Intent intent = new Intent(this, MainActivity.class);
       intent.putExtra("imei", result.getText());
       startActivity(intent);
       finish();



    }
}