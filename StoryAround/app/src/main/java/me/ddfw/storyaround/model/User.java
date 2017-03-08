package me.ddfw.storyaround.model;

import android.os.Parcel;
import android.os.Parcelable;



public class User implements Parcelable {

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

    public void setUserPhoNum(String userPhoNum){ this.userPhoNum = userPhoNum;}

    public int getUserGender(){return userGender;}

    public void setUserGender(int userGender){ this.userGender = userGender;}


    // START: Make parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.userImageURL);
        dest.writeString(this.userBio);
        dest.writeString(this.userEmail);
        dest.writeString(this.userPhoNum);
        dest.writeInt(this.userGender);
    }

    protected User(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.userImageURL = in.readString();
        this.userBio = in.readString();
        this.userEmail = in.readString();
        this.userPhoNum = in.readString();
        this.userGender = in.readInt();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    // END: Make parcelable
}
