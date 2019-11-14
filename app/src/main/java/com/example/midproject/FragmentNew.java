package com.example.midproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class FragmentNew extends Fragment {
    public FragmentNew(){

    }

    EditText memonew_Et;
    EditText memonew_yearEt;
    EditText memonew_monthEt;
    EditText memonew_dateEt;
    Button memonew_closeBtn;
    Button memonew_noBtn;
    Button memonew_yesBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.memonew, container, false);
        final MainActivity m = ((MainActivity)getActivity());

        memonew_Et = v.findViewById(R.id.memonew_Et);
        memonew_yearEt = v.findViewById(R.id.memonew_yearEt);
        memonew_monthEt = v.findViewById(R.id.memonew_monthEt);
        memonew_dateEt = v.findViewById(R.id.memonew_dateEt);
        memonew_closeBtn = v.findViewById(R.id.memonew_closeBtn);
        memonew_noBtn = v.findViewById(R.id.memonew_noBtn);
        memonew_yesBtn = v.findViewById(R.id.memonew_yesBtn);

        final Calendar cal = Calendar.getInstance();
        memonew_yearEt.setText(cal.get(Calendar.YEAR) + "");
        memonew_monthEt.setText((cal.get(Calendar.MONTH) + 1) + "");
        memonew_dateEt.setText(cal.get(Calendar.DATE) + "");

        memonew_noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //취소
                //just pop down
                m.popUpCancelConfirm();
            }
        });
        memonew_yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //저장
                //insert db
                DbSetting.insertDB(m.etToInt(memonew_yearEt), m.etToInt(memonew_monthEt) - 1, m.etToInt(memonew_dateEt), memonew_Et.getText().toString(), m);
                m.getAllFromDB(cal);
                m.popfrag(m.FRAG_MAIN, -1);
            }
        });
        memonew_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.popfrag(m.FRAG_MAIN, -1);
            }
        });
        return v;
    }
}
