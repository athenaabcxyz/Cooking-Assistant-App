package com.example.cookingrecipesmanager.database.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public String email;
    public String name;
    public String uid;
    public ArrayList<Integer> likedRecipes;
    public ArrayList<Integer> savedRecipes;
    public ArrayList<Recipe> recipesList;
}
