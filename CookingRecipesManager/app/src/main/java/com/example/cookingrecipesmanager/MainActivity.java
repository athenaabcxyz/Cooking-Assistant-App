package com.example.cookingrecipesmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
            Recipe note = (Recipe) getIntent().getSerializableExtra("RECIPE");
            if (note != null)
            {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.layoutFragment, RecipeDetailsFragment.newInstance(note))
                        .commitNow();
            }
            else {

                replaceFragment(new HomeFragment());
            }
        }
        catch (Exception e)
        {
            replaceFragment(new HomeFragment());
        }
//        replaceFragment(new HomeFragment());
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.homepage:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.library:
                    replaceFragment(new RecipeLibraryFragment());
                    break;
                case R.id.setting:
                    replaceFragment(new SettingFragment());
                    break;
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layoutFragment,fragment);
        fragmentTransaction.commit();
    }
}