package com.example.anandjha.buddytracker.Model;

import java.util.List;
import java.util.Map;

/**
 * Created by anandjha on 23/02/17.
 */

public class UserModel {

    private String FirstName;
    private String LastName;
    private String Mobile;
    private String Email;
    private String Address;
    private String Password;
    private Map<String, String> FriendList;
    private Map<String ,String> accessList;
    private double latitude;
    private double longitude;

    public UserModel() {
    }

    public Map<String, String> getAccessList() {
        return accessList;
    }

    public void setAccessList(Map<String, String> accessList) {
        this.accessList = accessList;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public Map<String, String> getFriendList() {
        return FriendList;
    }

    public void setFriendList(Map<String, String> friendList) {
        FriendList = friendList;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

