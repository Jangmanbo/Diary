package com.example.diary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class YearMonthPickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;

    private DatePickerDialog.OnDateSetListener listener;
    private int year, month;

    NumberPicker monthPicker, yearPicker;
    Button confirmBtn;
    Button cancelBtn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.year_month_picker, null);

        confirmBtn = dialog.findViewById(R.id.confirmBtn);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);

        monthPicker = (NumberPicker) dialog.findViewById(R.id.monthPicker);
        yearPicker = (NumberPicker) dialog.findViewById(R.id.yearPicker);

        //버튼 클릭 리스너 등록
        cancelBtn.setOnClickListener(click);
        confirmBtn.setOnClickListener(click);

        //최소, 최대값과 초기값 세팅
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(month + 1);

        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);

        builder.setView(dialog);

        return builder.create();
    }

    //버튼 클릭 리스너
    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancelBtn:    //취소 클릭 -> 변화 없음
                    YearMonthPickerDialog.this.getDialog().cancel();
                    break;
                case R.id.confirmBtn:   //확인 클릭 -> 선택한 날짜 ChartActivity 에게 전달
                    listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue() - 1, 0);
                    YearMonthPickerDialog.this.getDialog().cancel();
                    break;
            }
        }
    };

    //리스너 등록
    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    //ChartActivity 가 dialog 의 년도, 월 설정
    public void setDate(int year, int month) {
        this.year=year;
        this.month=month;
    }
}