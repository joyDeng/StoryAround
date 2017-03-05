package com.example.dengxi.myapplication.backend;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

/**
 * Created by xinbeifu on 2/27/17.
 */

public class LikesDatastoreSource {

    private DatastoreService datastoreService;

    public LikesDatastoreSource(){
        datastoreService = DatastoreServiceFactory.getDatastoreService();
    }

}
