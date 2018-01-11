package com.sanganan.app.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.zxing.Result;


import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class SimpleScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    ProgressDialog progressDialog;
    String qrCodeStr = "";
    String fromWHere = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        mScannerView = new ZXingScannerView(getActivity());
        Bundle bundle = getArguments();
        fromWHere = bundle.getString("from");

        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        qrCodeStr = rawResult.getText();

        if (fromWHere.equals("addHelper")) {
            AddHelperActivity.qrCode = qrCodeStr;
        } else {
            EditHelperActivity.qrCodeEdit = qrCodeStr;
        }

        getActivity().finish();

        // Note:
        // * Wait 4 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SimpleScannerFragment.this);
            }
        }, 4000);

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    public void flashOnOff(boolean mFlash) {
        mScannerView.setFlash(mFlash);
    }

}
