package com.example.cookingrecipesmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingrecipesmanager.database.Model.Recipe;
import com.example.cookingrecipesmanager.database.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    Button  btnRegister;
    TextView btnLoginActivity;
    ProgressDialog progressDialog;
    TextInputLayout textEmail, textFullName, textPassword, textConPassword;

    FirebaseAuth firebaseAuth;

    float v= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnLoginActivity = findViewById(R.id.btnLoginActivity);
        btnRegister = findViewById(R.id.btnRegister);

        textEmail = findViewById(R.id.textEmailDK);
        textFullName = findViewById(R.id.textFullNameDK);
        textPassword = findViewById(R.id.textPasswordDK);
        textConPassword = findViewById(R.id.textConPasswordDK);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User");

        firebaseAuth = FirebaseAuth.getInstance();

        //____________________________________________________________________

        btnLoginActivity.setOnClickListener((view)->{
            onBackPressed();
        });

        btnRegister.setOnClickListener((view)->{

            textFullName.setErrorEnabled(false);
            textEmail.setErrorEnabled(false);
            textPassword.setErrorEnabled(false);
            textConPassword.setErrorEnabled(false);

            String email = textEmail.getEditText().getText().toString().trim();
            String fullname = textFullName.getEditText().getText().toString().trim();
            String password = textPassword.getEditText().getText().toString().trim();
            String conpassword = textConPassword.getEditText().getText().toString().trim();

            if (TextUtils.isEmpty(fullname))
            {
                textFullName.setError("Please enter your name");
                textFullName.setFocusable(true);
                textFullName.setErrorIconDrawable(null);
                return;
            }

            if (TextUtils.isEmpty(email))
            {
                textEmail.setError("Please enter your email");
                textEmail.setFocusable(true);
                textEmail.setErrorIconDrawable(null);
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                textEmail.setError("Invalid email");
                textEmail.setFocusable(true);
                textEmail.setErrorIconDrawable(null);
                return;
            }

            if (TextUtils.isEmpty(password))
            {
                textPassword.setError("Please enter your password");
                textPassword.setFocusable(true);
                textPassword.setErrorIconDrawable(null);
                return;
            }

            if (password.length() < 6)
            {
                textPassword.setError("Password length at least 6 characters");
                textPassword.setFocusable(true);
                textPassword.setErrorIconDrawable(null);
                return;
            }

            if (TextUtils.isEmpty(conpassword))
            {
                textConPassword.setError("Please confirm password");
                textConPassword.setFocusable(true);
                textConPassword.setErrorIconDrawable(null);
                return;
            }

            if (!password.equals(conpassword))
            {
                textConPassword.setError("Confirm password does not match");
                textConPassword.setFocusable(true);
                textConPassword.setErrorIconDrawable(null);
                return;
            }

            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String userID ;
                                progressDialog.dismiss();
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                User data = new User();
                                data.name = fullname;
                                data.email = email;
                                data.uid = user.getUid();

//                                HashMap<Object,String> hashMap = new HashMap<>();
//                                hashMap.put("email",email);
//                                hashMap.put("name",fullname);
//                                hashMap.put("uid",user.getUid());
                                userID = user.getUid();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference drf = db.collection("Users").document(userID);
                                drf.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        firebaseAuth.signOut();
                                    }
                                });

                                //Hiện bảng báo thành công
                                View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.dialog_success,null);

                                TextView OK = view.findViewById(R.id.OK);
                                TextView description = view.findViewById(R.id.textDesCription);

                                description.setText("Back Login screen to continute.");

                                final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setView(view);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                                OK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onBackPressed();
                                    }
                                });

                                //Set null cái text
                                textEmail.getEditText().setText(null);
                                textFullName.getEditText().setText(null);
                                textPassword.getEditText().setText(null);
                                textConPassword.getEditText().setText(null);
                            }
                            else
                            {
                                progressDialog.dismiss();

                                View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.dialog_fail,null);

                                TextView OK = view.findViewById(R.id.OK);
                                TextView description = view.findViewById(R.id.textDesCription);

                                description.setText("The email address is already in use by another accoount.");

                                final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setView(view);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                                OK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public void onResume() {

        super.onResume();
        CheckInternet();
    }
    private void CheckInternet() {
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregistorNetwork(){
        try {
            unregisterReceiver(broadcastReceiver);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistorNetwork();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregistorNetwork();
    }
}