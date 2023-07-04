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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.cookingrecipesmanager.User.Adapter.UserRecipeAdapter;
import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.database.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserRecipe extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserRecipeAdapter adapter;
    private RecyclerView rcl;
    List<CookingNote> listMy = new ArrayList<>();
    TextView userName, email;
    ImageView userImage, backIcon;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            try{
               uid = (String) extras.getSerializable("userID");
            }
            catch (Exception e){

            }
        }

        setContentView(R.layout.activity_user_recipe);
        userName = findViewById(R.id.textTitle);
        email = findViewById(R.id.textEmail);
        userImage = findViewById(R.id.imageUser);
        backIcon = findViewById(R.id.back);

        if(uid != null){
            db.collection("Users").document(uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User currentUser = documentSnapshot.toObject(User.class);
                            assert currentUser != null;
                            try {
                                if(currentUser.name != null){
                                    userName.setText(currentUser.name);
                                    email.setText(currentUser.email);
                                }
                                if(currentUser.image != null){
                                    Picasso.get().load(currentUser.image).into(userImage);
                                }

                            }
                            catch (Exception e){

                            }
                            db.collection("recipes").whereEqualTo("userID", currentUser.uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                                    for(DocumentSnapshot snapshot:snapshotList)
                                    {
                                        Recipe recipe = snapshot.toObject(Recipe.class);
                                        assert recipe != null;
                                        recipe.userImage = currentUser.image;
                                        listMy.add(new CookingNote(recipe,recipe.id, recipe.title, currentUser.name, "", recipe.image, new Float("5"), true));
                                    }
                                    adapter.setData(listMy);
                                }
                            });
                        }
                    });

        }
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        rcl = findViewById(R.id.rcl_lib);
        rcl.setLayoutManager(layoutManager);
        adapter = new UserRecipeAdapter();
        adapter.setData(listMy);
        rcl.setAdapter(adapter);

        //------------------------------- Search -----------------------------------------------
        ImageView img = findViewById(R.id.sort);
        img.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Context wrapper = new ContextThemeWrapper(UserRecipe.this, R.style.YOURSTYLE_PopupMenu);
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
        SearchView searchView = findViewById(R.id.search_view);
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

        //------------------------------- Back -------------------------------------------
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void filterList(String text) {
        List<CookingNote> filterList = new ArrayList<>();
        List<CookingNote> listData = new ArrayList<>();
        listData = listMy;
        for (CookingNote item : listData) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filterList.add(item);
            }
        }
        if (filterList.isEmpty()) {
            adapter.setData(filterList);
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setData(filterList);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserRecipe.this, MainActivity.class);
        intent.putExtra("BACK_USER", true);
        startActivity(intent);
    }


}
