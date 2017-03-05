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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import me.ddfw.storyaround.R;
import me.ddfw.storyaround.model.Story;


public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    public final static int LOCATION_PERMISSION_REQUEST = 1;
    private View rootView;
    private MapView mMapView;
    private GoogleMap googleMap;
    private List<Marker> markers;
    private List<LatLng> locations;
    private List<Story> stories;
    private LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        stories = Story.getTestStories();
        locations = new ArrayList<>();
        markers = new ArrayList<>();
        for(Story s:stories){
            locations.add(s.getLocation());
        }
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
            Location current = locationManager.getLastKnownLocation(provider);
            if(googleMap!=null){
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inMutable = true;
                Bitmap imageBitmap= BitmapFactory.decodeResource(getResources(),
                        R.drawable.logo,opt);
                Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 150, 150, true);
                googleMap.addMarker(new MarkerOptions().icon(
                        BitmapDescriptorFactory.fromBitmap(resized)).position(locationToLatLng(current)));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(locationToLatLng(current)).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }catch (SecurityException e){

        }

        //onLocationChanged(location);
        //locationManager.requestLocationUpdates(provider, 0, 0, this);

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
        markers.add(googleMap.addMarker(new MarkerOptions().icon(
                BitmapDescriptorFactory.fromBitmap(resized)).position(locations.get(0))));
        markers.add(googleMap.addMarker(new MarkerOptions().icon(
                BitmapDescriptorFactory.fromBitmap(resized)).position(locations.get(1))));
        markers.add(googleMap.addMarker(new MarkerOptions().position(locations.get(2))));
        //start = map.addMarker(new MarkerOptions().position(list.get(0)));

        // For zooming automatically to the map of the marker
       // CameraPosition cameraPosition = new CameraPosition.Builder().target(locations.get(1)).zoom(12).build();
        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        checkPermission(getActivity());
    }
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(markers.get(1))) {
            //handle click here
            DialogFragment dialog;
            dialog = StoryDetailFragment.buildDialog(stories.get(1));
            dialog.show(getFragmentManager(), "");
        }
        return true;
    }




}
