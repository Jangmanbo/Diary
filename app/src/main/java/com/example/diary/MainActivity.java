package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PostAdapter adapter;
    FloatingActionButton btn;
    List<Post> items;
    boolean deleteMode = false;
    long backKeyPressedTime = 0;
    Toast toast;
    Snackbar snackbar;

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
                        hideCheckBox();
                        final AppDatabase db= Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Post-db").allowMainThreadQueries().build();
                        for (Post post:items) {
                            if (post.getSelected()) {
                                Log.e("MainActivity", post.getTitle()+" : delete");
                                db.postDao().delete(post);
                            }
                            else {
                                Log.e("MainActivity", post.getTitle()+" : don't delete");
                            }
                        }
                    }
                });

        btn = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);

        adapter=new PostAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //글쓰기 화면으로 전환
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });
    }

    //화면 보일 때마다 갱신
    @Override
    protected void onResume() {
        super.onResume();
        final AppDatabase db= Room.databaseBuilder(this, AppDatabase.class, "Post-db").allowMainThreadQueries().build();
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
            case R.id.delete:
                //게시글마다 체크박스 보이게
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
        if (deleteMode) { //삭제하기 버튼 클릭 상태
            hideCheckBox(); //체크박스 숨기기
            snackbar.dismiss(); //스낵바 삭제
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

    private void hideCheckBox() {
        adapter.setDeleteMode(false);
        adapter.notifyDataSetChanged();
        deleteMode = false;
    }
}