package com.example.cookingrecipesmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.databinding.ActivityMainBinding;
import com.example.cookingrecipesmanager.recipetracker.RecipeStepPreview;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

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
                Boolean isCreateRecipe = (Boolean) getIntent().getSerializableExtra("CREATE_RECIPE");
                if (note != null)
                {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.layoutFragment, RecipeDetailsFragment.newInstance(note))
                            .commitNow();
                }
                else if(isCreateRecipe != null){
                    binding.bottomNavigationView.setSelectedItemId(R.id.library);
                    replaceFragment(new RecipeLibraryFragment(), "Library");
                }
                else {
                    binding.bottomNavigationView.setSelectedItemId(R.id.homepage);
                    replaceFragment(new HomeFragment(),"Home");
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
        //super.onBackPressed();
        String fragInstance = getSupportFragmentManager().findFragmentById(R.id.layoutFragment).getClass().getSimpleName();

        if(fragInstance != null && fragInstance.equals("RecipeDetailsFragment")){
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.layoutFragment)).commitNow();

            if(getSupportFragmentManager().findFragmentById(R.id.layoutFragment) == null){
                replaceFragment(new HomeFragment(),"Home");
            }
        }
        else{
            finish();
        }
    }
}