package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AppDatabase db;
    RecyclerView recyclerView;
    PostAdapter adapter;
    FloatingActionButton btn;
    List<Post> items;
    boolean deleteMode = false;
    long backKeyPressedTime = 0;
    Toast toast;
    Snackbar snackbar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snackbar = Snackbar
                .make(findViewById(R.id.mainLayout), "게시글을 삭제하시겠습니까?", Snackbar.LENGTH_INDEFINITE)
                // 스낵바 Action 설정("표시할 텍스트", onClick)
                .setAction("삭제", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 스낵바의 삭제 클릭시 실행할 작업
                        hideCheckBox(); //체크박스 숨기기
                        db= AppDatabase.getInstance(getApplicationContext());
                        ArrayList<Post> deleteItems = adapter.getDeleteItems();
                        for (Post post : deleteItems) {
                            db.postDao().delete(post);
                        }
                        //리사이클러뷰 갱신
                        items=db.postDao().getAll();
                        adapter.setItems(items);
                    }
                });

        btn = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter=new PostAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));  //아이템 사이 구분선 넣기

        //글쓰기 화면으로 전환
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });
    }

    //화면 보일 때마다 리사이클러뷰 갱신
    @Override
    protected void onResume() {
        super.onResume();
        db= AppDatabase.getInstance(this);
        items=db.postDao().getAll();
        adapter.setItems(items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete: //삭제하기 클릭 -> 게시글마다 체크박스 보이게
                deleteMode = true;
                adapter.setDeleteMode(true);
                adapter.notifyDataSetChanged();
                snackbar.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (deleteMode) { //삭제하기 모드였을 경우
            hideCheckBox(); //체크박스 숨기기
            snackbar.dismiss(); //스낵바 삭제
            for (Post post : items) {   //모든 게시글의 selected 변수 초기화
                post.setSelected(false);
            }
        }
        else {
            // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
            // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
            // 2500 milliseconds = 2.5 seconds
            if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
                backKeyPressedTime = System.currentTimeMillis();
                toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
            // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
            if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
                toast.cancel();
                super.onBackPressed();
            }
        }
    }

    //게시글마다 체크박스 숨기기
    private void hideCheckBox() {
        adapter.setDeleteMode(false);
        adapter.notifyDataSetChanged();
        deleteMode = false;
    }
}