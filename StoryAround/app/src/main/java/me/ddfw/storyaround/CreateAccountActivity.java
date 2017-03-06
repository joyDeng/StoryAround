package me.ddfw.storyaround;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import me.ddfw.storyaround.Authen.ChooserActivity;
import me.ddfw.storyaround.model.User;

public class CreateAccountActivity extends AppCompatActivity
implements View.OnClickListener{

    private EditText mName;
    private RadioGroup mGender;
    private String mEmail;
    private String mPassword;
    private String mLoginMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mLoginMethod = getIntent().getStringExtra(Global.LOGIN_METHOD);
        if(mLoginMethod.equals(Global.EMAIL_SIGNIN)) {
            mPassword = getIntent().getStringExtra(Global.USER_PASSWORD);
        }
        mEmail = getIntent().getStringExtra(Global.USER_EMAIL);
        mName = (EditText) findViewById(R.id.create_editName);
        mGender = (RadioGroup) findViewById(R.id.create_radioSex);
        findViewById(R.id.btnCreate).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        // START:Create a new account
        User newAcc = new User(mName.getText().toString(),null,null,mEmail,null, mGender.getId());

        Intent intent = new Intent(this, ChooserActivity.class);
        intent.putExtra(Global.LOGIN_METHOD,mLoginMethod);
        if(mLoginMethod.equals(Global.EMAIL_SIGNIN))
            intent.putExtra(Global.USER_PASSWORD, mPassword);
        intent.putExtra(Global.USER_EMAIL, mEmail);
        intent.putExtra(Global.USER_NAME,mName.getText().toString());
        intent.putExtra(Global.USER_GENDER,mGender.getId());
        intent.putExtra(Global.NEWACCOUNT, newAcc);
        setResult(RESULT_OK,intent);
        finish();
    }
}