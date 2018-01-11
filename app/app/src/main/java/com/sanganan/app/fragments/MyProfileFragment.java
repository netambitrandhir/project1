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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanganan.app.R;
import com.sanganan.app.adapters.FullscreenImageAdapter;
import com.sanganan.app.adapters.VehicleProfileAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.WheelerModel;
import com.sanganan.app.utility.Util;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.lang.reflect.Type;

/**
 * Created by root on 2/11/16.
 */

public class MyProfileFragment extends BaseFragment {

    //VehicleProfileAdapter related page

    View view;
    TextView title, textNameInitial, nameT, nameR, mobileT, mobileR, cancall, emailT, emailR, professionT,
            professionR, socity_and_flat, socity, flatno, aboutme, aboutmetext, vehicle;
    ImageView edit1, edit2, edit3, edit4, edit5, editImage, addvehicle, profile_pic, cancallimg, changePassword;
    RelativeLayout relativeLayoutTop, relativeLayoutBelow;
    Typeface ubantuBold, karlaBold, karlaRegular;
    String rwa, name, addedby, mobileNumber, emailId, profession, societyString, flatString, aboutmeString, pickLink, isPhonePublic;
    Common common;
    boolean sliderOn = false;
    EditDialogFragment fragment;
    ProgressDialog progressDialog;
    public ArrayList<WheelerModel> vehicleList;

    //all initial constants for uploading,editing and saving image to server

    RequestQueue requestQueue;

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int REQUEST_CODE_Gallery = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    boolean isNewPicUploaded = false;
    Bitmap croppedBmp;
    private Uri fileUri; // file url to store image/video
    String createprofilepiclink = "";
    private static final String IMAGE_DIRECTORY_NAME = "mynukad";
    private static final String TAG = "UploadActivity";
    private TransferUtility transferUtility;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    static String timeStamp;
    static String profilepic;
    private Bitmap bitmap;
    ListView listViewVehicle;

    private static final int REQUEST_CROP_CAMERA = 1010;
    private static final int REQUEST_CROP_GALLERY = 1011;
    Uri galCroppedUri;
    Uri camCroppedUri;


