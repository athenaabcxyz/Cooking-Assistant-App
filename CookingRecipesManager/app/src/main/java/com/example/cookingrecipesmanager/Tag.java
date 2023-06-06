package com.example.cookingrecipesmanager;

public class Tag {
    private String name;

    private Boolean clicked;

    public Tag(String name, boolean clicked) {
        this.name = name;
        this.clicked = clicked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getClicked() {
        return clicked;
    }

    public void setClicked(Boolean clicked) {
        this.clicked = clicked;
    }
}
