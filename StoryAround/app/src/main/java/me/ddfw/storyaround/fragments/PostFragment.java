package me.ddfw.storyaround.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import me.ddfw.storyaround.MyDatabase;
import me.ddfw.storyaround.NewStoryActivity;
import me.ddfw.storyaround.R;
import me.ddfw.storyaround.model.Story;

import static me.ddfw.storyaround.fragments.MapFragment.LOCATION_PERMISSION_REQUEST;

public class PostFragment extends Fragment{
    List<LatLng> stories = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        setRetainInstance(true);

        Button btnStart = (Button) rootView.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartClicked();
            }
        });

        Button btnSave = (Button) rootView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClicked();
            }
        });


        LatLng northeast = new LatLng(43.70894514683601,-72.28166989982128);
        LatLng southwest = new LatLng(43.704637572959946,-72.28608381003141);
        LatLng lib = new LatLng(43.70509735633434,-72.28840056806803);
        LatLng np = new LatLng(43.705882882132016,-72.28292651474476);
        LatLng sudi = new LatLng(43.70672462667574,-72.28666987270117);
        LatLng pond = new LatLng(43.70970905267551,-72.28879988193512);
        LatLng tuck = new LatLng(43.70539426254353,-72.29504104703665);
        stories.add(northeast);
        stories.add(southwest);
        stories.add(lib);
        stories.add(np);
        stories.add(sudi);
        stories.add(pond);
        stories.add(tuck);



        return rootView;
    }



    public void onStartClicked() {
        Intent intent;
        intent = new Intent(getActivity().getApplicationContext(), NewStoryActivity.class);
        startActivity(intent);
    }

    public void onSaveClicked() {
        //write test data inside:
        checkPermission(getActivity());
    }

    public void writeRandomStory(){
        Log.d("DEBUG","random story written");
        MyDatabase db = new MyDatabase();
        for(LatLng location: stories){
            Story s = new Story();
            s.setStoryDateTime(System.currentTimeMillis());
            s.setStoryAuthorId("lily");
            s.setStoryLat(location.latitude);
            s.setStoryLng(location.longitude);
            s.setStoryContent("test");
            s.setStoryTitle("hi");
            db.createStory(s);
        }


    }

    public void checkPermission(Activity activity){
        Log.d("DEBUG","in check permission");
        if(Build.VERSION.SDK_INT < 23){
            writeRandomStory();
            return;
        }
        if( ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }else{
            writeRandomStory();
        }

    }


}