package com.example.cookingrecipesmanager.database.Model;

import java.io.Serializable;

public class Length implements Serializable {
    public int number;
    public String unit;

    public Length(int number, String unit) {
        this.number = number;
        this.unit = unit;
    }

    public Length() {

    }
}
