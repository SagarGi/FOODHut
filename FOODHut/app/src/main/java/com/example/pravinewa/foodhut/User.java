package com.example.pravinewa.foodhut;

public class User {
    public String fullname, email, phone, address, profileUrl;

    public User(){

    }

    public User(String name, String email, String phone, String address, String profileUrl) {
        this.fullname = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profileUrl = profileUrl;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
