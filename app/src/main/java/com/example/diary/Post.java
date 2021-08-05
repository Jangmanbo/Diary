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
    @Ignore
    private boolean selected = false;
    private String title, contents, date;

    public Post(String title, String contents, String date) {
        this.title=title;
        this.contents=contents;
        this.date=date;
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

    public String getDate() {
        return date;
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

    public void setDate(String date) {
        this.date = date;
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
        parcel.writeString(date);
    }

    private void readFromParcel(Parcel parcel) {
        id=parcel.readInt();
        title=parcel.readString();
        contents=parcel.readString();
        date=parcel.readString();
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
