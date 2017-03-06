package me.ddfw.storyaround;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import me.ddfw.storyaround.model.Likes;
import me.ddfw.storyaround.model.Story;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.ddfw.storyaround.fragments.DiaryFragment;
import me.ddfw.storyaround.fragments.LikesFragment;
import me.ddfw.storyaround.fragments.MapFragment;
import me.ddfw.storyaround.fragments.PostFragment;
import me.ddfw.storyaround.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST = 1;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<Fragment> fragments;
    private MapFragment mapFragment;
    private LikesFragment likesFragment;
    private PostFragment postFragment;
    private DiaryFragment diaryFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Likes likes = new Likes("1213", "-KeROcyUJUuqfCccbXhH", (long) 2017);

        MyDatabase myDatabase = new MyDatabase();

//        myDatabase.like(likes);

        checkPermission(this);
        pageSetup();
    }

    public void pageSetup(){
        mapFragment = new MapFragment();
        likesFragment = new LikesFragment();
        postFragment = new PostFragment();
        diaryFragment = new DiaryFragment();
        profileFragment = new ProfileFragment();

        fragments = new ArrayList<Fragment>();
        fragments.add(mapFragment);
        fragments.add(likesFragment);
        fragments.add(postFragment);
        fragments.add(diaryFragment);
        fragments.add(profileFragment);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getFragmentManager(), fragments, this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void checkPermission(Activity activity){
        if(Build.VERSION.SDK_INT < 23) return;
        boolean i,r,w;
        i = ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET) !=
                PackageManager.PERMISSION_GRANTED;
        r = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED;
        w = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED;
        if(i || r || w ){
            List<String> s = new ArrayList<String>();
            if(i)
                s.add(Manifest.permission.INTERNET);
            if(r)
                s.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if(w)
                s.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            String[] permissions = new String[s.size()];
            s.toArray(permissions);
            ActivityCompat.requestPermissions(activity, permissions, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                boolean a = false;
                if (grantResults.length == permissions.length) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                            a = true;
                    }
                } else {
                    a = true;
                }
                if (a) {
                    Toast.makeText(this, "Sorry, but we need these permissions :)",
                            Toast.LENGTH_SHORT);
                    checkPermission(this);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request

        }
    }

}










