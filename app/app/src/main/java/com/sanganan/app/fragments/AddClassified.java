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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sanganan.app.adapters.ComplainImageListAdapter;
import com.sanganan.app.adapters.ListPollAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.InsertToMangoDb;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.customview.HorizontalListView;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.Poll;
import com.sanganan.app.utility.Util;

import org.json.JSONArray;
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
 * Created by pranav on 30/11/16.
 */

public class AddClassified extends ActionBarActivity {

    Common common;
    RequestQueue requestQueue;
    Typeface ubuntuB, karlaB, karlaR;
    TextView postingTitle, foutyChar, postingDetails, addpictext;
    EditText editpostingTitle, editpostingDetails;
    ImageView addpicBtn, submit;
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
    public static ArrayList<File> imageFileList = new ArrayList<>();
    public static ArrayList<String> pathListOnAws = new ArrayList<>();
    HorizontalListView listhorizontal;
    String imageOne, imageTwo, imageThree, cTitle, cDetail;

    private FirebaseAnalytics mFirebaseAnalytics;
    private String current_time = "";

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_classified_layout);
        common = Common.getNewInstance(AddClassified.this);
        requestQueue = VolleySingleton.getInstance(AddClassified.this).getRequestQueue();

        ubuntuB = Typeface.createFromAsset(AddClassified.this.getAssets(), "Ubuntu-B.ttf");
        karlaB = Typeface.createFromAsset(AddClassified.this.getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(AddClassified.this.getAssets(), "Karla-Regular.ttf");
        final TextView title = (TextView) findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);

        initializeVariable();
        pathListOnAws.clear();
        imageFileList.clear();


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(AddClassified.this);

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("classified_add_new", logBundleInitial);


        addpicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doYourStuffToCHeckPermissionAddingDocs();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cTitle = editpostingTitle.getText().toString();
                cDetail = editpostingDetails.getText().toString();

                if (pathListOnAws.size() > 0) imageOne = pathListOnAws.get(0);
                common.setStringValue("imagelink1", imageOne);
                if (pathListOnAws.size() > 1) imageTwo = pathListOnAws.get(1);
                common.setStringValue("imagelink2", imageTwo);
                if (pathListOnAws.size() > 2) imageThree = pathListOnAws.get(2);
                common.setStringValue("imagelink3", imageThree);
                if (!cTitle.isEmpty() && !cDetail.isEmpty()) {
                    getData();
                } else {
                    common.showShortToast("Fill all mandatory fields");
                }
            }
        });

        transferUtility = Util.getTransferUtility(AddClassified.this);

    }


    private void getData() {

        common.showSpinner(AddClassified.this);

        String uri = Constants.BaseUrl + "addclassified";
        try {
            JSONObject json = new JSONObject();
            json.put("Title", common.StringToBase64StringConvertion(cTitle));
            json.put("Description", common.StringToBase64StringConvertion(cDetail));
            json.put("Image1", imageOne);
            json.put("Image2", imageTwo);
            json.put("Image3", imageThree);
            json.put("AddedBy", common.getStringValue(Constants.flatId));


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response);

                            } catch (Exception e) {
                                e.printStackTrace();
                                common.hideSpinner();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());
                            common.hideSpinner();

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
                common.hideSpinner();
            }
        } catch (Exception e) {
            e.printStackTrace();
            common.hideSpinner();

        }
    }

    void parsedData(JSONObject json) {
        String status = "";
        String message = "";
        String classifiedId = "";
        common.hideSpinner();
        try {

            status = json.optString("Status");
            message = json.optString("Message");


        } catch (Exception e) {

        }


        common.showShortToast(message);

        if (status.equalsIgnoreCase("1")) {
            classifiedId = json.optString("ClassifiedId");

            String imagePath = "";

            if (pathListOnAws.size() > 0) {
                for (int j = 0; j < pathListOnAws.size(); j++) {
                    imagePath = imagePath + pathListOnAws.get(j) + ",";
                }
                imagePath = imagePath.substring(0, imagePath.length() - 1);
            }

            InsertToMangoDb insertToMangoDb = new InsertToMangoDb(AddClassified.this, "classified", classifiedId, cDetail, imagePath);
            insertToMangoDb.insertDataToServer();
            
            Constants.isAnyNewPostAdded = true;
            Constants.isAnyClassifiedCreatedOrRemoved = true;
            Bundle logBundleOnSuccess = new Bundle();
            logBundleOnSuccess.putString("society_id", common.getStringValue(Constants.userRwa));
            logBundleOnSuccess.putString("user_id", common.getStringValue(Constants.id));
            mFirebaseAnalytics.logEvent("classified_add_new_success", logBundleOnSuccess);
        }
        onBackPressed();
    }

    private void initializeVariable() {


        postingTitle = (TextView) findViewById(R.id.postingTitle);
        postingTitle.setTypeface(karlaB);
        foutyChar = (TextView) findViewById(R.id.foutyChar);
        foutyChar.setTypeface(karlaR);
        postingDetails = (TextView) findViewById(R.id.postingDetails);
        postingDetails.setTypeface(karlaB);
        addpictext = (TextView) findViewById(R.id.addpictext);
        addpictext.setTypeface(karlaB);
        editpostingTitle = (EditText) findViewById(R.id.editpostingTitle);
        editpostingDetails = (EditText) findViewById(R.id.editpostingDetails);
        addpicBtn = (ImageView) findViewById(R.id.addpicBtn);
        submit = (ImageView) findViewById(R.id.submit);
        listhorizontal = (HorizontalListView) findViewById(R.id.listhorizontal);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void doYourStuffToCHeckPermissionAddingDocs() {

        int j = 1;
        ArrayList<String> listPermissionDenied = new ArrayList<String>();
        for (int i = 0; i < permissionArray.length; i++) {
            if (ActivityCompat.checkSelfPermission(AddClassified.this, permissionArray[i]) != PackageManager.PERMISSION_GRANTED) {

                listPermissionDenied.add(permissionArray[i]);
                j = j * 0;

            }
        }
        if (j == 0) {
            String array[] = new String[listPermissionDenied.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = listPermissionDenied.get(i);
            }
            AddClassified.this.requestPermissions(array, 4);
        } else {
            if (imageFileList.size() < 3) {
                AlertBoxForImagePic();
            } else {
                common.showShortToast("max three image...!!");
            }
        }
    }

    String permissionArray[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 4) {
            int k = 1;
            int l = 0;
            for (int i = 0; i < grantResults.length; i++) {
                k = k * grantResults[i];
                l = l + grantResults[i];
            }
            if (k != 0 && k != -1 && l == permissionArray.length) {
                if (imageFileList.size() < 3) {
                    AlertBoxForImagePic();
                } else {
                    common.showShortToast("max three image...!!");
                }
            } else {
                doYourStuffToCHeckPermissionAddingDocs();
                common.showShortToast("You need to give all permissions");
            }
        }
    }

    public void AlertBoxForImagePic() {


        new AlertDialog.Builder(AddClassified.this)
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
                        Toast.makeText(AddClassified.this,
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
            Toast.makeText(AddClassified.this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        File ff = saveBitmapToFile(file);


        current_time = String.valueOf(System.currentTimeMillis());
        String strPath = "classifieds/" + common.getStringValue(Constants.userRwa) + "/" + current_time + common.getStringValue(Constants.id) + ".jpg";

        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, strPath,
                ff);

        Constants.linkOne = "https://s3-ap-southeast-1.amazonaws.com/pyntandroid/" + strPath + "";

        imageFileList.add(ff);
        pathListOnAws.add(Constants.linkOne);

        ComplainImageListAdapter complainImageListAdapter = new ComplainImageListAdapter(AddClassified.this, imageFileList, "addClass");
        listhorizontal.setAdapter(complainImageListAdapter);

    }


    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(AddClassified.this, uri)) {
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
                cursor = AddClassified.this.getContentResolver()
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
}
