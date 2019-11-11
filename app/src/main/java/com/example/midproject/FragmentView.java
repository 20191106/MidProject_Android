package com.example.midproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FragmentView extends Fragment {
    public FragmentView() {
    }

    ArrayList<Memo> arrMemo = new ArrayList<>();
    boolean isEditMode;
    int position;

    public void setPos(int position){
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.memoview, container, false);
        final MainActivity m = ((MainActivity)getActivity());

        arrMemo = m.arrMemo;
        isEditMode = false;

        final TextView memoview_Tv = v.findViewById(R.id.memoview_Tv);
        final Button memoview_noBtn = v.findViewById(R.id.memoview_noBtn);
        final Button memoview_yesBtn = v.findViewById(R.id.memoview_yesBtn);
        final TextView memoview_dateTv = v.findViewById(R.id.memoview_dateTv);
        final EditText memoview_Et = v.findViewById(R.id.memoview_Et);
        final Button memoview_closeBtn = v.findViewById(R.id.memoview_closeBtn);

        memoview_Tv.setText(arrMemo.get(position).context);
        memoview_Et.setText(arrMemo.get(position).context);
        memoview_dateTv.setText(m.dateToString(arrMemo.get(position).date));

        memoview_noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode){
                    //취소
                    //just pop down
                    m.popUpCancelConfirm();
                }
                else{
                    //삭제
                    //delete db, pop down
                    m.popUpDeleteConfirm(position);
                }
            }
        });
        memoview_yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode){
                    //확인
                    //update db
                    DbSetting.updateDB(arrMemo.get(position)._id, memoview_Et.getText().toString(), m);
                    m.getFromDB(arrMemo.get(position).date);
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
                    m.dismissFragmentView();
                }
            }
        });

        return v;
    }
}