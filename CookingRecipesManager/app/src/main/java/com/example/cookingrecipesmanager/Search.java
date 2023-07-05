package com.example.cookingrecipesmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.home.Adapter.DishAdapter;
import com.example.cookingrecipesmanager.home.Adapter.TagAdapter;
import com.example.cookingrecipesmanager.search.Adapter.ReceiptAdapter;
import com.example.cookingrecipesmanager.search.Adapter.TagSearchAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    private ReceiptAdapter receiptApdapter;
    private TagSearchAdapter tagAdapter;
    private RecyclerView rcl;
    private RecyclerView rcl_tag;
    private SearchView searchView;
    private ImageView icon_sort;
    private ImageView icon_back;
    private ImageView img_not_found;
    private List<Recipe> listRecipe= new ArrayList<>();
    private List<Recipe> listRecipeSearch= new ArrayList<>();
    private String textSearch="";
    private List<Tag> listTag = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<DocumentSnapshot> snapshotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            try{
//                listRecipe = (List<Recipe>) extras.getSerializable("recipes");
                listRecipeSearch = listRecipe;
                listTag = (List<Tag>) extras.getSerializable("tags");
            }
            catch (Exception e){

            }
        }
        getListData();

//      ------------------ findViewId ----------------------------
        rcl = findViewById(R.id.rcl_search);
        rcl_tag = findViewById(R.id.search_rcl_tag);
        searchView = findViewById(R.id.search_view);
        icon_sort = findViewById(R.id.sort);
        icon_back = findViewById(R.id.back);
        img_not_found = findViewById(R.id.img_not_found);

//      ----------------- list data search  -----------------------
//      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Search.this, RecyclerView.VERTICAL, false);
        StaggeredGridLayoutManager linearLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rcl.setLayoutManager(linearLayoutManager);
        receiptApdapter = new ReceiptAdapter();
        rcl.setAdapter(receiptApdapter);

//      ----------------- list tag search --------------------------
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(Search.this, RecyclerView.HORIZONTAL, false);
        rcl_tag.setLayoutManager(linearLayoutManager2);

        tagAdapter = new TagSearchAdapter(new TagSearchAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Tag tag, TagSearchAdapter.TagViewHolder holder) {
//                if (tag.getClicked()==false && tag.getName().equals("All")){
//                    for(Tag item: listTag){
//                        item.setClicked(false);
//                    }
//                    tag.setClicked(true);
//                }
//                else if(tag.getClicked()==false){
//                    tag.setClicked(true);
//                    if(listTag.get(0).getClicked()){
//                        listTag.get(0).setClicked(false);
//                    }
//                }
//                else{
//                    tag.setClicked(false);
//                }
                if (tag.getClicked()==false){
                    tag.setClicked(true);
                }
                else{
                    tag.setClicked(false);
                }
                tagAdapter.setData(listTag);
                tagAdapter.notifyDataSetChanged();
                filterListByTag();
            }
        });
        tagAdapter.setData(listTag);
        rcl_tag.setAdapter(tagAdapter);

//      ----------------- Search by title -------------------------
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

//      ------------------ Sort asc or desc -----------------------
        icon_sort.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Context wrapper = new ContextThemeWrapper(Search.this, R.style.YOURSTYLE_PopupMenu);
                PopupMenu popup = new PopupMenu(wrapper, view);
                popup.inflate(R.menu.menu_popup_sort);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.Asc:
                                receiptApdapter.sortAsc();
                                break;
                            case R.id.Desc:
                                receiptApdapter.sortDes();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
            }
        });

//      --------------------- Back to home ------------------------
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void filterList(String text){
        textSearch = text;
        List<Recipe> filterList = new ArrayList<>();
        img_not_found.setVisibility(View.GONE);
        if(text.isEmpty() == false){
            for(Recipe item : listRecipeSearch){
                if(item.title.toLowerCase().contains(text.toLowerCase())){
                    filterList.add(item);
                }
            }
            if(filterList.isEmpty()){
                img_not_found.setVisibility(View.VISIBLE);
            }
        }
        receiptApdapter.setData(filterList);
        receiptApdapter.notifyDataSetChanged();
    }

    public void filterListByTag(){
        List<Recipe> listDish = new ArrayList<>();
        if(listRecipe.size()>0){
            if(listTag == null || listTag.size()<0){

            }
            else{
                List<String> tags= new ArrayList<>();
                for (Tag item: listTag){
                    if(item.getClicked() == true){
                        tags.add(item.getName());
                    }
                }
                if (tags.size()>0) {
                    for (Recipe item : listRecipe) {
                        if (item.dishTypes.containsAll(tags)) {
                            listDish.add(item);
                        }
                    }
                }
                else {
                    listDish = listRecipe;

                }            }
        }
        listRecipeSearch = listDish;
        filterList(textSearch);
    }



    public void getListData(){
        db.collection("recipes")
                //Use query to find specific document
                .orderBy("aggregateLikes", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Create a list of result. The number of result can be 1
                        snapshotList = queryDocumentSnapshots.getDocuments();
                        //Convert the result to class Object for easier processing.
                        for(DocumentSnapshot snapshot:snapshotList)
                        {
                            Recipe recipe = snapshot.toObject(Recipe.class);
                            assert recipe != null;
                            if(recipe.userID != null){
                                DocumentReference dr = db.collection("Users").document(recipe.userID);
                                dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        String name = value.getString("name");
                                        String image = value.getString("image");
                                        recipe.userName = name;
                                        recipe.userImage = image;
                                        listRecipe.add(recipe);

                                    }
                                });
                            }
                            else{
                                recipe.userName = "UserName";
                                listRecipe.add(recipe);
                            }
                        }
                        listRecipeSearch= listRecipe;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Search.this, MainActivity.class);
        intent.putExtra("BACK_HOME", true);
        startActivity(intent);
    }


}
