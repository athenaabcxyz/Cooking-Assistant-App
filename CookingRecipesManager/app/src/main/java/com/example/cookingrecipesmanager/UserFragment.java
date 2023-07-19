package com.example.cookingrecipesmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.cookingrecipesmanager.User.Adapter.UserAdapter;
import com.example.cookingrecipesmanager.database.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid;
    List<User> listAll = new ArrayList<>();
    List<User> listMy = new ArrayList<>();
    Context thisContext;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView rcl;
    private UserAdapter adapter;
    private Button btnMyRecipe;
    private Button btnGetAll;
    private boolean isAllRecipe = true;

    {
        assert user != null;
        uid = user.getUid();
    }


    public UserFragment() {
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
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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


        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        thisContext = container.getContext();

        getData();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rcl = rootView.findViewById(R.id.rcl_lib);
        rcl.setLayoutManager(layoutManager);
        adapter = new UserAdapter();
        adapter.setData(listAll);
        rcl.setAdapter(adapter);


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

        return rootView;
    }

    public void filterList(String text) {
        List<User> filterList = new ArrayList<>();
        List<User> listData = new ArrayList<>();
        if (isAllRecipe) {
            listData = listAll;
        } else {
            listData = listMy;
        }
        for (User item : listData) {
            if (item.name.toLowerCase().contains(text.toLowerCase())) {
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

    public void getData() {
        List<User> newdata = new ArrayList<>();
        db.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> listUser = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot documentSnapshot : listUser) {
                    User newUser = documentSnapshot.toObject(User.class);
                    assert newUser != null;
                    listAll.add(newUser);
                }
                adapter.setData(listAll);
            }
        });

    }

}