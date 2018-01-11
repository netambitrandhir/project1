package com.sanganan.app.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sanganan.app.R;
import com.sanganan.app.fragments.SimpleScannerFragment;


public class QRCodeReaderActivity extends AppCompatActivity {


    String fromWHere = "";
    ImageView imageFlashLyt;
    SimpleScannerFragment fragment;
    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;
    private ViewGroup mainLayout;
    private Boolean isTorchOn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_layout);
        mainLayout = (ViewGroup) findViewById(R.id.view);
        fromWHere = getIntent().getStringExtra("from");
        isTorchOn = false;
        Bundle bundle = new Bundle();
        bundle.putString("from", fromWHere);

        fragment = new SimpleScannerFragment();
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.framescanner, fragment);
        t.commit();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            requestCameraPermission();
        }
    }

    private void initQRCodeReaderView() {


        imageFlashLyt = (ImageView) findViewById(R.id.imageFlashLyt);


        imageFlashLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isTorchOn) {
                        imageFlashLyt.setImageResource(R.drawable.ic_flashlyt_off);
                        fragment.flashOnOff(false);
                        isTorchOn = false;
                    } else {
                        imageFlashLyt.setImageResource(R.drawable.ic_flashlyt_on);
                        fragment.flashOnOff(true);
                        isTorchOn = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            initQRCodeReaderView();
        } else {
            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(QRCodeReaderActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

}


