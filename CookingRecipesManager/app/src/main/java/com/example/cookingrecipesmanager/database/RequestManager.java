package com.example.cookingrecipesmanager.database;

import android.content.Context;

import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.database.Listener.RandomRecipeResponseListener;
import com.example.cookingrecipesmanager.database.Model.RandomRecipeAPIResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class RequestManager {
    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RequestManager(Context context) {
        this.context = context;
    }

    public void getRandomRecipe(RandomRecipeResponseListener listener){
        CallRandomRecipes callRandomRecipes = retrofit.create(CallRandomRecipes.class);
        Call<RandomRecipeAPIResponse> call = callRandomRecipes.callRandomRecipe(context.getString(R.string.api_key),"50");
        call.enqueue(new Callback<RandomRecipeAPIResponse>() {
            @Override
            public void onResponse(Call<RandomRecipeAPIResponse> call, Response<RandomRecipeAPIResponse> response) {
                if(!response.isSuccessful())
                {
                    listener.didError(response.message());
                    return;
                }listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RandomRecipeAPIResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    };

    private interface CallRandomRecipes{
        @GET("recipes/random")
        Call<RandomRecipeAPIResponse> callRandomRecipe(
                @Query("apiKey") String apiKey,
                @Query("number") String number
        );
    }
}
