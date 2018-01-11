package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanganan.app.R;
import com.sanganan.app.adapters.FavCommunityAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.HelperModel;

import com.sanganan.app.utility.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by root on 27/9/16.
 */
public class FavouriteHelpersFragment extends BaseFragment {
    View view;
    GridView gridView;
    ArrayList<HelperModel> helperModelArrayList = new ArrayList<>();
    TextView title;
    Typeface ubantuBold;
    Common common;
    RelativeLayout blank_layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.community_helpers_layout_fav, container, false);

        initializeVariables(view);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        ubantuBold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        title.setTypeface(ubantuBold);
        title.setText("Saved Helpers");


        addObjectsToListFromDB();

        if (common.isNetworkAvailable()) {
            new GetFavouriteHelpersAsynTask().execute();
        } else {
            common.showShortToast("no internet...!!");
        }


        return view;
    }

    private void initializeVariables(View view) {
        gridView = (GridView) view.findViewById(R.id.gridView);
        title = (TextView) view.findViewById(R.id.titletextView);
        blank_layout = (RelativeLayout) view.findViewById(R.id.blank_layout);
        common = new Common(getActivity());
    }

    private void addObjectsToListFromDB() {

    }


    private class GetFavouriteHelpersAsynTask extends AsyncTask<Void, Void, String> {

        ProgressDialog dialog;

        @Override
        protected String doInBackground(Void... voids) {


            String jsonRaw = "{\n" +
                    "\"UserID\":\"" + common.getStringValue(Constants.id) + "\"\n" +
                    "}";


            String stringResponse = null;

            String status = null;

            try {
                HttpResponse searchResponse = Utility.postDataOnUrl(
                        Constants.BaseUrl + "showfavhelper", jsonRaw);


                stringResponse = EntityUtils.toString(searchResponse.getEntity());
                JSONObject jobj = new JSONObject(stringResponse);

                status = jobj.getString("Status");
                JSONArray jsonArray = jobj.getJSONArray("Fav Helpers");
                helperModelArrayList.clear();
                GetFavouriteList.hsHelper.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    HelperModel helperModel = new HelperModel();

                    String ID = object.optString("ID");
                    String RWAID = object.optString("RWAID");
                    String Name = object.optString("Name").trim();
                    String ServiceOffered = object.optString("ServiceOffered");
                    String ResidentialAddress = object.optString("ResidentialAddress");
                    String PrimaryContactNbr = object.optString("PrimaryContactNbr");
                    String PhoneNbr2 = object.optString("PhoneNbr2");
                    String EmailId = object.optString("EmailId");
                    String AddedBy = object.optString("AddedBy");
                    String AddedOn = object.optString("AddedOn");
                    String ServiceCharge = object.optString("ServiceCharges");
                    String ProfilePhoto = object.optString("ProfilePhoto");
                    String PoilceVerificationScanImage1 = object.optString("PoilceVerificationScanImage1");
                    String PoliceVerificationScanImage2 = object.optString("PoliceVerificationScanImage2");
                    String PoliceVerificationScanImage3 = object.optString("PoliceVerificationScanImage3");
                    String EntryCardExpiry = object.optString("EntryCardExpiry");
                    String IsActive = object.optString("IsActive");
                    String helperId = object.optString("HelperID");
                    String rated = object.optString("Rated");
                    String is_in = object.optString("is_in");


                    GetFavouriteList.hsHelper.add(helperId);


                    helperModel.setID(ID);
                    helperModel.setRWAID(RWAID);
                    helperModel.setName(Name);
                    helperModel.setServiceCharge(ServiceCharge);
                    helperModel.setHelperId(helperId);
                    helperModel.setServiceOffered(ServiceOffered);
                    helperModel.setResidentialAddress(ResidentialAddress);
                    helperModel.setPrimaryContactNbr(PrimaryContactNbr);
                    helperModel.setPhoneNbr2(PhoneNbr2);
                    helperModel.setEmailId(EmailId);
                    helperModel.setAddedBy(AddedBy);
                    helperModel.setAddedOn(AddedOn);
                    helperModel.setAddedOn(AddedOn);
                    helperModel.setProfilePhoto(ProfilePhoto);
                    helperModel.setPoilceVerificationScanImage1(PoilceVerificationScanImage1);
                    helperModel.setPoliceVerificationScanImage2(PoliceVerificationScanImage2);
                    helperModel.setPoliceVerificationScanImage3(PoliceVerificationScanImage3);
                    helperModel.setEntryCardExpiry(EntryCardExpiry);
                    helperModel.setIsActive(IsActive);
                    helperModel.setRating(rated);
                    helperModel.setIs_in(is_in);

                    helperModelArrayList.add(helperModel);


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

            dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading...");
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
                    if (helperModelArrayList.size() != 0) {
                        gridView.setVisibility(View.VISIBLE);
                        blank_layout.setVisibility(View.GONE);
                        FavCommunityAdapter communityHelperAdapter = new FavCommunityAdapter(getActivity(), helperModelArrayList);
                        gridView.setAdapter(communityHelperAdapter);
                    } else {
                        gridView.setVisibility(View.GONE);
                        blank_layout.setVisibility(View.VISIBLE);
                    }
                } else if (s.equalsIgnoreCase("0")) {
                    gridView.setVisibility(View.GONE);
                    blank_layout.setVisibility(View.VISIBLE);

                }
            } else {
                // common.showShortToast("no data in favourite list");
                gridView.setVisibility(View.GONE);
                blank_layout.setVisibility(View.VISIBLE);

            }


            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment fragment = new CommunityHelperDetails();
                    Bundle bundle = new Bundle();
                    bundle.putString("helperId", helperModelArrayList.get(position).getID());
                    bundle.putString("profile_pic", helperModelArrayList.get(position).getProfilePhoto());
                    fragment.setArguments(bundle);
                    activityCallback.onButtonClick(fragment, false);

                }
            });
        }

    }
}