package com.example.cookingrecipesmanager;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cookingrecipesmanager.databinding.FragmentRecipeDetailsBinding;
import com.example.cookingrecipesmanager.home.Adapter.TagAdapter;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailsFragment extends Fragment {

    private boolean bSaved = false;
    private GestureDetectorCompat Gesture;

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
        TransitionInflater trans = TransitionInflater.from(requireContext());
        setEnterTransition(trans.inflateTransition(R.transition.slide_right));
        setExitTransition(trans.inflateTransition(R.transition.slide_right));
        RecipeDetailsFragment fragment = this;
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
            }
        });
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

        ArrayList<Tag> tags = new ArrayList<>(Arrays.asList(
                new Tag("Dessert", false),
                new Tag("Cold", false),
                new Tag("Fruit", false)
        ));
        binding.content.listTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TagAdapter adapter = new TagAdapter();
        adapter.setData(tags);
        binding.content.listTags.setAdapter(adapter);

        binding.content.listIngredient.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.content.listIngredient.setAdapter(new IngredientAdapter(ingredients));

        binding.content.btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton btn = (ImageButton) v;
                bSaved = !bSaved;
                btn.setImageResource(bSaved ? R.drawable.baseline_bookmark_added_24 : R.drawable.baseline_bookmark_add_24);
                btn.getDrawable().setTint(bSaved ? 0xFFDDDD00 : 0xFFAAAAAA);
            }
        });

        binding.appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                binding.appBarImage.setTranslationY(-verticalOffset/2);
                float baseRadius = getResources().getDisplayMetrics().density * 12.f;
                binding.appbarCard.setRadius(baseRadius + verticalOffset/16);
                float hPadding = (8 * getResources().getDisplayMetrics().density + verticalOffset/16);
                hPadding = hPadding < 0 ? 0 : hPadding;
                binding.toolbarParent.setPadding((int)hPadding, (int) (8 * getResources().getDisplayMetrics().density), (int)hPadding, 0);
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}