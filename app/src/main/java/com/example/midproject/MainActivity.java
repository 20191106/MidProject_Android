package com.example.midproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    ArrayList<Memo> arrMemo = new ArrayList<>();
    ArrayList<Day> arrDay = new ArrayList<>();

    GridView calendarGv;
    ListView memoLv;

    MyAdapter adapter_Gv;
    MyAdapter_Memo adapter_Lv;

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

        adapter_Gv = new MyAdapter(this);
        calendarGv.setAdapter(adapter_Gv);
        adapter_Lv = new MyAdapter_Memo(this);
        memoLv.setAdapter((adapter_Lv));

        dbInit();

        //test
        clearDB();
        dbInit();
        insertDB(2019, 10, 7, "asdf");
        insertDB(2019, 10, 11, "asdf");

        getFromDB();

    }

    private  void clearDB(){
        //only for test
        SQLiteDatabase db = openOrCreateDatabase("my_db.db", Context.MODE_PRIVATE, null);
        db.execSQL("DELETE FROM myDb");
        db.execSQL("DROP TABLE myDb");
        db.close();
    }

    private void dbInit() {
        SQLiteDatabase db = openOrCreateDatabase("my_db.db", Context.MODE_PRIVATE, null);

        //4개의 컬럼을 가진 테이블을 만든다
        db.execSQL("CREATE TABLE IF NOT EXISTS myDb("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "year INT,"
                + "month INT,"
                + "day INT,"
                + "context TEXT" + ");");

        db.close();
    }

    private void insertDB(int year, int month, int day, String context){
        SQLiteDatabase db = openOrCreateDatabase("my_db.db", Context.MODE_PRIVATE, null);

        //데이터 집어 넣기
        db.execSQL("INSERT INTO myDb (year, month, day, context) VALUES ('" + year + "','" + month + "','" + day + "','" + context + "');");

        db.close();
    }

    private void getFromDB(){

        // 1
        arrDay.clear();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        int startDay = cal.get(Calendar.DAY_OF_WEEK);
        int max = cal.getActualMaximum(Calendar.DATE);
        cal.set(Calendar.DATE, max);
        for (int i = 0; i < startDay - 1; i++){
            arrDay.add(new Day("", ""));
        }
        for (int i = 0; i < max; i ++){
            arrDay.add(new Day((i + 1) + "", ""));
        }
        if (cal.get(Calendar.DAY_OF_WEEK_IN_MONTH) <= 5){
            for (int i = 0; i < 7; i++){
                arrDay.add(new Day("", ""));
            }
        }

        // 2
        SQLiteDatabase db = openOrCreateDatabase("my_db.db", Context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * FROM myDb ORDER BY year, month, day", null);
        c.moveToFirst();
        arrMemo.clear();
        while (c.isAfterLast() == false) {
            cal = Calendar.getInstance();
            Calendar memoCal = Calendar.getInstance();
            memoCal.set(c.getInt(1), c.getInt(2), c.getInt(3));
            String context = c.getString(4);
            arrMemo.add(new Memo(c.getInt(0), memoCal, context));
            if (cal.get(Calendar.MONTH) == memoCal.get(Calendar.MONTH)){
                arrDay.get(startDay + cal.get(Calendar.DATE) - 2).preView = context;
            }
            c.moveToNext();
        }
        adapter_Gv.notifyDataSetChanged();
        c.close();
        db.close();
    }

    class MyAdapter extends ArrayAdapter {
        LayoutInflater lnf;

        public MyAdapter(Activity context) {
            super(context, R.layout.calendar, arrDay);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    class MyAdapter_Memo extends ArrayAdapter {
        LayoutInflater lnf;

        public MyAdapter_Memo(Activity context) {
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

            viewHolder.dateTv.setText(arrMemo.get(position).date.get(Calendar.DATE) + "");
            viewHolder.preViewTv.setText(arrMemo.get(position).context);

            return convertView;
        }
    }
}
