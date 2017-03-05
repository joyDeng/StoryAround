package com.example.dengxi.myapplication.backend;

import com.example.dengxi.myapplication.backend.data.User;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Created by xinbeifu on 2/27/17.
 */

public class UserDatastoreSource {

    private DatastoreService datastoreService;

    public UserDatastoreSource(){
        datastoreService = DatastoreServiceFactory.getDatastoreService();
    }

    public Key getParentKey(){
        Key retKey = KeyFactory.createKey(User.PARENT_KIND, User.PARENT_IDENTIFIER);
        return retKey;
    }

    public User getUserByIdentifier(String id){
        Entity entity = null;
        Key key = KeyFactory.createKey(getParentKey(), User.USER_ENTITY_KIND, id);

        try{
            entity = datastoreService.get(key);
        }catch (Exception e){}

        if(entity != null)
            return convertEntity2Activity(entity);
        else
            return null;
    }

    private User convertEntity2Activity(Entity entity){
        User user = new User((String) entity.getProperty(User.USER_ID),
                (String) entity.getProperty(User.USER_NAME),
                (String) entity.getProperty(User.USER_IMAGEURL),
                (String) entity.getProperty(User.USER_BIO),
                (String) entity.getProperty(User.USER_EMAIL),
                (String) entity.getProperty(User.USER_PHONENUM),
                (int) entity.getProperty(User.USER_GENDER));

        return user;

    }

    public boolean addUser2Datastore(User user){
        if(getUserByIdentifier(user.userId) != null)
            return false;
        else{
            Entity entity = new Entity(User.USER_ENTITY_KIND, user.userId, getParentKey());
            entity.setProperty(User.USER_ID, user.userId);
            entity.setProperty(User.USER_NAME, user.userName);
            entity.setProperty(User.USER_IMAGEURL, user.userImageURL);
            entity.setProperty(User.USER_BIO, user.userBio);
            entity.setProperty(User.USER_EMAIL, user.userEmail);
            entity.setProperty(User.USER_PHONENUM, user.userPhoneNum);
            entity.setProperty(User.USER_GENDER, user.userGender);
            datastoreService.put(entity);
            return true;
        }
    }


}
