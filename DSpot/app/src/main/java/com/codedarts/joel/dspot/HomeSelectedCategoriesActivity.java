package com.codedarts.joel.dspot;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import com.codedarts.joel.dspot.FirebaseDataObjects.CategoryObject;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeSelectedCategoriesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SELECTED_CATEGORY_STRING = "SELECTED_CATEGORY";
    private String userSelectedCategories;
    public String categoriesBranch;

    private int counter = 0;

    private GridView gridView;
    private GridAdapter gridAdapter;
    private ConstraintLayout constraintLayoutProgressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ValueEventListener selectedPreferencesValueEventListener;
    private ValueEventListener categoriesValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_selected_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCategorySelectionActivity();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gridView =  (GridView)findViewById(R.id.selectedCategoriesGridView);
        constraintLayoutProgressBar = (ConstraintLayout) findViewById(R.id.constraintLayoutProgressBar);
        gridAdapter = new GridAdapter();

        userSelectedCategories = getResources().getString(R.string.users_selected_categories);
        categoriesBranch = getResources().getString(R.string.categories);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        determineActivityToLoad();
        getSelectedPreferences();
    }

    @Override
    public void onRestart () {
        super.onRestart();
        recreate();
    }

    @Override
    public void onStop() {
        super.onStop();

        counter = 0;
        if (databaseReference != null && firebaseUser != null && selectedPreferencesValueEventListener != null && categoriesValueEventListener != null) {
            databaseReference.child(userSelectedCategories + firebaseUser.getUid()).removeEventListener(selectedPreferencesValueEventListener);
            databaseReference.child(categoriesBranch).removeEventListener(categoriesValueEventListener);
        }

        if (gridView != null) {
            gridView.invalidateViews();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_selected_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_home) {
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, HomeSelectedCategoriesActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_map) {
            startActivity(new Intent(this, MapsActivity.class));
        } else if (id == R.id.nav_saved_destinations) {
            startActivity(new Intent(this, SavedDestinationsActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToCategorySelectionActivity() {
        startActivity(new Intent(this, CategorySelectionActivity.class));
    }

    private void determineActivityToLoad () {
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
    }

    private void getSelectedPreferences () {
        //I'd need to search through the selected categories table and use that to know what categories from the categories table to display.
        counter = 0;
        if (firebaseUser != null) {
            selectedPreferencesValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String selectedCategory = childSnapshot.getKey();

                        gridAdapter.keys.add(selectedCategory);

                        displaySelectedPreferences(counter);
                        counter++;
                        gridAdapter.notifyDataSetChanged();
                        //Toast.makeText(HomeSelectedCategoriesActivity.this, String.valueOf(counter), Toast.LENGTH_SHORT).show();
                    }
                    gridView.setAdapter(gridAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(HomeSelectedCategoriesActivity.this, "Unable to do...something...with the selected categories branch.", Toast.LENGTH_SHORT).show();
                }
            };
            databaseReference.child(userSelectedCategories + firebaseUser.getUid()).addValueEventListener(selectedPreferencesValueEventListener);
        }
    }

    private void displaySelectedPreferences (final int index) {
        //I'd need to search through the selected categories table and use that to know what categories from the categories table to display.
        if (firebaseUser != null) {
            categoriesValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.getKey().equals(gridAdapter.keys.get(index))) {
                            CategoryObject categoryObject = childSnapshot.getValue(CategoryObject.class);

                            gridAdapter.categoryObjects.add(categoryObject);
                        }
                        gridAdapter.notifyDataSetChanged();
                    }
                    gridView.setAdapter(gridAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(HomeSelectedCategoriesActivity.this, "DOUBLE PANDAY!!!", Toast.LENGTH_SHORT).show();
                }
            };

            databaseReference.child(categoriesBranch).addValueEventListener(categoriesValueEventListener);
        }
    }

    public class GridAdapter extends BaseAdapter {

        public ArrayList<CategoryObject> categoryObjects = new ArrayList<>();
        public ArrayList<String> keys = new ArrayList<>();

        public GridAdapter () {
            categoryObjects.clear();
            keys.clear();
        }

        @Override
        public int getCount() {
            return categoryObjects.size();
        }

        @Override
        public Object getItem(int i) {
            return categoryObjects.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final TextView textView;
            ImageView imageView;

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.home_selected_categories_item, viewGroup, false);
            }

            textView = (TextView) view.findViewById(R.id.textView15);
            imageView = (ImageView)view.findViewById(R.id.imageView10);

            if (categoryObjects.size() > 0) {
                textView.setText(categoryObjects.get(i).preference_name);
                Picasso.with(getApplicationContext()).load(categoryObjects.get(i).preference_photo_url).into(imageView);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), DestinationListActivity.class);

                    intent.putExtra(SELECTED_CATEGORY_STRING, textView.getText().toString());
                    startActivity(intent);
                }
            });

            return view;
        }
    }

}