    private FirebaseAnalytics mFirebaseAnalytics;
    private String current_time ="";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.myprofile, container, false);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        initializeVariables(view);
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        Bundle logBundle = new Bundle();
        logBundle.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("myprofile", logBundle);

        //code to hide soft_keyboard
        View viewPresent = getActivity().getCurrentFocus();
        if (viewPresent != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewPresent.getWindowToken(), 0);
        }


        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        title.setTypeface(ubantuBold);

        transferUtility = Util.getTransferUtility(getActivity());

        Gson gson = new Gson();
        Type type = new TypeToken<List<WheelerModel>>() {
        }.getType();
        String jsonVehicles = common.getStringValue(Constants.VehicleList);
        vehicleList = gson.fromJson(jsonVehicles, type);


        if (vehicleList != null) {
            VehicleProfileAdapter adapter = new VehicleProfileAdapter(getActivity(), vehicleList);
            listViewVehicle.setAdapter(adapter);
            setListViewHeightBasedOnChildren(listViewVehicle);
        }
        pickLink = common.getStringValue(Constants.ProfilePic);
        name = common.getStringValue(Constants.FirstName);
        flatString = common.getStringValue(Constants.flatNumber);
        mobileNumber = common.getStringValue(Constants.Phone);
        emailId = common.getStringValue(Constants.email);
        addedby = common.getStringValue(Constants.ResidentRWAID);
        rwa = common.getStringValue(Constants.userRwa);
        profession = common.getStringValue(Constants.Occupation);
        aboutmeString = common.getStringValue(Constants.aboutMe);
        societyString = common.getStringValue(Constants.userRwaName);
        isPhonePublic = common.getStringValue(Constants.isPhonePublic);


        String webUrl[] = pickLink.split("\\.");

        if (!webUrl[webUrl.length - 1].equalsIgnoreCase("png") && !webUrl[webUrl.length - 1].equalsIgnoreCase("jpg")) {
            if (!name.isEmpty()) {
                textNameInitial.setText(name.substring(0, 1).toUpperCase());
            } else {
                textNameInitial.setText(name);
            }
            relativeLayoutTop.setVisibility(View.INVISIBLE);
            relativeLayoutBelow.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(getActivity()).load(pickLink).placeholder(R.drawable.galleryplacholder).into(profile_pic);
            relativeLayoutBelow.setVisibility(View.INVISIBLE);
            relativeLayoutTop.setVisibility(View.VISIBLE);
        }

        nameR.setText(name);
        mobileR.setText(mobileNumber);
        emailR.setText(emailId);
        professionR.setText(profession);
        flatno.setText("(" + flatString + ")");
        socity.setText(societyString);
        aboutmetext.setText(aboutmeString);

        if (isPhonePublic.equalsIgnoreCase("1")) {
            cancallimg.setImageResource(R.drawable.slider_on);
            sliderOn = true;
        } else {
            cancallimg.setImageResource(R.drawable.slider_off);
            sliderOn = false;
        }


        cancallimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sliderOn) {
                    cancallimg.setImageResource(R.drawable.slider_off);
                    sliderOn = false;
                    getData("0");
                } else {
                    cancallimg.setImageResource(R.drawable.slider_on);
                    sliderOn = true;
                    getData("1");
                }
                //getData();
            }
        });

        final Bundle bundle = new Bundle();

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setRetainInstance(true);
                bundle.putString("name",name);
                fragment.setArguments(bundle);
                fragment.show(getFragmentManager(), "Name");

            }
        });
        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setRetainInstance(true);
                bundle.putString("phone",mobileNumber);
                fragment.setArguments(bundle);
                fragment.show(getFragmentManager(), "Phone Number");

            }
        });
        edit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setRetainInstance(true);
                bundle.putString("emailId",emailId);
                fragment.setArguments(bundle);
                fragment.show(getFragmentManager(), "Email ID");
            }
        });
        edit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setRetainInstance(true);
                bundle.putString("profession",profession);
                fragment.setArguments(bundle);
                fragment.show(getFragmentManager(), "Profession");
            }
        });
        edit5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new UpdateAboutMe();
                bundle.putString("aboutme",aboutmeString);
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        addvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddVehicleDialog dialog = new AddVehicleDialog();
                dialog.setRetainInstance(true);
                dialog.show(getFragmentManager(), "add vehicle");
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertBoxForImagePic();

            }
        });


        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ChangePassword();
                activityCallback.onButtonClick(fragment, false);
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> listImageSingle = new ArrayList<String>();
                listImageSingle.clear();
                listImageSingle.add(pickLink);
                DialogFragment fragment = new ImageSlideShow();
                Bundle bundle1 = new Bundle();
                bundle1.putStringArrayList("imagelist", listImageSingle);
                fragment.setArguments(bundle1);
                fragment.setRetainInstance(true);
                fragment.show(getFragmentManager(), "tag_delivery_profile");


            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Gson gson = new Gson();
        Type type = new TypeToken<List<WheelerModel>>() {
        }.getType();
        String jsonVehicles = common.getStringValue(Constants.VehicleList);
        vehicleList = gson.fromJson(jsonVehicles, type);

        if (vehicleList != null) {
            VehicleProfileAdapter adapter = new VehicleProfileAdapter(getActivity(), vehicleList);
            listViewVehicle.setAdapter(adapter);
            setListViewHeightBasedOnChildren(listViewVehicle);
        }
    }

    public void AlertBoxForImagePic() {

        new AlertDialog.Builder(getActivity())
                .setTitle("Please Wait....")
                .setMessage("Want to choose photo from")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case REQUEST_CODE_Gallery:
                        galCroppedUri = getCustomUri();
                        if (galCroppedUri != null)
                            Crop.of(data.getData(), galCroppedUri).asSquare().start(getActivity(), this, REQUEST_CROP_GALLERY);
                        break;
                    case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                        camCroppedUri = getCustomUri();
                        Crop.of(fileUri, camCroppedUri).asSquare().start(getActivity(), this, REQUEST_CROP_CAMERA);
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
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.toast_cam_gallery_crop, Toast.LENGTH_SHORT).show();
        }

    }

    void setImageToImageView(Uri uri) {
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;
            bitmap = BitmapFactory.decodeFile(getPath(uri), options);
            croppedBmp = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight() - 1);

            profile_pic.setImageBitmap(croppedBmp);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(getActivity(), "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);

        current_time = String.valueOf(System.currentTimeMillis());
        String strPath = "profile/" + common.getStringValue(Constants.userRwa) + "/" + current_time + common.getStringValue(Constants.id) + ".jpg";

        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, strPath,
                file);


        createprofilepiclink = "https://s3-ap-southeast-1.amazonaws.com/pyntandroid/" + strPath + "";
        isNewPicUploaded = true;
        pickLink = createprofilepiclink;

        if (common.isNetworkAvailable()) {
            getData(isPhonePublic);
        } else {
            common.showShortToast("no internet...!!");
        }

    }

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


    private void getData(String callStatus) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "updateuserinfo";
        try {
            JSONObject json = new JSONObject();
            json.put("ID", common.getStringValue(Constants.id));
            if (isNewPicUploaded) {
                json.put("ProfilePic", createprofilepiclink);
            } else {
                json.put("IsPhonePublic", callStatus);
            }

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());
                            progressDialog.dismiss();

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
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();

        }
    }

    void parsedData(JSONObject json) {
        try {

            progressDialog.dismiss();

            String status = json.optString("Status");
            String messsage = json.optString("Message");

            JSONObject obj = json.getJSONObject("User");
            String phonePublicStatus = obj.optString("IsPhonePublic");
            String profilePicLink = obj.optString("ProfilePic");

            if (status.equalsIgnoreCase("1")) {
                common.setStringValue(Constants.isPhonePublic, phonePublicStatus);
                isPhonePublic = phonePublicStatus;
                if (isNewPicUploaded) {
                    isNewPicUploaded = false;
                    common.setStringValue(Constants.ProfilePic, createprofilepiclink);
                    relativeLayoutBelow.setVisibility(View.INVISIBLE);
                    relativeLayoutTop.setVisibility(View.VISIBLE);
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
                    Bundle logBundle = new Bundle();
                    logBundle.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("myprofile_pic_changed", logBundle);

                }
            }
            common.showShortToast(messsage);
        } catch (Exception e) {

        }


    }


    private void initializeVariables(View view) {


        fragment = new EditDialogFragment();

        common = Common.getNewInstance(getActivity());
        ubantuBold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        karlaBold = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        karlaRegular = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");

        changePassword = (ImageView) view.findViewById(R.id.changePassword);
        title = (TextView) view.findViewById(R.id.titletextView);
        relativeLayoutTop = (RelativeLayout) view.findViewById(R.id.relativeLayoutTop);
        relativeLayoutBelow = (RelativeLayout) view.findViewById(R.id.relativeLayoutBelow);
        textNameInitial = (TextView) view.findViewById(R.id.textNameInitial);
        textNameInitial.setTypeface(karlaRegular);
        nameT = (TextView) view.findViewById(R.id.nameT);
        nameT.setTypeface(karlaBold);
        nameR = (TextView) view.findViewById(R.id.nameR);
        nameR.setTypeface(karlaBold);
        mobileT = (TextView) view.findViewById(R.id.mobileT);
        mobileT.setTypeface(karlaBold);
        mobileR = (TextView) view.findViewById(R.id.mobileR);
        mobileR.setTypeface(karlaBold);
        cancall = (TextView) view.findViewById(R.id.cancall);
        cancall.setTypeface(karlaBold);
        emailT = (TextView) view.findViewById(R.id.emailT);
        emailT.setTypeface(karlaBold);
        emailR = (TextView) view.findViewById(R.id.emailR);
        emailR.setTypeface(karlaBold);
        professionT = (TextView) view.findViewById(R.id.professionT);
        professionT.setTypeface(karlaBold);
        professionR = (TextView) view.findViewById(R.id.professionR);
        professionR.setTypeface(karlaBold);
        socity_and_flat = (TextView) view.findViewById(R.id.socity_and_flat);
        socity_and_flat.setTypeface(karlaBold);
        socity = (TextView) view.findViewById(R.id.society);
        socity.setTypeface(karlaBold);
        flatno = (TextView) view.findViewById(R.id.flatno);
        flatno.setTypeface(karlaBold);
        aboutme = (TextView) view.findViewById(R.id.aboutme);
        aboutme.setTypeface(karlaBold);
        aboutmetext = (TextView) view.findViewById(R.id.aboutmetext);
        aboutmetext.setTypeface(karlaBold);
        vehicle = (TextView) view.findViewById(R.id.vehicle);
        vehicle.setTypeface(karlaBold);


        edit1 = (ImageView) view.findViewById(R.id.edit1);
        edit2 = (ImageView) view.findViewById(R.id.edit2);
        edit3 = (ImageView) view.findViewById(R.id.edit3);
        edit4 = (ImageView) view.findViewById(R.id.edit4);
        edit5 = (ImageView) view.findViewById(R.id.edit5);
        editImage = (ImageView) view.findViewById(R.id.editImage);
        addvehicle = (ImageView) view.findViewById(R.id.addvehicle);
        profile_pic = (ImageView) view.findViewById(R.id.profile_pic);
        cancallimg = (ImageView) view.findViewById(R.id.cancallimg);

        listViewVehicle = (ListView) view.findViewById(R.id.listViewVehicle);


    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                //listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}
