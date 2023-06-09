package com.example.cookingrecipesmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.search.Adapter.ReceiptAdapter;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    private ReceiptAdapter receiptApdapter;
    private RecyclerView rcl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);



        rcl = findViewById(R.id.rcl_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Search.this, RecyclerView.VERTICAL, false);
        rcl.setLayoutManager(linearLayoutManager);
        receiptApdapter = new ReceiptAdapter();
        receiptApdapter.setData(getListData());
        rcl.setAdapter(receiptApdapter);

    }
    private List<CookingNote> getListData() {
        List<CookingNote> list = new ArrayList<>();
        list.add(new CookingNote("How to cook Pizza Margherita", "Dyan","Description", R.drawable.mon2, new Float("4.5"), true));
        list.add(new CookingNote("cook Peking Duck", "XiaoCheng","Description", R.drawable.mon2, new Float("4.5"), true));
        list.add(new CookingNote("Fish and Chips", "Harry Maguire","Description", R.drawable.mon_1, new Float("4.5"), true));
        return list;
    }
}
