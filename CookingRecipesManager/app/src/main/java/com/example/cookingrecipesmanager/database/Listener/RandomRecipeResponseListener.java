package com.example.cookingrecipesmanager.database.Listener;

import com.example.cookingrecipesmanager.database.Model.RandomRecipeAPIResponse;

public interface RandomRecipeResponseListener {
    void didFetch(RandomRecipeAPIResponse response, String message);
    void didError(String message);
}
