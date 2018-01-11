package com.sanganan.app.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;

import com.sanganan.app.activities.QRCodeReaderActivity;
import com.sanganan.app.adapters.AddHelperImageListAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.customview.HorizontalListView;
import com.sanganan.app.utility.Util;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by root on 22/10/16.
 */

public class AddHelperActivity extends ActionBarActivity {

    ImageView profilePic, submit;
    RelativeLayout addpicdocs;
    TextView tvName, tvType, tvAddress, tvContactNo, tvWages, qrTextbtn_TV;
    EditText etName, etType, etAddress, etContactNo, etWages;
    String name, type, address, contactNo, wages;
    HorizontalListView horizontalListView;
    String current_time = "";
    Common common;
    RequestQueue requestQueue;

    public static ArrayList<File> listImages = new ArrayList<>();
    public static ArrayList<String> pathListOnAws = new ArrayList<>();

    private Bitmap bitmap;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int REQUEST_CODE_Gallery = 0;
    private static final String IMAGE_DIRECTORY_NAME = "mynukad";
    private static final String TAG = "UploadActivity";
    private TransferUtility transferUtility;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    static String profilepic;
    static String timeStamp;
    Bitmap croppedBmp;
    private Uri fileUri;
    boolean isDocsAdding = false;
    String profilePicPath = "";
    String link1 = "", link2 = "", link3 = "";
    String contactNumber2 = "";
    String link[] = {link1, link2, link3};

    Typeface ubuntuB, karlaB, karlaR, wsRegular, wsMedium;

