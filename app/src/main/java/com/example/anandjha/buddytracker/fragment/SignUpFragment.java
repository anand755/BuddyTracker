package com.example.anandjha.buddytracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.example.anandjha.buddytracker.Model.FriendModel;
import com.example.anandjha.buddytracker.Model.UserModel;
import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.activity.LoginActivity;
import com.example.anandjha.buddytracker.constant.Constant;
import com.example.anandjha.buddytracker.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anandjha on 23/02/17.
 */

public class SignUpFragment extends Fragment {

    private EditText mEtFName, mEtLName, mEtMobile, mEtEmail, mEtAddress, mEtPass, mEtConPass;
    private Button mBtnSignUp;

    private DigitsAuthButton mAuthBtn;

    private AuthCallback mAuthCallback;
    private UserModel newUser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_fragment_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializiViews(view);


        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Digits.clearActiveSession();
                if (validate()) {
                    if (!Utils.isNetworkConnected(getActivity())) {
                        Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    newUser = new UserModel();

                    newUser.setFirstName(mEtFName.getText().toString());
                    newUser.setLastName(mEtLName.getText().toString());
                    newUser.setMobile(mEtMobile.getText().toString());
                    newUser.setEmail(mEtEmail.getText().toString());
                    newUser.setAddress(mEtAddress.getText().toString());
                    newUser.setPassword(mEtPass.getText().toString());

                    Map<String, String> friendList = new HashMap<String, String>();
                    friendList.put(newUser.getMobile(), "0");
                    newUser.setFriendList(friendList);

                    Map<String, String> accessList = new HashMap<String, String>();
                    accessList.put(newUser.getMobile(), "1");
                    newUser.setAccessList(accessList);

                    newUser.setLatitude(40.712784);
                    newUser.setLongitude(-74.005941);

                    mAuthCallback = new AuthCallback() {
                        @Override

                        public void success(DigitsSession session, String phoneNumber) {

                            Constant.FIREBASE_REF.child("buddy").child(newUser.getMobile()).setValue(newUser);
                            resetFragment();
                            Toast.makeText(getActivity(), "Successfully Registered", Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void failure(DigitsException error) {
                            Toast.makeText(getActivity(), "OTP authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    };
                    mAuthBtn.setCallback(mAuthCallback);
                    AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                            .withAuthCallBack(mAuthCallback)
                            .withPhoneNumber("+91" + mEtMobile.getText().toString());
                    Digits.authenticate(authConfigBuilder.build());


                }
            }
        });

    }


    private boolean validate() {
        boolean status = false;
        String regexPass = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if (mEtFName.getText().toString().isEmpty()) {
            mEtFName.setError("Please enter first name");
        } else if (mEtLName.getText().toString().isEmpty()) {
            mEtLName.setError("Please enter last name");
        } else if (mEtMobile.getText().toString().isEmpty()) {
            mEtMobile.setError("Please enter mobile");
        } else if (mEtMobile.getText().toString().length() != 10) {
            mEtMobile.setError("Please enter valid mobile");
        } else if (mEtEmail.getText().toString().isEmpty()) {
            mEtEmail.setError("Please enter email");
        } else if (!mEtEmail.getText().toString().matches(String.valueOf(Patterns.EMAIL_ADDRESS))) {
            mEtEmail.setError("Please enter valid email");
        } else if (mEtAddress.getText().toString().isEmpty()) {
            mEtAddress.setError("Please enter address");
        } else if (mEtPass.getText().toString().isEmpty()) {
            mEtPass.setError("Please enter password");
        } else if (!mEtPass.getText().toString().matches(regexPass)) {
            mEtPass.setError("Password Policy: \nAt least 8 chars\n" +
                    "Contains at least one digit\n" +
                    "Contains at least one lower alpha char and one upper alpha char\n" +
                    "Contains at least one char within a set of special chars (@#%$^ etc.)\n" +
                    "Does not contain space, tab, etc.");
        } else if (!mEtConPass.getText().toString().equals(mEtPass.getText().toString())) {
            mEtPass.setError("Password doesn't match");
        } else status = true;
        return status;

    }

    private void initializiViews(View view) {
        mEtFName = (EditText) view.findViewById(R.id.et_first_name);
        mEtLName = (EditText) view.findViewById(R.id.et_last_name);
        mEtMobile = (EditText) view.findViewById(R.id.et_mobile);
        mEtEmail = (EditText) view.findViewById(R.id.et_email);
        mEtAddress = (EditText) view.findViewById(R.id.et_address);
        mEtPass = (EditText) view.findViewById(R.id.et_passwd);
        mEtConPass = (EditText) view.findViewById(R.id.et_confirm_password);
        mBtnSignUp = (Button) view.findViewById(R.id.btn_signup);
        mAuthBtn = (DigitsAuthButton) view.findViewById(R.id.bt_auth_registration);


    }

    public void resetFragment() {
        mEtAddress.setText("");
        mEtFName.setText("");
        mEtLName.setText("");
        mEtMobile.setText("");
        mEtEmail.setText("");
        mEtConPass.setText("");
        mEtPass.setText("");
        newUser = null;


    }
}
