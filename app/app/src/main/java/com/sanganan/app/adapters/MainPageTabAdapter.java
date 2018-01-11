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
import com.sanganan.app.model.TabData;
import java.util.ArrayList;

/**
 * Created by root on 7/3/17.
 */

public class MainPageTabAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Context context;
    Typeface ubantuM;
    ArrayList<TabData> tabDatas;


    public MainPageTabAdapter(Context context, ArrayList<TabData> tabDatas) {
        this.context = context;
        this.tabDatas = tabDatas;
    }

    @Override
    public int getCount() {
        return tabDatas.size();
    }

    @Override
    public TabData getItem(int i) {
        return tabDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (view == null) {

            viewHolder = new ViewHolder();
            ubantuM = Typeface.createFromAsset(context.getAssets(),"Ubuntu-M.ttf");

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.tab_single_view, null);

            viewHolder.countTab = (TextView) view.findViewById(R.id.txt_count_tab);
            viewHolder.imageTab = (ImageView) view.findViewById(R.id.image_container);
            viewHolder.countTab.setTypeface(ubantuM);


            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.imageTab.setImageResource(getItem(i).getTabImage());
        if(getItem(i).getTabData().equalsIgnoreCase("0")){
            viewHolder.countTab.setVisibility(View.GONE);
        }
        else{
            viewHolder.countTab.setVisibility(View.VISIBLE);
            viewHolder.countTab.setText(getItem(i).getTabData());
        }

        return view;
    }



    private class ViewHolder {
        TextView countTab;
        ImageView imageTab;
    }
}
