package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanganan.app.R;
import com.sanganan.app.activities.StartSearchPage;
import com.sanganan.app.model.GeneralNotification;

import java.util.ArrayList;

/**
 * Created by root on 2/9/16.
 */
public class GMemberNotificationsAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    Typeface ubuntuB, wsRegular, wsLight, karlaB;
    ArrayList<GeneralNotification> generalNotificationArraylist;

    public GMemberNotificationsAdapter(Context context, ArrayList<GeneralNotification> generalNotificationArraylist) {
        this.context = context;
        this.generalNotificationArraylist = generalNotificationArraylist;
    }

    public void updateReceiptsList(ArrayList<GeneralNotification> newlist) {
        this.generalNotificationArraylist = newlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return generalNotificationArraylist.size();
    }

    @Override
    public GeneralNotification getItem(int i) {
        return generalNotificationArraylist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(StartSearchPage.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.from_members, null);
            viewHolder = new ViewHolder();
            ubuntuB = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
            karlaB = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");
            wsRegular = Typeface.createFromAsset(context.getAssets(), "WorkSans-Regular.ttf");
            wsLight = Typeface.createFromAsset(context.getAssets(), "WorkSans-Light.ttf");
            viewHolder.imgSeverity = (ImageView) view.findViewById(R.id.imgSeverity);
            viewHolder.topTitle = (TextView) view.findViewById(R.id.topTitle);
            viewHolder.topTitle.setTypeface(karlaB);
            viewHolder.timeField = (TextView) view.findViewById(R.id.timeField);
            viewHolder.timeField.setTypeface(wsLight);
            viewHolder.causeField = (TextView) view.findViewById(R.id.causeField);
            viewHolder.causeField.setTypeface(wsRegular);

            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        viewHolder.topTitle.setText(generalNotificationArraylist.get(i).getTitle());
        viewHolder.causeField.setText(generalNotificationArraylist.get(i).getText());
        viewHolder.timeField.setText(generalNotificationArraylist.get(i).getDatecreated());
        if (!generalNotificationArraylist.get(i).getSeverity().equalsIgnoreCase("")) {
            if (generalNotificationArraylist.get(i).getSeverity().equalsIgnoreCase("1")) {
                viewHolder.imgSeverity.setImageResource(R.drawable.ic_info_bottom);
            } else if (generalNotificationArraylist.get(i).getSeverity().equalsIgnoreCase("2")) {
                viewHolder.imgSeverity.setImageResource(R.drawable.ic_critical_bottom);
            } else if (generalNotificationArraylist.get(i).getSeverity().equalsIgnoreCase("3")) {
                viewHolder.imgSeverity.setImageResource(R.drawable.ic_needaction_bottom);
            }
        }else{

        }
        return view;
    }

    private class ViewHolder {
        TextView topTitle, causeField, timeField;
        ImageView imgSeverity;
    }
}
