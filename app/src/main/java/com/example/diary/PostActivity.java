package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PostActivity extends AppCompatActivity {
    TextView title, contents;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        title=findViewById(R.id.titleTextView);
        contents=findViewById(R.id.contentsTextView);
        btn=findViewById(R.id.button);

        Bundle bundle = getIntent().getExtras();
        final Post post = bundle.getParcelable("post");

        title.setText(post.getTitle());
        contents.setText(post.getContents());

        //게시글 수정
        final AppDatabase db= Room.databaseBuilder(this, AppDatabase.class, "Post-db").allowMainThreadQueries().build();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post.setTitle(title.getText().toString());
                post.setContents(contents.getText().toString());
                db.postDao().update(post);
            }
        });
    }
}