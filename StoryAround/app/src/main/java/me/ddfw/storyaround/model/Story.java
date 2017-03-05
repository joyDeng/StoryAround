package me.ddfw.storyaround.model;

<<<<<<< HEAD
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
                new LatLng(51.5033640,-0.1276250),new Date(),false, "some test content2");
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
=======
import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xinbeifu on 3/3/17.
 */

public class Story {

    //table's name
    public static final String STORY_TABLE = "story";

    public static final String KEY_STORY_ID = "storyId";

    //colummns names
    public static final String KEY_STORY_LAT = "storyLat";
    public static final String KEY_STORY_LNG = "storyLng";
    public static final String KEY_STORY_IMG_URL = "storyImgURL";
    public static final String KEY_STORY_CONTENT = "storyContent";
    public static final String KEY_STORY_AUTHOR_ID = "storyAuthorId";
    public static final String KEY_STORY_TITLE = "storyTitle";
    public static final String KEY_STORY_TYPE = "storyType";
    public static final String KEY_STORY_DATE_TIME = "storyDateTime";
    public static final String KEY_STORY_MODE = "storyMode";
    public static final String KEY_STORY_LIKES = "storyLikes";

    private String storyId;
    private double storyLat;
    private double storyLng;
    private String storyImgURL;
    private String storyContent;
    private String storyAuthorId;
    private String storyTitle;
    private int storyType;
    private Long storyDateTime;
    private int storyMode;
    private int storyLikes;

    public Story(){}

    public Story(double storyLat, double storyLng,
                 String storyImgURL, String storyContent, String storyAuthorId,
                 String storyTitle, int storyType, Long storyDateTime,
                 int storyMode, int storyLikes){

        this.storyLat = storyLat;
        this.storyLng = storyLng;
        this.storyImgURL = storyImgURL;
        this.storyContent = storyContent;
        this.storyAuthorId = storyAuthorId;
        this.storyTitle = storyTitle;
        this.storyType = storyType;
        this.storyDateTime = storyDateTime;
        this.storyMode = storyMode;
        this.storyLikes = storyLikes;
    }

    public String getStoryId(){return storyId;}

    public void setStoryId(String storyId){ this.storyId = storyId;}

    public double getStoryLat(){return storyLat;}

    public void setStoryLat(double storyLat){ this.storyLat = storyLat;}

    public double getStoryLng(){return storyLng;}

    public void setStoryLng(double storyLng){ this.storyLng = storyLng;}

    public String getStoryImgURL(){return storyImgURL;}

    public void setStoryImgURL(String storyImgURL){ this.storyImgURL = storyImgURL;}

    public String getStoryContent(){return storyContent;}

    public void setStoryContent(String storyContent){ this.storyContent = storyContent;}

    public String getStoryAuthorId(){return storyAuthorId;}

    public void setStoryAuthorId(String storyAuthorId){ this.storyAuthorId = storyAuthorId;}

    public String getStoryTitle(){return storyTitle;}

    public void setStoryTitle(String storyTitle){ this.storyTitle = storyTitle;}

    public int getStoryType(){return storyType;}

    public void setStoryType(int storyType){ this.storyType = storyType;}

    public Long getStoryDateTime(){return storyDateTime;}

    public void setStoryDateTime(Long storyDateTime){this.storyDateTime = storyDateTime;}

    public int getStoryMode(){return storyMode;}

    public void setStoryMode(int storyMode){this.storyMode = storyMode;}

    public int getStoryLikes(){return storyLikes;}

    public void setStoryLikes(int storyLikes){this.storyLikes = storyLikes;}
>>>>>>> 8b78227f58763dc067e0fd2ea4da1c8a766b1eb2
}
