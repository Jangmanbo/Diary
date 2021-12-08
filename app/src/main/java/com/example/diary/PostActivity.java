package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostActivity extends AppCompatActivity {
    Intent intent;
    SpeechRecognizer speechRecognizer;
    final int PERMISSION = 1;
    boolean recording = false;  //녹음중인지 여부

    AppDatabase db;
    Post post;

    TextView title, contents;
    SeekBar moodSeekBar;
    ImageView faceImageView;
    Button modifyPostBtn;
    TextView recordTextView;
    ImageButton recordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        db = AppDatabase.getInstance(this);

        //기본 UI
        title=findViewById(R.id.titleTextView);
        contents=findViewById(R.id.contentsTextView);
        modifyPostBtn=findViewById(R.id.modifyPostBtn);

        //감정
        moodSeekBar=findViewById(R.id.moodSeekBar);
        faceImageView=findViewById(R.id.faceImageView);

        //녹음
        recordBtn = findViewById(R.id.recordBtn);
        recordTextView = findViewById(R.id.recordTextView);


        //화면 초기 세팅
        //인텐트 객체를 통해 Post 객체 가져옴
        post=getIntent().getParcelableExtra("post");

        title.setText(post.getTitle());
        contents.setText(post.getContents());

        //seekbar 세팅
        int mood=post.getMood();
        moodSeekBar.setProgress(mood);

        //감성 수치에 따라 표정 세팅
        if (mood >= 7) faceImageView.setImageResource(R.drawable.smile);
        else if (mood >= 4) faceImageView.setImageResource(R.drawable.neutral);
        else faceImageView.setImageResource(R.drawable.sad);

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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        //버튼 클릭 이벤트 리스터 등록
        modifyPostBtn.setOnClickListener(click);
        recordBtn.setOnClickListener(click);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                //수정 버튼
                case R.id.modifyPostBtn:   //db에 update
                    post.setTitle(title.getText().toString());
                    post.setContents(contents.getText().toString());
                    post.setMood(moodSeekBar.getProgress());
                    db.postDao().update(post);
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

        @Override
        public void onResults(Bundle bundle) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String originText = contents.getText().toString();
            String newText=" ";
            for (int i = 0; i < matches.size() ; i++) {
                newText += matches.get(i);
            }
            contents.setText(originText + newText);
            speechRecognizer.startListening(intent);
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    void StartRecord() {
        recording = true;
        recordBtn.setImageResource(R.drawable.stop_record);
        recordTextView.setText("음성 녹음 중지");
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);
    }

    void StopRecord() {
        recording = false;
        recordBtn.setImageResource(R.drawable.start_record);
        recordTextView.setText("음성 녹음 시작");
        speechRecognizer.stopListening();
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