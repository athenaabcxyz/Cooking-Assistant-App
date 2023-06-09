package com.example.cookingrecipesmanager.database;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.database.Listener.RandomRecipeResponseListener;
import com.example.cookingrecipesmanager.database.Model.RandomRecipeAPIResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DatabaseGenerator extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RequestManager manager;

    Button button;
    private  final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeAPIResponse response, String message) {
            for(int i=0; i<response.recipes.size()-1;i++)
            {
            db.collection("recipes").add(response.recipes.get(i));
            }
        }

        @Override
        public void didError(String message) {
            Toast.makeText(DatabaseGenerator.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_generator);

        button=findViewById(R.id.button);
        manager=new RequestManager(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.getRandomRecipe(randomRecipeResponseListener);
            }
        });



    }
}