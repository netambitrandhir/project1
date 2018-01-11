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
import android.widget.Toast;

import com.sanganan.app.R;
import com.sanganan.app.adapters.FilterListAdapter;
import com.sanganan.app.adapters.NearbyShopAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.Category;
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
 * Created by raj on 9/16/2016.
 */
public class FiltersFragment extends BaseFragment {
    View view;
    Typeface karlaB, ubuntuBold;
    TextView beauty_shops, sweet_shops, repair_shop, generalstore, restaurant, atms, meat_shops, medical_shops;

    ListView listView;
    ArrayList<Category> listCategory;
    Common common;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filtersfragment, container, false);

        initialVariables();
        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        ubuntuBold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        TextView title = (TextView) view.findViewById(R.id.titletextView);
        title.setText("Filters");
        title.setTypeface(ubuntuBold);

        TextView title_done = (TextView) view.findViewById(R.id.done_text);
        title_done.setTypeface(ubuntuBold);

        listView = (ListView) view.findViewById(R.id.filterlist);

        listCategory = new ArrayList<>();

        if (common.isNetworkAvailable()) {
            new CategoryListAsynTask().execute();
        } else {
            Toast.makeText(getActivity(), "No network connection...!!", Toast.LENGTH_SHORT).show();
        }


        title_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                getActivity().onBackPressed();
                Fragment fragment = new NearbyShops();
                activityCallback.onButtonClick(fragment, false);


            }
        });
        return view;
    }


    private void initialVariables() {
        common = new Common(getActivity());

    }


    private class CategoryListAsynTask extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;

        @Override
        protected String doInBackground(Void... voids) {


            String stringResponse = null;
            String status = null;

            try {
                HttpResponse categoryResponse = Utility.readDataFromUrl(
                        Constants.BaseUrl + "categories");


                stringResponse = EntityUtils.toString(categoryResponse.getEntity());
                JSONObject jobj = new JSONObject(stringResponse);

                status = jobj.getString("Status");
                JSONArray jsonArray = jobj.getJSONArray("list");

                String[] sureSelected = Constants.filterToCategoryById.split(",");
                ArrayList<String> listSureSelected = new ArrayList<>();
                for (int i = 0; i < sureSelected.length; i++) {
                    listSureSelected.add(sureSelected[i]);
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    Category category = new Category();
                    category.setIdCategory(jsonArray.getJSONObject(i).optString("ID"));
                    category.setAddedOn(jsonArray.getJSONObject(i).optString("AddedOn"));
                    category.setIsActive(jsonArray.getJSONObject(i).optString("IsActive"));
                    category.setName(jsonArray.getJSONObject(i).optString("Description"));

                    if (listSureSelected.contains(jsonArray.getJSONObject(i).optString("ID"))) {
                        category.setChecked(false);
                    } else {
                        category.setChecked(true);
                    }

                    listCategory.add(category);
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
            dialog.setMessage("Loading..");
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

                    FilterListAdapter filterListAdapter = new FilterListAdapter(getActivity(), listCategory);
                    listView.setAdapter(filterListAdapter);

                }
            }
        }

    }


}
