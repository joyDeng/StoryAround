package me.ddfw.storyaround.model;


public class User {

    //table's name
    public static final String USER_TABLE = "user";

    public static final String KEY_USER_ID = "userId";

    //columns names
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_IMAGEURL = "userImageURL";
    public static final String KEY_USER_BIO = "userBio";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_USER_PHONE_NUM = "userPhoNum";
    public static final String KEY_USER_GENDER = "userGender";

    //property
    private String userId;
    private String userName;
    private String userImageURL;
    private String userBio;
    private String userEmail;
    private String userPhoNum;
    private int userGender;

    public User(){}

    public User(String userName, String userImageURL, String userBio, String userEmail, String userPhoNum, int userGender){
        this.userName = userName;
        this.userImageURL = userImageURL;
        this.userBio = userBio;
        this.userEmail = userEmail;
        this.userPhoNum = userPhoNum;
        this.userGender = userGender;
    }

    public String getUserId(){return userId;}

    public void setUserId(String userId){ this.userId = userId;}

    public String getUserName(){return userName;}

    public void setUserName(String userName){ this.userName = userName;}

    public String getUserImageURL(){return userImageURL;}

    public void setUserImageURL(String userImageURL){ this.userImageURL = userImageURL;}

    public String getUserBio(){return userBio;}

    public void setUserBio(String userBio){ this.userBio = userBio;}

    public String getUserEmail(){return userEmail;}

    public void setUserEmail(String userEmail){ this.userEmail = userEmail;}

    public String getUserPhoNum(){return userPhoNum;}

    public void setUserPhoNum(String userName){ this.userPhoNum = userPhoNum;}

    public int getUserGender(){return userGender;}

    public void setUserGender(int userGender){ this.userGender = userGender;}

}
