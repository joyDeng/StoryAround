package me.ddfw.storyaround;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import me.ddfw.storyaround.Authen.ChooserActivity;
import me.ddfw.storyaround.model.User;

public class CreateAccountActivity extends AppCompatActivity
implements View.OnClickListener{

    private EditText mName;
    private RadioGroup mGender;
    private EditText mEmail;
    private EditText mPassword;


    private String email;
    private String password;



    private String mLoginMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mLoginMethod = getIntent().getStringExtra(Global.LOGIN_METHOD);
        if(mLoginMethod.equals(Global.EMAIL_SIGNIN)) {
            password = getIntent().getStringExtra(Global.USER_PASSWORD);
        }
        email = getIntent().getStringExtra(Global.USER_EMAIL);
        mEmail = (EditText) findViewById(R.id.create_editEmail);
        mPassword = (EditText) findViewById(R.id.create_editPassword);
        mName = (EditText) findViewById(R.id.create_editName);
        mGender = (RadioGroup) findViewById(R.id.create_radioSex);
        findViewById(R.id.btnCreate).setOnClickListener(this);

        if (email != null)
            mEmail.setText(email);
        if (password != null)
            mPassword.setText(password);
    }

    @Override
    public void onClick(View v) {
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();

        if (email.equals("") ||
                password.equals("") ||
                email == null || password == null) {
            //if input is empty
            Toast.makeText(CreateAccountActivity.this,
                    "Invalid Email or Password",
                    Toast.LENGTH_SHORT).show();
        }
        if (password.length() < 6) {
            //if password is less than 6 character
            Toast.makeText(CreateAccountActivity.this,
                    "Invalid  Password, Password should have at least 6 characters",
                    Toast.LENGTH_SHORT).show();
        }

        else {
            // START:Create a new account
            User newAcc = new User(
                    mName.getText().toString(),
                    null,
                    null,
                    email,
                    null, mGender.getId());
            Intent intent = new Intent(this, ChooserActivity.class);
            intent.putExtra(Global.LOGIN_METHOD, mLoginMethod);
            if (mLoginMethod.equals(Global.EMAIL_SIGNIN))
                intent.putExtra(Global.USER_PASSWORD, password);
            intent.putExtra(Global.USER_EMAIL, email);
            intent.putExtra(Global.USER_NAME, mName.getText().toString());
            intent.putExtra(Global.USER_GENDER, mGender.getId());
            intent.putExtra(Global.NEWACCOUNT, newAcc);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
