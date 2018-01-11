package com.sanganan.app.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.adapters.FilterListAdapter;
import com.sanganan.app.adapters.FiltersNotificationAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.Category;

import java.util.ArrayList;

/**
 * Created by Randhir Patel on 6/7/17.
 */


public class NotificationFilterFragment extends BaseFragment {
    View view;
    Typeface karlaB, ubuntuBold;
    TextView beauty_shops, sweet_shops, repair_shop, generalstore, restaurant, atms, meat_shops, medical_shops;

    ListView listView;
    ArrayList<Category> listCategory;
    Common common;
    String[] arrayCategory = {"Informational", "Critical", "Need Action"};

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


        ArrayList<String> listSureSelected = new ArrayList<>();

        if (!Constants.filterToCategoryNotification.isEmpty()) {
            String[] sureSelected = Constants.filterToCategoryNotification.split(",");
            for (int i = 0; i < sureSelected.length; i++) {
                listSureSelected.add(sureSelected[i]);
            }
        }

        Notification.alistNotification.clear();
        for (int i = 0; i < arrayCategory.length; i++) {
            Category category = new Category();
            category.setIdCategory(String.valueOf(i + 1));
            category.setName(arrayCategory[i]);
            if (listSureSelected.contains(category.getIdCategory())) {
                category.setChecked(true);
                Notification.alistNotification.add(category);
            } else {
                category.setChecked(false);
            }
            listCategory.add(category);
        }


        FiltersNotificationAdapter filterListAdapter = new FiltersNotificationAdapter(getActivity(), listCategory);
        listView.setAdapter(filterListAdapter);

        title_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                getActivity().onBackPressed();
                Fragment fragment = new Notification();
                activityCallback.onButtonClick(fragment, false);


            }
        });
        return view;
    }


    private void initialVariables() {
        common = new Common(getActivity());

    }


}

