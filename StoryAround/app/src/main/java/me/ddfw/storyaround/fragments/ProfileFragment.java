package me.ddfw.storyaround.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import me.ddfw.storyaround.Authen.ChooserActivity;
import me.ddfw.storyaround.Global;
import me.ddfw.storyaround.MainActivity;
import me.ddfw.storyaround.R;

import static android.R.attr.action;
import static android.R.attr.start;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    //Declare firebase user
    private FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;

        //Get current user;
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        //Check whether user is logged in
        if (mUser == null) {
            //User is not logged in then turn to Chooseractivity
            rootView = inflater.inflate(R.layout.fragment_profile_login,container,false);
            setLoginBtn(rootView);
        }
        else {
            //If User is logged in
            rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            setProfileBtn(rootView);
        }
        setRetainInstance(true);

        return rootView;
    }


    private void setLoginBtn(View rootView) {
        Button btnLogin = (Button) rootView.findViewById(R.id.btn_switch_to_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitch();
                Log.d(TAG,"onSwitch");
                Toast.makeText(getActivity().getApplicationContext(),"onSwitch",Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setProfileBtn(View rootView) {
        Button btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignout();
                Log.d(TAG,"onSignout");
                Toast.makeText(getActivity().getApplicationContext(),"Log out",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onSignout() {
        ((MainActivity)getActivity()).setmLoginMethod("");
        FirebaseAuth.getInstance().signOut();
    }

    private void onSwitch(){
        Intent intent = new Intent(getContext(),ChooserActivity.class);
        startActivity(intent);
        getActivity().finish();
    }








}
