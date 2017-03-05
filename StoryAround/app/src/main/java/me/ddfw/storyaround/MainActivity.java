package me.ddfw.storyaround;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import me.ddfw.storyaround.model.Likes;
import me.ddfw.storyaround.model.Story;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyDatabase myDatabase = new MyDatabase();

        LatLng northeast = new LatLng(40, 0);
        LatLng southwest = new LatLng(20, 100);

        ArrayList<Story> stories = myDatabase.getStoryByLocation(northeast, southwest);

        Log.d("msg", "the size of stories is: " + stories.size());
    }
}
