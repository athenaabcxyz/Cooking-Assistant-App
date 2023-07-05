package com.example.cookingrecipesmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cookingrecipesmanager.databinding.FragmentCommentsBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsFragment extends Fragment {

    private FragmentCommentsBinding binding;

    public class CommentsManager
    {
        public class Comment implements Serializable
        {
            public String username;
            public String text;
            public Comment(String username, String text)
            {
                this.username = username;
                this.text = text;
            }
        }
        protected ArrayList<Comment> comments;

        public CommentsManager() {
            comments = new ArrayList<>(Arrays.asList(new Comment("john", "aaaaaaaa"), new Comment("doe", "aaaaaaaaa")));
        }

        public void fetchComments()
        {
            //TODO
//            comments = ;
        }

        public ArrayList<Comment> getComments() {
            return comments;
        }

        public void addComment(String username, String text)
        {
            comments.add(new Comment(username, text));
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public View root;
        public TextView username;
        public TextView text;
        public ImageView img;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.text_comment);
            img = itemView.findViewById(R.id.user_image);
        }
    }

    public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
        protected List<CommentsManager.Comment> data;

        public CommentAdapter(List<CommentsManager.Comment> data)
        {
            this.data = data;
        }

        public void setData(List<CommentsManager.Comment> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            CommentsManager.Comment comment = data.get(position);
            holder.username.setText(comment.username);
            holder.text.setText(comment.text);
            holder.img.setImageDrawable(requireContext().getDrawable(R.drawable.img_recipe1));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    CommentsManager comments = new CommentsManager();

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance() {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    protected class State implements Serializable
    {
        public boolean submitFormVisible = false;
    }

    State mState = new State();

    private int bool2visibility(boolean b)
    {
        return b ? View.VISIBLE : View.GONE;
    }

    protected void updateSubmitForm()
    {
        boolean submitting = mState.submitFormVisible;
        binding.btnAddComment.setVisibility(bool2visibility(!submitting));
        binding.btnCancelComment.setVisibility(bool2visibility(submitting));
        binding.cardAddComment.setVisibility(bool2visibility(submitting));
        binding.editTextComment.setText("");

        if (getView() != null) {
            ((InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(requireView().getWindowToken(), 0);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommentsBinding.inflate(inflater, container, false);
        binding.comments.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.comments.setAdapter(new CommentAdapter(comments.getComments()));

        binding.btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mState.submitFormVisible = true;
                updateSubmitForm();
            }
        });

        binding.btnCancelComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mState.submitFormVisible = false;
                updateSubmitForm();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comments.addComment("Anon", String.valueOf(binding.editTextComment.getText()));
                ((CommentAdapter)binding.comments.getAdapter()).setData(comments.getComments());
                mState.submitFormVisible = false;
                updateSubmitForm();
            }
        });
        updateSubmitForm();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}