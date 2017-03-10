package com.example.anandjha.buddytracker.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anandjha.buddytracker.Model.FriendModel;
import com.example.anandjha.buddytracker.Model.UserModel;
import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.activity.MainActivity;
import com.example.anandjha.buddytracker.constant.Constant;
import com.example.anandjha.buddytracker.fragment.BuddyLocationFragment;
import com.example.anandjha.buddytracker.fragment.RealtimeTracking;
import com.example.anandjha.buddytracker.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anandjha on 02/03/17.
 */

public class FriendListAdapter extends BaseAdapter {
    private Context mContext;
    private Map<String, String> mapFrndList;
    private List<String> friends;
    private int positionIndex;
    private ImageView mIvSendOrTrack;

    public FriendListAdapter() {

    }

    public FriendListAdapter(Context mContext, Map<String, String> mapFrndList) {
        this.mContext = mContext;
        this.mapFrndList = mapFrndList;
    }

    @Override
    public int getCount() {
        return mapFrndList.size();
    }

    @Override
    public Object getItem(int position) {
        return mapFrndList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (!((AppCompatActivity) mContext).getSupportActionBar().isShowing())
            ((AppCompatActivity) mContext).getSupportActionBar().show();
        positionIndex = position;
        TextView mTvFrndName = null, mTvFrndMobile = null;
        mIvSendOrTrack = null;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_friend, parent, false);

        }
        mTvFrndName = (TextView) convertView.findViewById(R.id.tv_frnd_name);
        mTvFrndMobile = (TextView) convertView.findViewById(R.id.tv_frnd_mobile);
        mIvSendOrTrack = (ImageView) convertView.findViewById(R.id.iv_send_or_track);

        friends = new ArrayList<String>(mapFrndList.keySet());

        if (friends != null) {
            String name = getNameFromMobile(friends.get(position));
            mTvFrndName.setText(name);
            mTvFrndMobile.setText(friends.get(position));
        }
        if (mapFrndList.get(friends.get(position)).equals("0")) {
            mIvSendOrTrack.setImageResource(R.drawable.ic_send_request);
        } else if (mapFrndList.get(friends.get(position)).equals("1")) {
            mIvSendOrTrack.setImageResource(R.drawable.ic_requested);
        } else {
            mIvSendOrTrack.setImageResource(R.drawable.ic_location);
        }

        mIvSendOrTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isNetworkConnected(mContext)) {
                    Toast.makeText(mContext, "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                String mob = ((TextView) ((LinearLayout) ((LinearLayout) v.getParent()).getChildAt(0)).getChildAt(1)).getText().toString();
                ImageView clickedImage = (((ImageView) ((LinearLayout) v.getParent()).getChildAt(1)));
                if (mapFrndList.get(mob).equals("0")) {

                    clickedImage.setImageResource(R.drawable.ic_requested);
                    mapFrndList.put(mob, "1");
                    Constant.CurrentUser.setFriendList(mapFrndList);


                    UserModel selectedUser = getUserFromMobile(mob);
                    if (selectedUser != null) {
                        Map<String, String> accessList = selectedUser.getAccessList();
                        accessList.put(Constant.CurrentUser.getMobile(), "1");

                        if (accessList.containsKey(mob)) {
                            accessList.remove(mob);
                        }

                        selectedUser.setAccessList(accessList);
                        Constant.FIREBASE_REF.child("buddy").child(selectedUser.getMobile()).setValue(selectedUser);

                    }

                    Constant.FIREBASE_REF.child("buddy").child(Constant.CurrentUser.getMobile()).setValue(Constant.CurrentUser);
                    Toast.makeText(mContext, "Request Sent", Toast.LENGTH_SHORT).show();

                } else if (mapFrndList.get(mob).equals("1")) {
                    Toast.makeText(mContext, "Pending for approval", Toast.LENGTH_SHORT).show();
                } else if (mapFrndList.get(mob).equals("2")) {
                    RealtimeTracking realtimeTracking = new RealtimeTracking();
                    Bundle bundle = new Bundle();
                    bundle.putString("mobile", mob);
                    realtimeTracking.setArguments(bundle);
                    ((MainActivity) mContext).getSupportFragmentManager().beginTransaction().
                            add(R.id.fragment_container_mainactivity, realtimeTracking).addToBackStack("RealTime").commit();

                }
                new FriendListAdapter().notifyDataSetChanged();
            }
        });

        return convertView;
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
