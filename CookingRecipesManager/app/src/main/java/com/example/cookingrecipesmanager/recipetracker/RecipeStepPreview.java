package com.example.cookingrecipesmanager.recipetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookingrecipesmanager.CookingStep;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.RecipeDetail;
import com.example.cookingrecipesmanager.Tag;
import com.example.cookingrecipesmanager.database.Model.AnalyzedInstruction;
import com.example.cookingrecipesmanager.database.Model.ExtendedIngredient;
import com.example.cookingrecipesmanager.database.Model.Ingredient;
import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.database.Model.Step;
import com.example.cookingrecipesmanager.recipetracker.Adapter.StepListAdapter;
import com.example.cookingrecipesmanager.recipetracker.Adapter.TagListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeStepPreview extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView stepList;

    Toolbar textView;
    LinearLayoutManager linearLayoutManagerForStep;

    StepListAdapter stepListAdapter;

    RecipeDetail recipe= new RecipeDetail();
    List<DocumentSnapshot> snapshotList;
    ArrayList<CookingStep> cookingSteps = new ArrayList<CookingStep>();

    Recipe paramRecipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            try{
                paramRecipe = (Recipe) extras.getSerializable("RECIPE");
                Toast.makeText(this, "Name recipe: "+ paramRecipe.title, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){

            }
        }
        setContentView(R.layout.activity_recipe_step_preview);
        stepList = findViewById(R.id.stepList);
        textView = findViewById((R.id.toolbar));
        stepList=findViewById(R.id.stepList);
        ImageView appBarImage = findViewById(R.id.app_bar_image);
        Toolbar toolbar = findViewById(R.id.toolbar);

        //Create data sample for testing


        //Get data from cloud FireStore
        db.collection("recipes")
                //Use query to find specific document
                .whereEqualTo("id",paramRecipe.id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Create a list of result. The number of result can be 1
                        snapshotList = queryDocumentSnapshots.getDocuments();
                        //Convert the result to class Object for easier processing.
                        for(DocumentSnapshot snapshot:snapshotList)
                        {
                            Recipe recipe = snapshot.toObject(Recipe.class);
                            assert recipe != null;
                            StringBuilder prepareInstruction= new StringBuilder("Prepare these ingredients: \n");
                            for (ExtendedIngredient ingredient:recipe.extendedIngredients)
                            {
                                prepareInstruction.append(" - ").append(ingredient.original).append("\n");
                            }
                            cookingSteps.add(new CookingStep(prepareInstruction.toString(), "Prepare", 0));
                            stepListAdapter.notifyItemInserted(cookingSteps.size()-1);
                            AnalyzedInstruction instruction = recipe.analyzedInstructions.get(0);

                            for(Step step:instruction.steps)
                            {
                                String instructionDetail = step.step;
                                int time = 0;
                                String type="Basic";
                                if(step.length!=null)
                                {
                                    switch (step.length.unit)
                                    {
                                        case "seconds":
                                            time=step.length.number;
                                            break;
                                        case "minutes":
                                            time=step.length.number*60;
                                            break;
                                        case "hours":
                                            time=step.length.number*3600;
                                            break;
                                        default:

                                    }
                                    type="Timer";
                                }
                                else
                                {
                                    type="Basic";
                                }
                                cookingSteps.add(new CookingStep(instructionDetail, type, time));
                                stepListAdapter.notifyItemInserted(cookingSteps.size()-1);

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        Log.d("TagTest",""+cookingSteps.size());
        recipe.CreateStepList(cookingSteps);
        recipe.recipeName = "Boiled water";
        ArrayList<String> tagList = new ArrayList<String>();
        tagList.add("Boil");
        tagList.add("Water");
        recipe.CreateTagList(tagList);
        //End data sample creating

        linearLayoutManagerForStep = new LinearLayoutManager(RecipeStepPreview.this, LinearLayoutManager.VERTICAL, false);
        stepListAdapter = new StepListAdapter(RecipeStepPreview.this, recipe.cookingStepsList);
        stepList.setLayoutManager(linearLayoutManagerForStep);
        stepList.setAdapter(stepListAdapter);
        toolbar.setTitle(recipe.recipeName);
        appBarImage.setImageResource(R.drawable.mon3);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);


    }
}