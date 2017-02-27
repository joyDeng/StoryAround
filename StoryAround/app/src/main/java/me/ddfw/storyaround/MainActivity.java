package me.ddfw.storyaround;

import android.app.Fragment;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import me.ddfw.storyaround.fragments.DiaryFragment;
import me.ddfw.storyaround.fragments.LikesFragment;
import me.ddfw.storyaround.fragments.PostFragment;
import me.ddfw.storyaround.fragments.ProfileFragment;
import me.ddfw.storyaround.fragments.MapFragment;

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
        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);

                        // close the keyboard when change fragment
                        View focus = getCurrentFocus();
                        if (focus != null) {
                            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            keyboard.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
                        }
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }





}










