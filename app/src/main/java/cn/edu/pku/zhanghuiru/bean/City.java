package cn.edu.pku.zhanghuiru.bean;

import java.io.Serializable;

/**
 * Created by Nichole on 2016/11/1.
 */
public class City {
    private String province;
    private String number;
    private String city;
    private String firstPY;
    private String allPY;
    private String allFirstPY;

    public City(String _pro,String _city,String _num,String _firstpy,String _allpy,String _allfirstpy){
        this.province=_pro;
        this.city=_city;
        this.number=_num;
        this.firstPY=_firstpy;
        this.allFirstPY=_allfirstpy;
        this.allPY=_allpy;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }

    public String getAllPY() {
        return allPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public String getAllFirstPY() {
        return allFirstPY;
    }

    public void setAllFirstPY(String allFirstPY) {
        this.allFirstPY = allFirstPY;
    }

    public String toString(){
        return this.getCity();
    }
}
