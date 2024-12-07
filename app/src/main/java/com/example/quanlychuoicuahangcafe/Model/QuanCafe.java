package com.example.quanlychuoicuahangcafe.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class QuanCafe implements Serializable {
    public String id; 
    public String name; 
    public String address; 
    public String phoneNumber; 
    public String email; 
    public String description; 
    public String avatar;
    public String openTime; 
    public ArrayList<String> listHinhAnh; 
    public ArrayList<monAn> listMonAn; 
    public Float tb; 

    public QuanCafe() {}

    public QuanCafe(String id, String name, String address, String phoneNumber, String email,
                    String description, String avatar, String openTime, ArrayList<String> listHinhAnh, ArrayList<monAn> listMonAn, Float tb) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.description = description;
        this.avatar = avatar;
        this.openTime = openTime;
        this.listHinhAnh = listHinhAnh;
        this.listMonAn = listMonAn;
        this.tb = tb;
    }

    // Getter và Setter cho các trường
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public ArrayList<String> getListHinhAnh() {
        return listHinhAnh;
    }

    public void setListHinhAnh(ArrayList<String> listHinhAnh) {
        this.listHinhAnh = listHinhAnh;
    }

    public ArrayList<monAn> getListMonAn() {
        return listMonAn;
    }

    public void setListMonAn(ArrayList<monAn> listMonAn) {
        this.listMonAn = listMonAn;
    }

    public Float getTb() {
        return tb;
    }

    public void setTb(Float tb) {
        this.tb = tb;
    }

    public String getAnhQuanCafe() { 
        return listHinhAnh != null && !listHinhAnh.isEmpty() ? listHinhAnh.get(0) : null; 
    }

    public void setAnhQuanCafe(String anhQuanCafe) {
        if (listHinhAnh == null) {
            listHinhAnh = new ArrayList<>();
        }
        if (listHinhAnh.isEmpty()) {
            listHinhAnh.add(anhQuanCafe);
        } else {
            listHinhAnh.set(0, anhQuanCafe);
        }
    }
} 