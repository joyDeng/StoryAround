package com.example.dengxi.myapplication.backend;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

/**
 * Created by xinbeifu on 2/27/17.
 */

public class StoryDatastoreSource {

    private DatastoreService datastoreService;

    public StoryDatastoreSource(){
        datastoreService = DatastoreServiceFactory.getDatastoreService();
    }

}
