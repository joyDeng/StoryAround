package me.ddfw.storyaround.model;


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

}
