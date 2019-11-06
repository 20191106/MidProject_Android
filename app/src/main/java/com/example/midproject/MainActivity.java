package com.example.midproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{
    ArrayList<Memo> arrMemo;
    ArrayList<Day> arrDay;

    GridView calendarGv;
    ListView memoLv;

    MyAdapter adapter;

    public class CalendarHolder {
        TextView dateTv;
        TextView preViewTv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarGv = findViewById(R.id.main_calendarGv);
        memoLv = findViewById(R.id.main_memoLv);

        calendarGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        memoLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        adapter = new MyAdapter(this);
        calendarGv.setAdapter(adapter);

        Calendar cal = Calendar.getInstance(); //현재 내 핸드폰의 시간 2019.11.06 18:21
        int dow = cal.get(Calendar.DAY_OF_WEEK); // 일 = 1 월 = 2 ... 토 = 7
        cal.add(Calendar.DATE, 4); // 11.10
        cal.set(Calendar.DATE, 1); // 11.01
        cal.set(Calendar.MONTH, 11); // 12.01 주의
        int max = cal.getActualMaximum(Calendar.DATE); // 이번달이 몇일까지 있는가

    }

    private void updateDB(){
        //TODO
        // db -> arrMemo
        // cal, arrMemo -> arrDay

        adapter.notifyDataSetChanged();
    }

    class MyAdapter extends ArrayAdapter {
        LayoutInflater lnf;

        public MyAdapter(Activity context) {
            super(context, R.layout.calendar, arrMemo);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            CalendarHolder viewHolder;

            if (convertView == null){
                convertView = lnf.inflate(R.layout.calendar, parent, false);
                viewHolder = new CalendarHolder();

                viewHolder.dateTv = convertView.findViewById(R.id.calendar_dateTv);
                viewHolder.preViewTv = convertView.findViewById(R.id.calendar_preViewTv);

                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (CalendarHolder)convertView.getTag();
            }

            viewHolder.dateTv.setText(arrDay.get(position).date + "");
            viewHolder.preViewTv.setText(arrDay.get(position).preView);

            return convertView;
        }
    }
}
