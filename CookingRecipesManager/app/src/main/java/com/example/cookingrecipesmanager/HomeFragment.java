package com.example.cookingrecipesmanager;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.cookingrecipesmanager.database.Model.AnalyzedInstruction;
import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.database.Model.Step;
import com.example.cookingrecipesmanager.database.Model.User;
import com.example.cookingrecipesmanager.home.Adapter.DishAdapter;
import com.example.cookingrecipesmanager.home.Adapter.TagAdapter;
import com.example.cookingrecipesmanager.home.Adapter.TrendAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    public String fragmentType = "Home";
    private String mParam2;
    private RecyclerView rcl_trend;
    private RecyclerView rcl_tag;
    private RecyclerView rcl_dish;
    private TrendAdapter trendAdapter;
    private TagAdapter tagAdapter;
    private DishAdapter dishAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<DocumentSnapshot> snapshotList;
    List<Recipe> listRecipe = new ArrayList<>();
    List<Tag> listTag = new ArrayList<>();

    List<Tag> tagListClicked = new ArrayList<>();
    Context thiscontext;
    ProgressDialog progressDialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        thiscontext = container.getContext();
//        Tag firstTag = new Tag("All", false);
//        listTag.add(firstTag);

//      ------------------ Show loading --------------------
        progressDialog = new ProgressDialog(thiscontext);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

//      ------------------ Load list recipe --------------------
        if(listRecipe.size()>0){
            listRecipe.clear();
            getListData();
        }
        else {
            getListData();
        }

//      ------------------ create recycleView -------------------------
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(thiscontext, RecyclerView.HORIZONTAL, false);
        rcl_trend = rootView.findViewById(R.id.rcl_trend);
        rcl_trend.setLayoutManager(linearLayoutManager);

        StaggeredGridLayoutManager linearLayoutManager2 = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        rcl_tag = rootView.findViewById(R.id.rcl_tag);
        rcl_tag.setLayoutManager(linearLayoutManager2);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rcl_dish = rootView.findViewById(R.id.rcl_lib);
        rcl_dish.setLayoutManager(layoutManager);

//       ------------------ Intent to Search -------------------------
        SearchView searchView = (SearchView) rootView.findViewById(R.id.search_view);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Search.class);
                List<Tag> newListTag = new ArrayList<>();
//                Tag firstTag = new Tag("All", false);
//                newListTag.add(firstTag);
                List<Recipe> listTrend = new ArrayList<>();
                if(listRecipe.size()>0){
                    for(int i=0; i< 5;  i++){
                        listTrend.add(listRecipe.get(i));
                    }
                }
                for (Tag tag: listTag){
                    Tag newTag = tag;
                    newTag.setClicked(false);
                    newListTag.add(newTag);
                }
//                intent.putExtra("recipes", (Serializable) listTrend);
                intent.putExtra("tags", (Serializable) newListTag);
                startActivity(intent);
                ((MainActivity)getContext()).finish();
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Search.class);
                List<Tag> newListTag = new ArrayList<>();
//                Tag firstTag = new Tag("All", false);
//                newListTag.add(firstTag);
                for (Tag tag: listTag){
                    Tag newTag = tag;
                    newTag.setClicked(false);
                    newListTag.add(newTag);
                }
//                intent.putExtra("recipes", (Serializable) listRecipe);
                intent.putExtra("tags", (Serializable) newListTag);
                startActivity(intent);
                searchView.setIconified(true);
                ((MainActivity)getContext()).finish();
            }
        });
        return rootView;
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

                    tagAdapter = new TagAdapter(new TagAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(Tag tag, TagAdapter.TagViewHolder holder) {
//                                if(holder.name.getTextColors().getDefaultColor() == getResources().getColor(R.color.text, null)){
//                                    holder.name.setTextColor(getResources().getColor(R.color.white, null));
//                                    holder.content.getBackground().setTint(getResources().getColor(R.color.blue, null));
//                                    tagListClicked.add(tag);
//                                }
//                                else {
//                                    holder.name.setTextColor(getResources().getColor(R.color.text, null));
//                                    holder.content.getBackground().setTint(getResources().getColor(R.color.white, null));
////                    holder.getImg().setImageResource(R.drawable.mon3);
//                                    tagListClicked.remove(tag);
//
//                                }
//                                if (tag.getClicked()==false && tag.getName()=="All"){
//                                    for(Tag item: listTag){
//                                        item.setClicked(false);
//                                    }
//                                    tag.setClicked(true);
//                                }
//                                else if(tag.getClicked()==false){
//                                    tag.setClicked(true);
//                                    if(listTag.get(0).getClicked()){
//                                        listTag.get(0).setClicked(false);
//                                    }
//                                }
//                                else{
//                                    tag.setClicked(false);
//                                }
                            if(tag.getClicked()==false){
                                tag.setClicked(true);

                            }
                            else{
                                tag.setClicked(false);
                            }
                            tagAdapter.setData(listTag);
                            tagAdapter.notifyDataSetChanged();
                            loadDishList();
                        }
                    });

                    rcl_tag.setAdapter(tagAdapter);
                    dishAdapter = new DishAdapter();
                    rcl_dish.setAdapter(dishAdapter);
                    loadTrendList();
                    loadTagList();
                    loadDishList();
                    progressDialog.dismiss();

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
    }

    public void loadTrendList(){
        List<Recipe> listTrend = new ArrayList<>();
        if(listRecipe.size()>5){
            for(int i=0; i< 5;  i++){
                listTrend.add(listRecipe.get(i));
            }
        }
        trendAdapter = new TrendAdapter();
        trendAdapter.setData(listTrend);
        rcl_trend.setAdapter(trendAdapter);
    }

    public void loadTagList(){
        listTag.clear();
        List<String> listStringTag = new ArrayList<>();
        if(listRecipe.size()>0){
            for(Recipe recipe: listRecipe){
                if(recipe.dishTypes.size()>0){
                    listStringTag.addAll(recipe.dishTypes);
                }
            }
        }
        Set<String> set = new LinkedHashSet<String>(listStringTag);
        List<String> listWithoutDuplicateElements = new ArrayList<String>(set);

        for(String item: listWithoutDuplicateElements){

            Tag tag = new Tag(item, false);
            listTag.add(tag);
        }
        if(listTag.size()>0){
            listTag.get(0).setClicked(true);
        }
        tagAdapter.setData(listTag);

    }

    public void loadDishList(){
        List<Recipe> listDish = new ArrayList<>();
        if(listRecipe.size()>0){
//           if(listTag.size()>0 && listTag.get(0).getClicked()==true){
//               listDish = listRecipe;
//           }
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
           }
        }
        if(listDish.size()<=0){
            rcl_dish.setBackgroundResource(R.drawable.not_found);
        }
        else {
            rcl_dish.setBackground(null);
        }
        dishAdapter.setData(listDish);

    }

}