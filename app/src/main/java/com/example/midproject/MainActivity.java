package com.example.midproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.EditText;
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

    TextView main_todayTv;
    MyAdapter adapter_Gv;
    MyAdapter_Memo adapter_Lv;
    Button main_pastBtn;
    Button main_futureBtn;

    FragmentNew fragNew = new FragmentNew();
    FragmentView fragView = new FragmentView();

    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Calendar cal = Calendar.getInstance();

        calendarGv = findViewById(R.id.main_calendarGv);
        memoLv = findViewById(R.id.main_memoLv);
        memoLv.requestFocusFromTouch();
        main_todayTv = findViewById(R.id.main_todayTv);
        main_pastBtn = findViewById(R.id.main_pastBtn);
        main_futureBtn = findViewById(R.id.main_futureBtn);

        calendarGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!arrDay.get(position).date.equals("")){
                    calendarGv.setVisibility(View.GONE);
                    cal.set(Calendar.DATE, Integer.parseInt(arrDay.get(position).date));
                    setMemoLv(cal);
                }
            }
        });

        memoLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ft.add(R.id.mainLayout, fragNew);
                }
                else{
                    fragView.setPos(position);
                    ft.add(R.id.mainLayout, fragView);
                }
                ft.commit();
            }
        });

        main_pastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH,-1);
                getFromDB(cal);
            }
        });

        main_futureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH,1);
                getFromDB(cal);
            }
        });

        adapter_Gv = new MyAdapter(this, arrDay);
        calendarGv.setAdapter(adapter_Gv);
        adapter_Lv = new MyAdapter_Memo(this, arrMemo);
        memoLv.setAdapter((adapter_Lv));

//        //test
//        DbSetting.clearDB(this);
//        //test

        DbSetting.dbInit(this);

        getFromDB(cal);
    }

    private void setMain_todayTv(Calendar cal){
        main_todayTv.setText(cal.get(Calendar.YEAR) + "." + (cal.get(Calendar.MONTH) + 1) + "");
    }

    public void getFromDB(Calendar cal){
        setMain_todayTv(cal);
        setMemoLv(cal);

        // 1
        arrDay.clear();
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
        for (int i = 0; i < 43 - startDay - max; i++){
            arrDay.add(new Day("", ""));
        }

        // 2
        SQLiteDatabase db = openOrCreateDatabase("my_db.db", Context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * FROM myDb ORDER BY year, month, day", null);
        c.moveToFirst();
        arrMemo.clear();
        arrMemo.add(new Memo(-1, cal, "새로운 메모 추가하기"));
        while (c.isAfterLast() == false) {
            Calendar memoCal = Calendar.getInstance();
            memoCal.set(c.getInt(1), c.getInt(2), c.getInt(3));
            String context = c.getString(4);
            arrMemo.add(new Memo(c.getInt(0), memoCal, context));
            if (cal.get(Calendar.MONTH) == memoCal.get(Calendar.MONTH)){
                arrDay.get(startDay + memoCal.get(Calendar.DATE) - 2).preView = context;
            }
            c.moveToNext();
        }

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
        handler.sendEmptyMessageDelayed(getNewestMemo(cal),500);
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

    public String dateToString(Calendar cal){
        return cal.get(Calendar.YEAR) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.DATE);
    }

    public int etToInt(EditText et){
        return Integer.parseInt(et.getText().toString().trim());
    }

    public void dismissFragmentView(){
        ft.remove(fragView);
        ft.commit();
    }

    public void dismissFragmentNew(){
        ft.remove(fragNew);
        ft.commit();
    }

    public void popUpDeleteConfirm(final int position){
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
                getFromDB(arrMemo.get(position).date);
                confirmDialog.dismiss();
                dismissFragmentView();
            }
        });

        confirmDialog.show();
    }

    public void popUpCancelConfirm(){
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
                dismissFragmentNew();
            }
        });

        confirmDialog.show();
    }
}