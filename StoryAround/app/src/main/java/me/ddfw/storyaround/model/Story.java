package me.ddfw.storyaround.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by apple on 2017/2/27.
 */

public class Story implements Parcelable {
    public static final String DATE_FORMAT = "MMM/dd";

    private long id;
    private String userID;
    private String title;
    private String tag;
    private LatLng location;
    private Date dateAndTime;
    private boolean isPrivate;
    private String content;
    private String userName;

    Story(){

    }

    public Story(long id, String userID, String userName, String title, String tag, LatLng location,
                 Date dateAndTime, boolean isPrivate, String content) {
        this.id = id;
        this.userID = userID;
        this.userName = userName;
        this.title = title;
        this.tag = tag;
        this.location = location;
        this.dateAndTime = dateAndTime;
        this.isPrivate = isPrivate;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Date getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(Date dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getFormattedDate(){
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return format.format(dateAndTime);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    public static List<Story>  getTestStories(){
        List<Story> testList = new ArrayList<>();
        Story s = new Story(1,"id1","Lily","DEMO TITLE","travel",
                new LatLng(123,231),new Date(),false, "some test content");
        testList.add(s);
        s = new Story(2,"id2","Annie","DEMO TITLE2","historical",
                new LatLng(-31,151),new Date(),false, "some test content2");
        testList.add(s);
        s = new Story(3,"id2","Annie","DEMO TITLE3","historical",
                new LatLng(-31,151),new Date(),false, "some test content2");
        testList.add(s);
        return testList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.userID);
        dest.writeString(this.title);
        dest.writeString(this.tag);
        dest.writeParcelable(this.location, flags);
        dest.writeLong(this.dateAndTime != null ? this.dateAndTime.getTime() : -1);
        dest.writeByte(this.isPrivate ? (byte) 1 : (byte) 0);
        dest.writeString(this.content);
        dest.writeString(this.userName);
    }

    protected Story(Parcel in) {
        this.id = in.readLong();
        this.userID = in.readString();
        this.title = in.readString();
        this.tag = in.readString();
        this.location = in.readParcelable(LatLng.class.getClassLoader());
        long tmpDateAndTime = in.readLong();
        this.dateAndTime = tmpDateAndTime == -1 ? null : new Date(tmpDateAndTime);
        this.isPrivate = in.readByte() != 0;
        this.content = in.readString();
        this.userName = in.readString();
    }

    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}
