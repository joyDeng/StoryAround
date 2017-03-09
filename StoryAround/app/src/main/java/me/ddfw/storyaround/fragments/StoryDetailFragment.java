package me.ddfw.storyaround.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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

import me.ddfw.storyaround.MyDatabase;
import me.ddfw.storyaround.R;
import me.ddfw.storyaround.model.Likes;
import me.ddfw.storyaround.model.Story;
import me.ddfw.storyaround.model.User;



    // This dialog fragment is used to show the details of a story object
public class StoryDetailFragment extends DialogFragment {
    private static final String STORY_DATA_KEY = "story";

    // firebase database reference
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    // ids for query the story, user and likes object
    private String userId;
    private String storyId;
    private String likeId;
    private boolean liked = false;

    // My database object, used for some firebase database operations
    // like create and delete
    private MyDatabase mdb = new MyDatabase();
    private ViewHolder viewHolder = new ViewHolder();


    // build a dialog by story object
    public static StoryDetailFragment buildDialog(Story story){
        StoryDetailFragment dialog = new StoryDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(STORY_DATA_KEY, story);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        // get current user id
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        // get story object
        final Story story = getArguments().getParcelable(STORY_DATA_KEY);
        //
        storyId = story.getStoryId();
        final Activity parent = getActivity();

        // setup of views
        final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        LayoutInflater i = parent.getLayoutInflater();
        View v;
        viewHolder = new ViewHolder();
        v = i.inflate(R.layout.dialog_detail_story,null);
        viewHolder.user = (TextView) v.findViewById(R.id.story_user) ;
        viewHolder.title = (TextView) v.findViewById(R.id.story_title) ;
        viewHolder.location = (TextView) v.findViewById(R.id.story_location) ;
        viewHolder.tag = (TextView) v.findViewById(R.id.story_tag) ;
        viewHolder.content = (TextView) v.findViewById(R.id.story_content) ;
        viewHolder.like = (TextView) v.findViewById(R.id.story_like);
        viewHolder.storyImage = (ImageView)v.findViewById(R.id.story_image);
        viewHolder.heart = (ImageView)v.findViewById(R.id.heart_icon);

        // Reference to an image file in Firebase Storage
        if(story.getStoryImgURL() != null && !story.getStoryImgURL().isEmpty()){
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(story.getStoryImgURL());
            // Load the image using Glide
            Glide.with(getActivity())
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(viewHolder.storyImage);
        }

        // Get author name by ID
        databaseRef.child(User.USER_TABLE).child(story.getStoryAuthorId()).
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

        viewHolder.location.setText(story.getStoryAddress());

        // Check if user liked this story
        // It also count the number of likes for this story
        databaseRef.child(Likes.LIKES_TABLE).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count = 0;
                        for(DataSnapshot child: dataSnapshot.getChildren()){

                            if((child.child(Likes.KEY_LIKES_STORY_ID).getValue().equals(storyId))){
                                count++;
                                if(userId!=null)
                                if( ((String)child.child(Likes.KEY_LIKES_USER_ID).getValue())
                                        .equals(userId) ){
                                    liked = true;
                                    likeId = child.getKey();
                                    viewHolder.heart.setImageResource(R.drawable.heart1);
                                }
                            }
                        }
                        if(!liked){
                            viewHolder.heart.setImageResource(R.drawable.heart2);
                        }
                        viewHolder.like.setText(String.valueOf(count));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        liked = false;
                    }
                });

        // Like and unlike function
        LinearLayout likeLayout = (LinearLayout)v.findViewById(R.id.like_layout);
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!liked && userId!=null){
                    Likes l = new Likes(userId, storyId, System.currentTimeMillis());
                    likeId = storyId+userId;
                    databaseRef.child(Likes.LIKES_TABLE).child(likeId).setValue(l);
                    liked = true;
                }else if(liked && likeId!=null){
                    Log.d("UNLIKE","unlike called");
                    Likes l = new Likes();
                    l.setLikeId(likeId);
                    l.setStoryId(storyId);
                    databaseRef.child(Likes.LIKES_TABLE).child(likeId).setValue(null);
                    //mdb.unlike(l);
                    likeId = null;
                    liked = false;
                }
            }
        });

        // Check if current user is the author of this story
        // if true, set delete button
        // and provide delete function
        Button btn_delete = (Button) v.findViewById(R.id.btn_delete);
        if(userId != null){
            if(story.getStoryAuthorId().equals(userId)){
                btn_delete.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view){
                        mdb.deleteStory(storyId);
                        databaseRef.child(Likes.LIKES_TABLE).orderByChild(Likes.KEY_LIKES_STORY_ID).equalTo(storyId)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot child : dataSnapshot.getChildren()){
                                            child.getRef().setValue(null);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                        dismiss();
                    }

                });
            }else
                btn_delete.setVisibility(View.GONE);
        }else{
            btn_delete.setVisibility(View.GONE);
        }

        viewHolder.title.setText(story.getStoryTitle());
        viewHolder.tag.setText(story.getStoryType()+"");
        viewHolder.content.setText(story.getStoryContent());


        builder.setView(v);
        return builder.create();
    }

    // view holder for this dialog fragment
    private static class ViewHolder {
        TextView user;
        TextView title;
        TextView location;
        TextView tag;
        TextView content;
        TextView like;
        ImageView storyImage;
        ImageView heart;
    }

}