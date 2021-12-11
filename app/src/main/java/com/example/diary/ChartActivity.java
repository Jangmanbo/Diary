package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    AppDatabase db;
    List<Post> items;       //해당 기간의 게시글 데이터
    List<Entry> entries;    //차트에 나타내기 위해 가공한 데이터

    LineChart lineChart;
    ImageButton calenderBtn;
    TextView calenderTextView;
    int year, month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        calenderBtn=findViewById(R.id.calenderBtn);
        calenderTextView=findViewById(R.id.calenderTextView);
        lineChart = (LineChart)findViewById(R.id.chart);
        lineChart.setNoDataText("해당 기간에 작성한 일기가 없습니다.");
        lineChart.setNoDataTextColor(Color.BLUE);

        //현재 날짜 받아오기
        Calendar cal = new GregorianCalendar();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        calenderTextView.setText(year + "." + (month + 1));

        //클릭 리스너 등록
        calenderBtn.setOnClickListener(click);


        db = AppDatabase.getInstance(this);

        updateChart();
    }

    private void updateChart() {
        items = db.postDao().getMonthPeriod(year, month);
        entries = new ArrayList<>();
        for (Post post : items) {
            entries.add(new Entry(post.getDay(), post.getMood()));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "기분");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);
        LineData lineData = new LineData(lineDataSet);

        //해당 기간에 데이터가 없으면 그래프를 나타내지 않음
        if (items.isEmpty())
            lineChart.clear();
        else
            lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);
        xAxis.setLabelCount(31, true);
        xAxis.setAxisMinimum(1);
        xAxis.setAxisMaximum(31);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);
        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);
        yLAxis.setLabelCount(11, true);
        yLAxis.setAxisMinimum(0);
        yLAxis.setAxisMaximum(10);

        Description description = new Description();
        description.setText("");
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();
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
            calenderTextView.setText(year + "." + (month + 1));
            updateChart();
        }
    };
}