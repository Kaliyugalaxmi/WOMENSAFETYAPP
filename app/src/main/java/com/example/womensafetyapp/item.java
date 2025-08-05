package com.example.womensafetyapp;

public class item {
    int image;
    String name;
    String name1;
    int image1;

    public item(int image, String name, String name1, int image1) {
        this.image = image;
        this.name = name;
        this.name1 = name1;
        this.image1 = image1;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName1() {
        return name1;
    }

    public void setname1(String name1) {
        this.name1 = name1;
    }

    public int getImage1() {
        return image1;
    }

    public void setImage1(int image1) {
        this.image1 = image1;
    }
}