    String permissionArray[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};
    private FirebaseAnalytics mFirebaseAnalytics;
    public static String qrCode = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.add_helper_layout);
        common = Common.getNewInstance(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        listImages.clear();
        pathListOnAws.clear();

        initializeVariable();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (Constants.rolesGivenToUser.contains("Helperqr_Code")) {
            qrTextbtn_TV.setVisibility(View.VISIBLE);
        } else {
            qrTextbtn_TV.setVisibility(View.GONE);
        }

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("community_helper_add", logBundleInitial);

        addpicdocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doYourStuffToCHeckPermissionAddingDocs();
            }
        });
        qrCode = "";

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doYourStuffToCHeckPermission();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etName.getText().toString().isEmpty() && !etContactNo.getText().toString().isEmpty() && !etType.getText().toString().isEmpty()) {
                    name = etName.getText().toString();
                    address = etAddress.getText().toString();
                    wages = etWages.getText().toString();
                    contactNo = etContactNo.getText().toString();
                    type = etType.getText().toString();


                    for (int i = 0; i < pathListOnAws.size(); i++) {
                        link[i] = pathListOnAws.get(i);
                    }

                    getData();
                } else {
                    common.showShortToast("Enter mandatory fields");
                }
            }
        });

        qrTextbtn_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qrCode.isEmpty()) {
                    Intent intent = new Intent(AddHelperActivity.this, QRCodeReaderActivity.class);
                    intent.putExtra("from", "addHelper");
                    startActivity(intent);
                } else {
                    AlertBoxForRemoveQr();
                }
            }
        });

        transferUtility = Util.getTransferUtility(this);


    }



    @Override
    public void onResume() {
        super.onResume();
        if (!qrCode.isEmpty()) {
            qrTextbtn_TV.setText("Linked qr : " + qrCode + ", tap to remove");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void doYourStuffToCHeckPermission() {

        int j = 1;
        ArrayList<String> listPermissionDenied = new ArrayList<String>();
        for (int i = 0; i < permissionArray.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, permissionArray[i]) != PackageManager.PERMISSION_GRANTED) {

                listPermissionDenied.add(permissionArray[i]);
                j = j * 0;

            }
        }
        if (j == 0) {
            String array[] = new String[listPermissionDenied.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = listPermissionDenied.get(i);
            }
            this.requestPermissions(array, 2);
        } else {
            isDocsAdding = false;
            AlertBoxForImagePic();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void doYourStuffToCHeckPermissionAddingDocs() {

        int j = 1;
        ArrayList<String> listPermissionDenied = new ArrayList<String>();
        for (int i = 0; i < permissionArray.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, permissionArray[i]) != PackageManager.PERMISSION_GRANTED) {

                listPermissionDenied.add(permissionArray[i]);
                j = j * 0;

            }
        }
        if (j == 0) {
            String array[] = new String[listPermissionDenied.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = listPermissionDenied.get(i);
            }
            this.requestPermissions(array, 4);
        } else {
            isDocsAdding = true;
            if (listImages.size() < 3) {
                AlertBoxForImagePic();
            } else {
                common.showShortToast("maximum 3 documents will be added");
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            int k = 1;
            for (int i = 0; i < grantResults.length; i++) {
                k = k * grantResults[i];
            }
            if (k != 0) {
                isDocsAdding = false;
                AlertBoxForImagePic();
            } else {
                doYourStuffToCHeckPermission();
                common.showShortToast("You need to give all permissions");
            }
        }
        if (requestCode == 2) {
            int k = 1;
            for (int i = 0; i < grantResults.length; i++) {
                k = k * grantResults[i];
            }
            if (k != 0) {
                isDocsAdding = true;
                AlertBoxForImagePic();
            } else {
                doYourStuffToCHeckPermissionAddingDocs();
                common.showShortToast("You need to give all permissions");
            }
        }
    }

    private void initializeVariable() {

        ubuntuB = Typeface.createFromAsset(this.getAssets(), "Ubuntu-B.ttf");
        wsRegular = Typeface.createFromAsset(this.getAssets(), "WorkSans-Regular.ttf");
        karlaB = Typeface.createFromAsset(this.getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(this.getAssets(), "Karla-Regular.ttf");
        wsMedium = Typeface.createFromAsset(this.getAssets(), "WorkSans-Medium.ttf");

        TextView title = (TextView) findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);
        profilePic = (ImageView) findViewById(R.id.cirprofilePic);
        submit = (ImageView) findViewById(R.id.submit);
        addpicdocs = (RelativeLayout) findViewById(R.id.addpicdocs);
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setTypeface(karlaB);
        tvType = (TextView) findViewById(R.id.tvType);
        tvType.setTypeface(karlaB);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvAddress.setTypeface(karlaB);
        tvContactNo = (TextView) findViewById(R.id.tvContactNumber);
        tvContactNo.setTypeface(karlaB);
        tvWages = (TextView) findViewById(R.id.tvWages);
        tvWages.setTypeface(karlaB);

        TextView textViewMaxUT = (TextView) findViewById(R.id.textadd_anyuptoThree);
        textViewMaxUT.setTypeface(karlaB);

        qrTextbtn_TV = (TextView) findViewById(R.id.qrTextbtn);
        qrTextbtn_TV.setTypeface(wsMedium);


        etName = (EditText) findViewById(R.id.etName);
        etName.setTypeface(wsRegular);
        etType = (EditText) findViewById(R.id.etType);
        etType.setTypeface(wsRegular);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etAddress.setTypeface(wsRegular);
        etContactNo = (EditText) findViewById(R.id.etContactNumber);
        etContactNo.setTypeface(wsRegular);
        etWages = (EditText) findViewById(R.id.etWages);
        etWages.setTypeface(wsRegular);

        horizontalListView = (HorizontalListView) findViewById(R.id.horizontal_listview);
    }

    private void getData() {

        common.showSpinner(AddHelperActivity.this);
        String uri = Constants.BaseUrl + "addhelper";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("ServiceOffered", type);
            json.put("ServiceCharges", wages);
            json.put("ResidentialAddress", address);
            json.put("PrimaryContactNbr", contactNo);
            json.put("PhoneNbr2", contactNumber2);
            json.put("EmailId", "");
            json.put("Name", name);
            json.put("AddedBy", common.getStringValue(Constants.ResidentRWAID));
            json.put("ProfilePhoto", profilePicPath);
            json.put("OtherDocScanImage1", link[0]);
            json.put("OtherDocScanImage2", link[1]);
            json.put("OtherDocScanImage3", link[2]);
            if (!qrCode.isEmpty()) {
                json.put("HelperQRCode", qrCode);
            }
            json.put("EntryCardExpiry", "");


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());

                        }

                    }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    return headers;
                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    void parsedData(JSONObject json) {
        String status = "";
        String message = "";
        try {
            status = json.optString("Status");
            message = json.optString("Message");
            common.hideSpinner();
            if (status.equalsIgnoreCase("1")) {
                Constants.isAnyHelperEditedorAdded = true;
                finish();
                common.showShortToast(message);
                Bundle logBundleOnSuccess = new Bundle();
                logBundleOnSuccess.putString("society_id", common.getStringValue(Constants.userRwa));
                logBundleOnSuccess.putString("user_id", common.getStringValue(Constants.id));
                mFirebaseAnalytics.logEvent("community_helper_added_success", logBundleOnSuccess);

            } else {
                common.showShortToast(message);
            }
        } catch (Exception e) {

        }



    }

    public void AlertBoxForImagePic() {


        new AlertDialog.Builder(this)
                .setTitle("Please Wait....")
                .setMessage("Want to choose photo from")
                .setPositiveButton("Camera",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {


                                Intent intentcamera = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);

                                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                                intentcamera.putExtra(MediaStore.EXTRA_OUTPUT,
                                        fileUri);

                                // start the image capture Intent
                                startActivityForResult(intentcamera,
                                        CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                            }
                        })
                .setNegativeButton("Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {


                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(intent,
                                        REQUEST_CODE_Gallery);

                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

    }

    public Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                android.os.Environment
                        .getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        timeStamp = new SimpleDateFormat("HHmmss",
                Locale.getDefault()).format(new Date());
        String abc = timeStamp.toString() + ".jpg";
        profilepic = Constants.AWSLINK + abc;
        File mediaFile;

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_Gallery: {
                if (requestCode == REQUEST_CODE_Gallery
                        && resultCode == Activity.RESULT_OK)

                {
                    Uri uri = data.getData();

                    try {
                        String path = getPath(uri);
                        // String p=path.replace(path, timeStamp+".jpg");
                        beginUpload(path);

                    } catch (URISyntaxException e) {
                        Toast.makeText(this,
                                "Unable to get the file from the given URI.  See error log for details",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Unable to upload file from the given uri", e);
                    }
                    try {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 5;
                        bitmap = BitmapFactory.decodeFile(getPath(uri), options);
                        croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,
                                bitmap.getWidth(), bitmap.getHeight() - 1);
                        if (!isDocsAdding) {
                            profilePic.setImageBitmap(croppedBmp);
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                super.onActivityResult(requestCode, resultCode, data);
            }
            break;

            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE: {

                if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                    if (resultCode == Activity.RESULT_OK) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 5;
                        bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                        croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,
                                bitmap.getWidth(), bitmap.getHeight() - 1);

                        String path = fileUri.getPath();
                        beginUpload(path);

                        if (!isDocsAdding) {
                            profilePic.setImageBitmap(croppedBmp);
                        }
                    }
                }
                break;
            }
            default:
                break;
        }

    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 2;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 200;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        File ff = saveBitmapToFile(file);

        current_time = String.valueOf(System.currentTimeMillis());
        String strPath = "communityhelper/" + common.getStringValue(Constants.userRwa) + "/" +current_time+ common.getStringValue(Constants.id)+".jpg";

        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, strPath,
                ff);

        Constants.linkOne = "https://s3-ap-southeast-1.amazonaws.com/pyntandroid/" + strPath + "";

        if (isDocsAdding) {
            listImages.add(ff);

            AddHelperImageListAdapter complainImageListAdapter = new AddHelperImageListAdapter(this, listImages);
            horizontalListView.setAdapter(complainImageListAdapter);

            Constants.linkOne = "https://s3-ap-southeast-1.amazonaws.com/pyntandroid/" + strPath + "";
            pathListOnAws.add(Constants.linkOne);

            horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (pathListOnAws.size() != 0) {
                        DialogFragment fragment = new ImageSlideShow();
                        Bundle bundle1 = new Bundle();
                        bundle1.putStringArrayList("imagelist", pathListOnAws);
                        bundle1.putInt("position", position);
                        fragment.setArguments(bundle1);
                        fragment.setRetainInstance(true);
                        fragment.show(getSupportFragmentManager(), "tag_delivery");
                    }
                }
            });

        } else {
            profilePicPath = "https://s3-ap-southeast-1.amazonaws.com/pyntandroid/" + strPath + "";
            Bundle logBundleProfilePic = new Bundle();
            logBundleProfilePic.putString("society_id", common.getStringValue(Constants.userRwa));
            logBundleProfilePic.putString("user_id", common.getStringValue(Constants.id));
            mFirebaseAnalytics.logEvent("community_helper_profile_pic", logBundleProfilePic);
        }
    }


    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(this, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return android.os.Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = this.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void AlertBoxForRemoveQr() {


        new AlertDialog.Builder(this)
                .setTitle("Please Wait")
                .setMessage("Want to remove qr code")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                qrCode = "";
                                qrTextbtn_TV.setText("Link this helper to QR");

                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

    }


}
