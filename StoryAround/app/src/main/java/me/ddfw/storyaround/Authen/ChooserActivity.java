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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import me.ddfw.storyaround.Global;
import me.ddfw.storyaround.MainActivity;
import me.ddfw.storyaround.R;

public class ChooserActivity extends AppCompatActivity
    implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private static final String TAG = "ChooserActivity";
    private Context mcontext = this;
    private static final int RC_SIGN_IN = 9001;

    //declare_auth
    private FirebaseAuth mAuth;

    //declare_auth_listener
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;
    EditText mEmail;
    EditText mPassword;
    private String LoginMethod = Global.GUEST_VISIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        // get edit text feild
        mEmail = (EditText) findViewById(R.id.editUsername);
        mPassword = (EditText) findViewById(R.id.editPassword);

        //set button OnClickListener
        findViewById(R.id.btnNewAcc).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btn_google_signin).setOnClickListener(this);
        findViewById(R.id.btnGuest).setOnClickListener(this);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        // END: Configure

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this/*fragmentActivity*/,this/*onconnectionfaillistener*/)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        // initialize_auth
        mAuth = FirebaseAuth.getInstance();

        // START:auth_state_listener
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                   // User signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:"+user.getUid());
                    Intent intent = new Intent(mcontext, MainActivity.class);
                    intent.putExtra(Global.LOGIN_METHOD,LoginMethod);
                    SignOut(LoginMethod);
                    startActivity(intent);
                    finish();
                }else{
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                }
            }
        };


        // END:auth_state_listener
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

    public void EmailSignIn(String email, String password){
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(ChooserActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //START google signin
    public void GoogleSignIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    //END google signin

    //START signout
    public void SignOut(String mlogout){
        if(mlogout != null)
        switch(mlogout){
            case Global.GOOGLE_SIGNIN:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Toast.makeText(ChooserActivity.this
                                        , "Google login."
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                break;
            default:break;
        }
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

   @Override
    public void onClick(View v) {
        switch (v.getId()){
            //START: create_new_account
            case R.id.btnNewAcc:
                LoginMethod = Global.EMAIL_SIGNIN;
                createAccount(mEmail.getText().toString(),mPassword.getText().toString());
                break;
            //END: create_new_account

            //START: sign_in_with_email_and_password
            case R.id.btnLogin:
                LoginMethod = Global.EMAIL_SIGNIN;
                EmailSignIn(mEmail.getText().toString(),mPassword.getText().toString());
                break;
            //END: sign_in_with_email_and_password

            //START:continue_as_guest
            case R.id.btnGuest:
                LoginMethod = Global.GUEST_VISIT;
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra(Global.LOGIN_METHOD, LoginMethod);
                startActivity(intent);
                finish();
                break;
            //END:continue_as_guest

            //START:sign_in_with_google_account
            case R.id.btn_google_signin:
                LoginMethod = Global.GOOGLE_SIGNIN;
                GoogleSignIn();
                break;
            //END: sign_in_with_google_account
            default:break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else{
                //Google Sign In failed
                Toast.makeText(this, "Google Login Failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}