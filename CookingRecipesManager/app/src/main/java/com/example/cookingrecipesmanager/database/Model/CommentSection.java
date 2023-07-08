package com.example.cookingrecipesmanager.database.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class CommentSection implements Serializable {
    public String recipeId;
    public ArrayList<Comments> commentList;
}
