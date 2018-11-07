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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    public String userParentNode = "Users/";

    public TextView emailTextViewSignUp, passwordTextViewSignUp, loadingText;
    private ConstraintLayout progressViewGridLayout;

    private FirebaseAuth firebaseAuthentication;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userParentNode = getResources().getString(R.string.users_node);

        emailTextViewSignUp = (EditText)findViewById(R.id.emailEditTextSignUp);
        passwordTextViewSignUp = (EditText)findViewById(R.id.passwordEditTextSignUp);
        loadingText = (TextView)findViewById(R.id.loadingText);
        progressViewGridLayout = (ConstraintLayout)findViewById(R.id.progressViewCustom);

        firebaseAuthentication = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = firebaseAuthentication.getCurrentUser();
    }

    /*
    * Creates anccount for the user based off of the email address
    * and password that they have entered
    * */
    private void registerUserViaEmail(String email, String password) {
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
        loadingText.setText("Creating account...");
        firebaseAuthentication.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressViewGridLayout.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Successfully created your account!", Toast.LENGTH_SHORT).show();
                    addUserToDatabase();
                    startActivity(new Intent(SignUpActivity.this, AccountSetupActivity.class));
                }
                else {
                    Toast.makeText(SignUpActivity.this, "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Loads the Log In activity when the Log In button is pressed.
    public void loadLoginActivity (View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    /*This function is called when the user clicks the Sign Up button in the Sign Up activity.
    * It'll create an account for them, based on the email address and password they have provided*/
    public void register (View view) {
        registerUserViaEmail(emailTextViewSignUp.getText().toString().trim(), passwordTextViewSignUp.getText().toString().trim());
    }

    /*Adds the new user to the database.
    * It creates a branch in the database and
    * adds the user's email address and ID to that branch.
    * Further user data will be added to this branch as the user continues the sign up process.*/
    private void addUserToDatabase () {
        firebaseUser = firebaseAuthentication.getCurrentUser();

        if (databaseReference != null) {
            if (firebaseUser != null) {
                databaseReference.child(userParentNode + firebaseUser.getUid() + "/" + getResources().getString(R.string.user_id)).setValue(firebaseUser.getUid());
                databaseReference.child(userParentNode + firebaseUser.getUid() + "/" + getResources().getString(R.string.user_email)).setValue(firebaseUser.getEmail());
            }
        }
    }

}
