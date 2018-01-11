package com.sanganan.app.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sanganan.app.R;
import com.sanganan.app.activities.MainHomePageActivity;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.model.WheelerModel;
import com.sanganan.app.utility.Utility;
import com.sendbird.android.SendBird;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends ActionBarActivity {

    private static final String TAG = "SignInFragment";
    EditText mPhoneEditTxt, mPswdEditTxt;
    ImageView mLoginBtn, signInBtn;
    Toolbar mToolbar;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    String phonenoString, mPswdString;
    String fbid, fbemail, fbname;
    String mId, mPhoneNo, mEmailID, mPassword, mFirstName, mMiddleName, mLastName, mProfilePic,mChatUrl,
            mGender, mOccupation, mAddedOn, mIsActive, userRwaId, ResidentRWAID, mFlatId, mFlatNumber, rwaName, isPhonePublic, aboutMe, mApprovalStatus;
    Common common;
    ImageView sign_up;
    TextView phonenoText, passwordText, forgotPswdBtn;
    Typeface monster, karlaB, karlaR, wsregular;

    String sUserId, mNickname;
    List<WheelerModel> listVehicle = new ArrayList<>();
    TextView account_yet;

    String SocietyPrivateinfo, SocietyPublicInfo, SocietyBanner, SocietyPendingInfo;

    private static final String appId = "EBCD2CB7-F5CC-4F6C-8EF4-86548DCCDE38"; /* Sample SendBird Application */


    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        common = Common.getNewInstance(this);
        initializevariables();
        TextView title = (TextView) findViewById(R.id.titletextView);
        monster = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.otf");
        karlaB = Typeface.createFromAsset(getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(getAssets(), "Karla-Regular.ttf");
        wsregular = Typeface.createFromAsset(getAssets(), "WorkSans-Regular.ttf");
        Typeface ubuntuB = Typeface.createFromAsset(getAssets(), "Ubuntu-B.ttf");

        title.setText("Sign-in");
        title.setTypeface(ubuntuB);


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phonenoString = mPhoneEditTxt.getText().toString();
                mPswdString = mPswdEditTxt.getText().toString();

                if (!phonenoString.isEmpty() || !mPswdString.isEmpty()) {
                    new SignInActivityApi().execute();
                } else {
                    common.showShortToast("Enter mandatory fields");
                }
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignInFragment.this,SignupFirstPage.class);
                startActivity(intent);
            }
        });

        forgotPswdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = new OTPDialog();
                fragment.setRetainInstance(true);
                fragment.show(getSupportFragmentManager(), "otp");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initializevariables() {
        account_yet = (TextView) findViewById(R.id.account_yet);
        account_yet.setTypeface(wsregular);
        mPhoneEditTxt = (EditText) findViewById(R.id.phonenoEditText);
        mPhoneEditTxt.setTypeface(wsregular);
        mPswdEditTxt = (EditText) findViewById(R.id.passwordEditText);
        mPswdEditTxt.setTypeface(wsregular);
        mLoginBtn = (ImageView) findViewById(R.id.signInBtn);
        sign_up = (ImageView) findViewById(R.id.signUpText);
        signInBtn = (ImageView) findViewById(R.id.signInBtn);

        phonenoText = (TextView) findViewById(R.id.phonenoText);
        phonenoText.setTypeface(karlaB);

        passwordText = (TextView) findViewById(R.id.passwordText);
        passwordText.setTypeface(karlaB);

        forgotPswdBtn = (TextView) findViewById(R.id.forgotPswdBtn);
        forgotPswdBtn.setTypeface(karlaR);

    }


    private class SignInActivityApi extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        String status = "";
        String message = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SignInFragment.this);
            progressDialog.setMessage("Please wait data is Loading");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String json = "{\"PhoneNbr\":\"" + phonenoString + "\",\"Password\":\"" + mPswdString + "\"}\n";
            String stringResponse = null;
            JSONObject jsonObject = new JSONObject();

            HttpResponse httpResponse = Utility.postDataOnUrl(Constants.BaseUrl+"login", json);

            try {
                stringResponse = EntityUtils.toString(httpResponse.getEntity());
                jsonObject = new JSONObject(stringResponse);
                status = jsonObject.getString("Status");
                message = jsonObject.getString("message");
                if (status.equalsIgnoreCase("1")) {

                    JSONObject user = jsonObject.getJSONObject("User");

                    mId = user.optString("ID");
                    mPhoneNo = user.optString("PhoneNbr");
                    mEmailID = user.optString("EmailID");
                    mFirstName = user.optString("FirstName");
                    mMiddleName = user.optString("MiddleName");
                    mLastName = user.optString("LastName");
                    mProfilePic = user.optString("ProfilePic");
                    mGender = user.optString("Gender");
                    mOccupation = user.optString("Occupation");
                    mAddedOn = user.optString("AddedOn");
                    mIsActive = user.optString("IsActive");
                    userRwaId = user.optString("RWAID");
                    ResidentRWAID = user.optString("ResidentRWAID");
                    mFlatId = user.getJSONArray("Flats").getJSONObject(0).optString("FlatID");
                    mFlatNumber = user.getJSONArray("Flats").getJSONObject(0).optString("FlatNbr");
                    rwaName = user.optString("Name");
                    isPhonePublic = user.optString("IsPhonePublic");
                    aboutMe = user.optString("About");
                    mApprovalStatus = user.optString("ApprovalStatus");

                    SocietyPrivateinfo = user.optString("SocietyPrivateinfo");
                    SocietyPublicInfo = user.optString("SocietyPublicInfo");
                    SocietyBanner = user.optString("SocietyBanner");
                    SocietyPendingInfo = user.optString("SocietyPendingInfo");
                    mChatUrl = user.optString("ChatUrl");


                    JSONArray arrayVehicle = user.getJSONArray("Vehicles");
                    listVehicle.clear();
                    for (int i = 0; i < arrayVehicle.length(); i++) {
                        WheelerModel wheelerModel = new WheelerModel();
                        wheelerModel.setID(arrayVehicle.getJSONObject(i).optString("ID"));
                        wheelerModel.setVehicheType(arrayVehicle.getJSONObject(i).optString("VehicleType"));
                        wheelerModel.setVehicleNumber(arrayVehicle.getJSONObject(i).optString("VehicleNbr"));
                        listVehicle.add(wheelerModel);
                    }


                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (s.equalsIgnoreCase("1")) {

                SharedPreferences preferences = getSharedPreferences(Constants.preference, Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putBoolean(Constants.isLoggedIn, true);
                editor.putString(Constants.approvalStatus, mApprovalStatus);
                editor.putString(Constants.id, mId);
                editor.putString(Constants.Phone, mPhoneNo);
                editor.putString(Constants.email, mEmailID);
                editor.putString(Constants.Password, mPassword);
                editor.putString(Constants.FirstName, mFirstName);
                editor.putString(Constants.MiddleName, mMiddleName);
                editor.putString(Constants.LastName, mLastName);
                editor.putString(Constants.ProfilePic, mProfilePic);
                editor.putString(Constants.Gender, mGender);
                editor.putString(Constants.Occupation, mOccupation);
                editor.putString(Constants.AddedOn, mAddedOn);
                editor.putString(Constants.isActive, mIsActive);
                editor.putString(Constants.userRwa, userRwaId);
                editor.putString("ID",userRwaId);
                editor.putString(Constants.ResidentRWAID, ResidentRWAID);
                editor.putString(Constants.flatId, mFlatId);
                editor.putString(Constants.flatNumber, mFlatNumber);
                editor.putString(Constants.ResidentRWAID, ResidentRWAID);
                editor.putString(Constants.userRwaName, rwaName);
                editor.putString("SocietyName", rwaName);
                editor.putString(Constants.isPhonePublic, isPhonePublic);
                editor.putString(Constants.aboutMe, aboutMe);
                editor.putString("chat",mChatUrl);
                editor.putString(Constants.SocietyPendingInfo, SocietyPendingInfo);
                editor.putString(Constants.SocietyPublicInfo, SocietyPublicInfo);
                editor.putString(Constants.SocietyBanner, SocietyBanner);
                editor.putString(Constants.SocietyPrivateinfo, SocietyPrivateinfo);

                Gson gson = new Gson();
                String jsonVehicles = gson.toJson(listVehicle);
                Log.d("TAG", "jsonCars = " + jsonVehicles);
                editor.putString(Constants.VehicleList, jsonVehicles);
                editor.commit();

                doPrimarySetupBeforeStartingChat();

               finish();
                Intent intentSignIn = new Intent(SignInFragment.this, MainHomePageActivity.class);
                startActivity(intentSignIn);


            } else {
                Toast.makeText(SignInFragment.this, message, LENGTH_SHORT).show();
            }
        }
    }

    private void doPrimarySetupBeforeStartingChat() {

        sUserId = common.getStringValue(Constants.id);
        mNickname = common.getStringValue(Constants.FirstName);

        SendBird.init(appId, SignInFragment.this);


    }


}