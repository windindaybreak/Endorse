package com.example.user.shell;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.shell.UI.Activity.R;

import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {

    List<Map<String, Object>> list;
    LayoutInflater inflater;
    Cursor cursor;
    Context context;
    public int flag;

    public MyAdapter(Context context, int flag) {
        this.context = context;
        this.inflater = LayoutInflater.from( context );
        this.flag = flag;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Map getItem(int position) {
        return list.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate( R.layout.item, null );
        }
        switch (flag) {
            case 0:
                TextView CL_Date = convertView.findViewById( R.id.text0 );
                Map map = list.get( position );
                CL_Date.setText( (CharSequence) map.get( "开始日期" ) );
                break;
            case 1:
                TextView JD_Date = convertView.findViewById( R.id.text0 );
                Map map1 = list.get( position );
                JD_Date.setText( (CharSequence) map1.get( "接待时间" ) );
                break;
            case 2:
                TextView JT_Date = convertView.findViewById( R.id.text0 );
                Map map2 = list.get( position );
                JT_Date.setText( (CharSequence) map2.get( "出行日期" ) );
                break;
            case 3:
                TextView CLReason = convertView.findViewById( R.id.text0 );
                Map map3 = list.get( position );
                CLReason.setText( (CharSequence) map3.get( "理由" ) );
                break;
            case 4:
                TextView PX_Date = convertView.findViewById( R.id.text0 );
                Map map4 = list.get( position );
                PX_Date.setText( (CharSequence) map4.get( "培训日期" ) );
                break;
            case 5:
                TextView CGProcurementDate = convertView.findViewById( R.id.text0 );
                Map map5 = list.get( position );
                CGProcurementDate.setText( (CharSequence) map5.get( "采购日期" ) );
                break;
            case 6:
                TextView ZX_Date = convertView.findViewById( R.id.text0 );
                Map map6 = list.get( position );
                ZX_Date.setText( (CharSequence) map6.get( "咨询日期" ) );
                break;
            case 7:
                TextView ZLDate = convertView.findViewById( R.id.text0 );
                Map map7 = list.get( position );
                ZLDate.setText( (CharSequence) map7.get( "租赁日期" ) );
                break;
            case 8:
                TextView YSEstimatedDate = convertView.findViewById( R.id.text0 );
                Map map8 = list.get( position );
                YSEstimatedDate.setText( (CharSequence) map8.get( "预计日期" ) );
                break;
        }
        return convertView;
    }
}
