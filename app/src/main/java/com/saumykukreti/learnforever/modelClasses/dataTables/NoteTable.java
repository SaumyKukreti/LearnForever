package com.saumykukreti.learnforever.modelClasses.dataTables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by saumy on 12/7/2017.
 */

@Entity
public class NoteTable implements Parcelable{

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long mId;

    @ColumnInfo (name = "category")
    private String mCategory;

    @ColumnInfo (name = "title")
    private String mTitle;

    @ColumnInfo (name = "contentInShort")
    private String mContentInShort;

    @ColumnInfo (name = "content")
    private String mContent;

    @ColumnInfo (name = "timeStamp")
    private String mTimeStamp;

    @ColumnInfo (name = "learn")
    private boolean mLearn;

    //private String isLocal

    public NoteTable(){

    }

    public NoteTable(String category, String title, String contentInShort, String content, String timeStamp, boolean learn) {
        mCategory = category;
        mTitle = title;
        mContentInShort = contentInShort;
        mContent = content;
        mTimeStamp = timeStamp;
        mLearn = learn;
    }

    protected NoteTable(Parcel in) {
        mId = in.readLong();
        mCategory = in.readString();
        mTitle = in.readString();
        mContentInShort = in.readString();
        mContent = in.readString();
        mTimeStamp = in.readString();
        mLearn = in.readByte() != 0;
    }

    public static final Creator<NoteTable> CREATOR = new Creator<NoteTable>() {
        @Override
        public NoteTable createFromParcel(Parcel in) {
            return new NoteTable(in);
        }

        @Override
        public NoteTable[] newArray(int size) {
            return new NoteTable[size];
        }
    };

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContentInShort() {
        return mContentInShort;
    }

    public void setContentInShort(String contentInShort) {
        mContentInShort = contentInShort;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public boolean isLearn() {
        return mLearn;
    }

    public void setLearn(boolean learn) {
        mLearn = learn;
    }

    @Override
    public String toString() {
        StringBuilder value = new StringBuilder();
        value.append("UID = "+mId).append("\n");
        value.append("Category = "+mCategory).append("\n");
        value.append("Title = "+mTitle).append("\n");
        value.append("Content In short = "+mContentInShort).append("\n");
        value.append("Content = "+mContent).append("\n");
        return value.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeString(mCategory);
        parcel.writeString(mTitle);
        parcel.writeString(mContentInShort);
        parcel.writeString(mContent);
        parcel.writeString(mTimeStamp);
        parcel.writeByte((byte) (mLearn ? 1 : 0));
    }


}