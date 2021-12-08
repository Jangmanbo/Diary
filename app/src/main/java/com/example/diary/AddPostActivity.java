package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPostActivity extends AppCompatActivity {
    EditText title, contents;
    SeekBar moodSeekBar;
    ImageView faceImageView;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        final AppDatabase db= AppDatabase.getInstance(this);

        title=findViewById(R.id.titleEditText);
        contents=findViewById(R.id.contentsEditText);
        btn = findViewById(R.id.button);
        moodSeekBar=findViewById(R.id.moodSeekBar);
        faceImageView=findViewById(R.id.faceImageView);

        //버튼 누르면 db에 저장
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.postDao().insert(new Post(title.getText().toString(), contents.getText().toString(), moodSeekBar.getProgress(), (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date(System.currentTimeMillis()))));
                finish();
            }
        });

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
}