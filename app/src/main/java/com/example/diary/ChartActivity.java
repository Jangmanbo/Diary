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
    List<Post> items;       //해당 기간의 일기 데이터
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

        //액션바 변경
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("월별 기분 차트");  //액션바 제목설정
        actionBar.setDisplayHomeAsUpEnabled(true);  //뒤로가기 버튼

        //차트에 나타낼 데이터가 없는 경우
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

        updateChart();  //차트 보여주기
    }

    //액티비티 실행 or 날짜 변경 시 호출
    private void updateChart() {
        items = db.postDao().getMonthPeriod(year, month);   //해당 년월에 작성된 일기 데이터 가져오기
        entries = new ArrayList<>();
        //x좌표 : 일, y좌표 : 기분 수치
        for (Post post : items) {
            entries.add(new Entry(post.getDay(), post.getMood()));
        }

        //그래프 디자인 설정
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
        if (items.isEmpty())    //데이터 X
            lineChart.clear();
        else    //데이터 O
            lineChart.setData(lineData);

        //x축
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);
        xAxis.setLabelCount(31, true);  //31개 라벨링 -> (1,2,...,31)
        xAxis.setAxisMinimum(1);    //최소 x좌표 = 1
        xAxis.setAxisMaximum(31);   //최대 x좌표 = 31

        //y축
        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);
        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);
        yLAxis.setLabelCount(11, true); //11개 라벨링 -> (0,1,...,10)
        yLAxis.setAxisMinimum(0);   //최소 y좌표 = 0
        yLAxis.setAxisMaximum(10);  //최대 y좌표 = 10

        Description description = new Description();
        description.setText("");
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);  //차트 애니메이션 2초간 작동
        lineChart.invalidate(); //차트 보임
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.calenderLayout:   //달 선택
                    YearMonthPickerDialog dialog = new YearMonthPickerDialog();
                    dialog.setListener(callbackMethod); //리스너 등록
                    dialog.setDate(year, month);    //현재 보여지는 년, 월로 초기세팅
                    dialog.show(getSupportFragmentManager(), "YearMonthPicker");
                    break;
                case R.id.leftArrowBtn: //전달 차트 보기
                    if (month == 0) {   //1월이면
                        year--; month = 11;
                    }
                    else {  //2-12월이면
                        month--;
                    }
                    calenderTextView.setText(year + "." + (month + 1)); //날짜 텍스트 업데이트
                    updateChart();
                    break;
                case R.id.rightArrowBtn:    //다음달 차트 보기
                    if (month == 11) {//12월이면
                        year++; month = 0;
                    }
                    else {  //1-11월이면
                        month++;
                    }
                    calenderTextView.setText(year + "." + (month + 1)); //날짜 텍스트 업데이트
                    updateChart();
                    break;
            }
        }
    };

    //날짜 선택하면 다시 세팅
    DatePickerDialog.OnDateSetListener callbackMethod = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int y, int m, int d){
            year=y; month=m;
            calenderTextView.setText(year + "." + (month + 1));
            updateChart();
        }
    };
}