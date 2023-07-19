package com.example.cookingrecipesmanager;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    TextView title, des1, des2, ret;
    Button btnReset;
    TextInputLayout textEmail;

    float v = 0;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        firebaseAuth = FirebaseAuth.getInstance();

        title = findViewById(R.id.textTitle);
        des1 = findViewById(R.id.textDes1);
        des2 = findViewById(R.id.textDes2);
        ret = findViewById(R.id.textReturn);
        btnReset = findViewById(R.id.btnReset);
        textEmail = findViewById(R.id.textEmail);

        //____________________________________________________________________

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(ForgetPasswordActivity.this);
                progressDialog.setMessage("Sending mail...");

                textEmail.setErrorEnabled(false);

                String email = textEmail.getEditText().getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    textEmail.setError("Please enter your email");
                    textEmail.setFocusable(true);
                    textEmail.setErrorIconDrawable(null);
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    textEmail.setError("Invalid email");
                    textEmail.setFocusable(true);
                    textEmail.setErrorIconDrawable(null);
                    return;
                }

                progressDialog.show();

                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            View view = LayoutInflater.from(ForgetPasswordActivity.this).inflate(R.layout.dialog_success, null);

                            TextView OK = view.findViewById(R.id.OK);
                            TextView description = view.findViewById(R.id.textDesCription);

                            description.setText("Please check your mail to reset password.");

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);
                            builder.setView(view);

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            OK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onBackPressed();
                                }
                            });
                        } else {
                            View view = LayoutInflater.from(ForgetPasswordActivity.this).inflate(R.layout.dialog_fail, null);

                            TextView OK = view.findViewById(R.id.OK);
                            TextView description = view.findViewById(R.id.textDesCription);

                            description.setText("Please check your email address carefully again.");

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        //Toast.makeText(ForgetPasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
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

    protected void unregistorNetwork() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
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