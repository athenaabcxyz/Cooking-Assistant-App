package com.example.cookingrecipesmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    Toolbar toolbar;
    TextView textToolbar;

    TextInputLayout textCurrentPassword, textNewPassword, textConfirmPassword;
    Button btnChange;

    ProgressDialog progressDialog;
    ImageView backIcon;
    float v= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        backIcon = findViewById(R.id.user_back);
        textCurrentPassword = findViewById(R.id.textPasswordCurrent);
        textNewPassword = findViewById(R.id.textPasswordNew);
        textConfirmPassword = findViewById(R.id.textPasswordConfirm);

        btnChange = findViewById(R.id.btnChange);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textCurrentPassword.setErrorEnabled(false);
                textNewPassword.setErrorEnabled(false);
                textConfirmPassword.setErrorEnabled(false);

                String currentPassword = textCurrentPassword.getEditText().getText().toString().trim();
                String newPassword = textNewPassword.getEditText().getText().toString().trim();
                String confirmPassword = textConfirmPassword.getEditText().getText().toString().trim();

                if (TextUtils.isEmpty(currentPassword))
                {
                    textCurrentPassword.setError("Please enter your current password");
                    textCurrentPassword.setFocusable(true);
                    textCurrentPassword.setErrorIconDrawable(null);
                    return;
                }

                if (currentPassword.length() < 6)
                {
                    textCurrentPassword.setError("Password length at least 6 characters");
                    textCurrentPassword.setFocusable(true);
                    textCurrentPassword.setErrorIconDrawable(null);
                    return;
                }

                if (TextUtils.isEmpty(newPassword))
                {
                    textNewPassword.setError("Please enter your new password");
                    textNewPassword.setFocusable(true);
                    textNewPassword.setErrorIconDrawable(null);
                    return;
                }

                if (newPassword.length() < 6)
                {
                    textNewPassword.setError("Password length at least 6 characters");
                    textNewPassword.setFocusable(true);
                    textNewPassword.setErrorIconDrawable(null);
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword))
                {
                    textConfirmPassword.setError("Please enter your confirm password");
                    textConfirmPassword.setFocusable(true);
                    textConfirmPassword.setErrorIconDrawable(null);
                    return;
                }

                if (newPassword.equals(currentPassword))
                {
                    textNewPassword.setError("New password must be different from current password");
                    textNewPassword.setFocusable(true);
                    textNewPassword.setErrorIconDrawable(null);
                    return;
                }

                if (!newPassword.equals(confirmPassword))
                {
                    textConfirmPassword.setError("Confirm password does not match");
                    textConfirmPassword.setFocusable(true);
                    textConfirmPassword.setErrorIconDrawable(null);
                    return;
                }

                progressDialog.setMessage("Changing...");
                progressDialog.show();
                updatePassword(currentPassword,newPassword);

            }
        });
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //____________________________________________________________________
    }

    private void updatePassword(String currentPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),currentPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        //Toast.makeText(ChangePasswordActivity.this,"Password Updated...",Toast.LENGTH_SHORT).show();
                                        //Hiện bảng báo thành công
                                        View view = LayoutInflater.from(ChangePasswordActivity.this).inflate(R.layout.dialog_success,null);

                                        TextView OK = view.findViewById(R.id.OK);
                                        TextView description = view.findViewById(R.id.textDesCription);

                                        description.setText("Your password has been changed.");

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                                        builder.setView(view);

                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                        OK.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                onBackPressed();
                                            }
                                        });

                                        textNewPassword.getEditText().setText("");
                                        textConfirmPassword.getEditText().setText("");
                                        textCurrentPassword.getEditText().setText("");

                                        textCurrentPassword.getEditText().setCursorVisible(true);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        //Toast.makeText(ChangePasswordActivity.this,"" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                        View view = LayoutInflater.from(ChangePasswordActivity.this).inflate(R.layout.dialog_fail,null);

                                        TextView OK = view.findViewById(R.id.OK);
                                        TextView description = view.findViewById(R.id.textDesCription);

                                        description.setText("The password is invalid.");

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        //Toast.makeText(ChangePasswordActivity.this,"" + e.getMessage(),Toast.LENGTH_SHORT).show();
                        View view = LayoutInflater.from(ChangePasswordActivity.this).inflate(R.layout.dialog_fail,null);

                        TextView OK = view.findViewById(R.id.OK);
                        TextView description = view.findViewById(R.id.textDesCription);

                        description.setText("Please try again later.");

                        final AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
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
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
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