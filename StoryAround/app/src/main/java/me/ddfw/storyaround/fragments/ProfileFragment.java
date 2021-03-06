package me.ddfw.storyaround.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import me.ddfw.storyaround.Authen.ChooserActivity;
import me.ddfw.storyaround.Global;
import me.ddfw.storyaround.MainActivity;
import me.ddfw.storyaround.MyDatabase;
import me.ddfw.storyaround.R;
import me.ddfw.storyaround.model.User;

import static android.content.Context.MODE_PRIVATE;



public class ProfileFragment extends Fragment {

    // keys 
    private static final String TAG = "ProfileFragment";
    public static final String USER_IMAGE = "image";

    // database and storage 
    private DatabaseReference databaseRef;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private MyDatabase database;
    
    // data 
    private FirebaseUser mFirebaseUser;
    private User mUser;
    private Uri tempImgUri;
    private Uri firebaseUri;
    private boolean isNewImage;
    private boolean isEditMode;
    
    // views 
    private EditText editUsername;
    private EditText editEmail;
    private EditText editPhone;
    private RadioGroup editGender;
    private EditText editBio;
    private ImageView profileImage;

    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        setRetainInstance(true);

        //Get current user;
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Check whether user is logged in
        if (mFirebaseUser == null) {
            //User is not logged in then turn to ChooserActivity
            rootView = inflater.inflate(R.layout.fragment_profile_login, container, false);
            setLoginBtn(rootView);
        } else {
            //If User is logged in, setup the profile views 
            rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            editUsername = (EditText) rootView.findViewById(R.id.user_username);
            editEmail = (EditText) rootView.findViewById(R.id.user_email);
            editPhone = (EditText) rootView.findViewById(R.id.user_phone);
            editGender = (RadioGroup) rootView.findViewById(R.id.user_gender);
            editBio = (EditText) rootView.findViewById(R.id.user_bio);
            setProfileBtn(rootView);
            // get user information from firebase 
            databaseRef = FirebaseDatabase.getInstance().getReference();
            databaseRef.child(User.USER_TABLE).child(mFirebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            mUser = snapshot.getValue(User.class);
                            if (mUser != null) {
                                // set the information into the views 
                                setProfileContent();
                                isNewImage = false;
                                storage = FirebaseStorage.getInstance();
                                database = new MyDatabase();
                                storageReference = storage.getReference().child("image/profileImage_"
                                        + mUser.getUserId() + Calendar.getInstance().getTimeInMillis());
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
        }

        // make sure that the soft keyboard don't pop out
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        return rootView;
    }



    // ****** if user not log in ****** //
    // make a login button for user to login 
    private void setLoginBtn(View rootView) {
        Button btnLogin = (Button) rootView.findViewById(R.id.btn_switch_to_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitch();
            }
        });
    }
    // after click on the login button, switch to login activity 
    private void onSwitch() {
        Intent intent = new Intent(getContext(), ChooserActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


    // ****** if user already log in ****** //
    
    // set up the profile view 
    private void setProfileBtn(final View rootView) {
        // get the views and buttons 
        final Button btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        final Button btnSave = (Button) rootView.findViewById(R.id.btnSave);
        final Button btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        final Button btnEdit = (Button) rootView.findViewById(R.id.btnEdit);
        profileImage = (ImageView) rootView.findViewById(R.id.user_image);
        editUsername = (EditText) rootView.findViewById(R.id.user_username);

        // user can change profile image if click on profile image view 
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    pickImage();
                }
            }
        });

        // user can logout 
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignOut();
                Toast.makeText(getActivity().getApplicationContext(),
                        "Log out", Toast.LENGTH_SHORT).show();
            }
        });
        
        // user can modify the profile after click the "edit" button 
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show the save and cancel button if in the edit mode, hide edit button
                // enable all the EditText view and radio button
                isEditMode = true;
                btnEdit.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                editUsername.setEnabled(true);
                editEmail.setEnabled(true);
                editPhone.setEnabled(true);
                for (int i = 0; i < editGender.getChildCount(); i++)
                    editGender.getChildAt(i).setEnabled(true);
                editBio.setEnabled(true);
                Toast.makeText(getActivity().getApplicationContext(), "You can edit your profile", Toast.LENGTH_SHORT).show();
            }
        });
        
        
        // user can modify the profile and save the change 
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                // hide the save and cancel button and exit edit mode, show edit button
                // disable all the EditText view and radio button
                isEditMode = false;
                btnEdit.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                editUsername.setEnabled(false);
                editEmail.setEnabled(false);
                editPhone.setEnabled(false);
                for (int i = 0; i < editGender.getChildCount(); i++)
                    editGender.getChildAt(i).setEnabled(false);
                editBio.setEnabled(false);
                // save the change that user make 
                onClickSave();
                Toast.makeText(getActivity().getApplicationContext(),
                        "You have saved your profile", Toast.LENGTH_SHORT).show();
            }
        });

        // cancel the modifcation that user make to the profile 
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                // hide the save and cancel button and exit edit mode, show edit button
                // disable all the EditText view and radio button
                isEditMode = false;
                btnEdit.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                editUsername.setEnabled(false);
                editEmail.setEnabled(false);
                editPhone.setEnabled(false);
                for (int i = 0; i < editGender.getChildCount(); i++)
                    editGender.getChildAt(i).setEnabled(false);
                editBio.setEnabled(false);
                // reset the profile content to previous setting 
                setProfileContent();
                Toast.makeText(getActivity().getApplicationContext(),
                        "Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        // initially, disable all the EditText view and radio button
        // hide the save and cancel button, show edit button 
        editUsername.setEnabled(false);
        editEmail.setEnabled(false);
        editPhone.setEnabled(false);
        for (int i = 0; i < editGender.getChildCount(); i++)
            editGender.getChildAt(i).setEnabled(false);
        editBio.setEnabled(false);
        btnEdit.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }


    // sign out of account if user click "signout" button
    private void onSignOut() {
        ((MainActivity) getActivity()).setmLoginMethod("");
        FirebaseAuth.getInstance().signOut();
    }


    // get the profile from firebase and set it to the views 
    private void setProfileContent() {
        editEmail.setText(mUser.getUserEmail());
        if (mUser.getUserName() == null && mUser.getUserName().length() < 1)
            editUsername.getText().clear();
        else
            editUsername.setText(mUser.getUserName());

        if (mUser.getUserPhoNum() == null)
            editPhone.getText().clear();
        else
            editPhone.setText(mUser.getUserPhoNum());

        if (editGender.getChildAt(mUser.getUserGender()) == null)
            ((RadioButton) editGender.getChildAt(2)).setChecked(true);
        else
            ((RadioButton) editGender.getChildAt(mUser.getUserGender())).setChecked(true);

        if (mUser.getUserBio() == null)
            editBio.getText().clear();
        else
            editBio.setText(mUser.getUserBio());

        if (mUser.getUserImageURL() == null)
            tempImgUri = null;
        else
            setProfileImageFromFirebase();
        loadSnap();
    }


    // get the user input and update the user profile in firebase 
    private void onClickSave() {
        mUser.setUserName(editUsername.getText().toString());
        mUser.setUserEmail(editEmail.getText().toString());
        mUser.setUserPhoNum(editPhone.getText().toString());
        int genderIndex = editGender.indexOfChild(getActivity()
                .findViewById(editGender.getCheckedRadioButtonId()));
        mUser.setUserGender(genderIndex);
        mUser.setUserBio(editBio.getText().toString());
        upload2Firebase();
    }



    // ************** Image ************* //
    
    // dialog for picking the image
    private void pickImage() {
        new android.app.AlertDialog.Builder(getActivity())
                .setTitle("Upload Image")
                .setItems(new CharSequence[]{"Open Camera", "Select from Gallery"},
                        new DialogInterface.OnClickListener() {
                            public void onClick (DialogInterface dialog, int picker) {
                                switch (picker) {
                                    case 0:
                                        // if user select "open camera"
                                        checkCameraPermissions();
                                        break;
                                    case 1:
                                        // if user selcet "select from gallery"
                                        loadFromGallery();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).create().show();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        // open camera for user
        if(requestCode == Global.CAMERA_REQUEST_CODE){
            Crop.of(tempImgUri, tempImgUri).asSquare().start(getActivity());
        }
        // open gallery for user 
        else if(requestCode == Global.GALLERY_REQUEST_CODE){
            Crop.of(data.getData(), tempImgUri).asSquare().start(getActivity());
        }
        // crop the image for user and set it to the view 
        else if(requestCode == Crop.REQUEST_CROP){
            Uri selectedImgUri = Crop.getOutput(data);
            profileImage.setImageURI(null);
            profileImage.setImageURI(selectedImgUri);
            isNewImage = true;
        }
    }

    // load the image from camera and send it to crop 
    private void loadFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImgUri = getActivity().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        startActivityForResult(intent, Global.CAMERA_REQUEST_CODE);
    }

    // load the image from gallery and send it to crop 
    private void loadFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImgUri = getActivity().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        startActivityForResult(intent, Global.GALLERY_REQUEST_CODE);
    }

    // load the image to image view 
    private void loadSnap() {
        // if image already exists, put in on image view 
        if (tempImgUri != null) {
            isNewImage = false;
            profileImage.setImageURI(tempImgUri);
        }
        // else try to see if we have saved it in the phone 
        else {
            try {
                FileInputStream file = getActivity().openFileInput(USER_IMAGE);
                Bitmap bmap = BitmapFactory.decodeStream(file);
                profileImage.setImageBitmap(bmap);
                file.close();
            } catch (IOException e) {
                // Default story photo if no photo saved before.
                profileImage.setImageResource(R.drawable.profile);
            }
        }
    }

    // save the image to the phone storage  
    private void saveSnap() {
        profileImage.buildDrawingCache();
        Bitmap bmap = profileImage.getDrawingCache();
        try {
            FileOutputStream file = getActivity().openFileOutput(USER_IMAGE, MODE_PRIVATE);
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
                profileImage.buildDrawingCache();
                Bitmap bmap = profileImage.getDrawingCache();
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
                        // save the story and update the profile image url
                        firebaseUri = taskSnapshot.getDownloadUrl();
                        mUser.setUserImageURL(firebaseUri + "");
                        database.updateProfile(mUser);
                    }
                });
            } catch (Exception e) {
                Log.d("******", "Firebase upload fail: FileInputStream ------ " + e);
            }
        }
        // else only update profile without changing image
        else {
            database.updateProfile(mUser);
        }
    }

    // set the profile image using the URL provided by firebase 
    private void setProfileImageFromFirebase() {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(mUser.getUserImageURL());
        // Load the image using Glide
        Glide.with(getActivity())
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(profileImage);
    }


    // check the camera permission when user click "open camera"
    public void checkCameraPermissions(){
        if(Build.VERSION.SDK_INT < 23)
            return;
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA},
                    Global.MY_PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            loadFromCamera();
        }
    }

}
