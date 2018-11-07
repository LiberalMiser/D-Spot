package com.codedarts.joel.dspot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView emailTextViewLogin, passwordTextViewLogin, loadingText;
    private ConstraintLayout progressViewGridLayout;
    private FirebaseAuth firebaseAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextViewLogin = (EditText)findViewById(R.id.emailEditTextLogin);
        passwordTextViewLogin = (EditText)findViewById(R.id.passwordEditTextLogin);
        loadingText = (TextView)findViewById(R.id.loadingText);
        progressViewGridLayout = (ConstraintLayout)findViewById(R.id.progressViewCustom);
        firebaseAuthentication = FirebaseAuth.getInstance();

        if (firebaseAuthentication.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, HomeSelectedCategoriesActivity.class);
            startActivity(intent);
        }
    }

    /*Logs in the user when they enter their email address and password
    into the respective TextViews and click the Log In button.*/
    private void loginViaEmail (String email, String password) {
        if (TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter an email address...", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            if (TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter a password...", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Please enter an email address and password...", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        progressViewGridLayout.setVisibility(View.VISIBLE);
        loadingText.setText("Signing In...");
        firebaseAuthentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressViewGridLayout.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Successfully signed in!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomeSelectedCategoriesActivity.class);

                    startActivity(intent);
                }
            }
        });
    }

    //Logs the user into the application when they click the Log In button.
    public void login (View view) {
        loginViaEmail(emailTextViewLogin.getText().toString().trim(), passwordTextViewLogin.getText().toString().trim());
    }

    /*Launch the Register activity when the Sign Up button is clicked in the Login Activity.
    This'd be the activity where the user would be able to create their account.
    In this case, that would be the SignUpActivity class.*/
    public void loadRegisterActivity (View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

}
