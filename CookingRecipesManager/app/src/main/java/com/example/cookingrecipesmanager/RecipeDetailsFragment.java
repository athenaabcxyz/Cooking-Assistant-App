package com.example.cookingrecipesmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookingrecipesmanager.database.Model.User;
import com.example.cookingrecipesmanager.database.Model.ExtendedIngredient;
import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.databinding.FragmentRecipeDetailsBinding;
import com.example.cookingrecipesmanager.details.DetailsTagAdapter;
import com.example.cookingrecipesmanager.recipetracker.RecipeStepPreview;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailsFragment extends Fragment {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid;
    public String fragmentType = "Detail";
    {
        assert user != null;
        uid = user.getUid();
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean bSaved = false;
    private boolean bLiked = false;
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

            return item.original;
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

    protected void UpdateLikeBtn()
    {
        int color = (bLiked ? 0xffff8080 : 0xffaaaaaa);
        binding.content.btnLike.setIconTint(ColorStateList.valueOf(color));

        // set like count
        db.collection("recipes").whereEqualTo("id", mParamRecipe.id).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Recipe recipe = queryDocumentSnapshots.getDocuments().get(0).toObject(Recipe.class);
                        if (recipe != null)
                        {
                            binding.content.btnLike.setText(String.format("%d", recipe.aggregateLikes));
                        }
                    }
                });
    }

    protected void UpdateSaveBtn(boolean bSaved)
    {
        int color = (bSaved ? 0xffdddd40 : 0xffaaaaaa);
        binding.content.btnSave.setIconTint(ColorStateList.valueOf(color));
    }

    // whether the current user created this item
    protected boolean GetIsOwner()
    {
        final String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return currentUID.equals(mParamRecipe.userID);
    }

    // Set visibility for buttons meant only for the owner of this recipe
    protected void InitEditButtons()
    {
        if (!GetIsOwner())
        {
            binding.content.btnEdit.setVisibility(View.GONE);
            binding.content.btnRemove.setVisibility(View.GONE);
        }

        binding.content.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallEditor();
            }
        });
        binding.content.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallRemove();
            }
        });
    }

    // Opens editor activity passing recipe id
    protected void CallEditor()
    {
//        Intent intentEdit = new Intent(requireContext(), RecipeCreater.class); // ?
//        intentEdit.putExtra("RECIPE_DATA", (Serializable)mParamRecipe);
//        intentEdit.putExtra("RECIPE_ID", mParamRecipe.id);
        Toast.makeText(requireContext(), "editing not implemented", Toast.LENGTH_SHORT).show();
        //TODO: Opening editor activity
    }

    protected void RemoveCurrentItem()
    {
        RecipeDetailsFragment fragment = this;

        Log.d("DETAILS", String.format("removing recipe %d", mParamRecipe.id));
        db.collection("recipes").whereEqualTo("id", mParamRecipe.id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        queryDocumentSnapshots.forEach(new Consumer<QueryDocumentSnapshot>() {
                            @Override
                            public void accept(QueryDocumentSnapshot queryDocumentSnapshot) {
                                queryDocumentSnapshot.getReference().delete();
                            }
                        });
                    }
                });
        ((MainActivity)requireActivity()).getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();

        // TODO: refresh library
    }

    // Removes current item from the database (confirmation dialog)
    protected void CallRemove()
    {
//        Toast.makeText(requireContext(), "removing not implemented", Toast.LENGTH_SHORT).show();

        //      consider adding 'hidden' flag (ie. trash bin)?
        new AlertDialog.Builder(requireContext())
                .setTitle("Removing")
                .setMessage("Do you want to remove this recipe")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoveCurrentItem();
                    }
                })
                .setNegativeButton("no", null)
                .show();
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
        // back navigation is handled by parent activity

//        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                requireActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
//            }
//        });
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
        if (mParamRecipe.userName != null && mParamRecipe.userName.length() > 0)
        {
            binding.content.authorName.setText(mParamRecipe.userName);
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
        DetailsTagAdapter adapter = new DetailsTagAdapter();
        adapter.setData(tags);
        binding.content.listTags.setAdapter(adapter);

        binding.content.listIngredient.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.content.listIngredient.setAdapter(new IngredientAdapter(mParamRecipe.extendedIngredients));

        bSaved = false;
        db.collection("Users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               User user = documentSnapshot.toObject(User.class);
               assert user != null;
               if(user.savedRecipes!=null) {
                   for (int i = 0; i <= user.savedRecipes.size()-1; i++) {
                       if (user.savedRecipes.get(i) == mParamRecipe.id) {
                           bSaved = true;
                       }
                   }
               }
                UpdateSaveBtn(bSaved);
            }
        });

        binding.content.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").whereEqualTo("uid", uid).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                                for(DocumentSnapshot snapshot:snapshotList)
                                {
                                    User user = snapshot.toObject(User.class);
                                    assert user != null;
                                    boolean bRemovedSavedItem = false;
                                    // check if current item is in saved list, if so then remove it and set state
                                    if(user.savedRecipes!=null) {
                                        for (int i = 0; i <= user.savedRecipes.size()-1; i++) {
                                            if (user.savedRecipes.get(i) == mParamRecipe.id) {
                                                db.collection("Users").document(uid).update("savedRecipes", FieldValue.arrayRemove(mParamRecipe.id));
                                                bSaved = false;
                                                bRemovedSavedItem = true;
                                            }
                                        }
                                    }
                                    // current item not saved, adding it
                                    if (!bRemovedSavedItem) {
                                        db.collection("Users").document(uid).update("savedRecipes", FieldValue.arrayUnion(mParamRecipe.id));
                                        bSaved = true;
                                    }
                                }

                                UpdateSaveBtn(bSaved);
                            }
                        });
            }
        });

        UpdateLikeBtn();
        // TODO: create list of liked items for user
        binding.content.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bLiked = !bLiked;
                UpdateLikeBtn();
            }
        });

        InitEditButtons();

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

        binding.viewStepRecipe.hide();
        binding.scrollContainer.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY != 0)
                {
                    binding.viewStepRecipe.show();
                }
                else {
                    binding.viewStepRecipe.hide();
                }
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
        binding.content.btnCookStatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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