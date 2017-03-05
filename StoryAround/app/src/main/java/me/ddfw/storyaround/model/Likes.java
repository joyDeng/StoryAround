package me.ddfw.storyaround.model;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by xinbeifu on 3/3/17.
 */

public class Likes {

    //table's name
    public static final String LIKES_TABLE = "likes";

    public static final String KEY_LIKES_ID = "likeId";

    //columns names
    public static final String KEY_LIKES_USER_ID = "userId";
    public static final String KEY_LIKES_STORY_ID = "storyId";
    public static final String KEY_LIKES_TIME = "tabTime";

    //property
    private String likeId;
    private String userId;
    private String storyId;
    private Long tabTime;

    public Likes(String userId, String storyId, Long tabTime){
        this.userId = userId;
        this.storyId = storyId;
        this.tabTime = tabTime;
    }

    public String getLikeId(){return likeId;}

    public void setLikeId(String likeId){this.likeId = likeId;}

    public String getUserId(){return userId;}

    public void setUserId(String userId){this.userId = userId;}

    public String getStoryId(){return storyId;}

    public void setStoryId(String storyId){this.storyId = storyId;}

    public Long getTabTime(){return tabTime;}

    public void setTabTime(Long tabTime){this.tabTime = tabTime;}
}
