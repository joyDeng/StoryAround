package me.ddfw.storyaround.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import me.ddfw.storyaround.model.User;

/**
 * Created by xinbeifu on 3/3/17.
 */

public class UserDataSource {

    //Database fields
    private SQLiteDatabase database;
    private SQLHelper dbHelper;
    private String[] allColumns = {User.KEY_USER_ID, User.KEY_USER_NAME,
    User.KEY_USER_IMAGEURL, User.KEY_USER_BIO, User.KEY_USER_EMAIL,
    User.KEY_USER_PHONE_NUM, User.KEY_USER_GENDER};

    private String[] columnIds = {User.KEY_USER_ID};

    public UserDataSource(Context context){dbHelper = new SQLHelper(context);}

    public void open() throws SQLException{
        //open connection to write data
        database = dbHelper.getWritableDatabase();
    }

    public void close(){ dbHelper.close();}

    public long insert(User user){

        ContentValues values = new ContentValues();

        values.put(User.KEY_USER_NAME, user.getUserName());
        values.put(User.KEY_USER_IMAGEURL, user.getUserImageURL());
        values.put(User.KEY_USER_BIO, user.getUserBio());
        values.put(User.KEY_USER_EMAIL, user.getUserEmail());
        values.put(User.KEY_USER_PHONE_NUM, user.getUserPhoNum());
        values.put(User.KEY_USER_GENDER, user.getUserGender());

        long insertId = database.insert(User.USER_TABLE, null, values);

        return insertId;
    }

    public void delete(Long id){

        database.delete(User.USER_TABLE, User.KEY_USER_ID + " = " + id, null);
    }

    public User getOneUser(Long id){
        User user = new User();

        Cursor cursor = database.query(User.USER_TABLE, allColumns, User.KEY_USER_ID + " = " + id,
                null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            user = cursorToUser(cursor);
        }

        cursor.close();

        return user;
    }

    private User cursorToUser(Cursor cursor){
        User user = new User();

        user.setUserId(cursor.getString(0));
        user.setUserName(cursor.getString(1));
        user.setUserImageURL(cursor.getString(2));
        user.setUserBio(cursor.getString(3));
        user.setUserEmail(cursor.getString(4));
        user.setUserPhoNum(cursor.getString(5));
        user.setUserGender(cursor.getInt(6));

        return user;
    }
}
