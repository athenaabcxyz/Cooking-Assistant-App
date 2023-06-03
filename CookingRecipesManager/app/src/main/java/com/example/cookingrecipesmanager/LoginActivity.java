package com.example.cookingrecipesmanager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    Button btnLogin;

    TextView btnRegister,btnForgetPassword;

    TextInputLayout textEmail, textPassword;


    ProgressDialog progressDialog;

    float v= 0;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btnLogin  =findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgetPassword = findViewById(R.id.btnForgetPassword);

        textEmail = findViewById(R.id.textEmailDN);
        textPassword = findViewById(R.id.textPasswordDN);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        firebaseAuth = FirebaseAuth.getInstance();

        //Set animation______________________________________________________
        textEmail.setTranslationX(800);
        textPassword.setTranslationX(800);
        btnForgetPassword.setTranslationX(800);
        btnLogin.setTranslationX(800);
        btnRegister.setTranslationX(800);

        textEmail.setAlpha(v);
        textPassword.setAlpha(v);
        btnForgetPassword.setAlpha(v);
        btnLogin.setAlpha(v);
        btnRegister.setAlpha(v);

        textEmail.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(300).start();
        textPassword.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        btnForgetPassword.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(500).start();
        btnLogin.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        btnRegister.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(700).start();


        //____________________________________________________________________

        btnRegister.setOnClickListener((view)->{
            Intent intent = new Intent( this,RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener((view)->{

            textEmail.setErrorEnabled(false);
            textPassword.setErrorEnabled(false);

            String email = textEmail.getEditText().getText().toString().trim();
            String password  =textPassword.getEditText().getText().toString().trim();

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

            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//                        if (user.isEmailVerified())
//                        {
//                            Intent intent  =new Intent(LoginActivity.this,LoadActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                        else
//                        {
//                            user.sendEmailVerification();
//                            Toast.makeText(LoginActivity.this,"Kiểm tra mail để xác thực cho lần đầu đăng nhập",Toast.LENGTH_LONG).show();
//                        }
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
//                        Toast.makeText(LoginActivity.this, "Thanh cong", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Email or Password is incorrect!",Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        textEmail.setErrorEnabled(false);
        textPassword.setErrorEnabled(false);
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
