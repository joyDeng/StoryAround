package me.ddfw.storyaround.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.ddfw.storyaround.R;
import me.ddfw.storyaround.StoryListAdapter;
import me.ddfw.storyaround.model.Likes;
import me.ddfw.storyaround.model.Story;


public class LikesFragment extends Fragment {

    private StoryListAdapter storyListAdapter; // tester adapter
//    private ArrayList<String> data;
    private ArrayList<Story> stories;

    private ListView list;

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    // private ArrayList<Story> data;
    // private static ArrayAdapter<Story> mAdapter; // TODO

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_likes, container, false);


        //get current user id
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        list = (ListView) rootView.findViewById(R.id.story_list);

        stories = new ArrayList<>();

//        //initialize adapter
//        storyListAdapter = new StoryListAdapter(getActivity(), stories);

        //query story id
        database.child(Likes.LIKES_TABLE).orderByChild(Likes.KEY_LIKES_USER_ID).equalTo(userId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //fetch all stories that the user like
                        for(DataSnapshot child : dataSnapshot.getChildren()){

                            //get story id
                            String storyId = (String) child.child(Likes.KEY_LIKES_STORY_ID).getValue();


                            //pass the story id to fetch the story object
                            getStory(storyId);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        return rootView;
    }

    private void getStory(final String storyId){

        //get story whose id is storyId
        database.child(Story.STORY_TABLE).orderByChild(Story.KEY_STORY_ID).equalTo(storyId).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot child : dataSnapshot.getChildren()){

                    Story story = child.getValue(Story.class);
                    stories.add(story);

                }

                //add to adapter
                storyListAdapter = new StoryListAdapter(getActivity(), stories);

                list.setAdapter(storyListAdapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //// TODO: 3/5/17
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
