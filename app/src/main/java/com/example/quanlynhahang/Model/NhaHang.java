package com.example.quanlynhahang.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class NhaHang implements Serializable {
    //    ArrayList<String> binhLuan;
    public String id;
    public String diaChiNhaHang;
    public String gioMoCua;
    public String email;
    public String moTaNhaHang;
    public String anhNhaHang;
    public String soDienThoai;
    public String tenNhaHang;
    public ArrayList<monAn> listMonAn;
    public ArrayList<String> listHinhAnh;
    public Float tb;



    public NhaHang(){};

    public NhaHang(String diaChiNhaHang, String gioMoCua, String anhNhaHang, ArrayList<String> listHinhAnh, String id,
                   String email, String moTaNhaHang, String soDienThoai, String tenNhaHang, ArrayList<monAn> listMonAn,
                   Float tb) {
//        this.binhLuan = binhLuan;
        this.tb = tb;
        this.diaChiNhaHang = diaChiNhaHang;
        this.email = email;
        this.moTaNhaHang = moTaNhaHang;
        this.soDienThoai = soDienThoai;
        this.tenNhaHang = tenNhaHang;
        this.listMonAn = listMonAn;
        this.gioMoCua = gioMoCua;
        this.anhNhaHang = anhNhaHang;
        this.listHinhAnh = listHinhAnh;
        this.id = id;
    }

    public Float getTb() {
        return tb;
    }

    public void setTb(Float tb) {
        this.tb = tb;
    }

    public String getDiaChiNhaHang() {
        return diaChiNhaHang;
    }

    public void setDiaChiNhaHang(String diaChiNhaHang) {
        this.diaChiNhaHang = diaChiNhaHang;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMoTaNhaHang() {
        return moTaNhaHang;
    }

    public void setMoTaNhaHang(String moTaNhaHang) {
        this.moTaNhaHang = moTaNhaHang;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getTenNhaHang() {
        return tenNhaHang;
    }

    public void setTenNhaHang(String tenNhaHang) {
        this.tenNhaHang = tenNhaHang;
    }

    public ArrayList<monAn> getListMonAn() {
        return listMonAn;
    }

    public void setListMonAn(ArrayList<monAn> listMonAn) {
        this.listMonAn = listMonAn;
    }

    public String getGioMoCua() {
        return gioMoCua;
    }

    public void setGioMoCua(String gioMoCua) {
        this.gioMoCua = gioMoCua;
    }

    public String getAnhNhaHang() {
        return anhNhaHang;
    }

    public void setAnhNhaHang(String anhNhaHang) {
        this.anhNhaHang = anhNhaHang;
    }

    public ArrayList<String> getListHinhAnh() {
        return listHinhAnh;
    }

    public void setListHinhAnh(ArrayList<String> listHinhAnh) {
        this.listHinhAnh = listHinhAnh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
