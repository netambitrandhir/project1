package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.adapters.AttendenceListAdapter;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.TimeSheet;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 20/3/17.
 */
public class AttandenceSheetFragment extends BaseFragment {

    View view;
    Common common;
    RequestQueue requestQueue;
    TextView helperProfession_TV, helperName_TV, date_TV, titletextView;
    ImageView helper_image_IV, next_IV, previous_IV;
    Typeface ubantuB, ubantuR, karlaB, karlaR;
    String helper_image, helper_name, helper_profession, helper_id;
    ListView listAttendence;
    ArrayList<TimeSheet> timeSheets;
    long time_in_millis = 0;
    long timeAlpha = 0;
    private static final long One_day_millis = 60 * 60 * 24 * 1000L;
    AttendenceListAdapter adapter;
    private static int countApiHit = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.attendence_layout, container, false);

        Bundle bundle = getArguments();
        helper_id = bundle.getString("helper_id");


        intializeVariable();

        time_in_millis = System.currentTimeMillis();

        if (common.isNetworkAvailable()) {
            countApiHit = 0;
            getAttendenceData();
        } else {
            common.showShortToast("no internet!");
        }


        next_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countApiHit++;
                time_in_millis = time_in_millis + One_day_millis;
                if (time_in_millis > System.currentTimeMillis()) {
                    common.showShortToast("No attendence sheet for this date");
                    time_in_millis = time_in_millis - One_day_millis;
                } else {
                    getAttendenceData();
                }
            }
        });

        previous_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_in_millis = time_in_millis - One_day_millis;
                getAttendenceData();
            }
        });


        return view;
    }


    private void intializeVariable() {


        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");
        ubantuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        ubantuR = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");

        helperName_TV = (TextView) view.findViewById(R.id.helper_name);
        helperName_TV.setTypeface(karlaB);

        titletextView = (TextView) view.findViewById(R.id.titletextView);
        titletextView.setTypeface(ubantuB);


        helperProfession_TV = (TextView) view.findViewById(R.id.helper_profession);
        helperProfession_TV.setTypeface(karlaR);


        helper_image_IV = (ImageView) view.findViewById(R.id.helper_image);


        date_TV = (TextView) view.findViewById(R.id.date);
        date_TV.setTypeface(ubantuB);
        next_IV = (ImageView) view.findViewById(R.id.next);
        previous_IV = (ImageView) view.findViewById(R.id.previous);
        listAttendence = (ListView) view.findViewById(R.id.listAttendence);


    }


    private void getAttendenceData() {


        common.showSpinner(getActivity());

        String uri = Constants.BaseUrl + "helperattendance";
        try {
            JSONObject json = new JSONObject();
            json.put("HelperID", helper_id);
          /*  Long timeAtZeroZero = time_in_millis/(1000L*24*60*60);
            timeAtZeroZero = timeAtZeroZero*60*60*24;*/
            json.put("time", String.valueOf(time_in_millis / 1000L));

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
                            common.hideSpinner();
                            listAttendence.setVisibility(View.GONE);
                            common.showShortToast("No attendence sheet for this date");

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

    void parsedData(JSONObject json) {
        try {


            String status = json.optString("Status");

            String date1[] = common.convertFromUnix3(String.valueOf(time_in_millis / 1000)).split("/");
            String date2[] = common.convertFromUnix3(String.valueOf(System.currentTimeMillis() / 1000)).split("/");

            if (date1[0].equals(date2[0])) {
                date_TV.setText("Today");
            } else {
                String date = common.convertFromUnix3(String.valueOf(time_in_millis / 1000));
                date_TV.setText(date);
            }

            JSONArray arrayHelper = json.getJSONArray("Helper");

            JSONObject objectHelper = arrayHelper.getJSONObject(0);
            helper_name = objectHelper.optString("Name");
            helper_image = objectHelper.optString("ProfilePhoto");
            helper_profession = objectHelper.optString("ServiceOffered");



            if (status.equals("1")) {

                JSONArray array = json.getJSONArray("Attendance");
                timeSheets = new ArrayList<>();
                timeSheets.clear();
                for (int i = 0; i < array.length(); i++) {
                    TimeSheet timeSheet = new TimeSheet();
                    JSONObject jsonObject = array.getJSONObject(i);
                    String ID = jsonObject.optString("ID");
                    String is_in = jsonObject.optString("is_in");
                    String timestamp = jsonObject.optString("timestamp");
                    String name = jsonObject.optString("Name");
                    String profession = jsonObject.optString("ServiceOffered");
                    String image = jsonObject.optString("ProfilePhoto");

                    timestamp = common.convertFromUnix2(timestamp);
                    String[] time = timestamp.split(" ");
                    timestamp = time[1] + " " + time[2].toLowerCase();


                    timeSheet.setID(ID);
                    timeSheet.setTime(timestamp);
                    timeSheet.setIs_in(is_in);
                    timeSheet.setName(name);
                    timeSheet.setProfession(profession);
                    timeSheet.setImage(image);

                    timeSheets.add(timeSheet);
                }


                adapter = new AttendenceListAdapter(getActivity(), timeSheets);
                listAttendence.setAdapter(adapter);
                listAttendence.setVisibility(View.VISIBLE);
            } else {
                listAttendence.setVisibility(View.GONE);
                common.showShortToast("No attendence sheet for this date");
            }

            if (countApiHit == 0) {
               /* helper_image = timeSheets.get(0).getImage();
                helper_name = timeSheets.get(0).getName();
                helper_profession = "(" + timeSheets.get(0).getProfession() + ")";*/

                setInitialUiOfHelper();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        common.hideSpinner();
    }

    private void setInitialUiOfHelper() {
        if (helper_image.endsWith(".png") || helper_image.endsWith(".jpg")) {
            Picasso.with(getActivity()).load(helper_image).into(helper_image_IV);
        } else {
            String str = helper_name;
            str = String.valueOf(str.charAt(0));
            int positionH = 0;
            for (int j = 0; j < Alphabets.alphabets.size(); j++) {
                if (str.equalsIgnoreCase(Alphabets.alphabets.get(j))) {
                    positionH = j;
                    helper_image_IV.setImageResource(Alphabets.alphabetsDrawable.get(positionH));
                }
            }

        }

        helperName_TV.setText(helper_name);
        helperProfession_TV.setText("("+helper_profession+")");

    }

}
