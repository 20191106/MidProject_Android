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

public class FragmentView extends Fragment {
    public FragmentView() {
    }

    boolean isEditMode;
    int position;

    public void setPos(int position){
        this.position = position;
    }

    TextView memoview_Tv;
    Button memoview_noBtn;
    Button memoview_yesBtn;
    TextView memoview_dateTv;
    EditText memoview_Et;
    Button memoview_closeBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.memoview, container, false);
        final MainActivity m = ((MainActivity)getActivity());

        m.getArrMemoFromDB();
        isEditMode = false;

        memoview_Tv = v.findViewById(R.id.memoview_Tv);
        memoview_noBtn = v.findViewById(R.id.memoview_noBtn);
        memoview_yesBtn = v.findViewById(R.id.memoview_yesBtn);
        memoview_dateTv = v.findViewById(R.id.memoview_dateTv);
        memoview_Et = v.findViewById(R.id.memoview_Et);
        memoview_closeBtn = v.findViewById(R.id.memoview_closeBtn);

        memoview_Tv.setText(m.arrMemo.get(position).context);
        memoview_Et.setText(m.arrMemo.get(position).context);
        memoview_dateTv.setText(m.dateToString(m.arrMemo.get(position).date));

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
                    DbSetting.updateDB(m.arrMemo.get(position)._id, memoview_Et.getText().toString(), m);
                    m.getAllFromDB(m.arrMemo.get(position).date);
                    memoview_Tv.setText(m.arrMemo.get(position).context);

                    //edit -> view
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
                    //edit -> view
                    memoview_noBtn.setText("삭제");
                    memoview_yesBtn.setText("수정");
                    memoview_closeBtn.setText("X");
                    memoview_Et.setVisibility(View.GONE);
                    memoview_Tv.setVisibility(View.VISIBLE);
                    isEditMode = false;
                }
                else{
                    m.popfrag(m.FRAG_MAIN, -1);
                }
            }
        });

        return v;
    }
}