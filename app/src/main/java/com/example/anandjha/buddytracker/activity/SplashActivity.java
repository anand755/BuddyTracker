package com.example.anandjha.buddytracker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.anandjha.buddytracker.Model.UserModel;
import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.constant.Constant;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import static com.example.anandjha.buddytracker.constant.Constant.listAllUser;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences mSharedPreferences;
    private boolean checkUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        checkUser = mSharedPreferences.getBoolean("session", false);

        Firebase.setAndroidContext(this);

        if (Constant.FIREBASE_REF == null)
            Constant.FIREBASE_REF = new Firebase(Constant.FIREBASE_URL);
        getAllUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (checkUser) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 3000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void getAllUser() {
        listAllUser = new ArrayList<>();


        Constant.FIREBASE_REF.child("buddy").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            UserModel userModel = postSnapshot.getValue(UserModel.class);
                            listAllUser.add(userModel);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                }
        );

    }
}


