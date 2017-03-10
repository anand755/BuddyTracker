package com.example.anandjha.buddytracker.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.anandjha.buddytracker.Model.UserModel;
import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.constant.Constant;
import com.example.anandjha.buddytracker.fragment.DashboardFragment;
import com.example.anandjha.buddytracker.receiver.UpdateLatLongReceiver;
import com.example.anandjha.buddytracker.utils.Utils;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private SharedPreferences.Editor editor;
    private String usermodelJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
        if (Constant.CurrentUser!=null)
             usermodelJson = gson.toJson(Constant.CurrentUser);
        else

        {
            sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            usermodelJson = sharedPreferences.getString("usermodel", null);

            Constant.CurrentUser = gson.fromJson(usermodelJson, UserModel.class);
        }

        editor.putString("usermodel", usermodelJson);
        editor.commit();

        SetAlarm();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_mainactivity, new DashboardFragment()).commit();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setUpHomeButton();
            }
        });
        setUpHomeButton();
    }

    public void setUpHomeButton() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    public void SetAlarm() {
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, UpdateLatLongReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000 * 60, pi);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            Utils.showLogoutAlert(MainActivity.this);
        } else {
            getSupportActionBar().hide();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            getSupportActionBar().hide();
            getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
}
