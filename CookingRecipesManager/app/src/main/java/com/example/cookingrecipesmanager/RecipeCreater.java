package com.example.cookingrecipesmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookingrecipesmanager.CookingStep;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.RecipeDetail;
import com.example.cookingrecipesmanager.database.Model.AnalyzedInstruction;
import com.example.cookingrecipesmanager.database.Model.ExtendedIngredient;
import com.example.cookingrecipesmanager.database.Model.Length;
import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.database.Model.Step;
import com.example.cookingrecipesmanager.home.Adapter.TagAdapter;
import com.example.cookingrecipesmanager.recipetracker.Adapter.IngredientAdapter;
import com.example.cookingrecipesmanager.recipetracker.Adapter.RecipeCreaterAdapter;
import com.example.cookingrecipesmanager.recipetracker.Adapter.StepListAdapter;
import com.example.cookingrecipesmanager.recipetracker.Adapter.TagListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeCreater extends AppCompatActivity {

    RecyclerView stepList;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    final int GALLERY_REQ_CODE = 1000;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private PopupWindow popupWindow;
    EditText recipeName;
    EditText description;
    int currentRecipeQuantity;
    EditText time;
    Spinner spinner;
    LinearLayoutManager linearLayoutManagerForStep;
    LinearLayoutManager linearLayoutManagerForIngredients;
    Button buttonAddStep;
    StorageReference storageRef;
    EditText serving;
    Button buttonSave;
    Button addTag;
    EditText newTag;
    RecyclerView tagListView;
    LinearLayoutManager linearLayoutManagerForTag;
    TagAdapter tagAdapter;
    Button buttonCancel;
    EditText unit;
    EditText ingredient;
    Button buttonAddIngredients;
    RecyclerView ingredientsList;
    Button Save;
    Uri imageURI;
    Button addImage;
    ImageView recipeImage;
    EditText summary;
    List<Tag> tagList;
    EditText price;
    EditText readyTime;
    Button Cancel;
    EditText quantity;
    IngredientAdapter ingredientAdapter;
    RecipeCreaterAdapter recipeCreaterAdapter;

    RecipeDetail recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        stepList = findViewById(R.id.stepList);
        recipeName = findViewById((R.id.recipeName));
        buttonAddStep = findViewById(R.id.addStep);
        Save =findViewById(R.id.save_recipe);
        Cancel =findViewById(R.id.cancel_recipe);
        buttonAddIngredients=findViewById(R.id.addIngredients);
        ingredientsList=findViewById(R.id.ingredientsList);
        tagListView=findViewById(R.id.tagList);
        addTag=findViewById(R.id.addTag);
        summary=findViewById(R.id.summary);
        addImage=findViewById(R.id.addImage);
        recipeImage=findViewById(R.id.recipeImage);
        price=findViewById(R.id.price);
        readyTime=findViewById(R.id.readyTime);
        serving = findViewById(R.id.serving);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
            }
        });


        db.collection("recipes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                int count = snapshotList.size();
                currentRecipeQuantity=count+1;
            }
        });

        //Create data sample for testing
        recipe = new RecipeDetail();
        ArrayList<CookingStep> cookingSteps = new ArrayList<CookingStep>();
        ArrayList<String> ingredients = new ArrayList<>();
        tagList= new ArrayList<>();
        recipe.ingredientsList=ingredients;
        recipe.CreateStepList(cookingSteps);
        //End data sample creating

        linearLayoutManagerForTag=new LinearLayoutManager(RecipeCreater.this, LinearLayoutManager.HORIZONTAL, false);
        tagAdapter=new TagAdapter();
        tagAdapter.setData(tagList);
        tagListView.setLayoutManager(linearLayoutManagerForTag);
        tagListView.setAdapter(tagAdapter);

        linearLayoutManagerForIngredients = new LinearLayoutManager(RecipeCreater.this, LinearLayoutManager.VERTICAL, false);
        ingredientAdapter = new IngredientAdapter(RecipeCreater.this,recipe.ingredientsList);
        ingredientsList.setLayoutManager(linearLayoutManagerForIngredients);
        ingredientsList.setAdapter(ingredientAdapter);

        linearLayoutManagerForStep = new LinearLayoutManager(RecipeCreater.this, LinearLayoutManager.VERTICAL, false);
        recipeCreaterAdapter = new RecipeCreaterAdapter(RecipeCreater.this, recipe.cookingStepsList);
        stepList.setLayoutManager(linearLayoutManagerForStep);
        stepList.setAdapter(recipeCreaterAdapter);


        addTag.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_addtag, null);

            // Set the content view of the popup window.
            popupWindow = new PopupWindow(popupView, ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);

            // Set the focusable property of the popup window to true.
            popupWindow.setFocusable(true);

            // Set the outside touchable property of the popup window to true.
            popupWindow.setOutsideTouchable(false);
            spinner=popupView.findViewById(R.id.tagName);
            buttonSave = popupView.findViewById(R.id.save_edit);
            buttonCancel = popupView.findViewById(R.id.cancel_edit);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RecipeCreater.this, R.array.tag, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(adapter);
            buttonSave.setOnClickListener(view1 -> {
                boolean hasAlreadyBeen=false;
            for(Tag tag:tagList)
            {
                if(tag.getName().equals(spinner.getSelectedItem().toString()))
                    hasAlreadyBeen=true;
            }
            if(!hasAlreadyBeen)
            {
                tagList.add(new Tag(spinner.getSelectedItem().toString(), false));
                tagAdapter.notifyItemInserted(tagList.size()-1);
                popupWindow.dismiss();
            }
            else
            {
                Toast.makeText(RecipeCreater.this, "This tag is already added.", Toast.LENGTH_SHORT).show();
            }

            });
            buttonCancel.setOnClickListener(view12 -> {
                if(popupWindow.isShowing())
                    popupWindow.dismiss();
            });
            if(!popupWindow.isShowing())
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
        });


        buttonAddIngredients.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_addingredients, null);

            // Set the content view of the popup window.
            popupWindow = new PopupWindow(popupView, ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);

            // Set the focusable property of the popup window to true.
            popupWindow.setFocusable(true);

            // Set the outside touchable property of the popup window to true.
            popupWindow.setOutsideTouchable(false);
            unit=popupView.findViewById(R.id.unit);
            quantity=popupView.findViewById(R.id.quantity);
            ingredient=popupView.findViewById(R.id.ingredient);
            buttonSave = popupView.findViewById(R.id.save_edit);
            buttonCancel = popupView.findViewById(R.id.cancel_edit);
            buttonSave.setOnClickListener(view1 -> {
                if(ingredient.getText().toString().equals("")||unit.getText().toString().equals(""))
                {
                    Toast.makeText(RecipeCreater.this, "Please input description.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String ingredientString = quantity.getText().toString()+" "+unit.getText().toString()+" of "+ingredient.getText().toString();
                    recipe.ingredientsList.add(ingredientString);
                    ingredientAdapter.notifyItemInserted(recipe.ingredientsList.size()-1);
                    popupWindow.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view12 -> {
                if(popupWindow.isShowing())
                    popupWindow.dismiss();
            });
            if(!popupWindow.isShowing())
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
        });

        buttonAddStep.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_addstep, null);

            // Set the content view of the popup window.
            popupWindow = new PopupWindow(popupView, ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);

            // Set the focusable property of the popup window to true.
            popupWindow.setFocusable(true);

            // Set the outside touchable property of the popup window to true.
            popupWindow.setOutsideTouchable(false);
            spinner =popupView.findViewById(R.id.spinner);
            buttonSave = popupView.findViewById(R.id.save_edit);
            buttonCancel = popupView.findViewById(R.id.cancel_edit);
            description =popupView.findViewById(R.id.editTextTextMultiLine);
            time=popupView.findViewById(R.id.editTextNumber);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RecipeCreater.this, R.array.step_type, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(adapter);
            buttonSave.setOnClickListener(view1 -> {
                if(description.getText().toString().equals(""))
                {
                    Toast.makeText(RecipeCreater.this, "Please input description.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int timer = 0;
                    if(!time.getText().toString().equals(""))
                        timer=Integer.parseInt(time.getText().toString());
                    cookingSteps.add(new CookingStep(description.getText().toString(), spinner.getSelectedItem().toString(), timer));
                    recipeCreaterAdapter.notifyItemInserted(cookingSteps.size()-1);
                    popupWindow.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view12 -> {
                if(popupWindow.isShowing())
                    popupWindow.dismiss();
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    time.setEnabled(spinner.getSelectedItem().toString().equals("Timer"));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    time.setEnabled(spinner.getSelectedItem().toString().equals("Timer"));
                }
            });
            if(!popupWindow.isShowing())
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
        });
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder notice = new StringBuilder();
                if(tagList.size()==0)
                    notice.append("- Your recipe tag(s) is empty.\n");
                if(recipe.ingredientsList.size()==0)
                    notice.append("- Your ingredient list is empty.\n");
                if(recipe.cookingStepsList.size()==0)
                    notice.append("- Your step list is empty.\n");
                if(summary.getText().toString().equals(""))
                    notice.append("- Your summary is empty.\n");
                notice.append("Do you wish to save this recipe?");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:

                                                Recipe newRecipe = new Recipe();
                                                newRecipe.title=recipeName.getText().toString();
                                                newRecipe.extendedIngredients=new ArrayList<>();
                                                newRecipe.analyzedInstructions=new ArrayList<>();
                                                newRecipe.analyzedInstructions.add(new AnalyzedInstruction());
                                                newRecipe.analyzedInstructions.get(0).steps= new ArrayList<>();
                                                for(int i = 0; i<= recipe.cookingStepsList.size()-1;i++)
                                                {
                                                    newRecipe.analyzedInstructions.get(0).steps.add(new Step(i, recipe.cookingStepsList.get(i).stepIntruction, new Length(recipe.cookingStepsList.get(i).timerBySecond, "second")));
                                                }
                                                for(int i = 0; i<=recipe.ingredientsList.size()-1;i++)
                                                {
                                                    newRecipe.extendedIngredients.add(new ExtendedIngredient(recipe.ingredientsList.get(i)));
                                                }
                                                newRecipe.dishTypes=new ArrayList<>();
                                                for(int i=0; i<=tagList.size()-1; i++)
                                                {
                                                    newRecipe.dishTypes.add(tagList.get(i).getName());
                                                }
                                                newRecipe.aggregateLikes=0;
                                                if(!readyTime.getText().toString().equals(""))
                                                    newRecipe.readyInMinutes = Integer.parseInt(readyTime.getText().toString());
                                                else
                                                    newRecipe.readyInMinutes=0;

                                                if(!price.getText().toString().equals(""))
                                                    newRecipe.pricePerServing = Double.parseDouble(price.getText().toString());
                                                else
                                                    newRecipe.pricePerServing=0;

                                                if(!serving.getText().toString().equals(""))
                                                    newRecipe.servings=Integer.parseInt(serving.getText().toString());
                                                else
                                                    newRecipe.servings=0;
                                                newRecipe.userID=user.getUid();
                                                newRecipe.userName = user.getDisplayName();
                                                newRecipe.id=currentRecipeQuantity;
                                                newRecipe.summary=summary.getText().toString();
                                                storageRef=storage.getReference("images/"+newRecipe.id);
                                                storageRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                newRecipe.image=uri.toString();
                                                                db.collection("recipes").add(newRecipe).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentReference documentReference) {
                                                                        Toast.makeText(RecipeCreater.this, "Save recipe successfully!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        });


                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RecipeCreater.this, "Failed to save recipe.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeCreater.this);
                                builder.setMessage("Do you want the name "+recipeName.getText().toString()+"?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeCreater.this);
                builder.setMessage(notice.toString()).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==GALLERY_REQ_CODE)
            {
                assert data != null;
                imageURI = data.getData();
                recipeImage.setImageURI(data.getData());
            }
        }
    }
}