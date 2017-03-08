package me.ddfw.storyaround;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import me.ddfw.storyaround.model.Story;


// list of TODO
// story type
// story privacy
// rotation
// image compress


public class NewStoryActivity extends AppCompatActivity {
    private Story mStory;
    private MyDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;
    private LocationManager locationManager;

    public static final String STORY_LAT = "lat";
    public static final String STORY_LNG = "lng";
    public static final String STORY_IMAGE = "image";

    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private Uri tempImgUri;
    private Uri firebaseUri;
    private ImageView storyImageView;
    private String addressText = "  ";

    private boolean isNewImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_story);

        // create the button listener
        CreateStoryListener();
        tempImgUri = null;

        // set up the database
        database = new MyDatabase();
        mStory = new Story();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("image/storyImage_"
                + user.getUid() + Calendar.getInstance().getTimeInMillis());

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

        storyImageView = (ImageView) findViewById(R.id.story_image);
        loadSnap();
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(URI_INSTANCE_STATE_KEY, this.tempImgUri);
    }


    // set the pre-saved location as the location where you want to write a mStory
    private void setLocationText(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        TextView locationTextView = (TextView) findViewById(R.id.story_location);
        try {
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
        try {
            Location location = locationManager.getLastKnownLocation(provider);
            mStory.setStoryLat(location.getLatitude());
            mStory.setStoryLng(location.getLongitude());
            setLocationText(mStory.getStoryLat(), mStory.getStoryLng());
        }catch(SecurityException e) {
            checkPermissions();
        }
    }

    // create the button listener, onClick
    private void CreateStoryListener() {
        // pick your image
        findViewById(R.id.story_image).setOnClickListener(new View.OnClickListener() {
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


    // dialog for picking the image
    private void pickImage() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Upload Image")
                .setItems(new CharSequence[]{"Open Camera", "Select from Gallery"},
                        new DialogInterface.OnClickListener() {
                            public void onClick (DialogInterface dialog, int picker) {
                                switch (picker) {
                                    case 0:
                                        checkCameraPermissions();
                                        break;
                                    case 1:
                                        loadFromGallery();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).create().show();
    }


    // save the story when on click
    private void saveStory() {
        EditText storyTitleEditor = (EditText)findViewById(R.id.story_title_edit);
        EditText storyContentEditor = (EditText)findViewById(R.id.story_content_edit);
        Spinner storyTypeSpinner = (Spinner) findViewById(R.id.story_type);
        Log.d("******","load to firebase: " + mStory.getStoryImgURL());
        mStory.setStoryAuthorId(user.getUid());
        mStory.setStoryType(storyTypeSpinner.getSelectedItemPosition());
        mStory.setStoryMode(0);//TODO
        mStory.setStoryDateTime(Calendar.getInstance().getTimeInMillis());
        // mStory.setStoryImgURL("" + firebaseUri);
        mStory.setStoryTitle(storyTitleEditor.getText().toString());
        mStory.setStoryContent(storyContentEditor.getText().toString());
        mStory.setStoryLikes(0);

        upload2Firebase();
        Toast.makeText(this,"your story will be heard", Toast.LENGTH_SHORT).show();
        finish();
    }



    // ************** Image ************* //
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == Global.CAMERA_REQUEST_CODE){
            Crop.of(tempImgUri, tempImgUri)
                    .withAspect(storyImageView.getMeasuredWidth(),
                            storyImageView.getMeasuredHeight()).start(this);
        }
        else if(requestCode == Global.GALLERY_REQUEST_CODE){
            Crop.of(data.getData(), tempImgUri)
                    .withAspect(storyImageView.getMeasuredWidth(),
                            storyImageView.getMeasuredHeight()).
                    start(this);
        }
        else if(requestCode == Crop.REQUEST_CROP){
            Log.d("******", Crop.getOutput(data) + "");
            Uri selectedImgUri = Crop.getOutput(data);
            storyImageView.setImageURI(null);
            storyImageView.setPadding(0,0,0,0);
            storyImageView.setImageURI(selectedImgUri);
            isNewImage = true;
        }
    }

    private void loadFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        startActivityForResult(intent, Global.CAMERA_REQUEST_CODE);
    }

    private void loadFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        startActivityForResult(intent, Global.GALLERY_REQUEST_CODE);
    }

    private void loadSnap() {
        if (tempImgUri != null) {
            isNewImage = false;
            storyImageView.setImageURI(tempImgUri);
        }
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
                        // TODO
                        // if fail, pop up dialog
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        firebaseUri = taskSnapshot.getDownloadUrl();
                        mStory.setStoryImgURL(firebaseUri + "");
                        database.createStory(mStory);
                    }
                });
            } catch (Exception e) {
                Log.d("******", "Firebase upload fail: FileInputStream ------ " + e);
            }
        }
        // else only upload story without image
        else {
            database.createStory(mStory);
        }
    }

    private void getImageUriFromFirebase() {
        //Picasso.with(this).load(firebaseUri).into(storyImageView);
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        StorageReference storageReference = storageRef.child("image/storyImage");
//        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                firebaseUri = uri;
//                mStory.setStoryImgURL(firebaseUri+"");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.d("******", "Firebase get URI fail: " + exception);
//            }
//        });
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
