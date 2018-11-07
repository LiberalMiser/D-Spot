package com.codedarts.joel.dspot;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePictureImageView;
    private TextView nameTextView, addressTextView;
    private StorageReference storageReference;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuthentication;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView = findViewById(R.id.nameTextView);
        addressTextView = findViewById(R.id.cityCountryTextView);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuthentication = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuthentication.getCurrentUser();
        profilePictureImageView = (ImageView) findViewById(R.id.profilePhotoImageView);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.special_grey)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.special_grey_3));
        }

        displayProfilePicture();
        initializeUI();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.profile_and_account_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void openAccountSettingsActivity (View view) {
        startActivity(new Intent(this, AccountSettingsActivity.class));
    }

    private void displayProfilePicture () {
        storageReference.child("Profile Photos/" + user.getUid() + "/profile_photo.jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                String profilePhotoURL;

                if (task.isSuccessful()) {
                    profilePhotoURL = task.getResult().toString();
                    Picasso.with(getApplicationContext()).load(profilePhotoURL).into(profilePictureImageView);
                }
            }
        });
    }

    private void initializeUI () {
        databaseReference.child(getResources().getString(R.string.users_node) + firebaseAuthentication.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = String.valueOf(dataSnapshot.child(getResources().getString(R.string.first_name)).getValue());
                String lastName = String.valueOf(dataSnapshot.child(getResources().getString(R.string.last_name)).getValue());
                String address = String.valueOf(dataSnapshot.child(getResources().getString(R.string.address)).getValue());
                String fullName = firstName + " " + lastName;

                nameTextView.setText(fullName);
                addressTextView.setText(address);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
