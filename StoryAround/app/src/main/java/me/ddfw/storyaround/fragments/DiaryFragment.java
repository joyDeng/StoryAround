package me.ddfw.storyaround.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import me.ddfw.storyaround.Authen.ChooserActivity;
import me.ddfw.storyaround.NewStoryActivity;
import me.ddfw.storyaround.R;
import me.ddfw.storyaround.StoryListAdapter;
import me.ddfw.storyaround.model.Story;

import static android.app.Activity.RESULT_OK;
import static me.ddfw.storyaround.fragments.PostFragment.ADDR_KEY;
import static me.ddfw.storyaround.fragments.PostFragment.LOCATION_KEY;


public class DiaryFragment extends Fragment {
    // New story activity request code
    public final static int NEW_STORY_REQUEST = 3;

    // Mode of diary fragment(show your story or saved locations)
    private final static int MODE_DIARY = 1;
    private final static int MODE_SAVED = 2;

    // List adapter for list view
    private StoryListAdapter storyListAdapter;

    // Arrays for stories and saved locations
    private ArrayList<Story> stories;
    private ArrayList<LatLng> locations;
    private ArrayList<String> addrs;

    // Views and mode control
    private ListView list;
    private ListView locationList;
    private int mode = MODE_DIARY;

    // For user authentication
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private String userId;

    // SharedPreference for reading the saved locaitons
    private SharedPreferences mprefs;
    private SharedPreferences.Editor meditor;
    private Gson gson = new Gson();
    private int savedLocationItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization
        mprefs = getActivity().getSharedPreferences(PostFragment.PREF_KEY, Context.MODE_PRIVATE);
        meditor = mprefs.edit();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Set menu icon by mode of fragment
        // If user is not logged in, do not show menu
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mFirebaseUser != null){
            inflater.inflate(R.menu.menu_diary, menu);
            if(mode == MODE_SAVED){
                menu.getItem(0).setIcon(R.drawable.book);
            }
        }
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Toggle the menu button and show different list view
        // on click the menu item
        if(mode == MODE_DIARY){
            showSaved();
            item.setIcon(R.drawable.book);
            mode = MODE_SAVED;
        }else if(mode == MODE_SAVED){

            showDiary();
            item.setIcon(R.drawable.folder);
            mode = MODE_DIARY;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Check if user logged in
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        View rootView;
        if(mFirebaseUser == null){
            // if not logged in, show log in button
            rootView = inflater.inflate(R.layout.fragment_profile_login, container, false);
            setLoginBtn(rootView);
        }else{
            // if user logged in
            rootView = inflater.inflate(R.layout.fragment_diary, container, false);
            //get current user id
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
            // initialize list view and adapter
            list = (ListView) rootView.findViewById(R.id.story_list);
            stories = new ArrayList<>();
            storyListAdapter = new StoryListAdapter(getActivity(), stories);
            list.setAdapter(storyListAdapter);

            // set on item click listener: open a dialog when click it
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    DialogFragment dialog;
                    dialog = StoryDetailFragment.buildDialog(stories.get(i));
                    dialog.show(getFragmentManager(), "");
                }
            });

            // load saved location and set it to invisible
            // because we show user dairy in default
            locationList = (ListView) rootView.findViewById(R.id.location_list);
            loadSaved();
            locationList.setVisibility(View.INVISIBLE);

            // listen to the database change on all stories
            // with the author id = current user id
            // when new story inserted, add it to the list view
            // when deleted, remove it from the list view
            if(userId!=null)
                databaseRef.child(Story.STORY_TABLE).orderByChild(Story.KEY_STORY_AUTHOR_ID).
                        equalTo(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Story story = dataSnapshot.getValue(Story.class);
                        storyListAdapter.insert(story,0);
                        // stories.add(story);
                        //storyListAdapter.insert(story,0);
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
        }


        return rootView;
    }

    public void showDiary(){
        list.setVisibility(View.VISIBLE);
        locationList.setVisibility(View.INVISIBLE);
    }

    public void showSaved(){
        list.setVisibility(View.INVISIBLE);
        loadSaved();
        locationList.setVisibility(View.VISIBLE);
    }

    // load saved locations
    // from shared preference
    // set on click listener for them
    public void loadSaved(){
        String json = mprefs.getString(LOCATION_KEY, null);
        String json2 = mprefs.getString(ADDR_KEY,null);
        locations = new ArrayList<>();
        addrs = new ArrayList<>();
        if(json != null){
            locations = gson.fromJson(json, new TypeToken<List<LatLng>>(){}.getType());
        }
        if(json2 != null){
            addrs = gson.fromJson(json2, new TypeToken<List<String>>(){}.getType());
        }
        locationList.setAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_saved, R.id.addr, addrs));
        locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                savedLocationItem = i;
                Intent intent;
                intent = new Intent(getActivity().getApplicationContext(), NewStoryActivity.class);
                intent.putExtra(NewStoryActivity.STORY_LAT, locations.get(i).latitude);
                intent.putExtra(NewStoryActivity.STORY_LNG, locations.get(i).longitude);
                startActivityForResult(intent,NEW_STORY_REQUEST);
            }
        });
    }

    // after user post a story by using the save location,
    // delete the corresponding location
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_STORY_REQUEST && resultCode == RESULT_OK){
            //TODO
            deleteSavedLocation(savedLocationItem);
        }
    }

    // delete a saved location from shared preference
    public void deleteSavedLocation(int item){
        locations.remove(item);
        addrs.remove(item);
        meditor.clear();
        String json = gson.toJson(locations);
        String json2 = gson.toJson(addrs);
        meditor.putString(LOCATION_KEY, json);
        meditor.putString(ADDR_KEY, json2);
        meditor.commit();
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
