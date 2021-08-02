package com.example.diary;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Post.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PostDao postDao();
}
