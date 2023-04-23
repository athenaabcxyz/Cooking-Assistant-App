package com.example.cookingrecipesmanager;

public class CookingNote {
    private String title;
    private String author;
    private int img;
    private Float evaluate;
    private Boolean iFavorites;

    public CookingNote(String title, String author, int img, Float evaluate, Boolean iFavorites) {
        this.title = title;
        this.author = author;
        this.img = img;
        this.evaluate = evaluate;
        this.iFavorites = iFavorites;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public Float getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(Float evaluate) {
        this.evaluate = evaluate;
    }

    public Boolean getiFavorites() {
        return iFavorites;
    }

    public void setiFavorites(Boolean iFavorites) {
        this.iFavorites = iFavorites;
    }
}
