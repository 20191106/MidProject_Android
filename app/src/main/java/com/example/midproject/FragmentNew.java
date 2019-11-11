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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.memonew, container, false);
        final MainActivity m = ((MainActivity)getActivity());

        final EditText memonew_Et = v.findViewById(R.id.memonew_Et);
        final EditText memonew_yearEt = v.findViewById(R.id.memonew_yearEt);
        final EditText memonew_monthEt = v.findViewById(R.id.memonew_monthEt);
        final EditText memonew_dateEt = v.findViewById(R.id.memonew_dateEt);
        final Button memonew_closeBtn = v.findViewById(R.id.memonew_closeBtn);
        final Button memonew_noBtn = v.findViewById(R.id.memonew_noBtn);
        final Button memonew_yesBtn = v.findViewById(R.id.memonew_yesBtn);

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
                m.getFromDB(cal);
                m.dismissFragmentNew();
            }
        });
        memonew_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.dismissFragmentNew();
            }
        });
        return v;
    }
}
