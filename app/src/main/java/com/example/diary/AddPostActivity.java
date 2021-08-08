package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPostActivity extends AppCompatActivity {
    EditText title, contents;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        final AppDatabase db= Room.databaseBuilder(this, AppDatabase.class, "Post-db").allowMainThreadQueries().build();

        title=findViewById(R.id.titleEditText);
        contents=findViewById(R.id.contentsEditText);
        btn = findViewById(R.id.button);
        //버튼 누르면 db에 저장
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.postDao().insert(new Post(title.getText().toString(), contents.getText().toString(), (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date(System.currentTimeMillis()))));
                finish();
            }
        });
    }
}