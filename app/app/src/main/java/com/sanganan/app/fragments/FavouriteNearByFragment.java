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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanganan.app.R;

import com.sanganan.app.adapters.FavNearByAdapter;
import com.sanganan.app.common.Common;

import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.NearByShopSearch;
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
public class FavouriteNearByFragment extends BaseFragment {

    View view;
    ListView shopList;
    ArrayList<NearByShopSearch> nearByShopList = new ArrayList<>();
    TextView title;
    Typeface ubantuBold;
    Common common;

    RelativeLayout blank_layout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nearby_fav_layout, container, false);

        initialiseVariable(view);

        ubantuBold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        title.setTypeface(ubantuBold);
        title.setText("Saved Shops/Services");

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);


        addObjectsToListFromDB();

        if (common.isNetworkAvailable()) {
            nearByShopList.clear();
            new GetFavouriteNearByShopsAsynTask().execute();

        } else {
            common.showShortToast("no internet...!!");
        }

        shopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("Name", nearByShopList.get(position).getShopName());
                bundle.putString("bannerimage", nearByShopList.get(position).getBannerImage());
                bundle.putString("image2", nearByShopList.get(position).getImage2());
                bundle.putString("image3", nearByShopList.get(position).getImage3());
                bundle.putString("type", nearByShopList.get(position).getType());
                bundle.putString("address", nearByShopList.get(position).getAddress1() + "" + nearByShopList.get(position).getCity());
                bundle.putString("ishomedelivery", nearByShopList.get(position).getIsHomeDelivery());
                bundle.putString("is247", nearByShopList.get(position).getIs247());
                bundle.putString("description", nearByShopList.get(position).getDescription());
                bundle.putString("phoneNbr1", nearByShopList.get(position).getPhoneNbr1());
                bundle.putString("phoneNbr2", nearByShopList.get(position).getPhoneNbr2());
                bundle.putString("phoneNbr3", nearByShopList.get(position).getPhoneNbr3());
                bundle.putString("nearbyid", nearByShopList.get(position).getNearById());
                bundle.putString("distance",nearByShopList.get(position).getDistance());
                bundle.putString("latitude",nearByShopList.get(position).getLatitude());
                bundle.putString("longitude",nearByShopList.get(position).getLongitude());

                Fragment fragment = new NearByShop_Details();
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);

            }
        });


        return view;
    }

    private void initialiseVariable(View view) {
        shopList = (ListView) view.findViewById(R.id.nearbyshop_fav);
        title = (TextView) view.findViewById(R.id.titletextView);
        common = new Common(getActivity());
        blank_layout = (RelativeLayout) view.findViewById(R.id.blank_layout);

    }

    private void addObjectsToListFromDB() {

    }

    private class GetFavouriteNearByShopsAsynTask extends AsyncTask<Void, Void, String> {

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
                        Constants.BaseUrl + "showfavshop", jsonRaw);


                stringResponse = EntityUtils.toString(searchResponse.getEntity());
                JSONObject jobj = new JSONObject(stringResponse);

                status = jobj.getString("Status");
                JSONArray jsonArray = jobj.getJSONArray("Fav Shop ");
                GetFavouriteList.hsNearBy.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    NearByShopSearch nearByShopSearch = new NearByShopSearch();
                    nearByShopSearch.setShopName(jsonArray.getJSONObject(i).optString("Name"));
                    nearByShopSearch.setNearById(jsonArray.getJSONObject(i).optString("ID"));
                    nearByShopSearch.setNearById(jsonArray.getJSONObject(i).optString("NearByID"));
                    nearByShopSearch.setAddress1(jsonArray.getJSONObject(i).optString("Address1") + jsonArray.getJSONObject(i).optString("Address2"));
                    nearByShopSearch.setDescription(jsonArray.getJSONObject(i).optString("Description"));
                    nearByShopSearch.setIs247(jsonArray.getJSONObject(i).optString("Is247"));
                    nearByShopSearch.setIsHomeDelivery(jsonArray.getJSONObject(i).optString("IsHomeDelivery"));
                    nearByShopSearch.setLatitude(jsonArray.getJSONObject(i).optString("Latitude"));
                    nearByShopSearch.setLongitude(jsonArray.getJSONObject(i).optString("Longitude"));
                    nearByShopSearch.setPhoneNbr1(jsonArray.getJSONObject(i).optString("PhoneNbr1"));
                    nearByShopSearch.setPhoneNbr2(jsonArray.getJSONObject(i).optString("PhoneNbr2"));
                    nearByShopSearch.setPhoneNbr3(jsonArray.getJSONObject(i).optString("PhoneNbr3"));
                    nearByShopSearch.setCity(jsonArray.getJSONObject(i).optString("City"));
                    nearByShopSearch.setType(jsonArray.getJSONObject(i).optString("Type"));
                    nearByShopSearch.setBannerImage(jsonArray.getJSONObject(i).optString("BannerImage"));
                    nearByShopSearch.setImage2(jsonArray.getJSONObject(i).optString("Image2"));
                    nearByShopSearch.setImage3(jsonArray.getJSONObject(i).optString("Image3"));

                    String distance = jsonArray.getJSONObject(i).optString("distance");


                    double dist = Double.parseDouble(distance);
                    dist = round(dist, 2);
                    distance = String.valueOf(dist) + "KM";
                    nearByShopSearch.setDistance(distance);


                    GetFavouriteList.hsNearBy.add(nearByShopSearch.getNearById());

                    nearByShopSearch.setDistance(distance);
                    nearByShopList.add(nearByShopSearch);


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
                    blank_layout.setVisibility(View.GONE);
                    shopList.setVisibility(View.VISIBLE);
                    FavNearByAdapter nearbyShopAdapter = new FavNearByAdapter(getActivity(), nearByShopList);
                    shopList.setAdapter(nearbyShopAdapter);

                } else if (s.equalsIgnoreCase("0")) {
                    blank_layout.setVisibility(View.VISIBLE);
                    shopList.setVisibility(View.GONE);
                }
            }

        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
