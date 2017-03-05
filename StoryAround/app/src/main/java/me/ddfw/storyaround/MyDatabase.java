package me.ddfw.storyaround;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ddfw.storyaround.model.*;

/**
 * Created by xinbeifu on 3/4/17.
 */

public class MyDatabase {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private int ADD_LIKE = 0;
    private int REMOVE_LIKE = 1;

    public MyDatabase(){}

    //methods concerning User
    public void createProfile(User user){
        mDatabase.child(User.USER_TABLE).child(user.getUserId()).setValue(user);
    }

    public void updateProfile(User user){
        mDatabase.child(User.USER_TABLE).child(user.getUserId()).setValue(user);
    }

    public void getProfile(final String userId){

        mDatabase.child(User.USER_TABLE).child(userId).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot){
                User user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    //methods concerning Story
    public String createStory(Story story){

        DatabaseReference reference = mDatabase.child(Story.STORY_TABLE);
        String storyId = reference.push().getKey();
        story.setStoryId(storyId);
        reference.child(story.getStoryId()).setValue(story);

        return storyId;
    }

    public void updateStory(Story story){

        Log.d("msg", "update story");
        mDatabase.child(Story.STORY_TABLE).child(story.getStoryId()).setValue(story);

    }

    public void deleteStory(final String storyId){

        mDatabase.child(Story.STORY_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(storyId)){
                    dataSnapshot.getRef().child(storyId).setValue(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getStoryByLocation(LatLng northeast, LatLng southwest){

        final ArrayList<Story> stories = new ArrayList<>();
        final HashMap<String, Story> storiesMap = new HashMap<>();
        
        double nLat = northeast.latitude, sLat = southwest.latitude;
        final double nLng = northeast.longitude, sLng = southwest.longitude;

        mDatabase.child(Story.STORY_TABLE).orderByChild(Story.KEY_STORY_LAT).startAt(sLat).endAt(nLat).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot child : dataSnapshot.getChildren()){

                            Story story = child.getValue(Story.class);

                            if(story.getStoryLng() >= sLng && story.getStoryLng() <= nLng){

                                stories.add(story);
                                storiesMap.put(story.getStoryId(), story);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//        return stories;
//        return storiesMap;
    }
    
    //methods concerning Likes
    public String like(Likes like){
        
        DatabaseReference reference = mDatabase.child(Likes.LIKES_TABLE);
        String likeId = reference.push().getKey();
        like.setLikeId(likeId);
        reference.child(like.getLikeId()).setValue(like);
        
        updateStoryLikes(ADD_LIKE, like.getStoryId());

        return likeId;
    }

    public void unlike(final Likes like){

        mDatabase.child(Likes.LIKES_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(like.getLikeId())){
                    dataSnapshot.getRef().child(like.getLikeId()).setValue(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateStoryLikes(REMOVE_LIKE, like.getStoryId());
    }

    private void updateStoryLikes(final int code, final String storyId){

        mDatabase.child(Story.STORY_TABLE).child(storyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get the number of story likes
                int storyLikes = (int) dataSnapshot.child(Story.KEY_STORY_LIKES).getValue();

                Map<String,Object> map = new HashMap<String,Object>();

                DatabaseReference reference = mDatabase.child(Story.STORY_TABLE).child(storyId);

                if(code == ADD_LIKE){
                    //add like
                    storyLikes++;
                    map.put(Story.KEY_STORY_LIKES, storyLikes);
                    reference.updateChildren(map);
                }else{
                    //delete like
                    storyLikes--;
                    map.put(Story.KEY_STORY_LIKES, storyLikes);
                    reference.updateChildren(map);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}