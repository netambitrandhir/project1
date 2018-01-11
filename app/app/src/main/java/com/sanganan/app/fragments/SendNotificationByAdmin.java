package com.sanganan.app.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.adapters.ComplainImageListAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.InsertToMangoDb;
import com.sanganan.app.customview.HorizontalListView;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.utility.Util;
import com.sanganan.app.utility.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class SendNotificationByAdmin extends Activity {
    TextView titletextView, titleText, descriptionText, addpicText, descriptionTextType, textCheckboxInfo, textCheckboxCritical, textCheckbox;
    ImageView addNext, sendnotifiction;
    EditText titleEditText, descriptionField;
    Typeface worksansR, karlaR, karlaB, ubuntuB;
    HorizontalListView listhorizon;
    Common common;
    String rwa;
    String catagoryId, titleStr, textStr, imageOne = "", severity = "1";
    String RWAID = "5";
    public static String status = "1";

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
    int positionAccordId = 1;
    private FirebaseAnalytics mFirebaseAnalytics;
    RadioButton checkboxInfo, checkboxCritical, checkbox;
    RadioGroup checkboxContainer;
    String notificationId = "";
    private String current_time = "";


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?


        try {
            RelativeLayout relativeLayout = (RelativeLayout) view;
            RadioButton rb = (RadioButton) relativeLayout.getChildAt(0);
            boolean checked = rb.isChecked();

            // Check which radio button was clicked
            switch (view.getId()) {
                case R.id.boxCheck1:
                    if (checked)
                        severity = "1";
                    checkboxInfo.setChecked(true);
                    checkboxCritical.setChecked(false);
                    checkbox.setChecked(false);
                    break;
                case R.id.boxCheck2:
                    if (checked)
                        severity = "2";
                    checkboxCritical.setChecked(true);
                    checkbox.setChecked(false);
                    checkboxInfo.setChecked(false);
                    break;

                case R.id.boxCheck3:
                    if (checked)
                        severity = "3";
                    checkbox.setChecked(true);
                    checkboxInfo.setChecked(false);
                    checkboxCritical.setChecked(false);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.send_notification_fragment);

        initializeView();


        imageFileList.clear();
        pathListOnAws.clear();


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("notification_admin_added", logBundleInitial);


        addNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.hideKeyboard(SendNotificationByAdmin.this);

                AlertBoxForImagePic();

            }
        });

        sendnotifiction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catagoryId = String.valueOf(positionAccordId + 1);
                titleStr = StringToBase64StringConvertion(titleEditText.getText().toString().trim());
                textStr = StringToBase64StringConvertion(descriptionField.getText().toString().trim());

               if(pathListOnAws.size()>0) {
                   for (int k = 0; k < pathListOnAws.size(); k++) {
                       imageOne = imageOne + pathListOnAws.get(k) + ",";
                   }
                   imageOne = imageOne.substring(0, imageOne.length() - 1);
               }

                if (!titleStr.isEmpty() && !textStr.isEmpty()) {
                    new AddNotificationsAsynTask().execute();
                } else {
                    common.showShortToast("Enter mandatory fields");
                }
            }
        });

        checkboxInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    severity = "1";
                    checkboxInfo.setChecked(true);
                    checkboxCritical.setChecked(false);
                    checkbox.setChecked(false);
                }
            }
        });
        checkboxCritical.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    severity = "2";
                    checkboxCritical.setChecked(true);
                    checkbox.setChecked(false);
                    checkboxInfo.setChecked(false);
                }
            }
        });
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    severity = "3";
                    checkbox.setChecked(true);
                    checkboxInfo.setChecked(false);
                    checkboxCritical.setChecked(false);
                }
            }
        });


        transferUtility = Util.getTransferUtility(this);

    }


    private void initializeView() {

        karlaB = Typeface.createFromAsset(this.getAssets(), "Karla-Bold.ttf");
        ubuntuB = Typeface.createFromAsset(this.getAssets(), "Ubuntu-B.ttf");
        karlaR = Typeface.createFromAsset(this.getAssets(), "Karla-Regular.ttf");
        worksansR = Typeface.createFromAsset(this.getAssets(), "WorkSans-Regular.ttf");

        titletextView = (TextView) findViewById(R.id.titletextView);
        titleText = (TextView) findViewById(R.id.titleText);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        addpicText = (TextView) findViewById(R.id.addpicText);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        descriptionField = (EditText) findViewById(R.id.descriptionField);
        addNext = (ImageView) findViewById(R.id.addNext);
        sendnotifiction = (ImageView) findViewById(R.id.sendnotifiction);
        listhorizon = (HorizontalListView) findViewById(R.id.listhorizontal);


        descriptionTextType = (TextView) findViewById(R.id.descriptionTextType);
        textCheckboxInfo = (TextView) findViewById(R.id.textCheckboxInfo);
        textCheckboxCritical = (TextView) findViewById(R.id.textCheckboxCritical);
        textCheckbox = (TextView) findViewById(R.id.textCheckbox);


        checkboxInfo = (RadioButton) findViewById(R.id.checkboxInfo);
        checkboxCritical = (RadioButton) findViewById(R.id.checkboxCritical);
        checkbox = (RadioButton) findViewById(R.id.checkbox);
        checkboxContainer = (RadioGroup) findViewById(R.id.checkboxContainer);


        common = Common.getNewInstance(this);

        titletextView.setTypeface(ubuntuB);
        titleText.setTypeface(karlaB);
        descriptionText.setTypeface(karlaB);
        descriptionTextType.setTypeface(karlaB);
        textCheckbox.setTypeface(karlaB);
        textCheckboxInfo.setTypeface(karlaB);
        textCheckboxCritical.setTypeface(karlaB);
        addpicText.setTypeface(karlaB);
        titleEditText.setTypeface(worksansR);
        descriptionField.setTypeface(worksansR);
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

    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp).toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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

                       String path = getImageUrlWithAuthority(this,uri);
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

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        final File ff = saveBitmapToFile(file);

        current_time = String.valueOf(System.currentTimeMillis());
        final String strPath = "notifications/" + common.getStringValue(Constants.userRwa) + "/" + current_time + common.getStringValue(Constants.id) + ".jpg";


        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, strPath,
                ff);


        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (state == TransferState.COMPLETED) {
                    saveAndShowImagesToUI(strPath, ff);
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {

            }
        });

    }

    void saveAndShowImagesToUI(String strPath, File ff) {


        Constants.linkOne = "https://s3-ap-southeast-1.amazonaws.com/pyntandroid/" + strPath + "";

        imageFileList.add(ff);
        pathListOnAws.add(Constants.linkOne);

        ComplainImageListAdapter addnotificationAdapter = new ComplainImageListAdapter(this, imageFileList, "Send");
        listhorizon.setAdapter(addnotificationAdapter);

        Bundle logBundleUploadDocs = new Bundle();
        logBundleUploadDocs.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleUploadDocs.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("notification_admin_photo_added", logBundleUploadDocs);
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

    private class AddNotificationsAsynTask extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        String message;

        @Override
        protected String doInBackground(Void... voids) {

            rwa = common.getStringValue("ID");
            //check for userId login then provide below value to jsonRaw "CreatedBy" key - value
            String createdBy = common.getStringValue(Constants.id);

            String jsonRaw = "{\n" +
                    "\"RWAID\":\"" + rwa + "\",\n" +
                    "\"Severity\":\"" + severity + "\",\n" +
                    "\"Title\":\"" + titleStr + "\",\n" +
                    "\"Text\":\"" + textStr + "\",\n" +
                    "\"image\":\"" + imageOne + "\",\n" +
                    "\"CreatedBy\":\"" + common.getStringValue(Constants.id) + "\",\n" +
                    "\"Status\":\"0\"\n" +
                    "}";
            String stringResponse = null;
            String status = null;


            try {
                HttpResponse searchResponse = Utility.postDataOnUrl(Constants.BaseUrl + "addnotification", jsonRaw);
                stringResponse = EntityUtils.toString(searchResponse.getEntity());
                JSONObject jobj = new JSONObject(stringResponse);
                message = jobj.getString("Message");
                status = jobj.getString("Status");
                notificationId = jobj.optString("notificationId");

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
            dialog = new ProgressDialog(SendNotificationByAdmin.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading.....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (s != null) {
                if (s.equalsIgnoreCase("1")) {
                    common.showShortToast(message);
                    String imagePath = "";
                    if(pathListOnAws.size()>0) {
                        for (int l = 0; l < pathListOnAws.size(); l++) {
                            imagePath = imagePath + pathListOnAws.get(l) + ",";
                        }
                        imagePath = imagePath.substring(0, imagePath.length() - 1);
                    }
                    InsertToMangoDb insertToMangoDb = new InsertToMangoDb(SendNotificationByAdmin.this, "notification", notificationId, descriptionField.getText().toString(), imagePath);
                    insertToMangoDb.insertDataToServer();

                    onBackPressed();
                } else {
                    common.showShortToast("fill useful details");
                }
            } else {
                common.showShortToast("No internet...!!");
            }
        }

    }

    public String StringToBase64StringConvertion(String text) {
        String base64Result = "";
        if (text != null) {
            byte[] data = new byte[0];
            try {
                data = text.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            base64Result = Base64.encodeToString(data, Base64.NO_WRAP);
        }
        return base64Result;
    }
}