package me.ddfw.storyaround.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.ddfw.storyaround.MyDatabase;
import me.ddfw.storyaround.R;
import me.ddfw.storyaround.model.Likes;
import me.ddfw.storyaround.model.Story;
import me.ddfw.storyaround.model.User;

/**
 * Created by apple on 2017/2/27.
 */

public class StoryDetailFragment extends DialogFragment {
    //public static final int DETAILS_DIALOG = 1;
    private static final String STORY_DATA_KEY = "story";
    private Story story;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private String userId;
    private boolean liked = false;
    private MyDatabase mdb = new MyDatabase();



    public static StoryDetailFragment buildDialog(Story story){
        StoryDetailFragment dialog = new StoryDetailFragment();
        Bundle args = new Bundle();
        //args.putInt(DIALOG_ID_KEY, dialog_id);
        args.putParcelable(STORY_DATA_KEY, story);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        final Story story = getArguments().getParcelable(STORY_DATA_KEY);
        final String storyId = story.getStoryId();
        final Activity parent = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        LayoutInflater i = parent.getLayoutInflater();
        View v;
        final ViewHolder viewHolder = new ViewHolder();
        final TextView text;
        v = i.inflate(R.layout.dialog_detail_story,null);
        viewHolder.user = (TextView) v.findViewById(R.id.story_user) ;
        viewHolder.title = (TextView) v.findViewById(R.id.story_title) ;
        viewHolder.location = (TextView) v.findViewById(R.id.story_location) ;
        viewHolder.tag = (TextView) v.findViewById(R.id.story_tag) ;
        viewHolder.content = (TextView) v.findViewById(R.id.story_content) ;
        final TextView likeNumber = (TextView) v.findViewById(R.id.story_like);
        ImageView image = (ImageView)v.findViewById(R.id.story_image);
        // Reference to an image file in Firebase Storage
        if(story.getStoryImgURL() != null && !story.getStoryImgURL().isEmpty()){
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(story.getStoryImgURL());
            // Load the image using Glide
            Glide.with(getActivity())
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(image);
        }

        // Get author name by ID
        database.child(User.USER_TABLE).child(story.getStoryAuthorId()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //if the user exists
                        if(dataSnapshot.exists()){
                            //get user name
                            String authorName = (String) dataSnapshot.child(User.KEY_USER_NAME).getValue();
                            //show the user name
                            viewHolder.user.setText(authorName);
                        }else{
                            viewHolder.user.setText("Anonymous");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        try{
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(story.getStoryLat(), story.getStoryLng(), 1);
            Address address = addresses.get(0);
            String line = "";
            for(int c=0; c<address.getMaxAddressLineIndex(); c++){
                line += address.getAddressLine(c)+",";
            }
            line.substring(0,line.length()-1);
            viewHolder.location.setText(String.valueOf(line));
        }catch (IOException e){


        }
        final ImageView heart = (ImageView)v.findViewById(R.id.heart_icon);
        // Test if user liked this story
        final List<String> likeIds = new ArrayList<>();
        if(userId!=null)
        database.child(Likes.LIKES_TABLE).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        int count = 0;
                        for(DataSnapshot child: dataSnapshot.getChildren()){

                            if((child.child(Likes.KEY_LIKES_STORY_ID).getValue().equals(storyId))){
                                count++;
                                if( ((String)child.child(Likes.KEY_LIKES_USER_ID).getValue())
                                        .equals(userId) ){
                                    liked = true;
                                    likeIds.clear();
                                    likeIds.add(child.getKey());
                                    heart.setImageResource(R.drawable.heart1);
                                }
                            }
                        }
                        if(!liked){
                            heart.setImageResource(R.drawable.heart2);
                        }
                        likeNumber.setText(String.valueOf(count));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        LinearLayout likeLayout = (LinearLayout)v.findViewById(R.id.like_layout);
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!liked && userId!=null){
                    Likes l = new Likes(userId, storyId, System.currentTimeMillis());
                    mdb.like(l);
                }else if(liked && likeIds.size()>0){
                    Likes l = new Likes();
                    l.setLikeId(likeIds.get(0));
                    l.setStoryId(storyId);
                    mdb.unlike(l);
                    liked = false;
                }
            }
        });

        Button btn_delete = (Button) v.findViewById(R.id.btn_delete);

        if(userId != null){

            if(story.getStoryAuthorId().equals(userId)){

                btn_delete.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view){

                        mdb.deleteStory(storyId);

                        dismiss();

                    }

                });

            }else
                btn_delete.setVisibility(View.GONE);
        }else{
            btn_delete.setVisibility(View.GONE);
        }


        //viewHolder.user.setText(story.);
        viewHolder.title.setText(story.getStoryTitle());
        //viewHolder.like.setText(String.valueOf(story.getStoryLikes()));
        //viewHolder.location.setText(String.valueOf(story.getStoryLat()));
        viewHolder.tag.setText(story.getStoryType()+"");
        viewHolder.content.setText(story.getStoryContent());


        builder.setView(v);

        return builder.create();
    }

    private static class ViewHolder {
        TextView user;
        TextView title;
        TextView location;
        TextView tag;
        TextView content;
        TextView like;
    }

}
