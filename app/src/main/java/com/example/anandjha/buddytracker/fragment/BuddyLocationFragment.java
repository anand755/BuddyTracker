package com.example.anandjha.buddytracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.adapter.FriendListAdapter;
import com.example.anandjha.buddytracker.constant.Constant;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by anandjha on 28/02/17.
 */

public class BuddyLocationFragment extends Fragment {
    private ListView mLvFriendList;
    private Map<String,String> friendMapList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buddy_location_fragment_layout, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);



        mLvFriendList.setAdapter(new FriendListAdapter(getActivity(),friendMapList));
        ((BaseAdapter) mLvFriendList.getAdapter()).notifyDataSetChanged();


    }
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void initializeViews(View view) {
        mLvFriendList = (ListView) view.findViewById(R.id.lv_friends);
        friendMapList = Constant.CurrentUser.getFriendList();

        if(friendMapList.containsKey(Constant.CurrentUser.getMobile())){
            friendMapList.remove(Constant.CurrentUser.getMobile());
        }
    }
}
