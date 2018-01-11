package com.sanganan.app.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import com.google.gson.JsonArray;
import com.sanganan.app.R;
import com.sanganan.app.activities.MainHomePageActivity;
import com.sanganan.app.adapters.WheelerAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.WheelerModel;
import com.sanganan.app.model.YourNeighbourSearch;
import com.sanganan.app.utility.Util;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupSecondPage extends ActionBarActivity {

    Common common;
    RequestQueue requestQueue;
    ImageView profilePicture;
    TextView society_selected, societyName, vehicleno, professiontext, aboutyoutext, wheelertext, wheelerTypeTv;
    EditText vehicleId, profession, aboutCandidate;
    ImageView skipButton, doneButton;
    private Bitmap bitmap;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int REQUEST_CODE_Gallery = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    boolean isNewPicUploaded = false;
    Bitmap croppedBmp;
    private Uri fileUri; // file url to store image/video
    String createprofilepiclink = "";
    private static final String IMAGE_DIRECTORY_NAME = "mynukad";
    private static final String TAG = "UploadActivity";
    private SimpleAdapter simpleAdapter;
    private TransferUtility transferUtility;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    static String timeStamp;
    String s = Constants.AWSLINK;
    SharedPreferences spf;
    String userId, pickLink;
    Spinner spinner;
    TextView wheelerTextView;
    ImageView wheelerAdd;
    String[] wheelerType;
    String selectedSpinnerItem, wheelerNo;
    TextView vehicleType;
    ListView aadedVehicleList;
    ArrayList<WheelerModel> list = new ArrayList<>();
    static String profilepic;

    WheelerAdapter wheelerAdapter;
    Typeface karlaB, karlaR, semiB, wsRegular;
    int done = 0;

    SharedPreferences.Editor editor;
    Bundle bundle;
    private static final int REQUEST_CROP_CAMERA = 1010;
    private static final int REQUEST_CROP_GALLERY = 1011;
    Uri galCroppedUri;
    Uri camCroppedUri;
    private String current_time="";


    public static class AlphaNumericInputFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            // Only keep characters that are alphanumeric
            StringBuilder builder = new StringBuilder();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (Character.isLetterOrDigit(c)|Character.isWhitespace(c)) {
                    builder.append(c);
                }
            }

            // If all characters are valid, return null, otherwise only return the filtered characters
            boolean allCharactersValid = (builder.length() == end - start);
            return allCharactersValid ? null : builder.toString();
        }
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
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

     /*   mediaFile=new File(s+mediaStorageDir.getPath() + File.separator
                + timeStamp + ".jpg");

*/
        return mediaFile;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.almost_done);
        common = new Common(SignupSecondPage.this);
        bundle = getIntent().getExtras();

        Typeface ubuntuB = Typeface.createFromAsset(SignupSecondPage.this.getAssets(), "Ubuntu-B.ttf");

        TextView title = (TextView) findViewById(R.id.titletextView);
        title.setText("We are almost done!!");
        title.setTypeface(ubuntuB);
        TextView vehicleMessageText = (TextView) findViewById(R.id.vehicleMessageText);
        TextView professionMessageText = (TextView) findViewById(R.id.professionMessageText);
        vehicleMessageText.setTypeface(karlaR);
        professionMessageText.setTypeface(karlaR);

        requestQueue = VolleySingleton.getInstance(SignupSecondPage.this).getRequestQueue();

        karlaB = Typeface.createFromAsset(SignupSecondPage.this.getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(SignupSecondPage.this.getAssets(), "Karla-Regular.ttf");
        semiB = Typeface.createFromAsset(SignupSecondPage.this.getAssets(), "WorkSans-SemiBold.ttf");


        aadedVehicleList = (ListView) findViewById(R.id.aadedVehicleList);

        society_selected = (TextView) findViewById(R.id.society_selected);
        society_selected.setTypeface(karlaB);
        societyName = (TextView) findViewById(R.id.societyName);
        societyName.setTypeface(karlaR);
        vehicleno = (TextView) findViewById(R.id.vehicleno);
        vehicleno.setTypeface(karlaB);

        professiontext = (TextView) findViewById(R.id.professiontext);
        professiontext.setTypeface(karlaB);
        aboutyoutext = (TextView) findViewById(R.id.aboutyoutext);
        aboutyoutext.setTypeface(karlaB);

        wheelertext = (TextView) findViewById(R.id.wheelertext);
        wheelertext.setTypeface(semiB);

        wheelerTypeTv = (TextView) findViewById(R.id.wheelerType);
        wheelerTypeTv.setTypeface(karlaB);

        societyName.setText(common.getStringValue("SocietyName"));
        profilePicture = (ImageView) findViewById(R.id.profilePic);
        vehicleId = (EditText) findViewById(R.id.vehicleNo);

        ArrayList<InputFilter> curInputFilters = new ArrayList<InputFilter>(Arrays.asList(vehicleId.getFilters()));
        curInputFilters.add(0, new AddVehicleDialog.AlphaNumericInputFilter());
        curInputFilters.add(1, new InputFilter.AllCaps());
        InputFilter[] newInputFilters = curInputFilters.toArray(new InputFilter[curInputFilters.size()]);
        vehicleId.setFilters(newInputFilters);

        profession = (EditText) findViewById(R.id.professionField);
        aboutCandidate = (EditText) findViewById(R.id.aboutYouField);
        vehicleId.setTypeface(wsRegular);
        profession.setTypeface(wsRegular);
        aboutCandidate.setTypeface(wsRegular);
        skipButton = (ImageView) findViewById(R.id.skip);
        doneButton = (ImageView) findViewById(R.id.done);
        spinner = (Spinner) findViewById(R.id.wheeler);
        wheelerAdd = (ImageView) findViewById(R.id.wheelerAdd);


        wheelerType = new String[]{"2", "3", "4", "6"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignupSecondPage.this, R.layout.simple_spinner_view, wheelerType);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpinnerItem = spinner.getSelectedItem().toString();
                wheelerTypeTv.setText(selectedSpinnerItem);
                ((TextView) view).setText(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        wheelerAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WheelerModel model = new WheelerModel();
                wheelerNo = vehicleId.getText().toString();
                model.setVehicleNumber(wheelerNo);
                model.setVehicheType(selectedSpinnerItem);

                if (!wheelerNo.isEmpty() && !selectedSpinnerItem.isEmpty()) {
                    list.add(model);
                    if (list.size() == 0) {
                        aadedVehicleList.setVisibility(View.GONE);

                    } else {
                        wheelerAdapter = new WheelerAdapter(SignupSecondPage.this, list);
                        aadedVehicleList.setVisibility(View.VISIBLE);
                        aadedVehicleList.setAdapter(wheelerAdapter);
                        setListViewHeightBasedOnItems(aadedVehicleList);
                    }
                } else {
                    common.showShortToast("enter both fields");
                }

            }
        });


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (common.isNetworkAvailable()) {
                    if (!createprofilepiclink.isEmpty() || !aboutCandidate.getText().toString().isEmpty() || !profession.getText().toString().isEmpty() || list.size() != 0) {
                        done = 1;
                        callApi();
                    } else {
                        common.showShortToast("If nothing to add then skip .. ");
                    }
                } else {
                    common.showShortToast("no internet");
                }

            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (common.isNetworkAvailable()) {
                    done = 0;
                    callApi();
                } else {
                    common.showShortToast("no internet");
                }
            }
        });
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doYourStuffToCHeckPermissionAddingDocs();
            }
        });


        transferUtility = Util.getTransferUtility(SignupSecondPage.this);

    }

    String permissionArray[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};


    @TargetApi(Build.VERSION_CODES.M)
    private void doYourStuffToCHeckPermissionAddingDocs() {

        int j = 1;
        ArrayList<String> listPermissionDenied = new ArrayList<String>();
        for (int i = 0; i < permissionArray.length; i++) {
            if (ActivityCompat.checkSelfPermission(SignupSecondPage.this, permissionArray[i]) != PackageManager.PERMISSION_GRANTED) {

                listPermissionDenied.add(permissionArray[i]);
                j = j * 0;

            }
        }
        if (j == 0) {
            String array[] = new String[listPermissionDenied.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = listPermissionDenied.get(i);
            }
            SignupSecondPage.this.requestPermissions(array, 4);
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
                doYourStuffToCHeckPermissionAddingDocs();
                common.showShortToast("You need to give all permissions");
            }
        }
    }

    ProgressDialog progressDialog;

    private void callApi() {

        progressDialog = new ProgressDialog(SignupSecondPage.this);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setMessage("Registration in progress...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "registrationnew";
        try {

            String numberW = "";
            String typeW = "";


            JSONObject json = new JSONObject();
            json.put("PhoneNbr", bundle.getString("phonenumberKey"));
            json.put("EmailID", bundle.getString("emailidKey"));
            json.put("Password", bundle.getString("passwordKey"));
            json.put("FirstName", bundle.getString("fullnameKey"));
            json.put("Gender", "Male");
            json.put("RWAID", common.getStringValue("ID"));
            json.put("FlatNbr", bundle.getString("flatnumberKey"));
            json.put("IsOwner", bundle.getString("residentType"));

            if (done == 1) {
              /*  if (list.size() != 0) {

                    for (int i = 0; i < list.size(); i++) {
                        numberW = numberW + list.get(i).getVehicleNumber() + ",";
                        typeW = typeW + list.get(i).getVehicheType() + ",";
                    }

                    numberW = numberW.substring(0, numberW.length() - 1);
                    typeW = typeW.substring(0, typeW.length() - 1);
                }*/

                numberW = vehicleId.getText().toString();
                typeW = wheelerTypeTv.getText().toString();
                json.put("ProfilePic", createprofilepiclink);
                json.put("VehicleNbr", numberW);
                json.put("VehicleType", typeW);
                json.put("About", aboutCandidate.getText().toString());
                json.put("Occupation", profession.getText().toString());
            } else {
                json.put("ProfilePic", "");
                json.put("VehicleNbr", "");
                json.put("VehicleType", "");
                json.put("About", "");
                json.put("Occupation", "");
            }
            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            parseData(response);
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
                req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void parseData(JSONObject response) {

        {
            try {
                String message = response.optString("message");
                String Status = response.optString("Status");

                if (Status.equalsIgnoreCase("1")) {
                    userId = response.optString("userid");
                    common.setStringValue(Constants.id, userId);
                    common.showShortToast(message);
                    JSONObject object = response.getJSONObject("User");
                    String appStatus = object.optString("ApprovalStatus");
                    common.setStringValue(Constants.approvalStatus, appStatus);
                    SharedPreferences preferences = SignupSecondPage.this.getSharedPreferences(Constants.preference, Context.MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.putBoolean(Constants.isLoggedIn, true);
                    editor.putString(Constants.approvalStatus, object.optString("ApprovalStatus"));
                    editor.putString(Constants.id, object.optString("ID"));
                    editor.putString(Constants.Phone, object.optString("PhoneNbr"));
                    editor.putString(Constants.email, object.optString("EmailID"));
                    editor.putString(Constants.FirstName, object.optString("FirstName"));
                    editor.putString(Constants.MiddleName, object.optString("MiddleName"));
                    editor.putString(Constants.LastName, object.optString("LastName"));
                    editor.putString(Constants.ProfilePic, object.optString("ProfilePic"));
                    editor.putString(Constants.Gender, object.optString("Gender"));
                    editor.putString(Constants.Occupation, object.optString("Occupation"));
                    editor.putString(Constants.AddedOn, object.optString("AddedOn"));
                    editor.putString(Constants.isActive, object.optString("IsActive"));
                    editor.putString(Constants.userRwa, object.optString("RWAID"));
                    editor.putString("ID", object.optString("RWAID"));
                    editor.putString("chat", object.optString("ChatUrl"));
                    JSONArray array = object.getJSONArray("Flats");
                    JSONObject flatInfo1 = array.getJSONObject(0);
                    editor.putString(Constants.flatId, flatInfo1.optString("FlatID"));
                    editor.putString(Constants.flatNumber, flatInfo1.optString("FlatNbr"));
                    editor.putString(Constants.ResidentRWAID, object.optString("ResidentRWAID"));
                    editor.putString(Constants.userRwaName, object.optString("Name"));
                    editor.putString("SocietyName", object.optString("Name"));
                    editor.putString(Constants.isPhonePublic, object.optString("IsPhonePublic"));
                    editor.putString(Constants.aboutMe, object.optString("About"));

                    editor.commit();

                }

                progressDialog.dismiss();

                if (Status.equalsIgnoreCase("1") && done == 0) {
                    Intent intent = new Intent(SignupSecondPage.this, MainHomePageActivity.class);
                    startActivity(intent);
                } else if (Status.equalsIgnoreCase("1") && done == 1) {
                    SignupDoneDialog dialog = new SignupDoneDialog();
                    dialog.setRetainInstance(true);
                    dialog.show(getSupportFragmentManager(), "tag_delivery");

                } else if (Status.equalsIgnoreCase("0")) {
                    SignupSecondPage.this.onBackPressed();
                    common.showShortToast(message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void AlertBoxForImagePic() {


        new AlertDialog.Builder(SignupSecondPage.this)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_Gallery:
                galCroppedUri = getCustomUri();
                if (galCroppedUri != null)
                    Crop.of(data.getData(), galCroppedUri).asSquare().start(SignupSecondPage.this, REQUEST_CROP_GALLERY);
                break;
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                camCroppedUri = getCustomUri();
                Crop.of(fileUri, camCroppedUri).asSquare().start(SignupSecondPage.this, REQUEST_CROP_CAMERA);
                break;
            case REQUEST_CROP_CAMERA:
                if (camCroppedUri != null)
                    setImageToImageView(camCroppedUri);
                beginUpload(camCroppedUri.getPath());
                break;
            case REQUEST_CROP_GALLERY:
                if (galCroppedUri != null)
                    setImageToImageView(galCroppedUri);
                beginUpload(galCroppedUri.getPath());
                break;

        }

    }

    void setImageToImageView(Uri uri) {
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;
            bitmap = BitmapFactory.decodeFile(getPath(uri), options);
            croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight() - 1);

            profilePicture.setImageBitmap(croppedBmp);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static Uri getCustomUri() {
        // Create target folder if not exists.
        File fileDir = new File(Environment.getExternalStorageDirectory(), "nukad");
        if (!fileDir.exists())
            if (!fileDir.mkdirs())
                return null;
        // Create file object to save image.
        String filePath = "image_" + Calendar.getInstance().getTimeInMillis() + ".png";
        File file = new File(fileDir, filePath);
        return Uri.fromFile(file);
    }

    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(SignupSecondPage.this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);


        current_time = String.valueOf(System.currentTimeMillis());
        String strPath = "profile/" + common.getStringValue(Constants.userRwa) + "/" + current_time + common.getStringValue(Constants.id) + ".jpg";


        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, strPath,
                file);


        createprofilepiclink = "https://s3-ap-southeast-1.amazonaws.com/pyntandroid/" + strPath + "";
        pickLink = createprofilepiclink;
        isNewPicUploaded = true;

    }

    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(SignupSecondPage.this, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
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
                cursor = SignupSecondPage.this.getContentResolver()
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

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

}