package com.example.diary;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Post.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PostDao postDao();
    private static volatile AppDatabase INSTANCE;
    // 인스턴스를 생성하여 반환
    public static AppDatabase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (AppDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"Post-db")
                            .fallbackToDestructiveMigration()   //데이터베이스 버전 바뀌면 이전 테이블 삭제하고 새로운 테이블 생성
                            .allowMainThreadQueries()           //메인 스레드에서 처리
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
