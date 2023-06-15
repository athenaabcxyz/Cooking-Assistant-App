package com.example.cookingrecipesmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.database.Model.User;
import com.example.cookingrecipesmanager.home.Adapter.TagAdapter;
import com.example.cookingrecipesmanager.home.Adapter.TrendAdapter;
import com.example.cookingrecipesmanager.library.Adapter.LibraryAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeLibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeLibraryFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid;

    {
        assert user != null;
        uid = user.getUid();
    }

    List<CookingNote> listAll = new ArrayList<>();
    List<CookingNote> listMy = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    Context thisContext;
    private RecyclerView rcl;
    private LibraryAdapter adapter;
    private Button btnMyRecipe;
    public String fragmentType = "Library";
    private Button btnGetAll;
    private FloatingActionButton btn_create;
    private boolean isAllRecipe = true;



    public RecipeLibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeLibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeLibraryFragment newInstance(String param1, String param2) {
        RecipeLibraryFragment fragment = new RecipeLibraryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_library, container, false);
        thisContext = container.getContext();

//      -------------------------------- Get data --------------------------------------
        db.collection("recipes").whereEqualTo("userID", user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot snapshot:snapshotList)
                {
                   Recipe recipe = snapshot.toObject(Recipe.class);
                    assert recipe != null;
                    listMy.add(new CookingNote(recipe,recipe.id, recipe.title, user.getDisplayName(), "", recipe.image, new Float("5"), true));
                }
                adapter.setData(listMy);
            }
        });

        db.collection("Users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User currentUser = documentSnapshot.toObject(User.class);
                        assert currentUser != null;
                        if (currentUser.savedRecipes != null) {
                            for (int i = 0; i <= currentUser.savedRecipes.size() - 1; i++) {

                                db.collection("recipes").whereEqualTo("id", currentUser.savedRecipes.get(i)).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                                                for (DocumentSnapshot snapshot : snapshotList) {
                                                    Recipe recipe = snapshot.toObject(Recipe.class);
                                                    assert recipe != null;

                                                    if(recipe.userID != null){
                                                        DocumentReference dr = db.collection("Users").document(recipe.userID);
                                                        dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                                String name = value.getString("name");
                                                                recipe.userName = name;
                                                                listAll.add(new CookingNote(recipe,recipe.id, recipe.title, recipe.userName, "", recipe.image, new Float("5"), true));
                                                            }
                                                        });
                                                    }
                                                    else{
                                                        recipe.userName = "";
                                                        listAll.add(new CookingNote(recipe,recipe.id, recipe.title, "", "", recipe.image, new Float("5"), true));
                                                    }

                                                }
                                                adapter.setData(listAll);
                                            }
                                        });
                            }
                        }
                    }
                });

//      -------------------------------- Create RecycleView ------------------------------
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        rcl = rootView.findViewById(R.id.rcl_lib);
        rcl.setLayoutManager(layoutManager);
        adapter = new LibraryAdapter();
        adapter.setData(listAll);
        rcl.setAdapter(adapter);

//      -------------------------------- Load Button All and My Recipe ----------------------
        btnMyRecipe = rootView.findViewById(R.id.btn_my_recipe);
        btnGetAll = rootView.findViewById(R.id.btn_all);
        btnGetAll.performClick();

        btnMyRecipe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isAllRecipe = false;
                btnMyRecipe.setBackgroundColor(Color.parseColor("#2D67F6"));
                btnMyRecipe.setTextColor(getResources().getColor(R.color.white, null));
                btnGetAll.setBackgroundColor(getResources().getColor(R.color.white, null));
                btnGetAll.setTextColor(getResources().getColor(R.color.text, null));

                adapter.setData(listMy);
            }
        });
        btnGetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllRecipe = true;
                btnMyRecipe.setBackgroundColor(getResources().getColor(R.color.white, null));
                btnMyRecipe.setTextColor(getResources().getColor(R.color.text, null));
                btnGetAll.setBackgroundColor(Color.parseColor("#2D67F6"));
                btnGetAll.setTextColor(getResources().getColor(R.color.white, null));
                adapter.setData(listAll);

            }
        });

//      ------------------------------- Search -----------------------------------------------
        ImageView img = rootView.findViewById(R.id.sort);
        img.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Context wrapper = new ContextThemeWrapper(getContext(), R.style.YOURSTYLE_PopupMenu);
                PopupMenu popup = new PopupMenu(wrapper, view);
                popup.inflate(R.menu.menu_popup_sort);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.Asc:
                                adapter.sortAsc();
                                break;
                            case R.id.Desc:
                                adapter.sortDes();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
            }
        });
        SearchView searchView = rootView.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                filterList(query);
                return false;
            }
        });

//      ----------------------------- Button Create Recipe ------------------------------------------------
        btn_create = rootView.findViewById(R.id.create_btn);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecipeCreater.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public void filterList(String text) {
        List<CookingNote> filterList = new ArrayList<>();
        List<CookingNote> listData = new ArrayList<>();
        if (isAllRecipe) {
            listData = listAll;
        } else {
            listData = listMy;
        }
        for (CookingNote item : listData) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filterList.add(item);
            }
        }
        if (filterList.isEmpty()) {
            adapter.setData(filterList);
            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setData(filterList);
        }
    }


}