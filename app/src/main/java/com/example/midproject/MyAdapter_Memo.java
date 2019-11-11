package com.example.midproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class MyAdapter_Memo extends ArrayAdapter {
    LayoutInflater lnf;
    ArrayList<Memo> arrMemo;

    public MyAdapter_Memo(Activity context, ArrayList<Memo> arrMemo) {
        super(context, R.layout.calendar, arrMemo);
        lnf = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrMemo =  arrMemo;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrMemo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return arrMemo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        RowHolder viewHolder;

        if (convertView == null){
            convertView = lnf.inflate(R.layout.memo, parent, false);
            viewHolder = new RowHolder();

            viewHolder.dateTv = convertView.findViewById(R.id.memo_dateTv);
            viewHolder.preViewTv = convertView.findViewById(R.id.memo_preViewTv);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (RowHolder)convertView.getTag();
        }

        if (position == 0){
            viewHolder.dateTv.setText("");
        }
        else{
            viewHolder.dateTv.setText(arrMemo.get(position).date.get(Calendar.DATE) + "");
        }
        viewHolder.preViewTv.setText(arrMemo.get(position).context);

        return convertView;
    }
}