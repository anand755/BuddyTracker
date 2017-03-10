package com.example.anandjha.buddytracker.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.example.anandjha.buddytracker.Model.UserModel;
import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.constant.Constant;
import com.example.anandjha.buddytracker.fragment.SignUpFragment;
import com.example.anandjha.buddytracker.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

import static com.example.anandjha.buddytracker.constant.Constant.TWITTER_KEY;
import static com.example.anandjha.buddytracker.constant.Constant.TWITTER_SECRET;
import static com.example.anandjha.buddytracker.constant.Constant.listAllUser;

public class LoginActivity extends AppCompatActivity {

    private Button mBtLogin;
    private TextView mTvSignup;
    private EditText mEtMobile, mEtPasswd;
    private DigitsAuthButton mAuthBtn;

    private AuthCallback mAuthCallback;
    private ArrayList<UserModel> userModelArrayList;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        setContentView(R.layout.activity_login);

        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        initializeViews();


        mBtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.dismissKeyboard(LoginActivity.this);
                if (!Utils.isNetworkConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mEtMobile.getText().toString().isEmpty() && !mEtPasswd.getText().toString().isEmpty()) {

                    String mob = mEtMobile.getText().toString();
                    String pwd = mEtPasswd.getText().toString();

                    validateUserTask task = new validateUserTask();
                    task.execute(new String[]{mob, pwd});
                    checkSession();

                } else {
                    Toast.makeText(LoginActivity.this, "Mobile or Password can not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isNetworkConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new SignUpFragment()).addToBackStack("Sign Up").commit();

            }
        });

        mAuthBtn.setCallback(mAuthCallback);
        mAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isNetworkConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                Digits.clearActiveSession();

                AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                        .withAuthCallBack(mAuthCallback)
                        .withPhoneNumber("+91");
                Digits.authenticate(authConfigBuilder.build());
            }
        });

    }


    private void initializeViews() {
        mBtLogin = (Button) findViewById(R.id.btn_login);
        mTvSignup = (TextView) findViewById(R.id.tv_signup);
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mEtPasswd = (EditText) findViewById(R.id.et_passwd);
        mAuthBtn = (DigitsAuthButton) findViewById(R.id.tv_forgot_passwd);
        mAuthBtn.setTextColor(Color.parseColor("#2755ED"));
        mAuthBtn.setBackgroundColor(Color.TRANSPARENT);
        mAuthBtn.setTextSize(12);
        mAuthBtn.setText("Forgot Password?");

        userModelArrayList = new ArrayList<>();

        mAuthCallback = new AuthCallback() {
            @Override

            public void success(DigitsSession session, String phoneNumber) {

                Toast.makeText(LoginActivity.this, "sucessfully password changed", Toast.LENGTH_SHORT).show();
                sendMessage(phoneNumber);
            }

            @Override
            public void failure(DigitsException error) {
                Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        };
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private void sendMessage(String phone) {
        try {

            SmsManager smsManager = SmsManager.getDefault();

            String password = getPassword(phone);

            if (password != null)
                smsManager.sendTextMessage(phone, null, "Your Password is : " + password, null, null);
            else
                smsManager.sendTextMessage(phone, null, "User doesn't Registered. ", null, null);

        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void checkSession(){
        mSharedPreferences = getSharedPreferences("LoginSession",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("session",true);
        mEditor.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    private String getPassword(String phone) {
        if (listAllUser != null) {
            for (UserModel userModel : listAllUser) {
                String indiaCode = "+91" + userModel.getMobile();
                if (indiaCode.equals(phone)) {
                    return userModel.getPassword();
                } else {
                    Toast.makeText(this, "No username exists", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(LoginActivity.this, "No data found", Toast.LENGTH_SHORT).show();

        }
        return null;
    }

    private class validateUserTask extends AsyncTask<String, Void, String> {

        private ProgressDialog loginDialog = null;
        private boolean isValidUser = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginDialog = new ProgressDialog(LoginActivity.this);
            loginDialog.setMessage("Logging In");
            loginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loginDialog.setCanceledOnTouchOutside(false);

            if (loginDialog != null) {
                loginDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            //userModelArrayList = new ArrayList<>();
            Constant.FIREBASE_REF.child("buddy").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            userModelArrayList.clear();
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                UserModel userModel = postSnapshot.getValue(UserModel.class);
                                userModelArrayList.add(userModel);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    }
            );

            for (UserModel userModel : userModelArrayList) {
                if ((userModel.getMobile().toString().equals(params[0])) && userModel.getPassword().toString().equals(params[1])) {
                    isValidUser = true;
                    if (Constant.CurrentUser == null) {
                        Constant.CurrentUser = userModel;
                    }
                }
            }
            if (isValidUser) {

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (loginDialog.isShowing()) {
                loginDialog.dismiss();
                loginDialog = null;
            }
        }
    }
}


