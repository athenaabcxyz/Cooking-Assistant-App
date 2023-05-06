package com.example.cookingrecipesmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cookingrecipesmanager.home.Adapter.TagAdapter;
import com.example.cookingrecipesmanager.home.Adapter.TrendAdapter;
import com.example.cookingrecipesmanager.library.Adapter.LibraryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeLibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeLibraryFragment extends Fragment {

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
    private Button btnGetAll;



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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(thisContext, RecyclerView.VERTICAL, false);

        rcl = rootView.findViewById(R.id.rcl_lib);
        rcl.setLayoutManager(linearLayoutManager);
        adapter = new LibraryAdapter();
        adapter.setData(getListData());
        rcl.setAdapter(adapter);

        btnMyRecipe = rootView.findViewById(R.id.btn_my_recipe);
        btnGetAll = rootView.findViewById(R.id.btn_all);
        btnMyRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setData(getMyRecipeList());
            }
        });
        btnGetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setData(getListData());
            }
        });
        return rootView;
    }
    private List<CookingNote> getMyRecipeList() {
        List<CookingNote> list = new ArrayList<>();
        list.add(new CookingNote("XSA to cook 2", "Nguyen Hoang Nam", "", R.drawable.mon2, new Float("5"), true));
        list.add(new CookingNote("DSA  to cook 3", "Nguyen Hoang Nam", "", R.drawable.mon2, new Float("4.2"), true));
        list.add(new CookingNote("How to cook 4", "Nguyen Hoang Nam", "", R.drawable.mon2, new Float("3.4"), true));
        list.add(new CookingNote("How to cook 5", "Nguyen Hoang Nam", "", R.drawable.mon2, new Float("5.3"), true));
        list.add(new CookingNote("How to cook 6", "Nguyen Hoang Nam", "", R.drawable.mon2, new Float("4.5"), true));
        return list;
    }
    private List<CookingNote> getListData() {
        List<CookingNote> list = new ArrayList<>();
        list.add(new CookingNote("How to cook 1", "Nguyen Hoang Nam", requireContext().getResources().getString(R.string.sample_recipe_description), R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("How to cook 2", "Nguyen Hoang Nam", "", R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("How to cook 3", "Nguyen Hoang Nam", "", R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("How to cook 4", "Nguyen Hoang Nam", "", R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("How to cook 5", "Nguyen Hoang Nam", "", R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("How to cook 6", "Nguyen Hoang Nam", "", R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("How to cook 7", "Nguyen Hoang Nam", "", R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("How to cook 8", "Nguyen Hoang Nam", "", R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("How to cook 9", "Nguyen Hoang Nam", "", R.drawable.mon_1, new Float("4.5"), true));
        return list;
    }
}