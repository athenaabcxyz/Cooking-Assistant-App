package com.example.cookingrecipesmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.cookingrecipesmanager.home.Adapter.TagAdapter;
import com.example.cookingrecipesmanager.home.Adapter.TrendAdapter;

import java.util.ArrayList;
import java.util.List;

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
    private String mParam2;
    private RecyclerView rcl_trend;
    private RecyclerView rcl_tag;
    private TrendAdapter trendAdapter;
    private TagAdapter tagAdapter;

    Context thiscontext;
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(thiscontext, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(thiscontext, RecyclerView.HORIZONTAL, false);

        rcl_trend = rootView.findViewById(R.id.rcl_trend);
        rcl_trend.setLayoutManager(linearLayoutManager);
        trendAdapter = new TrendAdapter();
        trendAdapter.setData(getListData());
        rcl_trend.setAdapter(trendAdapter);

        rcl_tag = rootView.findViewById(R.id.rcl_tag);
        rcl_tag.setLayoutManager(linearLayoutManager2);
        tagAdapter = new TagAdapter();
        tagAdapter.setData(getListTagData());
        rcl_tag.setAdapter(tagAdapter);


        SearchView searchView = (SearchView) rootView.findViewById(R.id.search_view);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Search.class);
////                Bundle bd = new Bundle();
////                bd.put("student", data);
//
                startActivity(intent);
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getActivity(), Search.class);
////                Bundle bd = new Bundle();
////                bd.put("student", data);
//
                startActivity(intent);
                searchView.setIconified(true);
            }
        });

        return rootView;

    }

    private List<CookingNote> getListData() {
        List<CookingNote> list = new ArrayList<>();
        list.add(new CookingNote("How to cook see food", "Nguyen Hoang Nam", requireContext().getResources().getString(R.string.sample_recipe_description), R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("How to cook see food", "Nguyen Hoang Nam", "", R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("How to cook see food", "Nguyen Hoang Nam", "", R.drawable.mon_1, new Float("4.5"), true));
        return list;
    }

    private List<Tag> getListTagData() {
        List<Tag> list = new ArrayList<>();
        list.add(new Tag(R.drawable.mon_1, "See Food 1"));
        list.add(new Tag(R.drawable.mon_1, "See Food 2"));
        list.add(new Tag(R.drawable.mon_1, "See Food 3"));
        return list;
    }
}