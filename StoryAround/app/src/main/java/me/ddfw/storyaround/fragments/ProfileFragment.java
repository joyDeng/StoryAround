package me.ddfw.storyaround.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import me.ddfw.storyaround.R;

import static android.R.attr.action;

public class ProfileFragment extends Fragment {
    private boolean isLogin = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        if (isLogin) {
            rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            setProfileBtn(rootView);
        }
        else {
            rootView = inflater.inflate(R.layout.fragment_login, container, false);
            setLoginBtn(rootView);
        }
        setRetainInstance(true);

        return rootView;
    }


    private void setLoginBtn(View rootView) {
        Button btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
                Log.d("******","onLogin");
                Toast.makeText(getActivity().getApplicationContext(),"onLogin",Toast.LENGTH_SHORT).show();
            }
        });
        Button btnNewAcc = (Button) rootView.findViewById(R.id.btnNewAcc);
        btnNewAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onNewAcc();
                Log.d("******","onNewAcc");
                Toast.makeText(getActivity().getApplicationContext(),"onNewAcc",Toast.LENGTH_SHORT).show();
            }
        });
        Button btnGuest = (Button) rootView.findViewById(R.id.btnGuest);
        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGuest();
                Log.d("******","onGuest");
                Toast.makeText(getActivity().getApplicationContext(),"Welcome",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onLogin() {
        // TODO
        isLogin = true;
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void onGuest() {
        isLogin = true;
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void setProfileBtn(View rootView) {
        Button btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout();
                Log.d("******","onLogout");
                Toast.makeText(getActivity().getApplicationContext(),"Log out",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onLogout() {
        isLogin = false;
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }








}
