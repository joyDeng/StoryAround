package me.ddfw.storyaround.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.ddfw.storyaround.Authen.ChooserActivity;
import me.ddfw.storyaround.MainActivity;
import me.ddfw.storyaround.R;
import me.ddfw.storyaround.model.User;


// TODO
// user information loaded


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private FirebaseUser mFirebaseUser;
    private User mUser;
    private EditText editUsername;
    private EditText editEmail;
    private EditText editPhone;
    private RadioGroup editGender;

    private EditText editBio;
    private boolean isEditMode;
    private DatabaseReference databaseRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;

        //Get current user;
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Check whether user is logged in
        if (mFirebaseUser == null) {
            //User is not logged in then turn to ChooserActivity
            rootView = inflater.inflate(R.layout.fragment_profile_login, container, false);
            setLoginBtn(rootView);
        } else {
            //If User is logged in
            rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            editUsername = (EditText) rootView.findViewById(R.id.user_username);
            editEmail = (EditText) rootView.findViewById(R.id.user_email);
            editPhone = (EditText) rootView.findViewById(R.id.user_phone);
            editGender = (RadioGroup) rootView.findViewById(R.id.user_gender);
            editBio = (EditText) rootView.findViewById(R.id.user_bio);
            isEditMode = false;

            databaseRef = FirebaseDatabase.getInstance().getReference();
            databaseRef.child(User.USER_TABLE).child(mFirebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            mUser = snapshot.getValue(User.class);
                            if (mUser != null) {
                                setProfileContent();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
            setProfileBtn(rootView);
        }
        setRetainInstance(true);
        return rootView;
    }


    private void setLoginBtn(View rootView) {
        Button btnLogin = (Button) rootView.findViewById(R.id.btn_switch_to_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitch();
                Log.d(TAG, "onSwitch");
                Toast.makeText(getActivity().getApplicationContext(), "onSwitch", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setProfileBtn(final View rootView) {
        final Button btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        final Button btnSave = (Button) rootView.findViewById(R.id.btnSave);
        final Button btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        final Button btnEdit = (Button) rootView.findViewById(R.id.btnEdit);
        final ImageView profileImage = (ImageView) rootView.findViewById(R.id.user_image);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignOut();
                Toast.makeText(getActivity().getApplicationContext(),
                        "Log out", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if already in edit mode -> save
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                btnEdit.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                editUsername.setEnabled(false);
                editEmail.setEnabled(false);
                editPhone.setEnabled(false);
                for (int i = 0; i < editGender.getChildCount(); i++)
                    editGender.getChildAt(i).setEnabled(false);
                editBio.setEnabled(false);
                onClickSave();
                Toast.makeText(getActivity().getApplicationContext(),
                        "You have saved your profile", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                btnEdit.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                editUsername.setEnabled(false);
                editEmail.setEnabled(false);
                editPhone.setEnabled(false);
                for (int i = 0; i < editGender.getChildCount(); i++)
                    editGender.getChildAt(i).setEnabled(false);
                editBio.setEnabled(false);
                setProfileContent();
                Toast.makeText(getActivity().getApplicationContext(),
                        "Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editUsername = (EditText) rootView.findViewById(R.id.user_username);
                // if not in edit mode, -> edit
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

    private void setProfileContent() {
        editEmail.setText(mUser.getUserEmail());
        if (mUser.getUserName() == null && mUser.getUserName().length() < 1)
            editUsername.getText().clear();
        else
            editUsername.setText(mUser.getUserName());

        if (mUser.getUserPhoNum() == null)
            editUsername.getText().clear();
        else
            editPhone.setText(mUser.getUserPhoNum());

        if (mUser.getUserPhoNum() == null)
            editPhone.getText().clear();

        if (editGender.getChildAt(mUser.getUserGender()) == null)
            ((RadioButton) editGender.getChildAt(2)).setChecked(true);
        else
            ((RadioButton) editGender.getChildAt(mUser.getUserGender())).setChecked(true);

        if (mUser.getUserBio() == null)
            editBio.getText().clear();
        else
            editBio.setText(mUser.getUserBio());
    }

    private void onSignOut() {
        ((MainActivity) getActivity()).setmLoginMethod("");
        FirebaseAuth.getInstance().signOut();
    }

    private void onSwitch() {
        Intent intent = new Intent(getContext(), ChooserActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void onClickSave() {
        mUser.setUserName(editUsername.getText().toString());
        mUser.setUserEmail(editEmail.getText().toString());
        mUser.setUserPhoNum(editPhone.getText().toString());
        int genderIndex = editGender.indexOfChild(getActivity()
                .findViewById(editGender.getCheckedRadioButtonId()));
        mUser.setUserGender(genderIndex);
        mUser.setUserBio(editBio.getText().toString());
        // TODO
        //mUser.setUserImageURL();

        Log.d("******","email test: " + mUser.getUserEmail());
    }


}
