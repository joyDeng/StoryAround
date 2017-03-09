package me.ddfw.storyaround.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.ddfw.storyaround.Authen.ChooserActivity;
import me.ddfw.storyaround.R;
import me.ddfw.storyaround.StoryListAdapter;
import me.ddfw.storyaround.model.Likes;
import me.ddfw.storyaround.model.Story;

import static me.ddfw.storyaround.MyDatabase.stories;


public class LikesFragment extends Fragment {

    // list adapter and list view
    private StoryListAdapter storyListAdapter;
    private ListView list;
    // database reference for query the firebase real time database
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        View rootView;
        // Check if user logged in
        if(mFirebaseUser == null){
            // if not logged in, show log in button
            rootView = inflater.inflate(R.layout.fragment_profile_login, container, false);
            setLoginBtn(rootView);
        }else{
            rootView = inflater.inflate(R.layout.fragment_likes, container, false);
            String userId = null;
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
            // set the list adapter for displaying all liked stories
            list = (ListView) rootView.findViewById(R.id.story_list);
            list.setAdapter(storyListAdapter);
            stories = new ArrayList<>();
            storyListAdapter = new StoryListAdapter(getActivity(), stories);
            list.setAdapter(storyListAdapter);

            // set on item click listener to display the detail information
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    DialogFragment dialog;
                    dialog = StoryDetailFragment.buildDialog(stories.get(i));
                    dialog.show(getFragmentManager(), "");
                }
            });

            // query likes object by user id
            if(userId!=null)
                databaseRef.child(Likes.LIKES_TABLE).orderByChild(Likes.KEY_LIKES_USER_ID).
                        equalTo(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String storyId = (String)dataSnapshot.child(Likes.KEY_LIKES_STORY_ID).getValue();
                        // query story object by story id
                        insertStory(storyId);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    // if it is unliked
                    // remove from the list
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String id = (String) dataSnapshot.child(Likes.KEY_LIKES_STORY_ID).getValue();
                        for (Story s: stories){
                            if(s.getStoryId().equals(id)){
                                stories.remove(s);
                                break;
                            }
                        }
                        storyListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        }



        return rootView;
    }

    // query the story object by id, and add it into arraylist
    private void insertStory(final String storyId){
        databaseRef.child(Story.STORY_TABLE).child(storyId).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("ADD",(String)dataSnapshot.child(Story.KEY_STORY_TITLE).getValue());
                        Story story = dataSnapshot.getValue(Story.class);
                        storyListAdapter.insert(story,0);
                        storyListAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    // set login button
    // if user not logged in
    private void setLoginBtn(View rootView) {
        Button btnLogin = (Button) rootView.findViewById(R.id.btn_switch_to_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitch();
            }
        });
    }

    // open login activity
    private void onSwitch() {
        Intent intent = new Intent(getContext(), ChooserActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


}
