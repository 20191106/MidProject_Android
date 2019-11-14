package com.example.midproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{

    int curScr = 0;
    public final static int FRAG_MAIN = 0;
    public final static int FRAG_VIEW = 1;
    public final static int FRAG_NEW = 2;

    ArrayList<Memo> arrMemo = new ArrayList<>();
    ArrayList<Day> arrDay = new ArrayList<>();

    FragmentNew fragNew = new FragmentNew();
    FragmentView fragView = new FragmentView();
    FragmentMain fragMain = new FragmentMain();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar cal = Calendar.getInstance();
        getAllFromDB(cal);

//        //test
//        DbSetting.clearDB(this);
//        //test

        popfrag(FRAG_MAIN, -1);
    }

    @Override
    public void onBackPressed() {
        switch (curScr){
            case FRAG_MAIN:
                if(fragMain.onBackPressed()) finish();
                break;
            case FRAG_VIEW:
                popfrag(FRAG_MAIN, -1);
                break;
            case FRAG_NEW:
                popUpCancelConfirm();
                break;
        }
    }

    public void popfrag(int fragNum, int position){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (fragNum){
            case FRAG_MAIN:
                ft.replace(R.id.mainLayout, fragMain);
                curScr = FRAG_MAIN;
                break;
            case FRAG_VIEW:
                fragView.setPos(position);
                ft.replace(R.id.mainLayout, fragView);
                curScr = FRAG_VIEW;
                break;
            case FRAG_NEW:
                ft.replace(R.id.mainLayout, fragNew);
                curScr = FRAG_NEW;
                break;
        }
        ft.commit();
    }


    public void getAllFromDB(Calendar cal){
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


        c.close();
        db.close();
    }

    public void getArrMemoFromDB(){
        SQLiteDatabase db = openOrCreateDatabase("my_db.db", Context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * FROM myDb ORDER BY year, month, day", null);
        c.moveToFirst();
        arrMemo.clear();
        Calendar cal = Calendar.getInstance();
        arrMemo.add(new Memo(-1, cal, "새로운 메모 추가하기"));

        while (c.isAfterLast() == false) {
            Calendar memoCal = Calendar.getInstance();
            memoCal.set(c.getInt(1), c.getInt(2), c.getInt(3));
            String context = c.getString(4);
            arrMemo.add(new Memo(c.getInt(0), memoCal, context));
            c.moveToNext();
        }

        c.close();
        db.close();
    }

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
                getAllFromDB(arrMemo.get(position).date);
                confirmDialog.dismiss();
                popfrag(FRAG_MAIN, -1);
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
                popfrag(FRAG_MAIN, -1);
            }
        });

        confirmDialog.show();
    }
}