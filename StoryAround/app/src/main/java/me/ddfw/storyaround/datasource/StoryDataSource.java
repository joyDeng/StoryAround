package me.ddfw.storyaround.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import me.ddfw.storyaround.model.Story;

/**
 * Created by xinbeifu on 3/3/17.
 */

public class StoryDataSource {
    //Database fields
    private SQLiteDatabase database;
    private SQLHelper dbHelper;
    private String[] allColumns = {Story.KEY_STORY_ID, Story.KEY_STORY_LAT,
            Story.KEY_STORY_LNG, Story.KEY_STORY_IMG_URL, Story.KEY_STORY_CONTENT,
            Story.KEY_STORY_AUTHOR_ID, Story.KEY_STORY_TITLE, Story.KEY_STORY_TYPE,
            Story.KEY_STORY_DATE_TIME, Story.KEY_STORY_MODE, Story.KEY_STORY_LIKES};

    private String[] columnIds = {Story.KEY_STORY_ID};

    public StoryDataSource(Context context){dbHelper = new SQLHelper(context);}

    public void open() throws SQLException {
        //open connection to write data
        database = dbHelper.getWritableDatabase();
    }

    public void close(){ dbHelper.close();}

    public long insert(Story story){

        ContentValues values = new ContentValues();

        values.put(Story.KEY_STORY_LAT, story.getStoryLat());
        values.put(Story.KEY_STORY_LNG, story.getStoryLng());
        values.put(Story.KEY_STORY_IMG_URL, story.getStoryImgURL());
        values.put(Story.KEY_STORY_CONTENT, story.getStoryContent());
        values.put(Story.KEY_STORY_AUTHOR_ID, story.getStoryAuthorId());
        values.put(Story.KEY_STORY_TITLE, story.getStoryTitle());
        values.put(Story.KEY_STORY_TYPE, story.getStoryType());
        values.put(Story.KEY_STORY_DATE_TIME, story.getStoryDateTime());
        values.put(Story.KEY_STORY_MODE, story.getStoryMode());
        values.put(Story.KEY_STORY_LIKES, story.getStoryLikes());

        long insertId = database.insert(Story.STORY_TABLE, null, values);

        return insertId;
    }

    public void delete(Long id){

        database.delete(Story.STORY_TABLE, Story.KEY_STORY_ID + " = " + id, null);
    }

    public List<Story> getAllStories(){
        List<Story> stories = new ArrayList<>();

        Cursor cursor = database.query(Story.STORY_TABLE, allColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Story story = cursorToStory(cursor);
            stories.add(story);
            cursor.moveToNext();
        }

        //close the cursor
        cursor.close();

        return stories;
    }

    public Story getOneStory(Long id){
        Story story = new Story();

        Cursor cursor = database.query(Story.STORY_TABLE, allColumns, Story.KEY_STORY_ID + " = " + id,
                null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            story = cursorToStory(cursor);
        }

        cursor.close();

        return story;
    }

    private Story cursorToStory(Cursor cursor){
        Story story = new Story();

        story.setStoryId(cursor.getString(0));
        story.setStoryLat(cursor.getDouble(1));
        story.setStoryLng(cursor.getDouble(2));
        story.setStoryImgURL(cursor.getString(3));
        story.setStoryContent(cursor.getString(4));
        story.setStoryAuthorId(cursor.getString(5));
        story.setStoryTitle(cursor.getString(6));
        story.setStoryType(cursor.getInt(7));
        story.setStoryDateTime(cursor.getLong(8));
        story.setStoryMode(cursor.getInt(9));
        story.setStoryLikes(cursor.getInt(10));

        return story;
    }
}
