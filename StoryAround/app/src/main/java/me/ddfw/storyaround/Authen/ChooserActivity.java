package me.ddfw.storyaround.Authen;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import me.ddfw.storyaround.MainActivity;
import me.ddfw.storyaround.R;

public class ChooserActivity extends AppCompatActivity
    implements View.OnClickListener{

    private static final String TAG = "ChooserActivity";

    private Context mcontext = this;

    //declare_auth
    private FirebaseAuth mAuth;

    //declare_auth_listener
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText mEmail;
    EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        //get edit text feild
        mEmail = (EditText) findViewById(R.id.editUsername);
        mPassword = (EditText) findViewById(R.id.editPassword);

        //set button OnClickListener
        findViewById(R.id.btnNewAcc).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);

        //initialize_auth
        mAuth = FirebaseAuth.getInstance();

        //START:auth_state_listener
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                   //User signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:"+user.getUid());
                    Intent intent = new Intent(mcontext, MainActivity.class);
                    startActivity(intent);
                }else{
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                }
            }
        };
        //END:auth_state_listener
    }

    @Override
    public void onStart(){
        super.onStart();
        //addAuthListener to mAuth
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        //removeAuthListener from mAuth
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void createAccount(String email, String password){
        //CREATE NEW ACCOUNT

        //START: check_validation_of_input
        if(email.equals("") || password.equals("") || email == null || password == null) {
            //if input is empty
            Toast.makeText(ChooserActivity.this,
                    "Invalid Email or Password",
                    Toast.LENGTH_SHORT).show();
        }if(password.length() < 6){
            //if password is less than 6 character
            Toast.makeText(ChooserActivity.this,
                    "Invalid  Password, Password should have at least 6 characters",
                    Toast.LENGTH_SHORT).show();
        }else {//END: check_validation_of_input
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //ON CREATE USER COMPLETED
                            Log.d(TAG, "createUserWithEail:onComplete:" +
                                    task.isSuccessful());

                            //START: if create account failed toast reason
                            if (!task.isSuccessful()) {
                                Log.d(TAG,"FAILED REASON"+task.getException().getMessage());
                                Toast.makeText(ChooserActivity.this,
                                        "failed"+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }//END: if create failed
                        }//END:onComplete
                    });
        }
        //END:create_new_account
    }

    public void signIn(String email, String password){
        //sign in exit user

        //START:CHECK VALIDATION OF INPUT
        if(email.equals("") || password.equals("") || email == null || password == null) {
            //if input is empty
            Toast.makeText(ChooserActivity.this,
                    "Invalid Email or Password",
                    Toast.LENGTH_SHORT).show();
        }else {//END: CHECK
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            //if sign in failed out put reason
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "FAILED", task.getException());
                                Toast.makeText(ChooserActivity.this,
                                        "Failed:"+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }//END: if sign in failed
                        }//END: onComplete
                    });
            //END: signInWithEmailandPassword
        }
    }


   @Override
    public void onClick(View v) {
        switch (v.getId()){
            //START: create_new_account
            case R.id.btnNewAcc:
                createAccount(mEmail.getText().toString(),mPassword.getText().toString());
                break;
            //END: create_new_account

            //START: sign_in_with_email_and_password
            case R.id.btnLogin:
                signIn(mEmail.getText().toString(),mPassword.getText().toString());
                break;
            //END: sign_in_with_email_and_password

            default:break;
        }
    }
}
