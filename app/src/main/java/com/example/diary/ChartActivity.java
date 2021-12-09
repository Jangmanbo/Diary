package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ChartActivity extends AppCompatActivity {
    ImageButton calenderBtn;
    TextView calenderTextView;
    int year, month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        calenderBtn=findViewById(R.id.calenderBtn);
        calenderTextView=findViewById(R.id.calenderTextView);

        //현재 날짜 받아오기
        Calendar cal = new GregorianCalendar();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        calenderTextView.setText(year + "." + (month + 1));

        calenderBtn.setOnClickListener(click);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.calenderBtn:
                    YearMonthPickerDialog dialog = new YearMonthPickerDialog();
                    dialog.setListener(callbackMethod);
                    dialog.setDate(year, month);
                    dialog.show(getSupportFragmentManager(), "YearMonthPicker");
                    break;
            }
        }
    };

    DatePickerDialog.OnDateSetListener callbackMethod = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int y, int m, int d){
            year=y; month=m;
            Log.e("chartactivity", String.valueOf(month));
            calenderTextView.setText(year + "." + (month + 1));
        }
    };
}