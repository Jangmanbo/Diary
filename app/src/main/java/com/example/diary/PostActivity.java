package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PostActivity extends AppCompatActivity {
    TextView title, contents;
    SeekBar moodSeekBar;
    ImageView faceImageView;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        title=findViewById(R.id.titleTextView);
        contents=findViewById(R.id.contentsTextView);
        moodSeekBar=findViewById(R.id.moodSeekBar);
        faceImageView=findViewById(R.id.faceImageView);
        btn=findViewById(R.id.button);

        //인텐트 객체를 통해 Post 객체 가져옴
        final Post post=getIntent().getParcelableExtra("post");

        title.setText(post.getTitle());
        contents.setText(post.getContents());

        //seekbar 세팅
        int mood=post.getMood();
        moodSeekBar.setProgress(mood);

        //감성 수치에 따라 표정 초기세팅
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

        //게시글 수정
        final AppDatabase db= AppDatabase.getInstance(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post.setTitle(title.getText().toString());
                post.setContents(contents.getText().toString());
                post.setMood(moodSeekBar.getProgress());
                db.postDao().update(post);
            }
        });
    }
}