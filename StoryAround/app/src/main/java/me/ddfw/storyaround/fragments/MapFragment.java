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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import me.ddfw.storyaround.R;
import me.ddfw.storyaround.model.Story;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{
    // request code for permission
    public final static int LOCATION_PERMISSION_REQUEST = 1;

    // some views
    private View rootView;
    private MapView mMapView;
    private GoogleMap googleMap;
    private LocationManager locationManager;

    // firebase database reference
    private DatabaseReference mDatabase;

    // hashmap to manage markers and stories
    final private HashMap<String, Story> storyMap = new HashMap<>();
    final private HashMap<String, Marker> markerMap = new HashMap<>();

    // marker for current location
    private Marker current;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        MapsInitializer.initialize(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Mapview setup
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        if (mMapView != null) {
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


    // overrides functions
    // for controlling map view
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

    // check permission, after user permission granted
    // show current location
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

    public void drawCurrentLocation(){
        try{
            // get current location
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

            // load stories
            // set some listeners to load new stories when map idle
            // set marker click listener to show story details
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

    // get stories by current visible region from firebase
    // listen to the data change and update the map in real time
    public void getStoryByLocation(LatLngBounds bounds){
        LatLng northeast = bounds.northeast, southwest = bounds.southwest;
        double nLat = northeast.latitude, sLat = southwest.latitude;
        final double nLng = northeast.longitude, sLng = southwest.longitude;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        Bitmap imageBitmap= BitmapFactory.decodeResource(getResources(),
                R.drawable.logo,opt);
        final Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 150, 150, true);

        mDatabase.child(Story.STORY_TABLE).orderByChild(Story.KEY_STORY_LAT).
                startAt(sLat).endAt(nLat).addChildEventListener(new ChildEventListener() {
            // if new story added, put markers on map
            // and save them to hashmap for indexing
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Story story = dataSnapshot.getValue(Story.class);
                if(story.getStoryLng() >= sLng && story.getStoryLng() <= nLng){
                    storyMap.put(story.getStoryId(),story);
                    if(!markerMap.containsKey(story.getStoryId())){
                        Marker m = googleMap.addMarker(new MarkerOptions().icon(
                                BitmapDescriptorFactory.fromBitmap(resized)).position(
                                new LatLng(story.getStoryLat(),story.getStoryLng())).
                                title(story.getStoryId()));
                        markerMap.put(story.getStoryId(), m);
                    }
                }
            }

            // if a story changed, update the content,
            // but no need to redraw the markers
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Story story = dataSnapshot.getValue(Story.class);
                if(story.getStoryLng() >= sLng && story.getStoryLng() <= nLng){
                    storyMap.put(story.getStoryId(),story);
                    if(!markerMap.containsKey(story.getStoryId())){
                        Marker m = googleMap.addMarker(new MarkerOptions().icon(
                                BitmapDescriptorFactory.fromBitmap(resized)).position(
                                new LatLng(story.getStoryLat(),story.getStoryLng())).
                                title(story.getStoryId()));
                        markerMap.put(story.getStoryId(), m);
                    }
                }
            }

            // if stories deleted, remove them from the map
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Story story = dataSnapshot.getValue(Story.class);
                if(storyMap.containsKey(story.getStoryId())){
                    storyMap.remove(story.getStoryId());
                    Marker m = markerMap.get(story.getStoryId());
                    m.remove();
                    markerMap.remove(story.getStoryId());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // convert location to lat lng
    public static LatLng locationToLatLng(Location l){
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

    // handle the location permission requirements
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

    // if map is ready, change the map style
    // and draw location as well as all stories
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
        checkPermission(getActivity());
    }

    // if marker is clicked, show detail fragments
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(marker == current){
            return true;
        }
        if(storyMap.containsKey(marker.getTitle())){
            DialogFragment dialog;
            dialog = StoryDetailFragment.buildDialog(storyMap.get(marker.getTitle()));
            dialog.show(getFragmentManager(), "");
        }
        return true;
    }


}
