package com.example.diary;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

    Toolbar toolbar;
    LineChart lineChart;
    LinearLayout calenderLayout;
    ImageButton leftArrowBtn, rightArrowBtn;
    TextView calenderTextView;
    int year, month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        toolbar=findViewById(R.id.chart_toolbar);
        calenderLayout=findViewById(R.id.calenderLayout);
        leftArrowBtn=findViewById(R.id.leftArrowBtn);
        rightArrowBtn=findViewById(R.id.rightArrowBtn);
        calenderTextView=findViewById(R.id.calenderTextView);
        lineChart = (LineChart)findViewById(R.id.chart);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("월별 기분 차트");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);  //뒤로가기 버튼

        lineChart.setNoDataText("해당 기간에 작성한 일기가 없습니다.");
        lineChart.setNoDataTextColor(Color.BLUE);

        //현재 날짜 받아오기
        Calendar cal = new GregorianCalendar();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        calenderTextView.setText(year + "." + (month + 1));

        //클릭 리스너 등록
        calenderLayout.setOnClickListener(click);
        leftArrowBtn.setOnClickListener(click);
        rightArrowBtn.setOnClickListener(click);

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
                case R.id.calenderLayout:
                    YearMonthPickerDialog dialog = new YearMonthPickerDialog();
                    dialog.setListener(callbackMethod);
                    dialog.setDate(year, month);
                    dialog.show(getSupportFragmentManager(), "YearMonthPicker");
                    break;
                case R.id.leftArrowBtn:
                    if (month == 0) {   //1월이면
                        year--; month = 11;
                    }
                    else {  //2-12월이면
                        month--;
                    }
                    calenderTextView.setText(year + "." + (month + 1));
                    updateChart();
                    break;
                case R.id.rightArrowBtn:
                    if (month == 11) {//12월이면
                        year++; month = 0;
                    }
                    else {  //1-11월이면
                        month++;
                    }
                    calenderTextView.setText(year + "." + (month + 1));
                    updateChart();
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