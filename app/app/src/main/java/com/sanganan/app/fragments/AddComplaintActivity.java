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
import android.os.AsyncTask;
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
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.adapters.ComplainImageListAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.InsertToMangoDb;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.customview.HorizontalListView;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.utility.Util;
import com.sanganan.app.utility.Utility;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by root on 6/4/17.
 */

public class AddComplaintActivity extends ActionBarActivity {

    Typeface karlaB, ubuntuB, karlaR, worksansR;
    HorizontalListView listhorizontal;
    Common comonObj;

    TextView textCheckbox, textadd_anyuptoThree, complainreason, complainRegarding, complainDetails, title;
    ImageView submit, addButton;
    String rwaId, catagoryId, complainDetailsStr, imageOne, imageTwo, imageThree;
    CheckBox checkBox;
    String assignedByUser = "";

    private Bitmap bitmap;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int REQUEST_CODE_Gallery = 0;
    private static final String IMAGE_DIRECTORY_NAME = "Nukkad";
    private static final String TAG = "UploadActivity";
    private TransferUtility transferUtility;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    static String profilepic;
    static String timeStamp;
    Bitmap croppedBmp;
    private Uri fileUri;
    public static ArrayList<File> imageFileList = new ArrayList<>();
    public static ArrayList<String> pathListOnAws = new ArrayList<>();
    int isCommonIndicator = 0;
    EditText regarding, comDetail;

    private String[] countries_list;
    private String[] category_list;
    int positionAccordId = -10;

    RequestQueue requestQueue;
    ArrayAdapter<String> spinner_countries;
    RelativeLayout checkBoxPublicComplain;

    //No_Public_Complaint

    String permissionArray[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};
    private FirebaseAnalytics mFirebaseAnalytics;
    private String complaintId = "";
    private String current_time ="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcomplain);
        initializeVariables();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        imageFileList.clear();
        pathListOnAws.clear();

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", comonObj.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", comonObj.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("complaint_to_add", logBundleInitial);

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        getIssueList();

