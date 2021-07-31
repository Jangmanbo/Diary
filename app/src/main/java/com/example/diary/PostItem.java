package com.example.diary;

import java.util.Date;

public class PostItem {
    String title, contents;
    Date date;

    public PostItem(String title, String contents, Date date) {
        this.title=title;
        this.contents=contents;
        this.date=date;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public Date getDate() {
        return date;
    }
}
