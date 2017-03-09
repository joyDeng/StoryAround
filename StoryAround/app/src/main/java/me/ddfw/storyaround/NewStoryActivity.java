package me.ddfw.storyaround;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.ddfw.storyaround.fragments.PostFragment;
import me.ddfw.storyaround.model.Story;


public class NewStoryActivity extends AppCompatActivity {
    
    // keys 
    public static final String STORY_LAT = "lat";
    public static final String STORY_LNG = "lng";
    public static final String STORY_IMAGE = "image";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final String STORY_INSTANCE_STATE_KEY = "saved_story";
    
    // database 
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private MyDatabase database;
    private StorageReference storageReference;
    
    // data 
    private SharedPreferences mprefs;
    private SharedPreferences.Editor meditor;
    private Story mStory;
    private Uri tempImgUri;
    private Uri firebaseUri;
    private String addressText = "";
    private LocationManager locationManager;
    private boolean isNewImage;
    
    // views 
    private ImageView storyImageView;
    private EditText storyTitleEditor;
    private EditText storyContentEditor;
    private Spinner storyTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_story);
        mprefs = getSharedPreferences(PostFragment.PREF_KEY, Context.MODE_PRIVATE);
        meditor = mprefs.edit();

        // create the button listener and get the Edit view
        CreateStoryListener();
        storyTitleEditor = (EditText)findViewById(R.id.story_title_edit);
        storyContentEditor = (EditText)findViewById(R.id.story_content_edit);
        storyTypeSpinner = (Spinner) findViewById(R.id.story_type);

        // set up the database
        database = new MyDatabase();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("image/storyImage_"
                + user.getUid() + Calendar.getInstance().getTimeInMillis());

        // create a data (story) instance 
        if (savedInstanceState == null) {
            mStory = new Story();
            isNewImage = false;
        }

        // get the location pass from other activity if exists
        if (getIntent().getExtras() != null) {
            Double lat = getIntent().getExtras().getDouble(STORY_LAT);
            Double lng = getIntent().getExtras().getDouble(STORY_LNG);
            mStory.setStoryLat(lat);
            mStory.setStoryLng(lng);
            setLocationText(lat, lng);
        }
        // if not exists, get the current loctioan after check the permission 
        else {
            checkPermissions();
        }
        
        // reload the story image after configuration change 
        storyImageView = (ImageView) findViewById(R.id.story_image);
        loadSnap();
    }

    // save the instance befreo configuration change 
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mStory.setStoryType(storyTypeSpinner.getSelectedItemPosition());
        mStory.setStoryTitle(storyTitleEditor.getText().toString());
        mStory.setStoryContent(storyContentEditor.getText().toString());
        outState.putParcelable(URI_INSTANCE_STATE_KEY, this.tempImgUri);
        outState.putParcelable(STORY_INSTANCE_STATE_KEY, this.mStory);
        outState.putBoolean("isNewImage", isNewImage);
    }

    // reload the instance after configuration change 
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mStory = savedInstanceState.getParcelable(STORY_INSTANCE_STATE_KEY);
        tempImgUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
        isNewImage = savedInstanceState.getBoolean("isNewImage");
        loadSnap();
    }


    // set the pre-saved location as the location where you want to write a story
    private void setLocationText(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        TextView locationTextView = (TextView) findViewById(R.id.story_location);
        try {
            // get the human readable address from the lat, lng
            // set it to textview if success
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    if (i != address.getMaxAddressLineIndex() - 1) {
                        addressText += address.getAddressLine(i) + ", ";
                    } else {
                        addressText += address.getAddressLine(i);
                    }
                locationTextView.setText(addressText);
            }
            // if fail to get the location, show default text 
            else {
                locationTextView.setText("Middle of Nowhere");
            }
        } catch (Exception e) {
            locationTextView.setText("Middle of Nowhere");
            Log.d("******", "set location text fail");
        }
    }

    // set the current location as the location where you want to write a story
    private void setCurrentLocationText() {
        // get the current location 
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);
        try {
            Location location = locationManager.getLastKnownLocation(provider);
            mStory.setStoryLat(location.getLatitude());
            mStory.setStoryLng(location.getLongitude());
            // set it to textview 
            setLocationText(mStory.getStoryLat(), mStory.getStoryLng());
        }catch(SecurityException e) {
            checkPermissions();
        }
        catch (Exception e) {
            Log.d("******","fail to get current location: " + e);
        }
    }

    // create the button listener for: change image, save story, cancel 
    private void CreateStoryListener() {
        // pick your image
        findViewById(R.id.story_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        
        // save button, save the story to firebase database and storage 
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStory();
            }
        });
        
        // cancel button, pop out dialog to ask user if they want to exit
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // change the color of location icon (xml sometimes not working) 
        ImageView iconLocation = (ImageView) findViewById(R.id.location_icon);
        iconLocation.setColorFilter(getResources().getColor(R.color.color3));
    }


    // dialog for picking the image
    private void pickImage() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Upload Image")
                .setItems(new CharSequence[]{"Open Camera", "Select from Gallery"},
                        new DialogInterface.OnClickListener() {
                            public void onClick (DialogInterface dialog, int picker) {
                                switch (picker) {
                                    // if user select "open camera"
                                    case 0:
                                        checkCameraPermissions();
                                        break;
                                    // if user selcet "select from gallery"
                                    case 1:
                                        loadFromGallery();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).create().show();
    }


    // save the story to firebase when click on the Save btn
    private void saveStory() {
        // create the story that will be saved 
        mStory.setStoryAuthorId(user.getUid());
        mStory.setStoryType(storyTypeSpinner.getSelectedItemPosition());
        mStory.setStoryMode(0); 
        mStory.setStoryDateTime(Calendar.getInstance().getTimeInMillis());
        mStory.setStoryTitle(storyTitleEditor.getText().toString());
        mStory.setStoryContent(storyContentEditor.getText().toString());
        mStory.setStoryLikes(0);
        mStory.setStoryAddress(addressText);
        // upload to firebase
        upload2Firebase();
        Toast.makeText(this,"your story will be heard", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }



    // ************** For Image ************* //
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK) return;
        // open camera for user
        if(requestCode == Global.CAMERA_REQUEST_CODE){
            Crop.of(tempImgUri, tempImgUri)
                    .withAspect(storyImageView.getMeasuredWidth(),
                            storyImageView.getMeasuredHeight()).start(this);
        }
        // open gallery for user 
        else if(requestCode == Global.GALLERY_REQUEST_CODE){
            Crop.of(data.getData(), tempImgUri)
                    .withAspect(storyImageView.getMeasuredWidth(),
                            storyImageView.getMeasuredHeight()).
                    start(this);
        }
        // crop the image for user and set it to the view 
        else if(requestCode == Crop.REQUEST_CROP){
            Log.d("******", Crop.getOutput(data) + "");
            Uri selectedImgUri = Crop.getOutput(data);
            storyImageView.setImageURI(null);
            storyImageView.setPadding(0,0,0,0);
            storyImageView.setImageURI(selectedImgUri);
            isNewImage = true;
        }
    }

    // load the image from camera and send it to crop 
    private void loadFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        startActivityForResult(intent, Global.CAMERA_REQUEST_CODE);
    }

    // load the image from gallery and send it to crop 
    private void loadFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        startActivityForResult(intent, Global.GALLERY_REQUEST_CODE);
    }

    // load the image to image view 
    private void loadSnap() {
        // if image already exists, put in on image view 
        if (tempImgUri != null) {
            storyImageView.setImageURI(null);
            storyImageView.setPadding(0,0,0,0);
            storyImageView.setImageURI(tempImgUri);
        }
        // else try to see if we have saved it in the phone 
        else {
            try {
                FileInputStream file = openFileInput(STORY_IMAGE);
                Bitmap bmap = BitmapFactory.decodeStream(file);
                storyImageView.setImageBitmap(bmap);
                file.close();
            } catch (IOException e) {
                // Default story photo if no photo saved before.
                storyImageView.setPadding(50,50,50,50);
                storyImageView.setImageResource(R.drawable.icon_image);
            }
        }
    }

    // save the image to the phone storage  
    private void saveSnap() {
        storyImageView.buildDrawingCache();
        Bitmap bmap = storyImageView.getDrawingCache();
        try {
            FileOutputStream file = openFileOutput(STORY_IMAGE, MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, file);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // upload the stroy to the firebase database, and the image to firebase storage 
    private void upload2Firebase() {
        // upload image only if users add their own image
        if (isNewImage) {
            try {
                storyImageView.buildDrawingCache();
                Bitmap bmap = storyImageView.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = storageReference.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("******", "Firebase upload fail: " + exception);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    // if successfully upload the image
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // save the story and update the story image url
                        firebaseUri = taskSnapshot.getDownloadUrl();
                        mStory.setStoryImgURL(firebaseUri + "");
                        database.createStory(mStory);
                    }
                });
            } catch (Exception e) {
                Log.d("******", "Firebase upload fail: FileInputStream ------ " + e);
            }
        }
        // if user do not insert new image, upload the story without image 
        else {
            database.createStory(mStory);
        }
    }

    
    // ************** Permissions ************* //
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Global.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setCurrentLocationText();
                } else {
                    finish();
                }
                return;
            }
            case Global.MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadFromCamera();
                } else {
                    checkCameraPermissions();
                }
                return;
            }
        }
    }

    // check the location permission at the beginning of create new story
    // if pass, get the current location and set it to text view 
    public void checkPermissions(){
        if(Build.VERSION.SDK_INT < 23)
            return;
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Global.MY_PERMISSIONS_REQUEST_LOCATION);
        }
        else {
            setCurrentLocationText();
        }
    }

    // check the camera permission when user click "open camera"
    public void checkCameraPermissions(){
        if(Build.VERSION.SDK_INT < 23)
            return;
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA},
                    Global.MY_PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            loadFromCamera();
        }
    }



    @Override
    public void onBackPressed() {
        // pop out dialog to ask users if they really want to exit the editor 
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
