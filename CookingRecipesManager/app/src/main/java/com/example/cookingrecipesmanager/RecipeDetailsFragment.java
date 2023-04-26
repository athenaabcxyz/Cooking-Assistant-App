package com.example.cookingrecipesmanager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cookingrecipesmanager.databinding.FragmentRecipeDetailsBinding;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailsFragment extends Fragment {

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTextView;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.ingredient_name);
        }

        public TextView getViewIngredientName()
        {
            return mTextView;
        }
    }

    public class IngredientAdapter extends RecyclerView.Adapter<IngredientViewHolder>
    {
        private ArrayList<String> mData;

        public IngredientAdapter(ArrayList<String> data)
        {
            mData = data;
            mData.trimToSize();
        }

        @NonNull
        @Override
        public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new IngredientViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
            holder.mTextView.setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private FragmentRecipeDetailsBinding binding;

    private CookingNote mParamRecipe;

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailsFragment newInstance(CookingNote recipe) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("RECIPE", recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamRecipe = getArguments().getParcelable("RECIPE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false);
        binding.appBarImage.setImageDrawable(getResources().getDrawable(R.drawable.img_recipe1, getContext().getTheme()));

        binding.toolbar.setTitle(mParamRecipe.getTitle());
        binding.content.authorName.setText(mParamRecipe.getAuthor());
        binding.content.textDescription.setText(mParamRecipe.getDescription());

        ArrayList<String> ingredients = new ArrayList<String>(Arrays.asList(requireContext().getResources().getStringArray(R.array.sample_recipe_ingredients)));

        binding.content.listIngredient.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.content.listIngredient.setAdapter(new IngredientAdapter(ingredients));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}