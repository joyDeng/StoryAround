package me.ddfw.storyaround.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ddfw.storyaround.R;


public class DiaryFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary, container, false);

        Log.d("******","DiaryFragment onCreateView");


        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        menu.clear();
        inflater.inflate(R.menu.menu_diary,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


}
