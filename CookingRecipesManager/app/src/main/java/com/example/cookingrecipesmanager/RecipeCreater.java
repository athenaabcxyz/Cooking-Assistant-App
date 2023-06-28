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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;

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
    LinearLayout addImage;
    LinearLayout group_change_image;
    LinearLayout changeImage;
    LinearLayout deleteImage;
    ImageView recipeImage;
    EditText summary;
    List<Tag> tagList;
    EditText price;
    EditText readyTime;
    Button Cancel;
    EditText quantity;
    IngredientAdapter ingredientAdapter;
    RecipeCreaterAdapter recipeCreaterAdapter;

    TextView textTagHint;
    TextView textIngredientHint;
    TextView textStepHint;
    RecipeDetail recipe;
    Recipe paramRecipe;
    Recipe newRecipe;
    String before_screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            try{
                paramRecipe = (Recipe) extras.getSerializable("RECIPE");
                before_screen = (String) extras.getSerializable("BEFORE_SCREEN");
                Toast.makeText(this, "Name recipe: "+ paramRecipe.title, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){

            }
        }

        setContentView(R.layout.activity_create_recipe);
        stepList = findViewById(R.id.stepList);
        recipeName = findViewById((R.id.recipeName));
        buttonAddStep = findViewById(R.id.addStep);
        Save = findViewById(R.id.save_recipe);
        Cancel = findViewById(R.id.cancel_recipe);
        buttonAddIngredients = findViewById(R.id.addIngredients);
        ingredientsList = findViewById(R.id.ingredientsList);
        tagListView = findViewById(R.id.tagList);
        addTag = findViewById(R.id.addTag);
        summary = findViewById(R.id.summary);
        addImage = findViewById(R.id.addImage);
        recipeImage = findViewById(R.id.recipeImage);
        price = findViewById(R.id.price);
        readyTime = findViewById(R.id.readyTime);
        serving = findViewById(R.id.serving);
        group_change_image = findViewById(R.id.group_change_image);
        changeImage = findViewById(R.id.changeImage);
        deleteImage = findViewById(R.id.deleteImage);
        textTagHint = findViewById(R.id.text_tags_hint);
        textIngredientHint = findViewById(R.id.text_ingredients_hint);
        textStepHint = findViewById(R.id.text_step_hint);

        //Set Group Image
        group_change_image.setVisibility(View.GONE);

        //Add Image
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);

            }
        });

        //Change Image
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
            }
        });

        //Delete Image
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeImage.setImageDrawable(null);
                addImage.setVisibility(View.VISIBLE);
                group_change_image.setVisibility(View.GONE);
            }
        });

        //Create id recipe
        db.collection("recipes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                int count = 0;
                for(DocumentSnapshot snapshot:snapshotList)
                {
                    Recipe countRecipe = snapshot.toObject(Recipe.class);
                    if(count<countRecipe.id)
                        count = countRecipe.id;
                }
                currentRecipeQuantity = count + 1;
            }
        });

        //Create data sample for testing
        recipe = new RecipeDetail();
        ArrayList<CookingStep> cookingSteps = new ArrayList<CookingStep>();
        ArrayList<ExtendedIngredient> ingredients = new ArrayList<>();
        tagList = new ArrayList<>();

        //LoadData
        if(paramRecipe != null){
            imageURI = Uri.parse(paramRecipe.image);
            Picasso.get().load(paramRecipe.image).into(recipeImage);
            addImage.setVisibility(View.GONE);
            group_change_image.setVisibility(View.VISIBLE);
            recipeName.setText(paramRecipe.title);
            readyTime.setText(String.valueOf(paramRecipe.readyInMinutes));
            summary.setText(paramRecipe.summary);
            serving.setText(String.valueOf(paramRecipe.servings));
            price.setText(String.valueOf(paramRecipe.pricePerServing));

            for (String tagName : paramRecipe.dishTypes){
                Tag data = new Tag(tagName, false);
                tagList.add(data);
            }
            StringBuilder prepareInstruction= new StringBuilder("Prepare these ingredients: \n");
            for (ExtendedIngredient ingredient:paramRecipe.extendedIngredients)
            {
                prepareInstruction.append(" - ").append(ingredient.original).append("\n");
                ingredients.add(ingredient);
            }
            //cookingSteps.add(new CookingStep(prepareInstruction.toString(), "Prepare", 0));
            AnalyzedInstruction instruction = paramRecipe.analyzedInstructions.get(0);

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

            }
        }
        recipe.ingredientsList = ingredients;
        recipe.CreateStepList(cookingSteps);
        //End data sample creating

        linearLayoutManagerForTag = new LinearLayoutManager(RecipeCreater.this, LinearLayoutManager.HORIZONTAL, false);
        tagAdapter = new TagAdapter(true);
        tagAdapter.setData(tagList);
        tagListView.setLayoutManager(linearLayoutManagerForTag);
        tagListView.setAdapter(tagAdapter);

        linearLayoutManagerForIngredients = new LinearLayoutManager(RecipeCreater.this, LinearLayoutManager.VERTICAL, false);
        ingredientAdapter = new IngredientAdapter(RecipeCreater.this, recipe.ingredientsList);
        ingredientsList.setLayoutManager(linearLayoutManagerForIngredients);
        ingredientsList.setAdapter(ingredientAdapter);

        linearLayoutManagerForStep = new LinearLayoutManager(RecipeCreater.this, LinearLayoutManager.VERTICAL, false);
        recipeCreaterAdapter = new RecipeCreaterAdapter(RecipeCreater.this, recipe.cookingStepsList);
        stepList.setLayoutManager(linearLayoutManagerForStep);
        stepList.setAdapter(recipeCreaterAdapter);


        //Add Tag
        addTag.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_addtag, null);

            // Set the content view of the popup window.
            popupWindow = new PopupWindow(popupView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

            // Set the focusable property of the popup window to true.
            popupWindow.setFocusable(true);

            // Set the outside touchable property of the popup window to true.
            popupWindow.setOutsideTouchable(false);
            spinner = popupView.findViewById(R.id.tagName);
            buttonSave = popupView.findViewById(R.id.save_edit);
            buttonCancel = popupView.findViewById(R.id.cancel_edit);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RecipeCreater.this, R.array.tag, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(adapter);
            buttonSave.setOnClickListener(view1 -> {
                boolean hasAlreadyBeen = false;
                for (Tag tag : tagList) {
                    if (tag.getName().equals(spinner.getSelectedItem().toString()))
                        hasAlreadyBeen = true;
                }
                if (!hasAlreadyBeen) {
                    tagList.add(new Tag(spinner.getSelectedItem().toString(), false));
                    tagAdapter.notifyItemInserted(tagList.size() - 1);
                    popupWindow.dismiss();
                } else {
                    Toast.makeText(RecipeCreater.this, "This tag is already added.", Toast.LENGTH_SHORT).show();
                }
            });
            buttonCancel.setOnClickListener(view12 -> {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            });
            if (!popupWindow.isShowing())
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            RelativeLayout around = popupView.findViewById(R.id.around_popup_tag);
            around.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });

        });

        //Add Ingredients
        buttonAddIngredients.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_addingredients, null);

            // Set the content view of the popup window.
            popupWindow = new PopupWindow(popupView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

            // Set the focusable property of the popup window to true.
            popupWindow.setFocusable(true);

            // Set the outside touchable property of the popup window to true.
            popupWindow.setOutsideTouchable(false);

            unit = popupView.findViewById(R.id.unit);
            quantity = popupView.findViewById(R.id.quantity);
            ingredient = popupView.findViewById(R.id.ingredient);
            buttonSave = popupView.findViewById(R.id.save_edit);
            buttonCancel = popupView.findViewById(R.id.cancel_edit);
            ingredient.requestFocus();
            buttonSave.setOnClickListener(view1 -> {
                if (ingredient.getText().toString().equals("") || unit.getText().toString().equals("")) {
                    Toast.makeText(RecipeCreater.this, "Please input description.", Toast.LENGTH_SHORT).show();
                } else {
                    ExtendedIngredient newIngredient = new ExtendedIngredient();
                    newIngredient.unit=unit.getText().toString();
                    newIngredient.amount=Double.parseDouble(quantity.getText().toString());
                    newIngredient.name=ingredient.getText().toString();
                    newIngredient.original= quantity.getText().toString()+" "+unit.getText().toString()+" of "+ingredient.getText().toString();
                    recipe.ingredientsList.add(newIngredient);
                    ingredientAdapter.notifyItemInserted(recipe.ingredientsList.size() - 1);
                    popupWindow.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view12 -> {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            });
            if (!popupWindow.isShowing())
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            RelativeLayout around = popupView.findViewById(R.id.around_popup_ingredient);
            around.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });

        });

        //Add Step
        buttonAddStep.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_addstep, null);

            // Set the content view of the popup window.
            popupWindow = new PopupWindow(popupView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

            // Set the focusable property of the popup window to true.
            popupWindow.setFocusable(true);

            // Set the outside touchable property of the popup window to true.
            popupWindow.setOutsideTouchable(false);
            spinner = popupView.findViewById(R.id.spinner);
            buttonSave = popupView.findViewById(R.id.save_edit);
            buttonCancel = popupView.findViewById(R.id.cancel_edit);
            description = popupView.findViewById(R.id.editTextTextMultiLine);
            time = popupView.findViewById(R.id.editTextNumber);
            LinearLayout group_time = popupView.findViewById(R.id.group_time);
            group_time.setVisibility(View.GONE);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RecipeCreater.this, R.array.step_type, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(adapter);
            buttonSave.setOnClickListener(view1 -> {
                if (description.getText().toString().equals("")) {
                    Toast.makeText(RecipeCreater.this, "Please input description.", Toast.LENGTH_SHORT).show();
                } else {
                    int timer = 0;
                    if (!time.getText().toString().equals(""))
                        timer = Integer.parseInt(time.getText().toString());
                    cookingSteps.add(new CookingStep(description.getText().toString(), spinner.getSelectedItem().toString(), timer));
                    recipeCreaterAdapter.notifyItemInserted(cookingSteps.size() - 1);
                    popupWindow.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view12 -> {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    time.setEnabled(spinner.getSelectedItem().toString().equals("Timer"));
                    if(spinner.getSelectedItem().toString().equals("Timer")){
                        group_time.setVisibility(View.VISIBLE);
                    }
                    else {
                        group_time.setVisibility(View.GONE);
                        time.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    time.setEnabled(spinner.getSelectedItem().toString().equals("Timer"));
                    if(spinner.getSelectedItem().toString().equals("Timer")){
                        group_time.setVisibility(View.VISIBLE);
                    }
                    else {
                        group_time.setVisibility(View.GONE);
                        time.setText("");
                    }
                }
            });
            if (!popupWindow.isShowing())
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            RelativeLayout around = popupView.findViewById(R.id.around_popup_step);
            around.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });
        });

        //Save Recipe
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder notice = new StringBuilder();
                if (tagList.size() == 0)
                    notice.append("- Your recipe tag(s) is empty.\n");
                if (recipe.ingredientsList.size() == 0)
                    notice.append("- Your ingredient list is empty.\n");
                if (recipe.cookingStepsList.size() == 0)
                    notice.append("- Your step list is empty.\n");
                if (summary.getText().toString().equals(""))
                    notice.append("- Your summary is empty.\n");
                if (imageURI == null)
                    notice.append("- Your haven't choose image yet.\n");
                if (recipeName.getText().toString().equals(""))
                    notice.append("- Your recipe name is empty.\n");
                int totalTime = 0;
                for (int i = 0; i <= recipe.cookingStepsList.size() - 1; i++)
                    totalTime += recipe.cookingStepsList.get(i).timerBySecond;
                if (!readyTime.getText().toString().equals("") && Integer.parseInt(readyTime.getText().toString()) * 60 < totalTime)
                    Toast.makeText(RecipeCreater.this, "Ready time must bigger than the total time taken by every single step.\nYour total time is "+totalTime%60+" minute(s) and "+(totalTime-(totalTime%60)*60)+" second(s)", Toast.LENGTH_SHORT).show();
                else if (notice.length() > 5) {
                    Toast.makeText(RecipeCreater.this, notice.toString()+"\nPlease fill in all required information before saving.", Toast.LENGTH_SHORT).show();
                } else {
                    newRecipe = new Recipe();
                    newRecipe.title = recipeName.getText().toString();
                    newRecipe.extendedIngredients = new ArrayList<>();
                    newRecipe.analyzedInstructions = new ArrayList<>();
                    newRecipe.analyzedInstructions.add(new AnalyzedInstruction());
                    newRecipe.analyzedInstructions.get(0).steps = new ArrayList<>();
                    for (int i = 0; i <= recipe.cookingStepsList.size() - 1; i++) {
                        newRecipe.analyzedInstructions.get(0).steps.add(new Step(i, recipe.cookingStepsList.get(i).stepIntruction, new Length(recipe.cookingStepsList.get(i).timerBySecond, "second")));
                    }
                    for (int i = 0; i <= recipe.ingredientsList.size() - 1; i++) {
                        newRecipe.extendedIngredients.add(recipe.ingredientsList.get(i));
                    }
                    newRecipe.dishTypes = new ArrayList<>();
                    for (int i = 0; i <= tagList.size() - 1; i++) {
                        newRecipe.dishTypes.add(tagList.get(i).getName());
                    }
                    newRecipe.aggregateLikes = 0;
                    if (!readyTime.getText().toString().equals(""))
                        newRecipe.readyInMinutes = Integer.parseInt(readyTime.getText().toString());
                    else
                        newRecipe.readyInMinutes = 0;

                    if (!price.getText().toString().equals(""))
                        newRecipe.pricePerServing = Double.parseDouble(price.getText().toString());
                    else
                        newRecipe.pricePerServing = 0;

                    if (!serving.getText().toString().equals(""))
                        newRecipe.servings = Integer.parseInt(serving.getText().toString());
                    else
                        newRecipe.servings = 0;
                    newRecipe.userID = user.getUid();
                    newRecipe.userName = user.getDisplayName();
                    newRecipe.id = currentRecipeQuantity;
                    newRecipe.summary = summary.getText().toString();
                    String newPath = imageURI.toString();
                    if(paramRecipe == null || paramRecipe!= null && newPath != paramRecipe.image ){
                        storageRef = storage.getReference("images/" + newRecipe.id);
                        storageRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        newRecipe.image = uri.toString();
                                        db.collection("recipes").add(newRecipe).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(RecipeCreater.this, "Save recipe successfully!", Toast.LENGTH_SHORT).show();
                                                if(paramRecipe != null){
                                                    DeleteRecipe();
                                                }
                                                else {
                                                    ShowSuccess();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowFailure();
                                Toast.makeText(RecipeCreater.this, "Failed to save recipe.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        newRecipe.image = paramRecipe.image;
                        db.collection("recipes").add(newRecipe).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(RecipeCreater.this, "Save recipe successfully!", Toast.LENGTH_SHORT).show();
                                if(paramRecipe != null){
                                    DeleteRecipe();
                                }
                                else {
                                    ShowSuccess();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowFailure();
                                Toast.makeText(RecipeCreater.this, "Failed to save recipe.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                assert data != null;
                imageURI = data.getData();
                recipeImage.setImageURI(data.getData());
                addImage.setVisibility(View.GONE);
                group_change_image.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(paramRecipe != null){
            if(newRecipe == null){
                newRecipe = paramRecipe;
            }
            Intent intent = new Intent(RecipeCreater.this, MainActivity.class);
            intent.putExtra("EDIT_RECIPE", newRecipe);
            intent.putExtra("BEFORE_SCREEN", before_screen);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(RecipeCreater.this, MainActivity.class);
            intent.putExtra("CREATE_RECIPE", true);
            startActivity(intent);
        }
    }

    public void DeleteRecipe(){
        db.collection("recipes")
                .whereEqualTo("id", paramRecipe.id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                db.collection("recipes").document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        ShowSuccessAndBack();
                                    }
                                });
                                break;
                            }
                        }
                        else {
                            Toast.makeText(RecipeCreater.this, "Khong co du lieu phu hop", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void ShowSuccessAndBack (){
        View view = LayoutInflater.from(RecipeCreater.this).inflate(R.layout.dialog_success,null);

        TextView OK = view.findViewById(R.id.OK);
        TextView description = view.findViewById(R.id.textDesCription);

        description.setText("Save recipe successfully!");

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RecipeCreater.this);
        builder.setView(view);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();
            }
        });
    }
    public void ShowSuccess (){
        View view = LayoutInflater.from(RecipeCreater.this).inflate(R.layout.dialog_success,null);

        TextView OK = view.findViewById(R.id.OK);
        TextView description = view.findViewById(R.id.textDesCription);

        description.setText("Save recipe successfully!");

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RecipeCreater.this);
        builder.setView(view);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public void ShowFailure (){
        View view = LayoutInflater.from(RecipeCreater.this).inflate(R.layout.dialog_fail,null);

        TextView OK = view.findViewById(R.id.OK);
        TextView description = view.findViewById(R.id.textDesCription);

        description.setText("Failed to save recipe!");

        final AlertDialog.Builder builder = new AlertDialog.Builder(RecipeCreater.this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}