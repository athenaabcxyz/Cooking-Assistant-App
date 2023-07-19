package com.example.cookingrecipesmanager.details;

import androidx.annotation.NonNull;

import com.example.cookingrecipesmanager.database.Model.CommentSection;
import com.example.cookingrecipesmanager.database.Model.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public class CommentsManager {
    protected String mRecipeId;
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
    ArrayList<IListener> listeners = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionRef = db.collection("CommentSection");
    public CommentsManager(String recipeId) {
        mRecipeId = recipeId;
        fetchComments();
//        comments = new ArrayList<>(Arrays.asList(new Comment("john", "aaaaaaaa"), new Comment("doe", "aaaaaaaaa")));
    }

    protected void broadcastEvents() {
        listeners.forEach(new Consumer<IListener>() {
            @Override
            public void accept(IListener listener) {
                listener.onUpdateComments(CommentsManager.this);
            }
        });
    }

    protected void fetchComments() {
        collectionRef.document(mRecipeId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        boolean success = task.isSuccessful();
                        if (!success) return;

                        boolean exists = task.getResult().exists();
                        if (exists) {
                            commentSection = task.getResult().toObject(CommentSection.class);
                            broadcastEvents();
                        } else {
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

    public void addListener(IListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IListener listener) {
        listeners.remove(listener);
    }

    public ArrayList<Comments> getComments() {
        if (commentSection != null && commentSection.commentList != null) {
            return commentSection.commentList;
        }
        return new ArrayList<>();
    }

    public void addComment(String username, String text) {
        Comments comment = new Comments();
        comment.commentId = UUID.randomUUID().toString();
        comment.userId = username;
        comment.comment = text;
        if (commentSection != null) {
            collectionRef.document(mRecipeId).update("commentList", FieldValue.arrayUnion(comment)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    fetchComments();
                }
            });
        }
    }

    public void removeComment(Comments comment) {
        collectionRef.document(mRecipeId).update("commentList", FieldValue.arrayRemove(comment)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                fetchComments();
            }
        });
    }

    public interface IListener {
        void onUpdateComments(CommentsManager commentsManager);
    }
}

