package com.example.cookingrecipesmanager.database.Model;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public String email;
    public String name;
    public String uid;
    public ArrayList<Integer> likedRecipes;
    public ArrayList<Integer> savedRecipes;
    public ArrayList<Recipe> recipesList;

}
