package com.example.anandjha.buddytracker.constant;

import com.example.anandjha.buddytracker.Model.UserModel;
import com.firebase.client.Firebase;

import java.util.List;

/**
 * Created by anandjha on 24/02/17.
 */

public final class Constant {
    public static final String FIREBASE_URL = "https://buddytracker-d17d5.firebaseio.com/";
    public static Firebase FIREBASE_REF = null;
    public static UserModel CurrentUser = null;
    public static final String TWITTER_KEY = "rkUHkP9vcpbAyH9geSZTvQT8c";
    public static final String TWITTER_SECRET = "RGUMPwmtVVJd2Kvbsrlca29AfnfU6V4HLmuHwU21RksL109czh";
    public static List<UserModel> listAllUser = null;
}

