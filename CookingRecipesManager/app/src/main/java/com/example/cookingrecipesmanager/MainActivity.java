package com.example.cookingrecipesmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.cookingrecipesmanager.Common.Constants;
import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.databinding.ActivityMainBinding;
import com.example.cookingrecipesmanager.recipetracker.RecipeStepPreview;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Recipe recipeEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() == null)
            {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return;
            }
            else{
                Recipe note = (Recipe) getIntent().getSerializableExtra("RECIPE");
                Boolean backToHome = (Boolean) getIntent().getSerializableExtra("BACK_HOME");
                Recipe recipeEdit = (Recipe) getIntent().getSerializableExtra("EDIT_RECIPE");
                Recipe recipeUser = (Recipe) getIntent().getSerializableExtra("USER_RECIPE");
                Boolean backToUser = (Boolean) getIntent().getSerializableExtra("BACK_USER");
                Boolean isCreateRecipe = (Boolean) getIntent().getSerializableExtra("CREATE_RECIPE");
                Boolean isDeleteRecipe = (Boolean) getIntent().getSerializableExtra("DELETE_RECIPE");
                String before_screen = (String) getIntent().getSerializableExtra("BEFORE_SCREEN");
                String id_user_recipe = (String) getIntent().getSerializableExtra("ID_USER_RECIPE");
                if (note != null)
                {
                    binding.bottomNavigationView.setSelectedItemId(R.id.homepage);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.layoutFragment, RecipeDetailsFragment.newInstance(note, Constants.HOME_NAME))
                            .commitNow();
                }
                else if (backToHome != null){
                    binding.bottomNavigationView.setSelectedItemId(R.id.homepage);
                    replaceFragment(new HomeFragment(), "Home");
                }
                else if(recipeUser != null){
                    binding.bottomNavigationView.setSelectedItemId(R.id.user);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.layoutFragment, RecipeDetailsFragment.newInstance(recipeUser, Constants.USER_NAME))
                            .commitNow();

                }
                else if (backToUser != null){
                    binding.bottomNavigationView.setSelectedItemId(R.id.user);
                    replaceFragment(new UserFragment(),"User");
                }
                else if(recipeEdit != null){
                    if(recipeEdit.title != null){
                        if(before_screen.equals( Constants.LIBRARY_NAME)){
                            binding.bottomNavigationView.setSelectedItemId(R.id.library);
                        }
                        else if (before_screen.equals(Constants.USER_NAME)){
                            binding.bottomNavigationView.setSelectedItemId(R.id.user);
                        }
                        else {
                            binding.bottomNavigationView.setSelectedItemId(R.id.homepage);
                        }
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.layoutFragment, RecipeDetailsFragment.newInstance(recipeEdit,before_screen))
                                .commitNow();

                    }
                }
                else if(isCreateRecipe != null){
                    binding.bottomNavigationView.setSelectedItemId(R.id.library);
                    replaceFragment(new RecipeLibraryFragment(), "Library");
                }
                else if(isDeleteRecipe != null){
                    if(before_screen.equals( Constants.LIBRARY_NAME)){
                        binding.bottomNavigationView.setSelectedItemId(R.id.library);
                        replaceFragment(new RecipeLibraryFragment(), "Library");
                    }
                    else if (before_screen.equals(Constants.USER_NAME)){
                        binding.bottomNavigationView.setSelectedItemId(R.id.user);
                        Intent intent = new Intent(this, UserRecipe.class);
                        intent.putExtra("userID", id_user_recipe);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        binding.bottomNavigationView.setSelectedItemId(R.id.homepage);
                        replaceFragment(new HomeFragment(), "Home");
                    }
                }
                else {
                    binding.bottomNavigationView.setSelectedItemId(R.id.homepage);
                    replaceFragment(new HomeFragment(), "Home");
                }
            }
        }
        catch (Exception e)
        {

//            replaceFragment(new HomeFragment());
        }
//        replaceFragment(new HomeFragment());


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.homepage:
                    replaceFragment(new HomeFragment(), "Home");
                    break;
                case R.id.library:
                    replaceFragment(new RecipeLibraryFragment(), "Library");
                    break;
                case R.id.user:
                    replaceFragment(new UserFragment(),"User");
                    break;
                case R.id.setting:
                    replaceFragment(new SettingFragment(), "Setting");
                    break;
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment, String tag)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layoutFragment,fragment, tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String fragInstance = getSupportFragmentManager().findFragmentById(R.id.layoutFragment).getClass().getSimpleName();

        if(fragInstance != null && fragInstance.equals("RecipeDetailsFragment")){
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.layoutFragment)).commitNow();

            if(getSupportFragmentManager().findFragmentById(R.id.layoutFragment) == null){
                if(binding.bottomNavigationView.getSelectedItemId() == R.id.library){
                    replaceFragment(new RecipeLibraryFragment(), "Library");
                }
                else if(binding.bottomNavigationView.getSelectedItemId() == R.id.user){
                    finish();
                }
                else{
                    binding.bottomNavigationView.setSelectedItemId(R.id.homepage);
                    replaceFragment(new HomeFragment(), "Home");
                }
            }
        }
        else{
            finish();
        }
    }
}