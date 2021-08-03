package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PostActivity extends AppCompatActivity {
    TextView title, contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        title=findViewById(R.id.titleTextView);
        contents=findViewById(R.id.contentsTextView);


        Bundle bundle = getIntent().getExtras();
        Post post = bundle.getParcelable("post");

        title.setText(post.getTitle());
        contents.setText(post.getContents());
    }
}