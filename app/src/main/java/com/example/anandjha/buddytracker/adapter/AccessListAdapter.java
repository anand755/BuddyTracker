package com.example.anandjha.buddytracker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anandjha.buddytracker.Model.UserModel;
import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.activity.LoginActivity;
import com.example.anandjha.buddytracker.constant.Constant;
import com.example.anandjha.buddytracker.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by anandjha on 02/03/17.
 */

public class AccessListAdapter extends BaseAdapter {
    private Context mContext;
    private Map<String, String> mapAccessFrndList;
    private List<String> accessFriends;
    private int positionIndex;
    private Switch mSwitchEnableDisable;
    private boolean returnAlertStatus = false;


    public AccessListAdapter() {

    }

    public AccessListAdapter(Context mContext, Map<String, String> mapAccessFrndList) {
        this.mContext = mContext;
        this.mapAccessFrndList = mapAccessFrndList;
    }

    @Override
    public int getCount() {
        return mapAccessFrndList.size();
    }

    @Override
    public Object getItem(int position) {
        return mapAccessFrndList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        positionIndex = position;
        TextView mTvFrndName = null, mTvFrndMobile = null;
        mSwitchEnableDisable = null;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_access_friend, parent, false);

            mTvFrndName = (TextView) convertView.findViewById(R.id.tv_frnd_name);
            mTvFrndMobile = (TextView) convertView.findViewById(R.id.tv_frnd_mobile);
            mSwitchEnableDisable = (Switch) convertView.findViewById(R.id.switch_enable_disable);
        }
        accessFriends = new ArrayList<String>(mapAccessFrndList.keySet());

        String name = getNameFromMobile(accessFriends.get(position));
        mTvFrndName.setText(name);
        mTvFrndMobile.setText(accessFriends.get(position));

        if (mapAccessFrndList.get(accessFriends.get(position)).equals("1")) {
            mSwitchEnableDisable.setChecked(false);
        } else {
            mSwitchEnableDisable.setChecked(true);
        }
        mSwitchEnableDisable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String mob = ((TextView) ((LinearLayout) ((LinearLayout) ((Switch) buttonView).getParent()).getChildAt(0)).getChildAt(1)).getText().toString();
                if (!Utils.isNetworkConnected(mContext)) {
                    Toast.makeText(mContext, "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isChecked) {
                    showTrackingWarning("on", isChecked, mob);
                } else if (!isChecked) {
                    showTrackingWarning("off", isChecked, mob);
                }
                new AccessListAdapter().notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private void showTrackingWarning(String message, final boolean isChecked, final String mob) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setCancelable(false);


        alertDialog.setTitle("Warning");
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (isChecked) {
                    Map<String, String> currentUserAccessList = Constant.CurrentUser.getAccessList();
                    currentUserAccessList.put(mob, "2");
                    Constant.CurrentUser.setAccessList(currentUserAccessList);
                    Constant.FIREBASE_REF.child("buddy").child(Constant.CurrentUser.getMobile()).setValue(Constant.CurrentUser);

                    UserModel userModelFriend = getUserFromMobile(mob);
                    if (userModelFriend != null) {

                        Map<String, String> friendFriendList = userModelFriend.getFriendList();
                        friendFriendList.put(Constant.CurrentUser.getMobile(), "2");
                        userModelFriend.setFriendList(friendFriendList);
                        Constant.FIREBASE_REF.child("buddy").child(userModelFriend.getMobile()).setValue(userModelFriend);

                    }
                } else {
                    Map<String, String> currentUserAccessList = Constant.CurrentUser.getAccessList();
                    currentUserAccessList.put(mob, "1");
                    Constant.CurrentUser.setAccessList(currentUserAccessList);
                    Constant.FIREBASE_REF.child("buddy").child(Constant.CurrentUser.getMobile()).setValue(Constant.CurrentUser);

                    UserModel userModelFriend = getUserFromMobile(mob);
                    if (userModelFriend != null) {

                        Map<String, String> friendFriendList = userModelFriend.getFriendList();
                        friendFriendList.put(Constant.CurrentUser.getMobile(), "0");
                        userModelFriend.setFriendList(friendFriendList);
                        Constant.FIREBASE_REF.child("buddy").child(userModelFriend.getMobile()).setValue(userModelFriend);

                    }
                }

                dialog.dismiss();
            }
        });


        alertDialog.show();

    }

    private String getNameFromMobile(String mobile) {

        if (Constant.listAllUser != null) {

            for (int i = 0; i < Constant.listAllUser.size(); i++) {
                if (Constant.listAllUser.get(i).getMobile().equals(mobile)) {
                    return Constant.listAllUser.get(i).getFirstName() + " " + Constant.listAllUser.get(i).getLastName();
                }
            }
        }

        return null;
    }

    public UserModel getUserFromMobile(String mob) {
        List<UserModel> listUser = Constant.listAllUser;
        if (listUser != null) {
            for (int i = 0; i < listUser.size(); i++) {
                if (listUser.get(i).getMobile().equals(mob)) {
                    return listUser.get(i);
                }
            }
        }
        return null;
    }
}
