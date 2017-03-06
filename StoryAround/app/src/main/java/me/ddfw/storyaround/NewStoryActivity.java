package me.ddfw.storyaround;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.ddfw.storyaround.model.Story;


// list of TODO
// image to url
// story type
// story privacy




public class NewStoryActivity extends AppCompatActivity {
    private Story mStory;
    private MyDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private LocationManager locationManager;

    public static final String STORY_LAT = "lat";
    public static final String STORY_LNG = "lng";

    private final int MY_PERMISSIONS_REQUEST = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_story);

        // create the button listener
        CreateStoryListener();

        // set up the database
        database = new MyDatabase();
        mStory = new Story();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // get the location pass from other activity if exists
        if (getIntent().getExtras() != null ) {
            Double lat = getIntent().getExtras().getDouble(STORY_LAT);
            Double lng = getIntent().getExtras().getDouble(STORY_LNG);
            mStory.setStoryLat(lat);
            mStory.setStoryLng(lng);
        }
        else {
            checkPermissions();
        }
    }


    // set the pre-saved location as the location where you want to write a mStory
    private void setLocationText(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        TextView locationTextView = (TextView) findViewById(R.id.story_location);
        String addressText = "  ";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Log.d("******", "" + addresses.size());
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    if (i != address.getMaxAddressLineIndex() - 1) {
                        addressText += address.getAddressLine(i) + ", ";
                    } else {
                        addressText += address.getAddressLine(i);
                    }
                locationTextView.setText(addressText);
            } else {
                locationTextView.setText("Middle of Nowhere");
            }
        } catch (Exception e) {
            locationTextView.setText("Middle of Nowhere");
            Log.d("******", "set location text fail");
        }
    }

    // set the current location as the location where you want to write a mStory
    private void setCurrentLocationText() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);
        Log.d("******","isProviderEnabled: " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
        try {
            Location location = locationManager.getLastKnownLocation(provider);
            mStory.setStoryLat(location.getLatitude());
            mStory.setStoryLng(location.getLongitude());
            setLocationText(mStory.getStoryLat(), mStory.getStoryLng());
            Log.d("******", "lat: " + location.getLatitude()
                    + ", lng: " + location.getLongitude());
        }catch(SecurityException e) {
            checkPermissions();
        }
    }

    // create the button listener, onClick
    private void CreateStoryListener() {
        // pick your image
        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        // mStory type
        // TODO
        // mStory privacy
        // TODO
        // save button
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStory();
            }
        });
        // cancel button
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // some small modified with the UI
        ImageView iconLocation = (ImageView) findViewById(R.id.location_icon);
        iconLocation.setColorFilter(getResources().getColor(R.color.color3));
    }


    private void pickImage() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Upload Image")
                .setItems(new CharSequence[]{"Open Camera", "Select from Gallery"},
                        new DialogInterface.OnClickListener() {
                            public void onClick (DialogInterface dialog, int picker) {
                                switch (picker) {
                                    case 0:
                                        //loadFromCamera();
                                        break;
                                    case 1:
                                        //loadFromGallery();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).create().show();
    }




    private void saveStory() {
        EditText storyTitleEditor = (EditText)findViewById(R.id.story_title);
        EditText storyContentEditor = (EditText)findViewById(R.id.story_content);

        mStory.setStoryAuthorId(user.getUid());
        mStory.setStoryLat(0);
        mStory.setStoryLng(0);
        mStory.setStoryType(0);
        mStory.setStoryMode(0);
        mStory.setStoryDateTime(Calendar.getInstance().getTimeInMillis());
        mStory.setStoryImgURL("test image url");
        mStory.setStoryTitle(storyTitleEditor.getText().toString());
        mStory.setStoryContent(storyContentEditor.getText().toString());
        mStory.setStoryLikes(0);

        // TODO
        // thread
        database.createStory(mStory);
        Toast.makeText(this,"your mStory will be heard", Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setCurrentLocationText();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    public void checkPermissions(){
        if(Build.VERSION.SDK_INT < 23)
            return;
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        else {
            setCurrentLocationText();
        }
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Editor?")
                .setMessage("Are you sure you want to exit the editor?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
