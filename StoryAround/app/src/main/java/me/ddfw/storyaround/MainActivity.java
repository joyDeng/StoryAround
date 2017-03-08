package me.ddfw.storyaround;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;
import java.util.List;

import me.ddfw.storyaround.Authen.ChooserActivity;
import me.ddfw.storyaround.fragments.DiaryFragment;
import me.ddfw.storyaround.fragments.LikesFragment;
import me.ddfw.storyaround.fragments.MapFragment;
import me.ddfw.storyaround.fragments.PostFragment;
import me.ddfw.storyaround.fragments.ProfileFragment;


public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
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

    //comments
    //declare_auth
    private FirebaseAuth mAuth;

    //declare_auth_listener
    private FirebaseAuth.AuthStateListener mAuthListener;

    //set a context
    private Context mcontext = this;

    //declare_login_status
    private String mLoginMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUser();
        checkPermission(this);
        pageSetup();
    }

    public void checkUser(){

       mLoginMethod = getIntent().getStringExtra(Global.LOGIN_METHOD);
        // initialize_auth
        mAuth = FirebaseAuth.getInstance();

        if(mLoginMethod == null) mLoginMethod = "";

        // START:auth_state_listener
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null || !mLoginMethod.equals("") ){
                    // User signed in
                    //Log.d(TAG, "onAuthStateChanged:signed_in:"+user.getUid());
                }else{
                    //Log.d(TAG, "onAuthStateChanged:signed_out:");
                    startChooser();
                }
            }
        };
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

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                try {
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                }catch (Exception e) {}
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //addAuthListener to mAuth
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        //removeAuthListener from mAuth
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_diary, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP)
            profileFragment.onActivityResult(requestCode, resultCode, data);
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

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }*/



    // START: set_login_method
    public void setmLoginMethod(String string){
        mLoginMethod = string;
    }
    // END: set_login_method

    // START: Turn to login page
    public void startChooser(){
        Intent intent = new Intent(mcontext,ChooserActivity.class);
        intent.putExtra(Global.LOGIN_METHOD,mLoginMethod);
        startActivity(intent);
        finish();
    }
    // END: Turn to login page

}










