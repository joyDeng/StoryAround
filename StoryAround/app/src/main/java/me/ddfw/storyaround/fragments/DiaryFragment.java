package me.ddfw.storyaround.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import me.ddfw.storyaround.R;
import me.ddfw.storyaround.StoryListAdapter;
import me.ddfw.storyaround.model.Story;


public class DiaryFragment extends Fragment {
        private StoryListAdapter storyListAdapter; // tester adapter
        //    private ArrayList<String> data;
        private ArrayList<Story> stories;
        private ListView list;
        private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        private String userId;

        // private ArrayList<Story> data;
        // private static ArrayAdapter<Story> mAdapter; // TODO

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_diary, container, false);


            //get current user id
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }

            list = (ListView) rootView.findViewById(R.id.story_list);
            list.setAdapter(storyListAdapter);

            stories = new ArrayList<>();

//        //initialize adapter
            storyListAdapter = new StoryListAdapter(getActivity(), stories);
            list.setAdapter(storyListAdapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    DialogFragment dialog;
                    dialog = StoryDetailFragment.buildDialog(stories.get(i));
                    dialog.show(getFragmentManager(), "");
                }
            });
            if(userId!=null)
                databaseRef.child(Story.STORY_TABLE).orderByChild(Story.KEY_STORY_AUTHOR_ID).
                        equalTo(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Story story = dataSnapshot.getValue(Story.class);
                        storyListAdapter.insert(story,0);
                        storyListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String id = (String) dataSnapshot.getKey();
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



            return rootView;
        }

    @Override
    public void onStart() {
        super.onStart();
        //query story id

    }

}
