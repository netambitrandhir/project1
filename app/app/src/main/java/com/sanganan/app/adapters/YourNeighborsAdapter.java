package com.sanganan.app.adapters;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.fragments.PrivatePhoneDialog;
import com.sanganan.app.fragments.SendEmailFragment;
import com.sanganan.app.model.GeneralNotification;
import com.sanganan.app.model.YourNeighbourSearch;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

public class YourNeighborsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Context context;
    Common common;
    private FirebaseAnalytics mFirebaseAnalytics;

    Typeface karlaB, wsM, wsL;

    ArrayList<YourNeighbourSearch> yourNeighbourSearchesArrayList;


    public YourNeighborsAdapter(Context context, ArrayList<YourNeighbourSearch> searchList) {
        this.context = context;
        this.yourNeighbourSearchesArrayList = searchList;
        common = Common.getNewInstance(context);

    }

    public void updateReceiptsList(ArrayList<YourNeighbourSearch> newlist) {
        this.yourNeighbourSearchesArrayList = newlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return yourNeighbourSearchesArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return yourNeighbourSearchesArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (view == null) {

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_your_neighbors_adapter, null);
            viewHolder = new ViewHolder();

            karlaB = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");
            wsL = Typeface.createFromAsset(context.getAssets(), "WorkSans-Light.ttf");
            wsM = Typeface.createFromAsset(context.getAssets(), "WorkSans-Medium.ttf");

            viewHolder.picture = (ImageView) view.findViewById(R.id.picture);
            viewHolder.nameText = (TextView) view.findViewById(R.id.nameText);
            viewHolder.nameText.setTypeface(karlaB);

            viewHolder.relativeLayoutTop = (RelativeLayout) view.findViewById(R.id.relativeLayoutTop);
            viewHolder.relativeLayoutBelow = (RelativeLayout) view.findViewById(R.id.relativeLayoutBelow);
            viewHolder.textInitial = (TextView) view.findViewById(R.id.textNameInitial);
            viewHolder.colorback = (ImageView) view.findViewById(R.id.colorback);

            viewHolder.id_number = (TextView) view.findViewById(R.id.id_number);
            viewHolder.id_number.setTypeface(wsM);

            viewHolder.company_tag = (TextView) view.findViewById(R.id.company_tag);
            viewHolder.company_tag.setTypeface(wsL);

            viewHolder.call_imageView = (ImageView) view.findViewById(R.id.call_imageView);
            viewHolder.message_imageView = (ImageView) view.findViewById(R.id.message_imageView);

            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        String pickLink = yourNeighbourSearchesArrayList.get(i).getProfilePic();

        if (pickLink.endsWith(".png") || pickLink.endsWith(".jpg")) {
            viewHolder.relativeLayoutTop.setVisibility(View.VISIBLE);
            viewHolder.relativeLayoutBelow.setVisibility(View.INVISIBLE);
            Picasso.with(context).load(yourNeighbourSearchesArrayList.get(i).getProfilePic()).resize(1000, 1000).placeholder(R.drawable.galleryplacholder).into(viewHolder.picture);
        } else {

            String str = yourNeighbourSearchesArrayList.get(i).getFirstName();
            if(str.length()==0){
                str = "a";
            }
            str = String.valueOf(str.charAt(0));

            int position = 0;

            for (int j = 0; j < Alphabets.alphabets.size(); j++) {
                if (str.equalsIgnoreCase(Alphabets.alphabets.get(j))) {
                    position = j;
                }
            }

            Picasso.with(context)
                    .load(Alphabets.alphabetsDrawable.get(position))
                    .resize(1000, 1000).placeholder(R.drawable.galleryplacholder)
                    .into(viewHolder.picture);        }

        viewHolder.nameText.setText(yourNeighbourSearchesArrayList.get(i).getFirstName());
        viewHolder.id_number.setText(yourNeighbourSearchesArrayList.get(i).getFlatNbr());
        viewHolder.company_tag.setText(yourNeighbourSearchesArrayList.get(i).getOccupation());

        viewHolder.call_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yourNeighbourSearchesArrayList.get(i).getIsPhonePublic().equalsIgnoreCase("1")) {
                    AlertBoxForImagePic(i);
                } else {
                    PrivatePhoneDialog privatePhoneDialog = new PrivatePhoneDialog();
                    privatePhoneDialog.setRetainInstance(true);
                    Bundle bundle = new Bundle();
                    bundle.putString("email_id", yourNeighbourSearchesArrayList.get(i).getEmailID());
                    bundle.putString("user_id", yourNeighbourSearchesArrayList.get(i).getID());
                    bundle.putString("Username",yourNeighbourSearchesArrayList.get(i).getFirstName());
                    bundle.putString("fromAdapterNeighbours","yes");
                    privatePhoneDialog.setArguments(bundle);
                    privatePhoneDialog.show(((ActionBarActivity) context).getSupportFragmentManager(), "private_phone");

                }
            }
        });

        viewHolder.message_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yourNeighbourSearchesArrayList.get(i).getEmailID().equalsIgnoreCase("1")) {
                } else {
                    Fragment fragment = new SendEmailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("email_id", yourNeighbourSearchesArrayList.get(i).getEmailID());
                    bundle.putString("user_id", yourNeighbourSearchesArrayList.get(i).getID());
                    bundle.putString("Username",yourNeighbourSearchesArrayList.get(i).getFirstName());
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = ((ActionBarActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    fragmentTransaction.commit();


                }

            }
        });

        return view;
    }

    private class ViewHolder {
        TextView nameText, id_number, company_tag;
        ImageView picture;
        RelativeLayout relativeLayoutTop;
        TextView textInitial;
        RelativeLayout relativeLayoutBelow;
        ImageView colorback;
        ImageView call_imageView, message_imageView;


    }


    public void AlertBoxForImagePic(final int i) {


        new AlertDialog.Builder(context)
                .setTitle("Please Wait....")
                .setMessage("Want to call your Neighbour")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String uri = "tel:" + yourNeighbourSearchesArrayList.get(i).getPhoneNbr().trim();
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse(uri));
                                context.startActivity(callIntent);

                                mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                                Bundle logBundle = new Bundle();
                                logBundle.putString("society_id", common.getStringValue(Constants.userRwa));
                                logBundle.putString("user_id", common.getStringValue(Constants.id));
                                mFirebaseAnalytics.logEvent("neighbor_listing_call", logBundle);


                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }


                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

    }
}
