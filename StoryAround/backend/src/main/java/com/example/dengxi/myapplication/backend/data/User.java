package com.example.dengxi.myapplication.backend.data;

/**
 * Created by xinbeifu on 2/27/17.
 */

public class User {
    public static final String PARENT_KIND = "parent kind";
    public static final String PARENT_IDENTIFIER = "parent identifier";
    public static final String USER_ENTITY_KIND = "user";
    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_IMAGEURL = "imageurl";
    public static final String USER_BIO = "bio";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONENUM = "phonenum";
    public static final String USER_GENDER = "gender";

    public String userId;
    public String userName;
    public String userImageURL;
    public String userBio;
    public String userEmail;
    public String userPhoneNum;
    public int userGender;

    public User(String _id, String _name, String _imageurl, String _bio, String _email, String _phone,
                int _gender){
        userId = _id;
        userName = _name;
        userImageURL = _imageurl;
        userBio = _bio;
        userEmail = _email;
        userPhoneNum = _phone;
        userGender = _gender;
    }

}
