package com.example.midproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{
    ArrayList<Memo> arrMemo = new ArrayList<>();
    ArrayList<Day> arrDay = new ArrayList<>();

    GridView calendarGv;
    ListView memoLv;

    TextView main_todayTv;
    MyAdapter adapter_Gv;
    MyAdapter_Memo adapter_Lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarGv = findViewById(R.id.main_calendarGv);
        memoLv = findViewById(R.id.main_memoLv);
        memoLv.requestFocusFromTouch();
        main_todayTv = findViewById(R.id.main_todayTv);

        calendarGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO add month, year data
                //TODO add color today
                if (!arrDay.get(position).date.equals("")){
                    calendarGv.setVisibility(View.GONE);
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DATE, Integer.parseInt(arrDay.get(position).date));
                    setMemoLv(cal);
                }
            }
        });

        memoLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popUpMemoView(position);
            }
        });

        adapter_Gv = new MyAdapter(this, arrDay);
        calendarGv.setAdapter(adapter_Gv);
        adapter_Lv = new MyAdapter_Memo(this, arrMemo);
        memoLv.setAdapter((adapter_Lv));

        DbSetting.dbInit(this);

//        //test
//        DbSetting.clearDB(this);
//        DbSetting.dbInit(this);
//        DbSetting.insertDB(2019, 8, 7, "9/7", this);
//        DbSetting.insertDB(2019, 7, 7, "8/14", this);
//        DbSetting.insertDB(2019, 4, 7, "5/5", this);
//        DbSetting.insertDB(2019, 2, 7, "3/7", this);
//        DbSetting.insertDB(2019, 9, 16, "10/16", this);
//        DbSetting.insertDB(2019, 9, 21, "10/21", this);
//        DbSetting.insertDB(2019, 9, 28, "10/28", this);
//        DbSetting.insertDB(2019, 9, 29, "10/29 _ 할일1", this);
//        DbSetting.insertDB(2019, 9, 29, "10/29 _ 할일2", this);
//
//        for (int i = 1; i < 31; i++){
//            DbSetting.insertDB(2019, 10, i, i + "일할일", this);
//        }

        getFromDB();
        setMain_todayTv();
    }

    private void setMain_todayTv(){
        Calendar cal = Calendar.getInstance();
        main_todayTv.setText(cal.get(Calendar.YEAR) + "." + (cal.get(Calendar.MONTH) + 1) + "");
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
                arrDay.get(startDay + memoCal.get(Calendar.DATE) - 2).preView = context;
            }
            c.moveToNext();
        }
        setMemoLv(cal);
        adapter_Gv.notifyDataSetChanged();

        c.close();
        db.close();
    }

    @Override
    public void onBackPressed() {
        if (calendarGv.getVisibility() == View.GONE){
            calendarGv.setVisibility(View.VISIBLE);
        }
        else {
            finish();
        }
    }

    private int getNewestMemo(Calendar cal){
        // arrMemo에서 cal날짜 이후의 메모의 위치 return
        // 미래의 할일이 없다면 arrMemo.size() - 1 return
        int i = 0;
        for (i = 0; i < arrMemo.size(); i++) {
            if (arrMemo.get(i).date.get(Calendar.YEAR) >= cal.get(Calendar.YEAR)) {
                if (arrMemo.get(i).date.get(Calendar.MONTH) >= cal.get(Calendar.MONTH)){
                    if (arrMemo.get(i).date.get(Calendar.DATE) >= cal.get(Calendar.DATE)){
                        return i;
                    }
                }
            }
        }
        return i - 1;
    }

    private void setMemoLv(Calendar cal){
        adapter_Lv.notifyDataSetChanged();
        //handler.sendEmptyMessageDelayed(getNewestMemo(cal),500);
        //TODO remove msg
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            memoLv.setSelection(msg.what);
        }
    };

    private String todayString(){
        Calendar cal = Calendar.getInstance();
        String s = cal.get(Calendar.YEAR) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.DATE);
        return s;
    }

    public void popUpMemoView(final int position) {
        final LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View v2 = (View)inflater.inflate(R.layout.memoview, null);

        final AlertDialog.Builder ab = new AlertDialog.Builder((this));
        ab.setView(v2);

        final TextView memoview_Tv = v2.findViewById(R.id.memoview_Tv);
        final Button memoview_noBtn = v2.findViewById(R.id.memoview_noBtn);
        final Button memoview_yesBtn = v2.findViewById(R.id.memoview_yesBtn);
        final TextView memoview_dateTv = v2.findViewById(R.id.memoview_dateTv);
        final Button memoview_closeBtn = v2.findViewById(R.id.memoview_closeBtn);

        final AlertDialog temp = ab.create();

        memoview_Tv.setText(arrMemo.get(position).context);
        memoview_dateTv.setText(todayString());

        memoview_noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //삭제
                //delete db, pop down
                popUpDeleteConfirm(position, temp);
            }
        });
        memoview_yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //수정
                //popup memo edit
                popUpMemoEdit(position);
            }
        });
        memoview_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp.dismiss();
            }
        });
        temp.show();
    }

    public void popUpMemoEdit(final int position) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View v2 = (View)inflater.inflate(R.layout.memoedit, null);

        final AlertDialog.Builder ab = new AlertDialog.Builder((this));
        ab.setView(v2);

        final TextView memoedit_Et = v2.findViewById(R.id.memoedit_Et);
        final Button memoedit_noBtn = v2.findViewById(R.id.memoedit_noBtn);
        final Button memoedit_yesBtn = v2.findViewById(R.id.memoedit_yesBtn);
        final TextView memoedit_dateTv = v2.findViewById(R.id.memoedit_dateTv);
        final Button memoedit_closeBtn = v2.findViewById(R.id.memoedit_closeBtn);

        final AlertDialog temp = ab.create();

        memoedit_Et.setText(arrMemo.get(position).context);
        memoedit_dateTv.setText(todayString());

        memoedit_noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //취소
                //just pop down
                popUpCancelConfirm(temp);
            }
        });
        memoedit_yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //확인
                //update db
                DbSetting.updateDB(arrMemo.get(position)._id, memoedit_Et.getText().toString(), MainActivity.this);
                getFromDB();
                temp.dismiss();
                //TODO 수정내용 바로 적용되게
            }
        });
        memoedit_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp.dismiss();
            }
        });
        temp.show();
    }

    public void popUpDeleteConfirm(final int position, final AlertDialog parentDialog){
        final LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View vCon = (View)inflater.inflate(R.layout.confirm, null);

        final AlertDialog.Builder ab = new AlertDialog.Builder((this));
        ab.setView(vCon);

        final AlertDialog confirmDialog = ab.create();
        final TextView confirm_Tv = vCon.findViewById(R.id.confirm_Tv);
        final Button confirm_noBtn = vCon.findViewById(R.id.confirm_noBtn);
        final Button confirm_yesBtn = vCon.findViewById(R.id.confirm_yesBtn);

        confirm_Tv.setText("삭제 하시겠습니까 ?");
        confirm_noBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });
        confirm_yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbSetting.deleteDB(arrMemo.get(position)._id, MainActivity.this);
                getFromDB();
                confirmDialog.dismiss();
                parentDialog.dismiss();
            }
        });

        confirmDialog.show();
    }

    public void popUpCancelConfirm(final AlertDialog parentDialog){
        final LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View vCon = (View)inflater.inflate(R.layout.confirm, null);

        final AlertDialog.Builder ab = new AlertDialog.Builder((this));
        ab.setView(vCon);

        final AlertDialog confirmDialog = ab.create();
        final TextView confirm_Tv = vCon.findViewById(R.id.confirm_Tv);
        final Button confirm_noBtn = vCon.findViewById(R.id.confirm_noBtn);
        final Button confirm_yesBtn = vCon.findViewById(R.id.confirm_yesBtn);

        confirm_Tv.setText("취소 하시겠습니까 ?");
        confirm_noBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });
        confirm_yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
                parentDialog.dismiss();
            }
        });

        confirmDialog.show();
    }
}