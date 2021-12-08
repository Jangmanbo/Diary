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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        db = AppDatabase.getInstance(this);

        // 퍼미션 체크
        if ( Build.VERSION.SDK_INT >= 23 ){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        //녹음하기 위해 intent 객체 생성
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");


        title=findViewById(R.id.titleEditText);
        contents=findViewById(R.id.contentsEditText);
        addPostBtn = findViewById(R.id.addPostBtn);
        recordBtn = findViewById(R.id.recordBtn);
        recordTextView=findViewById(R.id.recordTextView);
        moodSeekBar=findViewById(R.id.moodSeekBar);
        faceImageView=findViewById(R.id.faceImageView);




        addPostBtn.setOnClickListener(click);
        recordBtn.setOnClickListener(click);


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
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.addPostBtn:   //db에 저장
                    db.postDao().insert(new Post(title.getText().toString(), contents.getText().toString(), moodSeekBar.getProgress(), (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date(System.currentTimeMillis()))));
                    finish();
                    break;
                case R.id.recordBtn:
                    if (!recording) {   //녹음 시작
                        StartRecord();
                        Toast.makeText(getApplicationContext(), "지금부터 음성으로 기록합니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {
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
}