package com.example.cookingrecipesmanager.database.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Step implements Serializable {
    public int number;
    public String step;
    public Length length;

    public Step(int number, String step, Length length) {
        this.number = number;
        this.step = step;
        this.length = length;
    }

    public Step(){

    }
}
