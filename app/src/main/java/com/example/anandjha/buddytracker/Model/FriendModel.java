package com.example.anandjha.buddytracker.Model;

/**
 * Created by anandjha on 02/03/17.
 */

public class FriendModel {
    String mobile;
    String status;

    public FriendModel(String mobile, String status) {
        this.mobile = mobile;
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
