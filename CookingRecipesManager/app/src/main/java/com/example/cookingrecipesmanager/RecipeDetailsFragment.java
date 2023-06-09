package com.example.cookingrecipesmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cookingrecipesmanager.database.Model.Ingredient;
import com.example.cookingrecipesmanager.database.Model.User;
import com.example.cookingrecipesmanager.database.Model.ExtendedIngredient;
import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.databinding.FragmentRecipeDetailsBinding;
import com.example.cookingrecipesmanager.home.Adapter.TagAdapter;
import com.example.cookingrecipesmanager.recipetracker.RecipeStepPreview;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailsFragment extends Fragment {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid;

    {
        assert user != null;
        uid = user.getUid();
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        private ArrayList<ExtendedIngredient> mData;

        public IngredientAdapter(ArrayList<ExtendedIngredient> data)
        {
            mData = data;
            mData.trimToSize();
        }

        @NonNull
        @Override
        public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new IngredientViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false));
        }

        protected String formatIngredient(ExtendedIngredient item)
        {
            if (item.name != null)
            {
                item.name = item.name.substring(0, 1).toUpperCase() + item.name.substring(1);
            }
            String funit = "";
            if (item.unit != null && item.unit.length() > 0)
            {
                funit = String.format(" %s", item.unit);
            }
            DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMaximumFractionDigits(3);
            return String.format("%s%s %s", df.format(item.amount), funit, item.name);
        }

        @Override
        public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
            holder.mTextView.setText(formatIngredient(mData.get(position)));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private FragmentRecipeDetailsBinding binding;

    private Recipe mParamRecipe;

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailsFragment newInstance(Recipe recipe) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("RECIPE", recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParamRecipe = (Recipe) getArguments().getSerializable("RECIPE");
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
        Picasso.get().load(mParamRecipe.image).into( binding.appBarImage);
        
        binding.content.authorName.setText("Foodista");
        binding.content.textDescription.setText(Jsoup.parse(mParamRecipe.summary).text());
//        binding.appBarImage.setImageDrawable(getResources().getDrawable(R.drawable.img_recipe1, getContext().getTheme()));
        Picasso.get().load(mParamRecipe.image).into(binding.appBarImage);

//        binding.toolbar.setTitle(mParamRecipe.title);
        binding.toolbarTitleEx.setText(mParamRecipe.title);
        binding.toolbarTitleEx.setSelected(true);
        binding.toolbarTitle.setText(mParamRecipe.title);
        if (mParamRecipe.userID != null && mParamRecipe.userID.length() > 0)
        {
            binding.content.authorName.setText(mParamRecipe.userID);
        }
        else
        {
            binding.content.authorName.setText(requireContext().getResources().getString(R.string.username_anon));
        }
        binding.content.textDescription.setText(Html.fromHtml(mParamRecipe.summary, 0));

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(2);

        binding.content.valueTime.setText(mParamRecipe.readyInMinutes + " mins");
        binding.content.valueCount.setText(String.format("%d", mParamRecipe.servings));
        binding.content.valueCost.setText(String.format("$%s", df.format(mParamRecipe.pricePerServing)));

        ArrayList<String> ingredients = new ArrayList<String>();
        for(int i =0; i<=mParamRecipe.extendedIngredients.size()-1;i++)
        {
            ingredients.add(mParamRecipe.extendedIngredients.get(i).original);
        }

        ArrayList<Tag> tags = new ArrayList<>();
        for(int i =0; i<=mParamRecipe.dishTypes.size()-1;i++)
        {
            tags.add(new Tag(mParamRecipe.dishTypes.get(i), false));
        }
        binding.content.listTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TagAdapter adapter = new TagAdapter();
        adapter.setData(tags);
        binding.content.listTags.setAdapter(adapter);

        binding.content.listIngredient.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.content.listIngredient.setAdapter(new IngredientAdapter(mParamRecipe.extendedIngredients));

        db.collection("Users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               User user = documentSnapshot.toObject(User.class);
               assert user != null;
               if(user.savedRecipes!=null) {
                   for (int i = 0; i <= user.savedRecipes.size()-1; i++) {
                       if (user.savedRecipes.get(i) == mParamRecipe.id) {
                           bSaved = true;
                           binding.content.btnBookmark.setImageResource(R.drawable.baseline_bookmark_added_24);
                           binding.content.btnBookmark.getDrawable().setTint(0xFFDDDD00);
                           return;
                       }
                   }
                   binding.content.btnBookmark.setImageResource(R.drawable.baseline_bookmark_add_24);
                   binding.content.btnBookmark.getDrawable().setTint(0xFFAAAAAA);
                   bSaved = false;
               }
               else {
                   binding.content.btnBookmark.setImageResource(R.drawable.baseline_bookmark_add_24);
                   binding.content.btnBookmark.getDrawable().setTint(0xFFAAAAAA);
                   bSaved = false;
               }
            }
        });


        binding.content.btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton btn = (ImageButton) v;
                bSaved = !bSaved;
                btn.setImageResource(bSaved ? R.drawable.baseline_bookmark_added_24 : R.drawable.baseline_bookmark_add_24);
                btn.getDrawable().setTint(bSaved ? 0xFFDDDD00 : 0xFFAAAAAA);
                db.collection("Users").whereEqualTo("uid", uid).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                                for(DocumentSnapshot snapshot:snapshotList)
                                {
                                    User user = snapshot.toObject(User.class);
                                    assert user != null;
                                    if(user.savedRecipes!=null) {
                                        for (int i = 0; i <= user.savedRecipes.size()-1; i++) {
                                            if (user.savedRecipes.get(i) == mParamRecipe.id) {
                                                db.collection("Users").document(uid).update("savedRecipes", FieldValue.arrayRemove(mParamRecipe.id));
                                                return;
                                            }

                                        }
                                        db.collection("Users").document(uid).update("savedRecipes", FieldValue.arrayUnion(mParamRecipe.id));
                                    }
                                    else
                                    {
                                        db.collection("Users").document(uid).update("savedRecipes", FieldValue.arrayUnion(mParamRecipe.id));
                                    }
                                }


                            }
                        });
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

                float titleAlpha = 1.f - (float)Math.abs(verticalOffset) / 200;
                titleAlpha = Math.max(0.f, Math.min(1.f, titleAlpha));
                binding.toolbarTitleEx.setAlpha(titleAlpha);
                binding.toolbarTitleEx.setSelected(titleAlpha == 1.f);
                binding.toolbarTitle.setAlpha(1.f - titleAlpha);
            }
        });

        binding.viewStepRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecipeStepPreview.class);
                intent.putExtra("RECIPE", (Serializable) mParamRecipe);
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}