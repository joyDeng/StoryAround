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
import android.view.Menu;
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
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.ddfw.storyaround.MyItem;
import me.ddfw.storyaround.R;
import me.ddfw.storyaround.model.Story;


public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    public final static int LOCATION_PERMISSION_REQUEST = 1;
    private View rootView;
    private MapView mMapView;
    private GoogleMap googleMap;
    private List<Marker> markers;
    private List<LatLng> locations;
    private LocationManager locationManager;
    private DatabaseReference mDatabase;
    final private List<Story> stories = new ArrayList<>();
    final private HashMap<String, Story> storyMap = new HashMap<>();
    private ClusterManager<MyItem> mClusterManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MapsInitializer.initialize(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        if (mMapView != null) {
            // Initialise the MapView
            Log.d("DEBUG", mMapView.toString());
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            // Set the map ready callback to receive the GoogleMap object
            mMapView.getMapAsync(this);
        }

    }







    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.add("MapFragment test");
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
            final Location current = locationManager.getLastKnownLocation(provider);
            if(googleMap!=null){
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inMutable = true;
                Bitmap imageBitmap= BitmapFactory.decodeResource(getResources(),
                        R.drawable.logo,opt);
                final Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 150, 150, true);
                googleMap.addMarker(new MarkerOptions().icon(
                        BitmapDescriptorFactory.fromBitmap(resized)).position(locationToLatLng(current)));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(locationToLatLng(current)).zoom(17).build();
                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        //googleMap.clear();
                        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
                        getStoryByLocation(bounds);

                        /*Log.i("BOUND northeast",bounds.northeast.toString());
                        Log.i("BOUND southwest",bounds.southwest.toString());
                        Log.i("BOUND current",locationToLatLng(current).toString());
                        // Instantiates a new Polyline object and adds points to define a rectangle
                        PolylineOptions rectOptions = new PolylineOptions().add(bounds.northeast).add(bounds.southwest);
                        googleMap.addPolyline(rectOptions);
                        googleMap.addMarker(new MarkerOptions().icon(
                                BitmapDescriptorFactory.fromBitmap(resized)).position(locationToLatLng(current)));
                        googleMap.addMarker(new MarkerOptions().icon(
                                BitmapDescriptorFactory.fromBitmap(resized)).position(bounds.northeast));
                        googleMap.addMarker(new MarkerOptions().icon(
                                BitmapDescriptorFactory.fromBitmap(resized)).position(bounds.southwest));*/
                    }
                });
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

        //onLocationChanged(location);
        //locationManager.requestLocationUpdates(provider, 0, 0, this);

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
                        // after got all stories

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public LatLng locationToLatLng(Location l){
        return new LatLng(l.getLatitude(),l.getLongitude());
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

        // For showing a move to my map button
        //googleMap.setMyLocationEnabled(true);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                           getActivity(), R.raw.style_json));



            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }
        // Position the map's camera near Sydney, Australia.
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));

        // For dropping a marker at a point on the Map

        //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        Bitmap imageBitmap= BitmapFactory.decodeResource(getResources(),
                R.drawable.logo,opt);
        Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 150, 150, true);
        googleMap.setOnMarkerClickListener(this);
        markers = new ArrayList<>();
        mClusterManager = new ClusterManager<MyItem>(getActivity(), googleMap);


        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        //googleMap.setOnCameraIdleListener(mClusterManager);
        //googleMap.setOnMarkerClickListener(mClusterManager);
        /*markers.add(googleMap.addMarker(new MarkerOptions().icon(
                BitmapDescriptorFactory.fromBitmap(resized)).position(locations.get(0))));
        markers.add(googleMap.addMarker(new MarkerOptions().icon(
                BitmapDescriptorFactory.fromBitmap(resized)).position(locations.get(1))));
        markers.add(googleMap.addMarker(new MarkerOptions().position(locations.get(2))));*/
        //start = map.addMarker(new MarkerOptions().position(list.get(0)));

        // For zooming automatically to the map of the marker
       // CameraPosition cameraPosition = new CameraPosition.Builder().target(locations.get(1)).zoom(12).build();
        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
