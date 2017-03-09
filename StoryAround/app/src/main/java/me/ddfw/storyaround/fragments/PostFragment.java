package me.ddfw.storyaround.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.ddfw.storyaround.Authen.ChooserActivity;
import me.ddfw.storyaround.NewStoryActivity;
import me.ddfw.storyaround.R;

import static me.ddfw.storyaround.fragments.MapFragment.LOCATION_PERMISSION_REQUEST;

public class PostFragment extends Fragment{
    List<LatLng> stories = new ArrayList<>();

    public static final String PREF_KEY = "my_shared_preferences";
    public static final String LOCATION_KEY = "locations";
    public static final String ADDR_KEY = "addr";
    private SharedPreferences mprefs;
    private SharedPreferences.Editor meditor;
    private ArrayList<LatLng> savedLocations;
    private ArrayList<String> savedAddr;
    private Gson gson = new Gson();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        View rootView;
        if(mFirebaseUser == null){
            rootView = inflater.inflate(R.layout.fragment_profile_login, container, false);
            setLoginBtn(rootView);
        }else{
            rootView = inflater.inflate(R.layout.fragment_post, container, false);

            setRetainInstance(true);

            mprefs = getActivity().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
            meditor = mprefs.edit();
            String json = mprefs.getString(LOCATION_KEY, null);
            String json2 = mprefs.getString(ADDR_KEY, null);

            savedLocations = new ArrayList<>();
            savedAddr = new ArrayList<>();
            if(json != null){
                savedLocations = gson.fromJson(json, new TypeToken<List<LatLng>>(){}.getType());
            }

            if(json2 != null){
                savedAddr = gson.fromJson(json2, new TypeToken<List<String>>(){}.getType());
            }


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
        }








        return rootView;
    }



    public void onStartClicked() {
        Intent intent;
        intent = new Intent(getActivity().getApplicationContext(), NewStoryActivity.class);
        startActivity(intent);
    }

    public void onSaveClicked() {
        //write test data inside:
        //markLocation();
        checkPermission(getActivity());
    }


    public void checkPermission(Activity activity){
        Log.d("DEBUG","in check permission");
        if(Build.VERSION.SDK_INT < 23){
            markLocation();
            return;
        }
        if( ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }else{
            markLocation();
        }

    }

    private void markLocation(){
        meditor.clear();
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        try{
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);
            final Location last = locationManager.getLastKnownLocation(provider);
            if(last != null){

                savedLocations.add(MapFragment.locationToLatLng(last));
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                String line = "";
                try {
                    List<Address> addresses = geocoder.getFromLocation(last.getLatitude(),
                            last.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                            if (i != address.getMaxAddressLineIndex() - 1) {
                                line += address.getAddressLine(i) + ", ";
                            } else {
                                line += address.getAddressLine(i);
                            }
                    } else {

                    }
                } catch (Exception e) {

                }
                savedAddr.add(line);

                String json = gson.toJson(savedLocations);
                String json2 = gson.toJson(savedAddr);
                //JSONArray jsonArray= new JSONArray(savedLocations);

                meditor.putString(LOCATION_KEY, json);
                meditor.putString(ADDR_KEY, json2);

                meditor.commit();
                Toast.makeText(getActivity(), "your location is saved", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(), "your location can not be detected", Toast.LENGTH_SHORT).show();
            }
        }catch(SecurityException e){}
    }

    private void setLoginBtn(View rootView) {
        Button btnLogin = (Button) rootView.findViewById(R.id.btn_switch_to_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitch();
            }
        });
    }

    private void onSwitch() {
        Intent intent = new Intent(getContext(), ChooserActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


}