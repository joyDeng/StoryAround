package me.ddfw.storyaround.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import me.ddfw.storyaround.R;
import me.ddfw.storyaround.model.Story;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{
    public final static int LOCATION_PERMISSION_REQUEST = 1;
    private View rootView;
    private MapView mMapView;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private DatabaseReference mDatabase;
    final private HashMap<String, Story> storyMap = new HashMap<>();
    private Marker current;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        if (mMapView != null) {
            Log.d("DEBUG", mMapView.toString());
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void checkPermission(Activity activity){
        Log.d("DEBUG","in check permission");
        if(Build.VERSION.SDK_INT < 23){
            drawCurrentLocation();
            return;
        }
        if( ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }else{
            drawCurrentLocation();
        }

    }

    public boolean isLocationPermitted(Activity activity){
        if(Build.VERSION.SDK_INT < 23) return true;
        return  ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public void drawCurrentLocation(){
        try{
            initLocationManager();
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);
            final Location last = locationManager.getLastKnownLocation(provider);
            if(last!=null){
                current = googleMap.addMarker(new MarkerOptions().position(locationToLatLng(last))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)));
            }
            locationManager.requestLocationUpdates(provider, 0, 0, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if(googleMap!=null){
                        if(current!=null)
                            current.remove();
                        current = googleMap.addMarker(new MarkerOptions().position(locationToLatLng(location))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)));
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
            if(googleMap!=null){
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inMutable = true;
                Bitmap imageBitmap= BitmapFactory.decodeResource(getResources(),
                        R.drawable.logo,opt);
                final Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 150, 150, true);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(locationToLatLng(last)).zoom(17).build();
                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        //googleMap.clear();
                        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
                        getStoryByLocation(bounds);
                    }
                });
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.setOnMarkerClickListener(this);
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        googleMap.addMarker(new MarkerOptions().icon(
                                BitmapDescriptorFactory.fromBitmap(resized)).position(latLng));
                        Log.i("BOUND This is",latLng.toString());
                    }
                });

            }
        }catch (SecurityException e){

        }
    }

    public void getStoryByLocation(LatLngBounds bounds){
        LatLng northeast = bounds.northeast, southwest = bounds.southwest;
        double nLat = northeast.latitude, sLat = southwest.latitude;
        final double nLng = northeast.longitude, sLng = southwest.longitude;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        Bitmap imageBitmap= BitmapFactory.decodeResource(getResources(),
                R.drawable.logo,opt);
        final Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 150, 150, true);

        mDatabase.child(Story.STORY_TABLE).orderByChild(Story.KEY_STORY_LAT).startAt(sLat).endAt(nLat).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Story story = child.getValue(Story.class);
                            if(story.getStoryLng() >= sLng && story.getStoryLng() <= nLng){
                                if(!storyMap.containsKey(story.getStoryId())){
                                    storyMap.put(story.getStoryId(),story);
                                    googleMap.addMarker(new MarkerOptions().icon(
                                            BitmapDescriptorFactory.fromBitmap(resized)).position(
                                            new LatLng(story.getStoryLat(),story.getStoryLng())).
                                            title(story.getStoryId()));
                                }

                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public LatLng locationToLatLng(Location l){
        if (l != null) {
            return new LatLng(l.getLatitude(), l.getLongitude());
        }
        else {
            return new LatLng(0,0);
        }
    }


    private void initLocationManager(){
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    drawCurrentLocation();
                    Log.d("DEBUG","granted");

                }else {
                    Log.d("DEBUG","NOT granted");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));



            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        Bitmap imageBitmap= BitmapFactory.decodeResource(getResources(),
                R.drawable.logo,opt);

        //mClusterManager = new ClusterManager<MyItem>(getActivity(), googleMap);


        checkPermission(getActivity());
    }
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(storyMap.containsKey(marker.getTitle())){
            DialogFragment dialog;
            dialog = StoryDetailFragment.buildDialog(storyMap.get(marker.getTitle()));
            dialog.show(getFragmentManager(), "");
        }
        return true;
    }


}
