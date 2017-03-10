package com.example.anandjha.buddytracker.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.constant.Constant;
import com.example.anandjha.buddytracker.utils.Utils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by anandjha on 28/02/17.
 */

public class DashboardFragment extends Fragment {
    private LinearLayout mLlTrackLocation, mLlFindFriendLocation, mLlSearchFriend, mLlBlock;
    private TextView mTvNameLogo, mTvName;
    private ImageView mIvLogout,mIvChangePwd;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment_layout, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setName();


        mLlTrackLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_mainactivity, new TrackFragment())
                        .addToBackStack("Track").commit();
            }
        });

        mLlFindFriendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_mainactivity, new BuddyLocationFragment()).addToBackStack("Buddy Location").commit();
            }
        });

        mLlSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_mainactivity, new SearchFriendFragment()).addToBackStack("Search Friend").commit();
            }
        });
        mLlBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_mainactivity, new BlockBuddyFragment()).addToBackStack("Block").commit();
            }
        });

        mIvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSharedPreferences = getActivity().getSharedPreferences("LoginSession",MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
                mEditor.clear();
                mEditor.commit();

                Constant.CurrentUser = null;
                Utils.showLogoutAlert(getActivity());
            }
        });
        mIvChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_mainactivity, new ChangePwdFragment()).addToBackStack("Change Pwd").commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    private void initializeViews(View view) {
        mLlTrackLocation = (LinearLayout) view.findViewById(R.id.ll_dashboard_my_location);
        mLlSearchFriend = (LinearLayout) view.findViewById(R.id.ll_dashboard_search_friend);
        mLlFindFriendLocation = (LinearLayout) view.findViewById(R.id.ll_dashboard_buddy_location);
        mLlBlock = (LinearLayout) view.findViewById(R.id.ll_dashboard_location_status);
        mIvChangePwd = (ImageView) view.findViewById(R.id.iv_change_password);


        mTvNameLogo = (TextView) view.findViewById(R.id.tv_name_logo);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mIvLogout = (ImageView) view.findViewById(R.id.iv_logout);
    }

    private void setName() {
        if (Constant.CurrentUser != null) {
            String firstChar = Constant.CurrentUser.getFirstName().substring(0, 1).toUpperCase();
            String lastChar = Constant.CurrentUser.getLastName().substring(0, 1).toUpperCase();

            mTvNameLogo.setText(firstChar + lastChar);
            mTvName.setText(Constant.CurrentUser.getFirstName() + " " + Constant.CurrentUser.getLastName());
        }
    }
}
