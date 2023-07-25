package com.example.cookingrecipesmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.TransitionInflater;

import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.database.Model.User;
import com.example.cookingrecipesmanager.databinding.FragmentRecipeDetailsBinding;
import com.example.cookingrecipesmanager.details.DetailsTagAdapter;
import com.example.cookingrecipesmanager.details.IngredientAdapter;
import com.example.cookingrecipesmanager.recipetracker.RecipeStepPreview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    {
        assert user != null;
        uid = user.getUid();
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    State mState = new State();
    private boolean bSaved = false;
    private boolean bLiked = false;
    private GestureDetectorCompat Gesture;
    private FragmentRecipeDetailsBinding binding;
    private Recipe mParamRecipe;

    ;
    private String beforeScreen, idUserRecipe;

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailsFragment newInstance(Recipe recipe, String beforeScreen) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("RECIPE", recipe);
        args.putSerializable("BEFORE_SCREEN", beforeScreen);
        fragment.setArguments(args);
        return fragment;
    }

    protected void UpdateDescription() {
        binding.content.containerTextDescShort.setVisibility(mState.descriptionExpand ? View.GONE : View.VISIBLE);
        binding.content.textDescFull.setVisibility(mState.descriptionExpand ? View.VISIBLE : View.GONE);
    }

    protected void InitLikeBtn() {
        bLiked = false;
        db.collection("Users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                if (user.likedRecipes != null) {
                    for (long item : user.likedRecipes){
                        if(item == mParamRecipe.id){
                            bLiked = true;
                            break;
                        }
                    }
//                    user.likedRecipes.forEach(new Consumer<Integer>() {
//                        @Override
//                        public void accept(Integer integer) {
//                            if (integer == mParamRecipe.id) {
//                                bLiked = true;
//                            }
//                        }
//                    });
                }
                UpdateLikeBtn();
            }
        });

        binding.content.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").document(uid).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                User user = snapshot.toObject(User.class);
                                assert user != null;
                                boolean bRemovingItem = false;
                                // check if current item is in saved list, if so then remove it and set state
                                if (user.likedRecipes != null) {
                                    for (int i = 0; i <= user.likedRecipes.size() - 1; i++) {
                                        if (user.likedRecipes.get(i) == mParamRecipe.id) {
                                            db.collection("Users").document(uid).update("likedRecipes", FieldValue.arrayRemove(mParamRecipe.id));
                                            bLiked = false;
                                            bRemovingItem = true;
                                            db.collection("recipes").whereEqualTo("id", mParamRecipe.id).get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            if (queryDocumentSnapshots.isEmpty())
                                                                return;
                                                            String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                                                            db.collection("recipes").document(id).update(
                                                                    "aggregateLikes", FieldValue.increment(-1)
                                                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    UpdateLikeBtn();
                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    }
                                }
                                // current item not saved, adding it
                                if (!bRemovingItem) {
                                    db.collection("Users").document(uid).update("likedRecipes", FieldValue.arrayUnion(mParamRecipe.id));
                                    bLiked = true;
                                    db.collection("recipes").whereEqualTo("id", mParamRecipe.id).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if (queryDocumentSnapshots.isEmpty()) return;
                                                    String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                                                    db.collection("recipes").document(id).update(
                                                            "aggregateLikes", FieldValue.increment(1)
                                                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            UpdateLikeBtn();
                                                        }
                                                    });
                                                }
                                            });
                                }

                            }
                        });
            }
        });
    }

    protected void UpdateLikeBtn() {
        int color = (bLiked ? 0xffff8080 : 0xffaaaaaa);
        binding.content.btnLike.setIconTint(ColorStateList.valueOf(color));

        // set like count
        db.collection("recipes").whereEqualTo("id", mParamRecipe.id).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Recipe recipe = queryDocumentSnapshots.getDocuments().get(0).toObject(Recipe.class);
                        if (recipe != null) {
                            binding.content.btnLike.setText(String.format("%d", recipe.aggregateLikes));
                        }
                    }
                });
    }

    protected void UpdateSaveBtn(boolean bSaved) {
        int color = (bSaved ? 0xffdddd40 : 0xffaaaaaa);
        binding.content.btnSave.setIconTint(ColorStateList.valueOf(color));
    }

    // whether the current user created this item
    protected boolean GetIsOwner() {
        final String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return currentUID.equals(mParamRecipe.userID);
    }

    // Set visibility for buttons meant only for the owner of this recipe
    protected void InitEditButtons() {
        if (!GetIsOwner()) {
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
    protected void CallEditor() {
//        Intent intentEdit = new Intent(requireContext(), RecipeCreater.class); // ?
//        intentEdit.putExtra("RECIPE_DATA", (Serializable)mParamRecipe);
//        intentEdit.putExtra("RECIPE_ID", mParamRecipe.id);
        Toast.makeText(requireContext(), "editing not implemented", Toast.LENGTH_SHORT).show();
        //TODO: Opening editor activity
    }

    protected void RemoveCurrentItem() {
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
        ((MainActivity) requireActivity()).getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();

        // TODO: refresh library
    }

    // Removes current item from the database (confirmation dialog)
    protected void CallRemove() {
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
            beforeScreen = (String) getArguments().getSerializable("BEFORE_SCREEN");
        }
        TransitionInflater trans = TransitionInflater.from(requireContext());
        setEnterTransition(trans.inflateTransition(R.transition.slide_right));
        setExitTransition(trans.inflateTransition(R.transition.slide_right));
        RecipeDetailsFragment fragment = this;
        // back navigation is handled by parent activity

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
//                requireActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false);
        Picasso.get().load(mParamRecipe.image).into(binding.appBarImage);

        binding.content.authorName.setText("Foodista");
        binding.content.textDescription.setText(Jsoup.parse(mParamRecipe.summary).text());
//        binding.appBarImage.setImageDrawable(getResources().getDrawable(R.drawable.img_recipe1, getContext().getTheme()));
        Picasso.get().load(mParamRecipe.image).into(binding.appBarImage);

//        binding.toolbar.setTitle(mParamRecipe.title);
        binding.toolbarTitleEx.setText(mParamRecipe.title);
        binding.toolbarTitleEx.setSelected(true);
        binding.toolbarTitle.setText(mParamRecipe.title);
        if (mParamRecipe.userName != null && mParamRecipe.userName.length() > 0) {
            binding.content.authorName.setText(mParamRecipe.userName);
        } else {
            binding.content.authorName.setText(requireContext().getResources().getString(R.string.username_anon));
        }
        binding.content.textDescription.setText(Html.fromHtml(mParamRecipe.summary, 0));
        binding.content.textDescFull.setText(Html.fromHtml(mParamRecipe.summary, 0));

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(2);

        binding.content.valueTime.setText(mParamRecipe.readyInMinutes + " mins");
        binding.content.valueCount.setText(String.format("%d", mParamRecipe.servings));
        binding.content.valueCost.setText(String.format("$%s", df.format(mParamRecipe.pricePerServing)));

        ArrayList<String> ingredients = new ArrayList<String>();
        for (int i = 0; i <= mParamRecipe.extendedIngredients.size() - 1; i++) {
            ingredients.add(mParamRecipe.extendedIngredients.get(i).original);
        }

        ArrayList<Tag> tags = new ArrayList<>();
        for (int i = 0; i <= mParamRecipe.dishTypes.size() - 1; i++) {
            tags.add(new Tag(mParamRecipe.dishTypes.get(i), false));
        }
        binding.content.listTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        DetailsTagAdapter adapter = new DetailsTagAdapter();
        adapter.setData(tags);
        binding.content.listTags.setAdapter(adapter);

        binding.content.listIngredient.setLayoutManager(new LinearLayoutManager(getContext()));
        int ingredientListLength = mParamRecipe.extendedIngredients.size();
        if(ingredientListLength>3)
            ingredientListLength=3;
        else
        {
            binding.content.btnIngredientsExpand.setEnabled(false);
        }
        binding.content.listIngredient.setAdapter(new IngredientAdapter(mParamRecipe.extendedIngredients.subList(0, ingredientListLength)));

        binding.content.btnIngredientsExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mState.ingredientsExpanded = !mState.ingredientsExpanded;
                if (mState.ingredientsExpanded) {
                    binding.content.btnIngredientsExpand.setText("Collapse");
                    binding.content.listIngredient.setAdapter(new IngredientAdapter(mParamRecipe.extendedIngredients));
                } else {
                    binding.content.btnIngredientsExpand.setText("Expand");
                    binding.content.listIngredient.setAdapter(new IngredientAdapter(mParamRecipe.extendedIngredients.subList(0, 3)));
                }
            }
        });
        binding.content.btnIngredientsExpand.setVisibility(mParamRecipe.extendedIngredients.size() > 3 ? View.VISIBLE : View.GONE);
        binding.content.btnIngredientsExpand.setText("Expand");

        bSaved = false;
        db.collection("Users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                if (user.savedRecipes != null) {
                    for (int i = 0; i <= user.savedRecipes.size() - 1; i++) {
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
                                for (DocumentSnapshot snapshot : snapshotList) {
                                    User user = snapshot.toObject(User.class);
                                    assert user != null;
                                    boolean bRemovedSavedItem = false;
                                    // check if current item is in saved list, if so then remove it and set state
                                    if (user.savedRecipes != null) {
                                        for (int i = 0; i <= user.savedRecipes.size() - 1; i++) {
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

        InitLikeBtn();

        InitEditButtons();

        binding.appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                binding.appBarImage.setTranslationY(-verticalOffset / 2);
                float baseRadius = getResources().getDisplayMetrics().density * 12.f;
                binding.appbarCard.setRadius(baseRadius + verticalOffset / 16);
                float hPadding = (8 * getResources().getDisplayMetrics().density + verticalOffset / 16);
                hPadding = hPadding < 0 ? 0 : hPadding;
                binding.toolbarParent.setPadding((int) hPadding, (int) (8 * getResources().getDisplayMetrics().density), (int) hPadding, 0);

                float titleAlpha = 1.f - (float) Math.abs(verticalOffset) / 200;
                titleAlpha = Math.max(0.f, Math.min(1.f, titleAlpha));
                binding.toolbarTitleEx.setAlpha(titleAlpha);
                binding.toolbarTitleEx.setSelected(titleAlpha == 1.f);
                binding.toolbarTitle.setAlpha(1.f - titleAlpha);
            }
        });

        binding.viewStepRecipe.hide();
//        binding.scrollContainer.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY != 0)
//                {
//                    binding.viewStepRecipe.show();
//                }
//                else {
//                    binding.viewStepRecipe.hide();
//                }
//            }
//        });

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

        binding.content.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to delete?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DeleteRecipe();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                // User cancelled the dialog
                            }
                        });
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        binding.content.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecipeCreater.class);
                intent.putExtra("RECIPE", (Serializable) mParamRecipe);
                intent.putExtra("BEFORE_SCREEN", beforeScreen);
                startActivity(intent);
                ((MainActivity) getContext()).finish();
            }
        });

        if (mParamRecipe.userID != null) {
            idUserRecipe = mParamRecipe.userID;
        }

        UpdateDescription();
        View.OnClickListener listenerDescToggle = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mState.descriptionExpand = !mState.descriptionExpand;
                UpdateDescription();
            }
        });
        binding.content.textDescFull.setOnClickListener(listenerDescToggle);
        binding.content.textDescFull.setMovementMethod(LinkMovementMethod.getInstance());
        binding.content.textDescription.setOnClickListener(listenerDescToggle);

        getChildFragmentManager().beginTransaction().add(R.id.fragment_comments, CommentsFragment.newInstance(String.valueOf(mParamRecipe.id))).commitNow();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void DeleteRecipe() {
        db.collection("recipes")
                .whereEqualTo("id", mParamRecipe.id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                db.collection("recipes").document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_success, null);

                                        TextView OK = view.findViewById(R.id.OK);
                                        TextView description = view.findViewById(R.id.textDesCription);

                                        description.setText("Delete recipe successfully!");

                                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                        builder.setView(view);

                                        android.app.AlertDialog dialog = builder.create();
                                        dialog.show();

                                        OK.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                onBack();
                                            }
                                        });
                                        dialog.setOnCancelListener(
                                                new DialogInterface.OnCancelListener() {
                                                    @Override
                                                    public void onCancel(DialogInterface dialog) {
                                                        onBack();
                                                    }
                                                }
                                        );
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(getContext(), "Khong co du lieu phu hop", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onBack() {
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("DELETE_RECIPE", true);
            intent.putExtra("BEFORE_SCREEN", beforeScreen);
            intent.putExtra("ID_USER_RECIPE", idUserRecipe);
            startActivity(intent);
            ((MainActivity) getContext()).finish();
        }
    }

    protected class State implements Serializable {
        public boolean ingredientsExpanded = false;
        public boolean descriptionExpand = false;
    }

}