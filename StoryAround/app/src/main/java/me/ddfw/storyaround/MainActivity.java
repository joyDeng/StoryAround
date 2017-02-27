package me.ddfw.storyaround;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import me.ddfw.storyaround.fragments.DiaryFragment;
import me.ddfw.storyaround.fragments.LikesFragment;
import me.ddfw.storyaround.fragments.MapFragment;
import me.ddfw.storyaround.fragments.PostFragment;
import me.ddfw.storyaround.fragments.ProfileFragment;

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






















}










