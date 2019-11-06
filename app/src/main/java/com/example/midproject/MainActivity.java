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

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar cal = Calendar.getInstance(); //현재 내 핸드폰의 시간 2019.11.06 18:21
        int dow = cal.get(Calendar.DAY_OF_WEEK); // 일 = 1 월 = 2 ... 토 = 7
        cal.add(Calendar.DATE, 4); // 11.10
        cal.set(Calendar.DATE, 1); // 11.01
        cal.set(Calendar.MONTH, 11); // 12.01 주의
        int max = cal.getActualMaximum(Calendar.DATE); // 이번달이 몇일까지 있는가

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
