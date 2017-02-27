package me.ddfw.storyaround.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.github.irshulx.BaseClass;
import com.github.irshulx.Editor;

import me.ddfw.storyaround.MainActivity;
import me.ddfw.storyaround.NewStoryActivity;
import me.ddfw.storyaround.R;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import com.github.irshulx.models.EditorTextStyle;

import java.io.IOException;
import java.util.Calendar;

public class PostFragment extends Fragment{

    private Editor mEditor;
    private TableLayout tabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        //setRetainInstance(true);
        Log.d("******","PostFragment onCreateView");

        Button btnStart = (Button) rootView.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartClicked();
            }
        });

        Button btnSave = (Button) rootView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClicked();
            }
        });

        return rootView;
    }



    public void onStartClicked() {
        Intent intent;
        intent = new Intent(getActivity().getApplicationContext(), NewStoryActivity.class);
        startActivity(intent);
    }

    public void onSaveClicked() {
    }


}
