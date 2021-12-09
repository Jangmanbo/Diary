package com.example.diary;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "postTable")
public class Post implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    //db에 저장 X
    @Ignore
    private boolean selected = false;
    private String title, contents, reportingDate;
    private int mood;
    private int year, month, day;

    public Post(String title, String contents, int mood, int year, int month, int day, String reportingDate) {
        this.title=title;
        this.contents=contents;
        this.mood=mood;
        this.year=year;
        this.month=month;
        this.day=day;
        this.reportingDate=reportingDate;
    }

    public Post(Parcel parcel) {
        readFromParcel(parcel);
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public int getId() {
        return id;
    }

    public boolean getSelected() { return selected; }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public int getMood() {
        return mood;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getReportingDate() {
        return reportingDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSelected(boolean selected) { this.selected = selected; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setReportingDate(String reportingDate) {
        this.reportingDate = reportingDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(contents);
        parcel.writeInt(mood);
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
        parcel.writeString(reportingDate);
    }

    private void readFromParcel(Parcel parcel) {
        id=parcel.readInt();
        title=parcel.readString();
        contents=parcel.readString();
        mood=parcel.readInt();
        year=parcel.readInt();
        month=parcel.readInt();
        day=parcel.readInt();
        reportingDate=parcel.readString();
    }

    public class PostCreator implements Parcelable.Creator<Post> {

        @Override
        public Post createFromParcel(Parcel parcel) {
            return new Post(parcel);
        }

        @Override
        public Post[] newArray(int i) {
            return new Post[i];
        }
    }
}
