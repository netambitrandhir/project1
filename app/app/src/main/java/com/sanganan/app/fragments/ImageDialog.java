package com.sanganan.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.sanganan.app.R;
import com.squareup.picasso.Picasso;

/**
 * Created by root on 22/9/16.
 */

public class ImageDialog extends DialogFragment {

    View view;
    ImageView imageView, crossL;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.imagedialoglayout, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Bundle b = getArguments();

        String imageLink = b.getString("imagelink");


        imageView = (ImageView) view.findViewById(R.id.imagefull);
        crossL = (ImageView) view.findViewById(R.id.crossnew);


        Picasso.with(getActivity()).load(imageLink).placeholder(R.drawable.galleryplacholder).into(imageView);

        crossL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return view;
    }
}
