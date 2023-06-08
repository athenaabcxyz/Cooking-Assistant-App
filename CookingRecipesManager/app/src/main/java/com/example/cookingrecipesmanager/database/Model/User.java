package com.example.cookingrecipesmanager.database.Model;

import java.util.ArrayList;

public class User {
    String email;
    String name;
    String uid;
    ArrayList<Integer> likedRecipes;
    ArrayList<Integer> savedRecipes;
    ArrayList<Recipe> recipesList;
}