        if (Constants.rolesGivenToUser.contains("No_Public_Complaint")) {
            checkBoxPublicComplain.setVisibility(View.GONE);
        } else {
            checkBoxPublicComplain.setVisibility(View.VISIBLE);
        }


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comonObj.hideKeyboard(AddComplaintActivity.this);
                doYourStuffToCHeckPermissionAddingDocs();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    isCommonIndicator = 1;
                } else {
                    isCommonIndicator = 0;
                }
            }
        });


        regarding.setInputType(InputType.TYPE_NULL); //To hide the softkeyboard


        regarding.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new AlertDialog.Builder(AddComplaintActivity.this)
                        .setTitle("Select Category")
                        .setAdapter(spinner_countries, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                regarding.setText(countries_list[which].toString());
                                positionAccordId = which;
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rwaId = "5";
                complainDetailsStr = comDetail.getText().toString();


                if (pathListOnAws.size() > 0) imageOne = pathListOnAws.get(0);
                comonObj.setStringValue("imagelink1", imageOne);
                if (pathListOnAws.size() > 1) imageTwo = pathListOnAws.get(1);
                comonObj.setStringValue("imagelink2", imageTwo);
                if (pathListOnAws.size() > 2) imageThree = pathListOnAws.get(2);
                comonObj.setStringValue("imagelink3", imageThree);
                if (positionAccordId != -10 && !complainDetailsStr.isEmpty()) {
                    assignedByUser = comonObj.getStringValue(Constants.id);
                    catagoryId = category_list[positionAccordId];
                    complainDetailsStr = comonObj.StringToBase64StringConvertion(complainDetailsStr);
                    comonObj.setStringValue("complaindetail", complainDetailsStr);

                    new AddComplainAsynTask().execute();
                } else {
                    comonObj.showShortToast("Enter mandatory fields");
                }
            }
        });

        transferUtility = Util.getTransferUtility(this);
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
            if (imageFileList.size() < 3) {
                AlertBoxForImagePic();
            } else {
                comonObj.showShortToast("max three image...!!");
            }
        }
    }

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
                    comonObj.showShortToast("max three image...!!");
                }
            } else {
                doYourStuffToCHeckPermissionAddingDocs();
                comonObj.showShortToast("You need to give all permissions");
            }
        }


    }


    private void initializeVariables() {

        comonObj = Common.getNewInstance(this);

        karlaB = Typeface.createFromAsset(this.getAssets(), "Karla-Bold.ttf");
        ubuntuB = Typeface.createFromAsset(this.getAssets(), "Ubuntu-B.ttf");
        karlaR = Typeface.createFromAsset(this.getAssets(), "Karla-Regular.ttf");
        worksansR = Typeface.createFromAsset(this.getAssets(), "WorkSans-Regular.ttf");

        imageFileList.clear();


        checkBoxPublicComplain = (RelativeLayout) findViewById(R.id.checkBoxPublicComplain);

        title = (TextView) findViewById(R.id.titletextView);
        title.setTypeface(ubuntuB);

        checkBox = (CheckBox) findViewById(R.id.checkbox);
        regarding = (EditText) findViewById(R.id.EditText);
        regarding.setTypeface(worksansR);
        comDetail = (EditText) findViewById(R.id.pswdEditText);
        comDetail.setTypeface(worksansR);
        comDetail.setMovementMethod(new ScrollingMovementMethod());
        submit = (ImageView) findViewById(R.id.submit);
        textCheckbox = (TextView) findViewById(R.id.textCheckbox);
        textCheckbox.setTypeface(karlaB);
        textadd_anyuptoThree = (TextView) findViewById(R.id.textadd_anyuptoThree);
        textadd_anyuptoThree.setTypeface(karlaB);
        complainreason = (TextView) findViewById(R.id.textView1);
        complainreason.setTypeface(karlaB);
        complainRegarding = (TextView) findViewById(R.id.textView2);
        complainRegarding.setTypeface(karlaR);
        complainDetails = (TextView) findViewById(R.id.textView3);
        complainDetails.setTypeface(karlaB);

        addButton = (ImageView) findViewById(R.id.addButton);

        listhorizontal = (HorizontalListView) findViewById(R.id.listhorizontal);
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
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        File ff = saveBitmapToFile(file);

        current_time = String.valueOf(System.currentTimeMillis());
        String strPath = "complaints/" + comonObj.getStringValue(Constants.userRwa) + "/" +current_time+ comonObj.getStringValue(Constants.id)+".jpg";

        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, strPath,
                ff);

        Constants.linkOne = "https://s3-ap-southeast-1.amazonaws.com/pyntandroid/" + strPath + "";

        imageFileList.add(ff);
        pathListOnAws.add(Constants.linkOne);

        ComplainImageListAdapter complainImageListAdapter = new ComplainImageListAdapter(this, imageFileList, "Add");
        listhorizontal.setAdapter(complainImageListAdapter);


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


    private class AddComplainAsynTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {
            String rwa = comonObj.getStringValue("ID");

            String jsonRaw = "{\n" +
                    "\"RWAID\":\"" + rwa + "\",\n" +
                    "\"CategoryID\":\"" + catagoryId + "\",\n" +
                    "\"RWAResidentFlatID\":\"" + comonObj.getStringValue(Constants.flatId) + "\",\n" +
                    "\"ComplaintDetails\":\"" + complainDetailsStr + "\",\n" +
                    "\"IsCommonAreaComplaint\":\"" + String.valueOf(isCommonIndicator) + "\",\n" +
                    "\"Image1\":\"" + imageOne + "\",\n" +
                    "\"Image2\":\"" + imageTwo + "\",\n" +
                    "\"Image3\":\"" + imageThree + "\",\n" +
                    "\"Status\":\"0\",\n" +
                    "\"AssignedBy\":\"" + assignedByUser + "\",\n" +
                    "\"FlatNbr\":\"" + comonObj.getStringValue(Constants.flatId) + "\"\n" +
                    "}";

            String stringResponse = null;

            String status = null;

            try {
                HttpResponse searchResponse = Utility.postDataOnUrl(
                        Constants.BaseUrl + "addcomplain", jsonRaw);
                stringResponse = EntityUtils.toString(searchResponse.getEntity());
                JSONObject jobj = new JSONObject(stringResponse);

                status = jobj.getString("Status");
                if (status.equals("1")) {
                    complaintId = jobj.optString("complainId");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            comonObj.showSpinner(AddComplaintActivity.this);

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            comonObj.hideSpinner();

            if (s != null) {
                if (s.equalsIgnoreCase("1")) {
                    if (isCommonIndicator == 1) {
                        Constants.isCommonAreaComplain = "1";
                    } else {
                        Constants.isCommonAreaComplain = "0";
                    }
                    Constants.statusComplain = "0";
                    Constants.isComplainStatusChangedOrEdited = true;
                    Bundle logBundleComplainSuccess = new Bundle();
                    logBundleComplainSuccess.putString("society_id", comonObj.getStringValue(Constants.userRwa));
                    logBundleComplainSuccess.putString("user_id", comonObj.getStringValue(Constants.id));
                    logBundleComplainSuccess.putString("image_count", "" + pathListOnAws.size() + "");
                    mFirebaseAnalytics.logEvent("complaint_to_add_success", logBundleComplainSuccess);

                    if (isCommonIndicator == 1) {
                        String imagePath = "";
                        if (pathListOnAws.size() > 0) {
                            for (int l = 0; l < pathListOnAws.size(); l++) {
                                imagePath = imagePath + pathListOnAws.get(l) + ",";
                            }
                            imagePath = imagePath.substring(0, imagePath.length() - 1);
                        }
                        InsertToMangoDb insertToMangoDb = new InsertToMangoDb(AddComplaintActivity.this, "complain", complaintId, comDetail.getText().toString(), imagePath);
                        insertToMangoDb.insertDataToServer();
                        Constants.isAnyNewPostAdded = true;
                    }

                    finish();
                } else {
                    comonObj.showShortToast("fill useful details");
                }
            } else {
                comonObj.showShortToast("No internet...!!");
            }
        }

    }


    private void getIssueList() {

        comonObj.showSpinner(AddComplaintActivity.this);

        /*http://52.91.49.255/Nukkad/nukadmanager.php/nukad/complaincategories?Id=7*/

        String uri = Constants.BaseUrl + "complaincategories?Id=" + comonObj.getStringValue(Constants.userRwa);
        try {

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, uri, new JSONObject(),
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

        try {
            JSONArray array = json.getJSONArray("list");
            countries_list = new String[array.length()];
            category_list = new String[array.length()];

            for (int i = 0; i < array.length(); i++) {

                JSONObject jsonObject = array.getJSONObject(i);
                String str = jsonObject.optString("Description");
                String categoryId = jsonObject.optString("ID");
                countries_list[i] = str;
                category_list[i] = categoryId;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        comonObj.hideSpinner();
        spinner_countries = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, countries_list);
        comonObj.hideKeyboard(this);

    }

}
