package com.example.cookingrecipesmanager;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeDetail implements Parcelable {


    public String recipeName;
    public String recipeAuthor;
    public String recipeDescription;
    public ArrayList<String> ingredients;

    //List of step for cooking this recipe
    public ArrayList<CookingStep> cookingStepsList;

    //The tags of this recipe
    public ArrayList<String> tagList;

    public RecipeDetail(Parcel in) {
        recipeName = in.readString();
        recipeAuthor = in.readString();
        recipeDescription = in.readString();
        ingredients = in.createStringArrayList();
        tagList = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(recipeName);
        dest.writeString(recipeAuthor);
        dest.writeString(recipeDescription);
        dest.writeStringList(ingredients);
        dest.writeStringList(tagList);
    }

    public RecipeDetail() {
    }

    public void CreateStepList(ArrayList<CookingStep> data) {
        cookingStepsList = data;
    }

    public void CreateTagList(ArrayList<String> data) {
        tagList = data;
    }

    public static final Creator<RecipeDetail> CREATOR = new Creator<RecipeDetail>() {
        @Override
        public RecipeDetail createFromParcel(Parcel in) {
            return new RecipeDetail(in);
        }

        @Override
        public RecipeDetail[] newArray(int size) {
            return new RecipeDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public static RecipeDetail GetDemoRecipe(Context context)
    {
        RecipeDetail out = new RecipeDetail();
        out.recipeName = context.getResources().getString(R.string.sample_recipe_name);
        out.recipeAuthor = context.getResources().getString(R.string.sample_recipe_author);
        out.recipeDescription = context.getResources().getString(R.string.sample_recipe_description);
        out.ingredients = new ArrayList<String> (Arrays.asList(context.getResources().getStringArray(R.array.sample_recipe_ingredients)));
        return out;
    }
}

