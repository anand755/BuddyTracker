package com.example.anandjha.buddytracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.activity.LoginActivity;
import com.example.anandjha.buddytracker.activity.MainActivity;
import com.example.anandjha.buddytracker.constant.Constant;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by anandjha on 27/02/17.
 */

public class Utils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public static void showLogoutAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Logout");
        alertDialog.setMessage("Are you sure you want to Logout?");
        alertDialog.setIcon(R.drawable.ic_logout);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                //Constant.CurrentUser = null;
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();


            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "You Cancelled", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void hideKeyboard(View v, Context context) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getApplicationWindowToken(), 0);
    }
}
