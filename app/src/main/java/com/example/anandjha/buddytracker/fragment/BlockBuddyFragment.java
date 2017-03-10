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
import com.example.anandjha.buddytracker.adapter.AccessListAdapter;
import com.example.anandjha.buddytracker.adapter.FriendListAdapter;
import com.example.anandjha.buddytracker.constant.Constant;

import java.util.Map;

/**
 * Created by anandjha on 28/02/17.
 */

public class BlockBuddyFragment extends Fragment {

    private ListView mLvAccessList;
    private Map<String,String> accessMapList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.block_buddy_fragment_layout, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);


        mLvAccessList.setAdapter(new AccessListAdapter(getActivity(), accessMapList));
        ((BaseAdapter) mLvAccessList.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void initializeViews(View view) {
        mLvAccessList = (ListView) view.findViewById(R.id.lv_access_friends);
        accessMapList = Constant.CurrentUser.getAccessList();

        if(accessMapList.containsKey(Constant.CurrentUser.getMobile())){
            accessMapList.remove(Constant.CurrentUser.getMobile());
        }
    }
}
