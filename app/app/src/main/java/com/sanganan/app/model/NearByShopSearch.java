package com.sanganan.app.model;

/**
 * Created by raj on 8/16/2016.
 */
public class NearByShopSearch {

    String distance;
    String nearById;
    String shopName;
    String address1;
    String address4;
    String Image2;
    String Image3;
    String BannerImage;
    String Latitude;
    String Longitude;
    String Is247;
    String IsHomeDelivery;
    String PhoneNbr1;
    String PhoneNbr2;
    String typeOfShop;
    String description;
    String Name;
    String CategoryID;
    String PhoneNbr3;
    String Type;
    String city;


    public String getNearById() {
        return nearById;
    }

    public void setNearById(String nearById) {
        this.nearById = nearById;
    }


    public String getImage2() {
        return Image2;
    }

    public void setImage2(String image2) {
        Image2 = image2;
    }

    public String getImage3() {
        return Image3;
    }

    public void setImage3(String image3) {
        Image3 = image3;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getPhoneNbr3() {
        return PhoneNbr3;
    }

    public void setPhoneNbr3(String phoneNbr3) {
        PhoneNbr3 = phoneNbr3;
    }

    public String getBannerImage() {
        return BannerImage;
    }

    public void setBannerImage(String bannerImage) {
        BannerImage = bannerImage;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTypeOfShop() {
        return typeOfShop;
    }

    public void setTypeOfShop(String typeOfShop) {
        this.typeOfShop = typeOfShop;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getIs247() {
        return Is247;
    }

    public void setIs247(String is247) {
        Is247 = is247;
    }

    public String getIsHomeDelivery() {
        return IsHomeDelivery;
    }

    public void setIsHomeDelivery(String isHomeDelivery) {
        IsHomeDelivery = isHomeDelivery;
    }

    public String getPhoneNbr1() {
        return PhoneNbr1;
    }

    public void setPhoneNbr1(String phoneNbr1) {
        PhoneNbr1 = phoneNbr1;
    }

    public String getPhoneNbr2() {
        return PhoneNbr2;
    }

    public void setPhoneNbr2(String phoneNbr2) {
        PhoneNbr2 = phoneNbr2;
    }

    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        if (address4.equalsIgnoreCase("null")) {
            this.address4 = "";
        } else {
            this.address4 = address4;
        }
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

}
