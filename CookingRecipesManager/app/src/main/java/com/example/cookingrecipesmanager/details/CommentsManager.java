package com.example.cookingrecipesmanager.details;

import androidx.annotation.NonNull;

import com.example.cookingrecipesmanager.database.Model.CommentSection;
import com.example.cookingrecipesmanager.database.Model.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class CommentsManager
{
    protected String mRecipeId;

    public interface IListener {
        void onUpdateComments(CommentsManager commentsManager);
    }
    ArrayList<IListener> listeners = new ArrayList<>();

    protected void broadcastEvents()
    {
        listeners.forEach(new Consumer<IListener>() {
            @Override
            public void accept(IListener listener) {
                listener.onUpdateComments(CommentsManager.this);
            }
        });
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = db.collection("CommentSection");
//    public class Comment implements Serializable
//    {
//        public String username;
//        public String text;
//        public Comment(String username, String text)
//        {
//            this.username = username;
//            this.text = text;
//        }
//    }
    protected CommentSection commentSection;
    protected ArrayList<Comments> comments;

    public CommentsManager(String recipeId) {
        mRecipeId = recipeId;
        fetchComments();
//        comments = new ArrayList<>(Arrays.asList(new Comment("john", "aaaaaaaa"), new Comment("doe", "aaaaaaaaa")));
    }

    protected void fetchComments()
    {
        collectionRef.document(mRecipeId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        boolean success = task.isSuccessful();
                        if (!success) return;

                        boolean exists = task.getResult().exists();
                        if (exists)
                        {
                            commentSection = task.getResult().toObject(CommentSection.class);
                            broadcastEvents();
                        }
                        else
                        {
                            commentSection = new CommentSection();
                            commentSection.recipeId = mRecipeId;
                            commentSection.commentList = new ArrayList<>();
                            collectionRef.document(mRecipeId).set(commentSection).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    broadcastEvents();
                                }
                            });
                        }

                        return;
                    }
                });
    }

    public void addListener(IListener listener)
    {
        listeners.add(listener);
    }

    public void removeListener(IListener listener)
    {
        listeners.remove(listener);
    }

    public ArrayList<Comments> getComments() {
        if (commentSection != null && commentSection.commentList != null) {
            return commentSection.commentList;
        }
        return new ArrayList<>();
    }

    public void addComment(String username, String text)
    {
        Comments comment = new Comments();
        comment.userId = username;
        comment.comment = text;
        if (commentSection != null) {
            commentSection.commentList.add(comment);
            collectionRef.document(mRecipeId).set(commentSection).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    fetchComments();
                }
            });
        }
    }

    public void removeComment(Comments comment)
    {
        commentSection.commentList.remove(comment);
        collectionRef.document(mRecipeId).set(commentSection).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                fetchComments();
            }
        });
    }
}

