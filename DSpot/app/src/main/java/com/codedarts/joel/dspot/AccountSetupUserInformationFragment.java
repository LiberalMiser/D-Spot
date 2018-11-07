package com.codedarts.joel.dspot;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountSetupUserInformationFragment extends Fragment {

    private EditText firstNameEditText, middleNameEditText, lastNameEditText, dateOfBirthText, addressEditText, educationEditText,
            currentEmploymentEditText, pastEmploymentEditText, phoneNumberEditText, usernameEditText, religionEditText,
            relationshipStatusEditText, bioEditText;
    private Spinner genderSpinner;
    private ImageView profilePictureImageView;

    private static final int CHOOSE_IMAGE_REQUEST = 234;
    private Uri filePath;
    private Bitmap bitmap;
    private Timer timer;
    private Handler uploadHandler = new Handler();
    private boolean isUploaded = false;
    private String photoPath;

    private StorageReference storageReference;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuthentication;
    private DatabaseReference databaseReference;

    private ValueEventListener valueEventListener;

    public AccountSetupUserInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_setup_user_information, container, false);

        profilePictureImageView = view.findViewById(R.id.profilePhoto);

        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        middleNameEditText = view.findViewById(R.id.middleNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        dateOfBirthText = view.findViewById(R.id.dateOfBirthEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        educationEditText = view.findViewById(R.id.educationEditText);
        currentEmploymentEditText = view.findViewById(R.id.currentEmploymentEditText);
        pastEmploymentEditText = view.findViewById(R.id.pastEmploymentEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        religionEditText = view.findViewById(R.id.religionEditText);
        relationshipStatusEditText = view.findViewById(R.id.relationshipEditText);
        bioEditText = view.findViewById(R.id.bioEditText);

        genderSpinner = view.findViewById(R.id.genderSpinner);

        firebaseAuthentication = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuthentication.getCurrentUser();

        //photoPath = "Profile Photos/" + user.getUid() + "/profile_photo.jpg;

        displayProfilePicture();
        initializeEditTextListeners();
        setUIToUserData(getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid());

        return view;
    }

    private void initializeEditTextListeners () {
        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.first_name),
                        firstNameEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        middleNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.middle_name),
                        middleNameEditText.getText().toString()
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.last_name),
                        lastNameEditText.getText().toString()
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dateOfBirthText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.date_of_birth),
                        dateOfBirthText.getText().toString()
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.gender),
                        adapterView.getSelectedItem().toString()
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        educationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.education),
                        educationEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        currentEmploymentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.current_employment),
                        currentEmploymentEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        pastEmploymentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.past_employment),
                        pastEmploymentEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.address),
                        addressEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.phone_number),
                        phoneNumberEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.username),
                        usernameEditText.getText().toString()
                );
                //Toast.makeText(getActivity(), String.valueOf(firebaseAuthentication.getCurrentUser().getDisplayName()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        religionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.religion),
                        religionEditText.getText().toString()
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        relationshipStatusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.relationship_status),
                        relationshipStatusEditText.getText().toString()
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        bioEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveToDatabase(
                        getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid() + "/" + getActivity().getResources().getString(R.string.bio),
                        bioEditText.getText().toString()
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void displayProfilePicture () {
        if (user != null && storageReference != null) {
            storageReference.child("Profile Photos/" + user.getUid() + "/profile_photo.jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    String profilePhotoURL;


                    if (task.isSuccessful()) {
                        profilePhotoURL = task.getResult().toString();
                        Picasso.with(getActivity().getApplicationContext()).load(profilePhotoURL).into(profilePictureImageView);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getActivity(), "NO PROFILE PHOTO!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void getPhotoFile () {
        Intent photoIntent = new Intent();

        photoIntent.setType("image/*");
        photoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(photoIntent, "Select a profile picture..."), CHOOSE_IMAGE_REQUEST);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.e("PHOTO_UPLOAD", "run() called...");
                uploadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("PHOTO_UPLOAD", "run() inner, called...");
                        if (filePath != null) {
                            Log.e("PHOTO_UPLOAD", "File path not null...");
                            updateProfilePicture();
                            Log.e("PHOTO_UPLOAD", "callbacks are taking place");
                            if (isUploaded) {
                                //uploadHandler.removeCallbacks(this);
                                Log.e("PHOTO_UPLOAD", "photo uploaded...");
                                timer.cancel();
                            }
                        }
                        if (isUploaded) {
                            Log.e("PHOTO_UPLOAD", "Photo uploaded outer...");
                            //uploadHandler.removeCallbacks(this);
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    public void updateProfilePicture () {
        StorageReference riversRef = storageReference.child("Profile Photos/" + user.getUid() + "/profile_photo.jpg");

        riversRef.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Photo uploaded...", Toast.LENGTH_SHORT).show();
                        isUploaded = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), "Unable to upload photo...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profilePictureImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToDatabase (String databasePath, String value) {
        databaseReference.child(databasePath).setValue(value);
        clearUpdateChecks();
    }

    private void setUIToUserData (final String path) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = String.valueOf(dataSnapshot.child(getResources().getString(R.string.first_name)).getValue());
                String lastName = String.valueOf(dataSnapshot.child(getResources().getString(R.string.last_name)).getValue());
                String address = String.valueOf(dataSnapshot.child(getResources().getString(R.string.address)).getValue());
                String middleName = String.valueOf(dataSnapshot.child(getResources().getString(R.string.middle_name)).getValue());
                String dateOfBirth = String.valueOf(dataSnapshot.child(getResources().getString(R.string.date_of_birth)).getValue());
                String gender = String.valueOf(dataSnapshot.child(getResources().getString(R.string.gender)).getValue());
                String education = String.valueOf(dataSnapshot.child(getResources().getString(R.string.education)).getValue());
                String currentEmployment = String.valueOf(dataSnapshot.child(getResources().getString(R.string.current_employment)).getValue());
                String pastEmployment = String.valueOf(dataSnapshot.child(getResources().getString(R.string.past_employment)).getValue());
                String phoneNumber = String.valueOf(dataSnapshot.child(getResources().getString(R.string.phone_number)).getValue());
                String username = String.valueOf(dataSnapshot.child(getResources().getString(R.string.username)).getValue());
                String religion = String.valueOf(dataSnapshot.child(getResources().getString(R.string.religion)).getValue());
                String relationshipStatus = String.valueOf(dataSnapshot.child(getResources().getString(R.string.relationship_status)).getValue());
                String bio = String.valueOf(dataSnapshot.child(getResources().getString(R.string.bio)).getValue());

                firstNameEditText.setText(firstName);
                lastNameEditText.setText(lastName);
                addressEditText.setText(address);
                middleNameEditText.setText(middleName);
                dateOfBirthText.setText(dateOfBirth);
                genderSpinner.setSelection(0);
                educationEditText.setText(education);
                currentEmploymentEditText.setText(currentEmployment);
                pastEmploymentEditText.setText(pastEmployment);
                phoneNumberEditText.setText(phoneNumber);
                usernameEditText.setText(username);
                religionEditText.setText(religion);
                relationshipStatusEditText.setText(relationshipStatus);
                bioEditText.setText(bio);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.child(getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid()).addValueEventListener(valueEventListener);
    }

    private void clearUpdateChecks () {
        databaseReference.child(getActivity().getResources().getString(R.string.users_node) + firebaseAuthentication.getUid()).removeEventListener(valueEventListener);
    }

}
