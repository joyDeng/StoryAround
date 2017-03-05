package me.ddfw.storyaround;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import me.ddfw.storyaround.Authen.ChooserActivity;
import me.ddfw.storyaround.fragments.DiaryFragment;
import me.ddfw.storyaround.fragments.LikesFragment;
import me.ddfw.storyaround.fragments.MapFragment;
import me.ddfw.storyaround.fragments.PostFragment;
import me.ddfw.storyaround.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";

    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<Fragment> fragments;
    private MapFragment mapFragment;
    private LikesFragment likesFragment;
    private PostFragment postFragment;
    private DiaryFragment diaryFragment;
    private ProfileFragment profileFragment;

<<<<<<< HEAD
    //comments
    //declare_auth
    private FirebaseAuth mAuth;

    //declare_auth_listener
    private FirebaseAuth.AuthStateListener mAuthListener;

    //set a context
    private Context mcontext = this;

    //declare_login_status
    private String mLoginMethod = "";

=======
>>>>>>> 8b78227f58763dc067e0fd2ea4da1c8a766b1eb2
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       mLoginMethod = getIntent().getStringExtra(Global.LOGIN_METHOD);
        // initialize_auth
        mAuth = FirebaseAuth.getInstance();

        if(mLoginMethod == null) startChooser();

        // START:auth_state_listener
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null || !mLoginMethod.equals("") ){
                    // User signed in
                    //Log.d(TAG, "onAuthStateChanged:signed_in:"+user.getUid());
                }else{
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                    startChooser();
                }
            }
        };
        // END:auth_state_listener


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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

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










