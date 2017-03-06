package me.ddfw.storyaround.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import me.ddfw.storyaround.R;


public class LikesFragment extends Fragment {

    private ArrayAdapter<String> mAdapter; // tester adapter
    private ArrayList<String> data;

    // private ArrayList<Story> data;
    // private static ArrayAdapter<Story> mAdapter; // TODO

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_likes, container, false);


        /*
        ListView list = (ListView) rootView.findViewById(R.id.story_list);
        /*final List<Story> testData = Story.getTestStories();*/
        /*StoryListAdapter storyListAdapter = new StoryListAdapter(getActivity(),testData);*/
        //list.setAdapter(storyListAdapter);
        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DialogFragment dialog;
                dialog = StoryDetailFragment.buildDialog(data.get(i));
                dialog.show(getFragmentManager(), "");
            }
        });*/



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




}
