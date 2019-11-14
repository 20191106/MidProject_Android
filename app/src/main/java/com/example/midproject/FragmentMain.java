package com.example.midproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;

public class FragmentMain extends Fragment {
    public FragmentMain(){

    }

    GridView calendarGv;
    ListView memoLv;

    TextView main_todayTv;
    MyAdapter adapter_Gv;
    MyAdapter_Memo adapter_Lv;
    Button main_pastBtn;
    Button main_futureBtn;


    private void setMain_todayTv(Calendar cal){
        main_todayTv.setText(cal.get(Calendar.YEAR) + "." + (cal.get(Calendar.MONTH) + 1) + "");
    }

    private void setMemoLv(Calendar cal, MainActivity m){
        adapter_Lv.notifyDataSetChanged();
        handler.sendEmptyMessageDelayed(getNewestMemo(cal, m),500);
    }

    private int getNewestMemo(Calendar cal, MainActivity m){
        // arrMemo에서 cal날짜 이후의 메모의 위치 return
        // 미래의 할일이 없다면 arrMemo.size() - 1 return
        int i;
        for (i = 0; i < m.arrMemo.size(); i++) {
            if (m.arrMemo.get(i).date.get(Calendar.YEAR) >= cal.get(Calendar.YEAR)) {
                if (m.arrMemo.get(i).date.get(Calendar.MONTH) >= cal.get(Calendar.MONTH)){
                    if (m.arrMemo.get(i).date.get(Calendar.DATE) >= cal.get(Calendar.DATE)){
                        return i;
                    }
                }
            }
        }
        return i - 1;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            memoLv.setSelection(msg.what);
        }
    };

    public boolean onBackPressed(){
        boolean isEnd = false;
        if (calendarGv.getVisibility() == View.VISIBLE) isEnd = true;
        else calendarGv.setVisibility(View.VISIBLE);
        return isEnd;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main, container, false);
        final MainActivity m = ((MainActivity)getActivity());

        final Calendar cal = Calendar.getInstance();

        calendarGv = v.findViewById(R.id.main_calendarGv);
        memoLv = v.findViewById(R.id.main_memoLv);
        memoLv.requestFocusFromTouch();

        main_todayTv = v.findViewById(R.id.main_todayTv);
        main_pastBtn = v.findViewById(R.id.main_pastBtn);
        main_futureBtn = v.findViewById(R.id.main_futureBtn);

        calendarGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!m.arrDay.get(position).date.equals("")){
                    calendarGv.setVisibility(View.GONE);
                    cal.set(Calendar.DATE, Integer.parseInt(m.arrDay.get(position).date));
                    setMemoLv(cal, m);
                }
            }
        });

        memoLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    m.popfrag(m.FRAG_NEW, -1);
                }
                else{
                    m.popfrag(m.FRAG_VIEW, position);
                }
            }
        });

        main_pastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH,-1);
                m.getAllFromDB(cal);
                adapter_Gv.notifyDataSetChanged();
            }
        });

        main_futureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH,1);
                m.getAllFromDB(cal);
                adapter_Gv.notifyDataSetChanged();
            }
        });

        adapter_Gv = new MyAdapter(m, m.arrDay);
        calendarGv.setAdapter(adapter_Gv);
        adapter_Lv = new MyAdapter_Memo(m, m.arrMemo);
        memoLv.setAdapter(adapter_Lv);

        DbSetting.dbInit(m);

        setMain_todayTv(cal);

        m.getAllFromDB(cal);
        adapter_Gv.notifyDataSetChanged();
        adapter_Lv.notifyDataSetChanged();

        return v;
    }
}
