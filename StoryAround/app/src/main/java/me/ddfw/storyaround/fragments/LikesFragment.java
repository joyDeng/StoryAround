package me.ddfw.storyaround.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import me.ddfw.storyaround.R;


public class LikesFragment extends ListFragment {

    private ArrayAdapter<String> mAdapter; // tester adapter
    private ArrayList<String> data;

    // private ArrayList<Story> data;
    // private static ArrayAdapter<Story> mAdapter; // TODO

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_likes, container, false);
        setRetainInstance(true);

        Log.d("******","LikesFragment onCreateView");

        data = new ArrayList<String>();
        mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, data);
        mAdapter.add("Story 1");
        mAdapter.add("Story 2");
        mAdapter.add("Story 3");
        mAdapter.add("Story 4");
        mAdapter.add("Story 5");

        setListAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {

        // TODO
        // Story Activity
        // Story object class
        /*
        Intent intent;
        Bundle bundle;
        Story story = data.get(position); // later on when we implement the Story object
        intent = new Intent(getActivity().getApplicationContext(), StoryActivity.class);
        bundle = new Bundle();
        bundle.putLong("id", story.getId());
        startActivity(intent);
        */


        Toast.makeText(getActivity().getApplicationContext(),
                "Story Activity #" + position,
                Toast.LENGTH_SHORT).show();

    }


}
