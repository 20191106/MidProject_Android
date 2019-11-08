package com.example.midproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

class MyAdapter extends ArrayAdapter {
    LayoutInflater lnf;
    ArrayList<Day> arrDay;

    public MyAdapter(Activity context, ArrayList<Day> arrDay) {
        super(context, R.layout.calendar, arrDay);
        lnf = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrDay = arrDay;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrDay.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return arrDay.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        RowHolder viewHolder;

        if (convertView == null){
            convertView = lnf.inflate(R.layout.calendar, parent, false);
            viewHolder = new RowHolder();

            viewHolder.dateTv = convertView.findViewById(R.id.calendar_dateTv);
            viewHolder.preViewTv = convertView.findViewById(R.id.calendar_preViewTv);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (RowHolder)convertView.getTag();
        }

        viewHolder.dateTv.setText(arrDay.get(position).date);
        viewHolder.preViewTv.setText(arrDay.get(position).preView);

        return convertView;
    }
}