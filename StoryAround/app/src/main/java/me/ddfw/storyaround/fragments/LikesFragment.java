package me.ddfw.storyaround.fragments;

import android.app.ListFragment;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
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

        ListView list = (ListView) rootView.findViewById(R.id.story_list);
        final List<Story> testData = Story.getTestStories();
        StoryListAdapter storyListAdapter = new StoryListAdapter(getActivity(),testData);
        list.setAdapter(storyListAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DialogFragment dialog;
                dialog = StoryDetailFragment.buildDialog(testData.get(i));
                dialog.show(getFragmentManager(), "");
            }
        });


        return rootView;
    }


}
