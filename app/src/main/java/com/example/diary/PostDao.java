package com.example.diary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface PostDao {
    @Insert
    void insert(Post post);

    @Update
    void update(Post post);

    @Delete
    void delete(Post post);

    //모든 데이터 리턴
    @Query("SELECT * FROM postTable ORDER BY year DESC, month DESC, day DESC")
    List<Post> getAll();

    //파라미터로 받은 년도, 월에 해당하는 데이터 리턴
    @Query("SELECT * FROM postTable WHERE year = :year and month = :month ORDER BY day")
    List<Post> getMonthPeriod(int year, int month);

    @Query("DELETE FROM postTable")
    void deleteAll();
}
