package com.example.diary;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "postTable")
public class Post {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title, contents, date;

    public Post(String title, String contents, String date) {
        this.title=title;
        this.contents=contents;
        this.date=date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public String getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
