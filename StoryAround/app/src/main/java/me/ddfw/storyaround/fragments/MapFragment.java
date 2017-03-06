package me.ddfw.storyaround.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
    private View rootView;
    private MapView mMapView;
    private GoogleMap googleMap;
    private List<Marker> markers;
    private List<LatLng> locations;
    private List<Story> stories;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     MapsInitializer.initialize(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
//        stories = Story.getTestStories();
//        locations = new ArrayList<>();
//        markers = new ArrayList<>();
//        for(Story s:stories){
//            locations.add(s.getLocation());
//        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
//        if (mMapView != null) {
//            // Initialise the MapView
//            Log.d("DEBUG", mMapView.toString());
//            mMapView.onCreate(savedInstanceState);
//            mMapView.onResume();
//            // Set the map ready callback to receive the GoogleMap object
//            mMapView.getMapAsync(this);
//        }

    }
   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        setRetainInstance(true);


        mMapView = (MapView) rootView.findViewById(mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
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
                LatLng sydney = new LatLng(-34, 151);
                //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the map of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }*/

    @Override
    public void onResume() {
        super.onResume();
//        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
 //       mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
  //      mMapView.onLowMemory();
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
        CameraPosition cameraPosition = new CameraPosition.Builder().target(locations.get(1)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
