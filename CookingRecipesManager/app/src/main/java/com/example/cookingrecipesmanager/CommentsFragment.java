package com.example.cookingrecipesmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cookingrecipesmanager.database.Model.CommentSection;
import com.example.cookingrecipesmanager.database.Model.Comments;
import com.example.cookingrecipesmanager.database.Model.User;
import com.example.cookingrecipesmanager.databinding.FragmentCommentsBinding;
import com.example.cookingrecipesmanager.details.CommentsManager;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsFragment extends Fragment implements CommentsManager.IListener {

    private FragmentCommentsBinding binding;
    private String mRecipeId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public View root;
        public TextView username;
        public TextView text;
        public ImageView img;
        public ImageButton btnDelete;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.text_comment);
            img = itemView.findViewById(R.id.user_image);
            btnDelete = itemView.findViewById(R.id.btn_deleteComment);
        }
    }

    public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
        protected List<Comments> data;
        protected Map<String, User> userMap = new HashMap<>();

        public CommentAdapter(List<Comments> data)
        {
            this.data = data;
        }

        public void setData(List<Comments> data) {
            this.data = data;
//            ArrayList<String> uids = new ArrayList<>();
//            data.forEach(new Consumer<Comments>() {
//                @Override
//                public void accept(Comments comment) {
//                    uids.add(comment.userId);
//                }
//            });
//            FirebaseFirestore.getInstance().collection("Users").whereIn("uid", uids).get()
//                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                    queryDocumentSnapshots.forEach(new Consumer<QueryDocumentSnapshot>() {
//                                        @Override
//                                        public void accept(QueryDocumentSnapshot queryDocumentSnapshot) {
//                                            User user = queryDocumentSnapshot.toObject(User.class);
//                                            userMap.put(user.uid, user);
//                                        }
//                                    });
//                                }
//                            });
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
            Comments comment = data.get(position);
            holder.username.setText("Anon");
            holder.img.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.user));
            holder.btnDelete.setVisibility(View.GONE);
            db.collection("Users").whereEqualTo("uid", comment.userId).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.isEmpty()) return;
                            User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                            holder.username.setText(user.name);
                            if (comment.userId.equals(user.uid))
                            {
                                holder.btnDelete.setVisibility(View.VISIBLE);
                                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mComments.removeComment(comment);
                                    }
                                });
                            }
                            if (user.image != null && !user.image.isEmpty()) {
                                Picasso.get().load(user.image).into(holder.img);
                            }
                        }
                    });
            holder.text.setText(comment.comment);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    CommentsManager mComments;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance(String recipeId) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString("RECIPE_ID", recipeId);
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
    public void onUpdateComments(CommentsManager commentsManager) {
        ((CommentAdapter)binding.comments.getAdapter()).setData(commentsManager.getComments());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipeId = getArguments().getString("RECIPE_ID");

            mComments = new CommentsManager(mRecipeId);
            mComments.addListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommentsBinding.inflate(inflater, container, false);
        binding.comments.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (mComments != null) {
            binding.comments.setAdapter(new CommentAdapter(mComments.getComments()));
        }

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
                if (mComments == null) return;
                if (binding.editTextComment.getText().toString().isEmpty()) return;

                String uid = FirebaseAuth.getInstance().getUid();

                mComments.addComment(uid, String.valueOf(binding.editTextComment.getText()));
                ((CommentAdapter)binding.comments.getAdapter()).setData(mComments.getComments());
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