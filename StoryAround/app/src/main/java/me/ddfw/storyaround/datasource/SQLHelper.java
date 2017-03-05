package me.ddfw.storyaround.datasource;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import me.ddfw.storyaround.model.*;
/**
 * Created by xinbeifu on 3/3/17.
 */

public class SQLHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    //database name
    private static final String DATABASE_NAME = "story_around.db";

    //databse creation sql statement
    private static final String DATABASE_USER_CREATE = "create table " + User.USER_TABLE
            + " (" + User.KEY_USER_ID + " integer primary key autoincrement, "
            + User.KEY_USER_NAME + " text not null, "
            + User.KEY_USER_IMAGEURL + " text, "
            + User.KEY_USER_BIO + " text, "
            + User.KEY_USER_EMAIL + " text, "
            + User.KEY_USER_PHONE_NUM + " text, "
            + User.KEY_USER_GENDER + " integer );";

    private static final String DATABASE_STORY_CREATE = "create table " + Story.STORY_TABLE
            + " (" + Story.KEY_STORY_ID + " integer primary key autoincrement, "
            + Story.KEY_STORY_LAT + " double, "
            + Story.KEY_STORY_LNG + " double, "
            + Story.KEY_STORY_IMG_URL + " text, "
            + Story.KEY_STORY_CONTENT + " text, "
            + Story.KEY_STORY_AUTHOR_ID + " integer not null, "
            + Story.KEY_STORY_TITLE + " text, "
            + Story.KEY_STORY_TYPE + " integer not null, "
            + Story.KEY_STORY_DATE_TIME + " integer, "
            + Story.KEY_STORY_MODE + " integer, "
            + Story.KEY_STORY_LIKES + " integer );";
    //date time data type????

    private static final String DATABASE_LIKES_CREATE = "create table " + Likes.LIKES_TABLE
            + " (" + Likes.KEY_LIKES_ID + " integer primary key autoincrement, "
            + Likes.KEY_LIKES_USER_ID + " integer not null, "
            + Likes.KEY_LIKES_STORY_ID + " integer not null, "
            + Likes.KEY_LIKES_TIME + " integer );";

    public SQLHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        //create new tables
        db.execSQL(DATABASE_USER_CREATE);
        db.execSQL(DATABASE_STORY_CREATE);
        db.execSQL(DATABASE_LIKES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        //drop older table if existed, all data will be gone
        db.execSQL("DROP TABLE IF EXISTS " + User.USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Story.STORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Likes.LIKES_TABLE);

        //create tables again
        onCreate(db);
    }
}
