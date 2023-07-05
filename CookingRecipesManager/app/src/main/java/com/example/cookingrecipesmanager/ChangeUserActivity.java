package com.example.cookingrecipesmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingrecipesmanager.database.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ChangeUserActivity extends AppCompatActivity {

    TextView email;
    EditText userName;
    ImageView userImage, backIcon;
    LinearLayout addImage, group_change_image, changeImage, deleteImage;
    Button btnChange;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User currentUser;
    Uri imageURI;
    final int GALLERY_REQ_CODE = 1000;
    ProgressDialog progressDialog;
    StorageReference storageRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // ============= Khai bao ===============
        email = findViewById(R.id.email);
        userName = findViewById(R.id.userName);
        userImage = findViewById(R.id.userImage);
        btnChange = findViewById(R.id.btnChangeUser);
        backIcon = findViewById(R.id.user_back);
        addImage = findViewById(R.id.addImage);
        changeImage = findViewById(R.id.changeImage);
        deleteImage = findViewById(R.id.deleteImage);
        group_change_image = findViewById(R.id.group_change_image);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // ============= Load data ==============
        getUser();

        // ============= Add Image ==============
        group_change_image.setVisibility(View.GONE);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);

            }
        });

        // ============= Change Image ===========
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
            }
        });

        // ============= Delete Image ===========
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userImage.setImageDrawable(null);
                addImage.setVisibility(View.VISIBLE);
                group_change_image.setVisibility(View.GONE);
                imageURI = null;
            }
        });

        // ============= Update =================
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().toString().equals("")){
                    Toast.makeText(ChangeUserActivity.this, "Please fill in UserName", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.setMessage("Changing...");
                    progressDialog.show();
                    updateUser();
                }
            }
        });

        // ============= Back ===================
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //____________________________________________________________________
    }
    private void getUser(){
        db.collection("Users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentUser = documentSnapshot.toObject(User.class);
                        assert currentUser != null;
                        try {
                            if(currentUser.name != null){
                                userName.setText(currentUser.name);
                                email.setText(currentUser.email);
                            }
                            if(currentUser.image != null){
                                Picasso.get().load(currentUser.image).into(userImage);
                                imageURI = Uri.parse(currentUser.image);
                                group_change_image.setVisibility(View.VISIBLE);
                                addImage.setVisibility(View.GONE);
                            }
                            progressDialog.dismiss();

                        }
                        catch (Exception e){

                        }
                    }
                });
    }
    private void updateUser() {
        String newPath = null;
        if(imageURI!= null) newPath = imageURI.toString();
        currentUser.name = userName.getText().toString();
        if(newPath != currentUser.image && newPath != null){
            storageRef = storage.getReference("images/" + user.getUid());
            storageRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            currentUser.image = uri.toString();
                            db.collection("Users")
                                    .document(user.getUid()).set(currentUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            View view = LayoutInflater.from(ChangeUserActivity.this).inflate(R.layout.dialog_success,null);

                                            TextView OK = view.findViewById(R.id.OK);
                                            TextView description = view.findViewById(R.id.textDesCription);

                                            description.setText("Your profile has been changed.");

                                            final AlertDialog.Builder builder = new AlertDialog.Builder(ChangeUserActivity.this);
                                            builder.setView(view);

                                            AlertDialog dialog = builder.create();
                                            dialog.show();

                                            OK.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    onBackPressed();
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            //Toast.makeText(ChangePasswordActivity.this,"" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                            View view = LayoutInflater.from(ChangeUserActivity.this).inflate(R.layout.dialog_fail,null);

                                            TextView OK = view.findViewById(R.id.OK);
                                            TextView description = view.findViewById(R.id.textDesCription);

                                            description.setText("The profile is invalid.");

                                            final AlertDialog.Builder builder = new AlertDialog.Builder(ChangeUserActivity.this);
                                            builder.setView(view);

                                            AlertDialog dialog = builder.create();
                                            dialog.show();

                                            OK.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    return;
                                                }
                                            });
                                        }
                                    });
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    View view = LayoutInflater.from(ChangeUserActivity.this).inflate(R.layout.dialog_fail,null);

                    TextView OK = view.findViewById(R.id.OK);
                    TextView description = view.findViewById(R.id.textDesCription);

                    description.setText("The profile is invalid.");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(ChangeUserActivity.this);
                    builder.setView(view);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    OK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            return;
                        }
                    });
                }
            });

        }
        else{
            currentUser.image = newPath;
            db.collection("Users")
                    .document(user.getUid()).set(currentUser)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            View view = LayoutInflater.from(ChangeUserActivity.this).inflate(R.layout.dialog_success,null);

                            TextView OK = view.findViewById(R.id.OK);
                            TextView description = view.findViewById(R.id.textDesCription);

                            description.setText("Your profile has been changed.");

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ChangeUserActivity.this);
                            builder.setView(view);

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            OK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onBackPressed();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            //Toast.makeText(ChangePasswordActivity.this,"" + e.getMessage(),Toast.LENGTH_SHORT).show();
                            View view = LayoutInflater.from(ChangeUserActivity.this).inflate(R.layout.dialog_fail,null);

                            TextView OK = view.findViewById(R.id.OK);
                            TextView description = view.findViewById(R.id.textDesCription);

                            description.setText("The profile is invalid.");

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ChangeUserActivity.this);
                            builder.setView(view);

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            OK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    return;
                                }
                            });
                        }
                    });

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                assert data != null;
                imageURI = data.getData();
                userImage.setImageURI(data.getData());
                addImage.setVisibility(View.GONE);
                group_change_image.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}