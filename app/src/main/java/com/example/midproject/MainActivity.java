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
import android.widget.EditText;
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
    Button main_pastBtn;
    Button main_futureBtn;

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
                if (position == 0)
                    popUpMemoNew();
                    //TODO 추가하기가 항상 맨위로 오도록
                else
                    popUpMemoView(position);
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

    private void getFromDB(Calendar cal){
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

    private String dateToString(Calendar cal){
        return cal.get(Calendar.YEAR) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.DATE);
    }

    boolean isEditMode = false;

    public void popUpMemoView(final int position) {
        final LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View v2 = (View)inflater.inflate(R.layout.memoview, null);

        final AlertDialog.Builder ab = new AlertDialog.Builder((this));
        ab.setView(v2);

        final TextView memoview_Tv = v2.findViewById(R.id.memoview_Tv);
        final Button memoview_noBtn = v2.findViewById(R.id.memoview_noBtn);
        final Button memoview_yesBtn = v2.findViewById(R.id.memoview_yesBtn);
        final TextView memoview_dateTv = v2.findViewById(R.id.memoview_dateTv);
        final EditText memoview_Et = v2.findViewById(R.id.memoview_Et);
        final Button memoview_closeBtn = v2.findViewById(R.id.memoview_closeBtn);

        final AlertDialog temp = ab.create();

        memoview_Tv.setText(arrMemo.get(position).context);
        memoview_Et.setText(arrMemo.get(position).context);
        memoview_dateTv.setText(dateToString(arrMemo.get(position).date));

        memoview_noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode){
                    //취소
                    //just pop down
                    popUpCancelConfirm(temp);
                }
                else{
                    //삭제
                    //delete db, pop down
                    popUpDeleteConfirm(position, temp);
                }
            }
        });
        memoview_yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode){
                    //확인
                    //update db
                    DbSetting.updateDB(arrMemo.get(position)._id, memoview_Et.getText().toString(), MainActivity.this);
                    getFromDB(arrMemo.get(position).date);
                    memoview_Tv.setText(arrMemo.get(position).context);

                    memoview_noBtn.setText("삭제");
                    memoview_yesBtn.setText("수정");
                    memoview_closeBtn.setText("X");
                    memoview_Et.setVisibility(View.GONE);
                    memoview_Tv.setVisibility(View.VISIBLE);
                    isEditMode = false;
                }
                else{
                    //수정
                    //view -> edit
                    memoview_noBtn.setText("취소");
                    memoview_yesBtn.setText("확인");
                    memoview_closeBtn.setText("<-");
                    memoview_Et.setVisibility(View.VISIBLE);
                    memoview_Tv.setVisibility(View.GONE);
                    isEditMode = true;
                }
            }
        });
        memoview_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode){
                    memoview_noBtn.setText("취소");
                    memoview_yesBtn.setText("확인");
                    memoview_closeBtn.setText("X");
                    memoview_Et.setVisibility(View.GONE);
                    memoview_Tv.setVisibility(View.VISIBLE);
                    isEditMode = false;
                }
                else{
                    temp.dismiss();
                }
            }
        });
        temp.show();
    }
    
    public void popUpMemoNew() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View v2 = (View)inflater.inflate(R.layout.memonew, null);

        final AlertDialog.Builder ab = new AlertDialog.Builder((this));
        ab.setView(v2);

        final EditText memonew_Et = v2.findViewById(R.id.memonew_Et);
        final EditText memonew_yearEt = v2.findViewById(R.id.memonew_yearEt);
        final EditText memonew_monthEt = v2.findViewById(R.id.memonew_monthEt);
        final EditText memonew_dateEt = v2.findViewById(R.id.memonew_dateEt);
        final Button memonew_closeBtn = v2.findViewById(R.id.memonew_closeBtn);
        final Button memonew_noBtn = v2.findViewById(R.id.memonew_noBtn);
        final Button memonew_yesBtn = v2.findViewById(R.id.memonew_yesBtn);

        final AlertDialog temp = ab.create();

        final Calendar cal = Calendar.getInstance();
        memonew_yearEt.setText(cal.get(Calendar.YEAR) + "");
        memonew_monthEt.setText((cal.get(Calendar.MONTH) + 1) + "");
        memonew_dateEt.setText(cal.get(Calendar.DATE) + "");

        memonew_noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //취소
                //just pop down
                popUpCancelConfirm(temp);
            }
        });
        memonew_yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //저장
                //insert db
                DbSetting.insertDB(etToInt(memonew_yearEt), etToInt(memonew_monthEt) - 1, etToInt(memonew_dateEt), memonew_Et.getText().toString(), MainActivity.this);
                getFromDB(cal);
                temp.dismiss();
            }
        });
        memonew_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp.dismiss();
            }
        });
        temp.show();
    }

    private int etToInt(EditText et){
        return Integer.parseInt(et.getText().toString().trim());
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
                getFromDB(arrMemo.get(position).date);
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