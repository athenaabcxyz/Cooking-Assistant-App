package com.example.cookingrecipesmanager.User.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingrecipesmanager.MainActivity;
import com.example.cookingrecipesmanager.R;
import com.example.cookingrecipesmanager.UserRecipe;
import com.example.cookingrecipesmanager.database.Model.User;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.LibraryViewHolder> {
    private Context context;
    private List<User> userList;
    public void setData( List<User> list){
        this.userList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_item, parent, false);
        context = parent.getContext();
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User note = userList.get(position);
        if(note == null){
            return;
        }
        holder.title.setText(note.name);
        holder.email.setText(note.email);
        if(note.image != null){
            Picasso.get().load(note.image).into(holder.image);
        }

        try {

            holder.rootView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserRecipe.class);
                    intent.putExtra("userID", note.uid);
                    context.startActivity(intent);
                    ((MainActivity)context).finish();

                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(userList!= null){
            return userList.size();
        }
        return 0;
    }

    public class LibraryViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView title;
        private TextView email;
        private ImageView image;
        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            title = itemView.findViewById(R.id.textName) ;
            email = itemView.findViewById(R.id.textEmail);
            image = itemView.findViewById(R.id.imageUser);
        }
    }
    public void sortAsc(){
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User i0, User i1) {
                return i0.name.compareToIgnoreCase(i1.name);
            }
        });
        notifyDataSetChanged();
    }
    public void sortDes(){
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User i0, User i1) {
                return i0.name.compareToIgnoreCase(i1.name);
            }
        });
        Collections.reverse(userList);
        notifyDataSetChanged();
    }
}