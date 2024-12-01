package com.example.quanlynhahang.Model;

import java.io.Serializable;

public class monAn implements Serializable {
    String id;
    String tenMonAn;
    String hinhAnhMinhHoa;
    float giaMonAn;
    String moTaMonAn;

    public monAn(){};

    public monAn(String id,String tenMonAn, String hinhAnhMinhHoa, float giaMonAn, String moTaMonAn) {
        this.tenMonAn = tenMonAn;
        this.hinhAnhMinhHoa = hinhAnhMinhHoa;
        this.giaMonAn = giaMonAn;
        this.moTaMonAn = moTaMonAn;
        this.id = id;
    }



    public String getTenMonAn() {
        return tenMonAn;
    }

    public void setTenMonAn(String tenMonAn) {
        this.tenMonAn = tenMonAn;
    }

    public String getHinhAnhMinhHoa() {
        return hinhAnhMinhHoa;
    }

    public void setHinhAnhMinhHoa(String hinhAnhMinhHoa) {
        this.hinhAnhMinhHoa = hinhAnhMinhHoa;
    }

    public float getGiaMonAn() {
        return giaMonAn;
    }

    public void setGiaMonAn(float giaMonAn) {
        this.giaMonAn = giaMonAn;
    }

    public String getMoTaMonAn() {
        return moTaMonAn;
    }

    public void setMoTaMonAn(String moTaMonAn) {
        this.moTaMonAn = moTaMonAn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
