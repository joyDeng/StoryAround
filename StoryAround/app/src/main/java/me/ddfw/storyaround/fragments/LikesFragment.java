package me.ddfw.storyaround.fragments;

import android.app.ListFragment;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.ddfw.storyaround.R;
import me.ddfw.storyaround.StoryListAdapter;
import me.ddfw.storyaround.model.Story;


public class LikesFragment extends Fragment {

    private ArrayAdapter<String> mAdapter; // tester adapter
    private ArrayList<String> data;

    // private ArrayList<Story> data;
    // private static ArrayAdapter<Story> mAdapter; // TODO

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_likes, container, false);

//        ListView list = (ListView) rootView.findViewById(R.id.story_list);
//        final List<Story> testData = Story.getTestStories();
//        StoryListAdapter storyListAdapter = new StoryListAdapter(getActivity(),testData);
//        list.setAdapter(storyListAdapter);
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                DialogFragment dialog;
//                dialog = StoryDetailFragment.buildDialog(testData.get(i));
//                dialog.show(getFragmentManager(), "");
//            }
//        });


        /*data = new ArrayList<String>();
        mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, data);
        mAdapter.add("Story 1");
        mAdapter.add("Story 2");
        mAdapter.add("Story 3");
        mAdapter.add("Story 4");
        mAdapter.add("Story 5");

        setListAdapter(mAdapter);*/


        return rootView;
    }

    /*@Override
    public void onListItemClick(ListView parent, View v, int position, long id) {

        // TODO
        // Story Activity
        // Story object class
        *//*
        Intent intent;
        Bundle bundle;
        Story story = data.get(position); // later on when we implement the Story object
        intent = new Intent(getActivity().getApplicationContext(), StoryActivity.class);
        bundle = new Bundle();
        bundle.putLong("id", story.getId());
        startActivity(intent);
        *//*


        Toast.makeText(getActivity().getApplicationContext(),
                "Story Activity #" + position,
                Toast.LENGTH_SHORT).show();

    }*/


}
