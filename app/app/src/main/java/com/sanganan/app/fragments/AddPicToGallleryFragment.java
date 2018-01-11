package com.sanganan.app.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
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
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.InsertToMangoDb;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.utility.Util;
import com.squareup.picasso.Picasso;

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
 * Created by root on 10/10/16.
 */

public class AddPicToGallleryFragment extends BaseFragment {

    View view;
    EditText caption;
    ImageView addPictureLocation, done, saveAndAddMore;


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
    Typeface ubuntuB, wsRegular;
    Common common;
    RequestQueue requestQueue;
    String captionText;
    boolean morePicToAdd;
    File ff;

    String current_time = "";


    String permissionArray[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};


    @TargetApi(Build.VERSION_CODES.M)
    private void doYourStuffToCHeckPermissionForImageAdd() {

        int j = 1;
        ArrayList<String> listPermissionDenied = new ArrayList<String>();
        for (int i = 0; i < permissionArray.length; i++) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permissionArray[i]) != PackageManager.PERMISSION_GRANTED) {

                listPermissionDenied.add(permissionArray[i]);
                j = j * 0;

            }
        }
        if (j == 0) {
            String array[] = new String[listPermissionDenied.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = listPermissionDenied.get(i);
            }
            getActivity().requestPermissions(array, 4);
        } else {
            AlertBoxForImagePic();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 2) {
            int k = 1;
            for (int i = 0; i < grantResults.length; i++) {
                k = k * grantResults[i];
            }
            if (k != 0) {
                AlertBoxForImagePic();
            } else {
                doYourStuffToCHeckPermissionForImageAdd();
                common.showShortToast("You need to give all permissions");
            }
        }
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.addpic_gallery_layout, container, false);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        intializeView(view);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("gallery_add_new_pic", logBundleInitial);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePicToAdd = false;
                captionText = caption.getText().toString();
                if (!localPicLink.isEmpty()) {
                    getData();
                } else {
                    Toast.makeText(getActivity(), "Enter mandatory fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveAndAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePicToAdd = true;
                captionText = caption.getText().toString();
                if (!captionText.isEmpty() && !localPicLink.isEmpty()) {
                    getData();
                } else {
                    Toast.makeText(getActivity(), "Enter mandatory fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addPictureLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                doYourStuffToCHeckPermissionForImageAdd();
            }
        });
        return view;
    }


    void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void intializeView(View view) {

        common = Common.getNewInstance(getActivity());

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        wsRegular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");

        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        transferUtility = Util.getTransferUtility(getActivity());

        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setTypeface(ubuntuB);
        caption = (EditText) view.findViewById(R.id.caption_add_pic);
        caption.setTypeface(wsRegular);
        addPictureLocation = (ImageView) view.findViewById(R.id.imageAdded);
        done = (ImageView) view.findViewById(R.id.doneButton);
        saveAndAddMore = (ImageView) view.findViewById(R.id.saveAndAddMoreButton);


    }

    private void getData() {

        common.showSpinner(getActivity());

        String uri = Constants.BaseUrl + "addphotogallery";
        try {
            JSONObject json = new JSONObject();
            json.put("PhotoPath", localPicLink);
            json.put("PhotoCaption", common.StringToBase64StringConvertion(captionText));
            json.put("ResidentID", common.getStringValue(Constants.id));
            json.put("RWAID", common.getStringValue("ID"));


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
        String addedBy = "";
        String photoId = "";
        try {
            status = json.optString("Status");
            message = json.optString("Message");

            if (status.equals("1")) {
                photoId = json.optString("PhotoGallery");
                InsertToMangoDb insertToMangoDb = new InsertToMangoDb(getActivity(), "photos", photoId, captionText, localPicLink);
                insertToMangoDb.insertDataToServer();
            }
           /* JSONArray array = json.getJSONArray("");
            JSONObject object = array.getJSONObject(0);
            addedBy = object.getString("ResidentRWAID");*/


        } catch (Exception e) {

        }

        common.hideSpinner();
        if (status.equalsIgnoreCase("1")) {
            common.showShortToast(message);
            Constants.isAnyNewPostAdded = true;
            Constants.isAnyPicAddedOrRemoved = true;
            Constants.positionListEdited = 0;
        } else {
            common.showShortToast("No internet...!!");
        }
        if (!morePicToAdd) {
            getActivity().onBackPressed();

        } else {

            Picasso.with(getActivity()).load(R.drawable.galleryplacholder).into(addPictureLocation);
            caption.setText("");
            localPicLink = "";

        }
        common.showShortToast(message);
    }


    public void AlertBoxForImagePic() {


        new AlertDialog.Builder(getActivity())
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
                        Toast.makeText(getActivity(),
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
            Toast.makeText(getActivity(), "Could not find the filepath of the selected file",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        ff = saveBitmapToFile(file);

        current_time = String.valueOf(System.currentTimeMillis());
        String strPath = "gallery/" + common.getStringValue(Constants.userRwa) + "/" +current_time+ common.getStringValue(Constants.id)+".jpg";

        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, strPath,
                ff);

        localPicLink = "https://s3-ap-southeast-1.amazonaws.com/pyntandroid/" + strPath + "";
        Picasso.with(getActivity()).load(ff).into(addPictureLocation);


    }

    String localPicLink = "";

    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getActivity(), uri)) {
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
                cursor = getActivity().getContentResolver()
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
