package me.ddfw.storyaround;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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

        mapFragment = new MapFragment();
        likesFragment = new LikesFragment();
        postFragment = new PostFragment();
        diaryFragment = new DiaryFragment();
        profileFragment = new ProfileFragment();

        fragments = new ArrayList<Fragment>();
        fragments.add(mapFragment);
        fragments.add(likesFragment);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getFragmentManager(), fragments, this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(myFragmentPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
    }






















}










