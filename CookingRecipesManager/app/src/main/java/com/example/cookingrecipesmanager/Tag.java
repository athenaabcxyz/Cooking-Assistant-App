package com.example.cookingrecipesmanager;

public class Tag {
    private int img;
    private String name;

    public Tag(Integer img, String name) {
        this.img = img;
        this.name = name;
    }

    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
