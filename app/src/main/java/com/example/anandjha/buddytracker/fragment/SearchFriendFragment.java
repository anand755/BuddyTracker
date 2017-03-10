package com.example.anandjha.buddytracker.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


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

import okhttp3.internal.Util;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * Created by anandjha on 28/02/17.
 */

public class SearchFriendFragment extends Fragment {
    private AutoCompleteTextView mAutoTvSearch;
    private ArrayAdapter<String> mAdapter;
    private String[] allMobileList;
    private LinearLayout mLlSearchResult;
    private TextView mTvName, mTvEmail, mTvAddress;
    private ImageView mIvAdd;
    private List<FriendModel> friendModelList;
    private UserModel userModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_friend_fragment_layout, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);

        setList();
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, allMobileList);


        mAutoTvSearch.setThreshold(1);
        mAutoTvSearch.setAdapter(mAdapter);
        mAutoTvSearch.setTextColor(Color.parseColor("#2755ED"));

        mAutoTvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Utils.dismissKeyboard(getActivity());

                if (Constant.listAllUser != null) {

                    for (int i = 0; i < Constant.listAllUser.size(); i++) {
                        if (Constant.listAllUser.get(i).getMobile().equals(((AppCompatTextView) view).getText())) {
                            userModel = Constant.listAllUser.get(i);

                            mTvName.setText(userModel.getFirstName() + " " + userModel.getLastName());
                            mTvEmail.setText(userModel.getEmail());
                            mTvAddress.setText(userModel.getAddress());
                            mLlSearchResult.setVisibility(View.VISIBLE);
                            Utils.hideKeyboard(view, getActivity());

                        }
                    }
                }
            }
        });

        mIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.dismissKeyboard(getActivity());

                if (!Utils.isNetworkConnected(getActivity())) {
                    Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> frndMap = Constant.CurrentUser.getFriendList();

                frndMap.put(userModel.getMobile(), "0");


                String removeMob = Constant.CurrentUser.getMobile();
                if (frndMap.containsKey(removeMob)) {
                    frndMap.remove(removeMob);
                }


                Constant.CurrentUser.setFriendList(frndMap);
                Constant.FIREBASE_REF.child("buddy").child(Constant.CurrentUser.getMobile()).setValue(Constant.CurrentUser);

                Toast.makeText(getActivity(), userModel.getFirstName() + " " + userModel.getLastName() + "has been added successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setList() {
        if (Constant.listAllUser != null) {
            allMobileList = new String[Constant.listAllUser.size()];
            for (int i = 0; i < Constant.listAllUser.size(); i++) {
                allMobileList[i] = Constant.listAllUser.get(i).getMobile();

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void initializeViews(View view) {
        mAutoTvSearch = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView1);
        mTvName = (TextView) view.findViewById(R.id.tv_frnd_name);
        mTvEmail = (TextView) view.findViewById(R.id.tv_frnd_mail);
        mTvAddress = (TextView) view.findViewById(R.id.tv_frnd_address);
        mLlSearchResult = (LinearLayout) view.findViewById(R.id.ll_search_result);
        mIvAdd = (ImageView) view.findViewById(R.id.iv_add_frnd);
        friendModelList = new ArrayList<>();
        if (Constant.listAllUser != null)
            allMobileList = new String[Constant.listAllUser.size()];

    }
}
