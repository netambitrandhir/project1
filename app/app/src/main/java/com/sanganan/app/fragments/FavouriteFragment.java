package com.sanganan.app.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.adapters.FavouriteAdapter;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.interfaces.DrawerLocker;

import java.util.ArrayList;

/**
 * Created by root on 27/9/16.
 */
public class FavouriteFragment extends BaseFragment {

    View view;
    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    int drawable[] = {R.drawable.maid_fav, R.drawable.nearby_fav};
    TextView title;
    ImageView alert;
    Typeface ubantuBold;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.favourite_main, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        initializeVariables(view);


        ubantuBold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        title.setTypeface(ubantuBold);
        title.setText("My Favourites");
        alert.setVisibility(View.GONE);

        arrayList.clear();

        if (Constants.rolesGivenToUser.contains("Helpers_Tab")) {
            arrayList.add("Community Helpers");
        }
        if (Constants.rolesGivenToUser.contains("Nearby_Tab")) {
            arrayList.add("NearBy");
        }


        FavouriteAdapter favouriteAdapter = new FavouriteAdapter(getActivity(), arrayList, drawable);
        listView.setAdapter(favouriteAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList.get(position).equalsIgnoreCase("Community Helpers")) {
                    Fragment fragment1 = new FavouriteHelpersFragment();
                    activityCallback.onButtonClick(fragment1, false);
                } else if (arrayList.get(position).equalsIgnoreCase("NearBy")) {
                    Fragment fragment1 = new FavouriteNearByFragment();
                    activityCallback.onButtonClick(fragment1, false);
                }
            }
        });


        return view;
    }

    private void initializeVariables(View view) {
        listView = (ListView) view.findViewById(R.id.listBaseCategory);
        title = (TextView) view.findViewById(R.id.titletextView);
        alert = (ImageView) getActivity().findViewById(R.id.alert);

    }
}
