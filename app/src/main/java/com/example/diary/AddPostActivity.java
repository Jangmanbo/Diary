package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddPostActivity extends AppCompatActivity {
    Intent intent;
    SpeechRecognizer speechRecognizer;
    AppDatabase db;
    final int PERMISSION = 1;
    boolean recording = false;  //녹음중인지 여부

    EditText title, contents;
    SeekBar moodSeekBar;
    ImageView faceImageView;
    Button addPostBtn;

    TextView recordTextView;
    ImageButton recordBtn;

    LinearLayout calenderLayout;
    TextView calenderTextView;

    DatePickerDialog.OnDateSetListener callbackMethod;  //날짜 선택 이벤트 리스너
    int year, month, day;   //일기 날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        db = AppDatabase.getInstance(this);

        //기본 UI
        title=findViewById(R.id.titleEditText);
        contents=findViewById(R.id.contentsTextView);
        addPostBtn = findViewById(R.id.addPostBtn);

        //감정
        moodSeekBar=findViewById(R.id.moodSeekBar);
        faceImageView=findViewById(R.id.faceImageView);

        //달력
        calenderLayout=findViewById(R.id.calenderLayout);
        calenderTextView=findViewById(R.id.calenderTextView);

        //현재 날짜 받아오기
        Calendar cal = new GregorianCalendar();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        calenderTextView.setText(year + "." + (month + 1) + "." + day);

        //날짜 선택하면 다시 세팅
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                year = y; month = m; day = d;
                calenderTextView.setText(year + "." + (month + 1) + "." + day);
            }
        };

        //녹음
        recordBtn = findViewById(R.id.recordBtn);
        recordTextView=findViewById(R.id.recordTextView);


        //seekbar가 움직이면
        moodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //seekbar 값에 따라 표정 변화
                if (i >= 7) faceImageView.setImageResource(R.drawable.smile);
                else if (i >= 4) faceImageView.setImageResource(R.drawable.neutral);
                else faceImageView.setImageResource(R.drawable.sad);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        CheckPermission();  //녹음 퍼미션 체크

        //녹음하기 위해 intent 객체 생성
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");   //한국어

        //버튼 클릭 이벤트 리스터 등록
        addPostBtn.setOnClickListener(click);
        calenderLayout.setOnClickListener(click);
        recordBtn.setOnClickListener(click);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.addPostBtn:   //등록 -> db에 저장
                    db.postDao().insert(new Post(title.getText().toString(), contents.getText().toString(), moodSeekBar.getProgress(), year, month, day, (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new GregorianCalendar().getTime())));
                    finish();
                    break;
                case R.id.calenderLayout:   //날짜 선택
                    DatePickerDialog dialog = new DatePickerDialog(AddPostActivity.this, callbackMethod, year, month, day);
                    dialog.show();
                    break;
                //녹음 버튼
                case R.id.recordBtn:
                    if (!recording) {   //녹음 시작
                        StartRecord();
                        Toast.makeText(getApplicationContext(), "지금부터 음성으로 기록합니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {  //이미 녹음 중이면 녹음 중지
                        StopRecord();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {    //토스트 메세지로 에러 출력
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    //message = "클라이언트 에러";
                    //stopListening을 호출하면 발생하는 에러
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    //message = "찾을 수 없음";
                    //녹음을 오래하거나(?) stopListening을 호출하면 발생하는 에러
                    //speechRecognizer를 다시 생성하여 녹음 재개
                    if (recording)
                        StartRecord();
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        //사용자가 말을 잠시 쉬면 호출
        @Override
        public void onResults(Bundle bundle) {
            // 말을 하면 ArrayList에 단어를 넣고 기존의 text에 이어붙임
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String originText = contents.getText().toString();  //기존 text
            //녹음한 말
            String newText="";
            for (int i = 0; i < matches.size() ; i++) {
                newText += matches.get(i);
            }
            contents.setText(originText + newText + " ");
            speechRecognizer.startListening(intent);    //녹음버튼을 누를 때까지 계속 녹음해야 하므로 녹음 재개
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    //녹음 시작
    void StartRecord() {
        recording = true;
        //마이크 이미지와 텍스트 변경
        recordBtn.setImageResource(R.drawable.stop_record);
        recordTextView.setText("음성 녹음 중지");
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);
    }

    //녹음 중지
    void StopRecord() {
        recording = false;
        //마이크 이미지와 텍스트 변경
        recordBtn.setImageResource(R.drawable.start_record);
        recordTextView.setText("음성 녹음 시작");
        speechRecognizer.stopListening();   //녹음 중지
        Toast.makeText(getApplicationContext(), "음성 기록을 중지합니다.", Toast.LENGTH_SHORT).show();
    }

    // 퍼미션 체크
    void CheckPermission() {
        if ( Build.VERSION.SDK_INT >= 23 ){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.RECORD_AUDIO},PERMISSION);
        }
    }
}