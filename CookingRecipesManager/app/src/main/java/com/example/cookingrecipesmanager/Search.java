package com.example.cookingrecipesmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.search.Adapter.ReceiptAdapter;
import com.example.cookingrecipesmanager.search.Adapter.TagSearchAdapter;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    private ReceiptAdapter receiptApdapter;
    private TagSearchAdapter tagAdapter;
    private RecyclerView rcl;
    private RecyclerView rcl_tag;

    private SearchView searchView;

    private List<CookingNote> listData= new ArrayList<>();

    private List<Tag> tagListClicked = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        Bundle extras = getIntent().getExtras();

        rcl = findViewById(R.id.rcl_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Search.this, RecyclerView.VERTICAL, false);
        rcl.setLayoutManager(linearLayoutManager);
        receiptApdapter = new ReceiptAdapter();
        listData = getListData();
        receiptApdapter.setData(listData);
        rcl.setAdapter(receiptApdapter);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(Search.this, RecyclerView.HORIZONTAL, false);
        rcl_tag = findViewById(R.id.search_rcl_tag);
        rcl_tag.setLayoutManager(linearLayoutManager2);

        tagAdapter = new TagSearchAdapter(Search.this, new TagSearchAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Tag tag, TagSearchAdapter.TagViewHolder holder) {
                if(holder.name.getTextColors().getDefaultColor() == getResources().getColor(R.color.text, null)){
                    holder.name.setTextColor(getResources().getColor(R.color.white, null));
                    holder.content.getBackground().setTint(getResources().getColor(R.color.blue, null));
                    tagListClicked.add(tag);
                }
                else {
                    holder.name.setTextColor(getResources().getColor(R.color.text, null));
                    holder.content.getBackground().setTint(getResources().getColor(R.color.white, null));
//                    holder.getImg().setImageResource(R.drawable.mon3);
                    tagListClicked.remove(tag);

                }
                String tags="";
                for (Tag item: tagListClicked){
                    tags = tags + item.getName();
                }
                filterListByTag(tags);
            }
        });
        tagAdapter.setData(getListTagData());
        rcl_tag.setAdapter(tagAdapter);

        searchView = findViewById(R.id.search_view);
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

        ImageView img = findViewById(R.id.sort);
        img.setOnClickListener(new View.OnClickListener() {
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

    }

    public void filterList(String text){
        List<CookingNote> filterList = new ArrayList<>();
        for(CookingNote item : listData){
            if(item.getTitle().toLowerCase().contains(text.toLowerCase())){
                filterList.add(item);
            }
        }
        if(filterList.isEmpty()){

//            receiptApdapter.setData(filterList);
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }
        else {
//            receiptApdapter.setData(filterList);
        }
        receiptApdapter.setData(filterList);
    }
    public void filterListByTag(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        if(text == ""){
            listData = getListData();
        }
        else{
            listData = getListData2();
        }
        receiptApdapter.setData(listData);
    }

    private List<CookingNote> getListData() {
        List<CookingNote> list = new ArrayList<>();
        list.add(new CookingNote("How to cook Pizza Margherita", "Dyan","Description", R.drawable.mon2, new Float("4.5"), true));
        list.add(new CookingNote("cook Peking Duck", "XiaoCheng","Description", R.drawable.mon_1, new Float("4.5"), true));
        list.add(new CookingNote("Fish and Chips", "Harry Maguire","Description", R.drawable.mon_1, new Float("4.5"), true));
        return list;
    }

    private List<Tag> getListTagData() {
        List<Tag> list = new ArrayList<>();
        list.add(new Tag(R.drawable.mon_1, "See Food 1"));
        list.add(new Tag(R.drawable.mon_1, "See Food 2"));
        list.add(new Tag(R.drawable.mon_1, "See Food 3"));
        list.add(new Tag(R.drawable.mon_1, "See Food 3"));
        list.add(new Tag(R.drawable.mon_1, "See Food 3"));
        list.add(new Tag(R.drawable.mon_1, "See Food 3"));
        return list;
    }
    private List<CookingNote> getListData2() {
        List<CookingNote> list = new ArrayList<>();
        list.add(new CookingNote("How to cook Pizza Margherita", "Dyan","Description", R.drawable.mon2, new Float("4.5"), true));
        list.add(new CookingNote("cook Peking Duck", "XiaoCheng","Description", R.drawable.mon_1, new Float("4.5"), true));

        return list;
    }
}
