package me.ddfw.storyaround;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.ddfw.storyaround.model.Likes;
import me.ddfw.storyaround.model.Story;
import me.ddfw.storyaround.model.User;



public class MyDatabase {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private int ADD_LIKE = 0;
    private int REMOVE_LIKE = 1;
    public User mUser;

    public static ArrayList<Story> stories = new ArrayList<>();

    public MyDatabase(){}

    //methods concerning User
    public void createProfile(User user){
        mDatabase.child(User.USER_TABLE).child(user.getUserId()).setValue(user);
    }

    public void updateProfile(User user){
        mDatabase.child(User.USER_TABLE).child(user.getUserId()).setValue(user);
    }


    //methods concerning Story
    public String createStory(Story story){
        Log.d("DEBUG","create now");
        DatabaseReference reference = mDatabase.child(Story.STORY_TABLE);
        String storyId = reference.push().getKey();
        story.setStoryId(storyId);
        reference.child(story.getStoryId()).setValue(story);

        return storyId;
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

    /*public void unlike(String userId, String storyId){

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
    }*/

    private void updateStoryLikes(final int code, final String storyId){

        mDatabase.child(Story.STORY_TABLE).child(storyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get the number of story likes
                long storyLikes = (long) dataSnapshot.child(Story.KEY_STORY_LIKES).getValue();

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
