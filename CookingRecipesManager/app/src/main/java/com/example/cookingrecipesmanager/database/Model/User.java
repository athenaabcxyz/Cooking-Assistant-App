package com.example.cookingrecipesmanager.database.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public String email;
    public String name;
    public String image;
    public String uid;
    public ArrayList<Long> likedRecipes;
    public ArrayList<Long> savedRecipes;

}
