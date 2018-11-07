package com.codedarts.joel.dspot;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.codedarts.joel.dspot.FirebaseDataObjects.CategoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategorySelectionFragment extends Fragment {

    private ListView listView;
    private CategoryLoaderAdapter categoryLoaderAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    public String selectedCategoriesBranch;
    public String selectedCategoryKey;
    public String categoriesBranch;

    public CategorySelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_selection, container, false);
        selectedCategoriesBranch = getResources().getString(R.string.users_selected_categories);
        selectedCategoryKey = getResources().getString(R.string.user_selected_category);
        categoriesBranch = getResources().getString(R.string.categories);

        listView = (ListView) view.findViewById(R.id.categoriesListView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = firebaseAuth.getCurrentUser();

        loadCategories();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            getActivity().finish();
        }
        return true;
    }

    private void loadCategories () {
        databaseReference.child(categoriesBranch).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryLoaderAdapter = new CategoryLoaderAdapter();

                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    CategoryObject value = childSnapshot.getValue(CategoryObject.class);

                    categoryLoaderAdapter.list.add(value);
                    categoryLoaderAdapter.notifyDataSetChanged();
                }
                listView.setAdapter(categoryLoaderAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(CategorySelectionActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class CategoryLoaderAdapter extends BaseAdapter {

        public ArrayList<CategoryObject> list = new ArrayList<>();
        //public ArrayList<ViewGroup> viewGroups = new ArrayList<>();
        //private ArrayList<View> views = new ArrayList<>();
        //private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        private ArrayList<Boolean> checkboxStates = new ArrayList<>();

        /**When getView is called, the max size of the list is already there.
         * Within the getView method, view == null is true when views are being instantiated onto the screen for the first time. It is never called again after the view shows up on screen once*/

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, final ViewGroup viewGroup) {
            CheckBox checkbox;

            Log.e("NUMBER_OF_CHECKBOXES", "i is at: " + String.valueOf(i));
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.category_selection_layout, viewGroup, false);
                checkbox = (CheckBox)view.findViewById(R.id.checkBox1);
                //Log.e("NUMBER_OF_CHECKBOXES", "list size: " + String.valueOf(list.size()));

                //views.add(view);
                //viewGroups.add((ViewGroup) view);
                //checkBoxes.add(checkbox);
            }

            checkbox = (CheckBox)view.findViewById(R.id.checkBox1);
            if (checkboxStates.size() != list.size()) {
                checkboxStates.add(checkbox.isChecked());
            }
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (compoundButton.isChecked()) {
                        compoundButton.setTextColor(getResources().getColor(R.color.colorAccent));
                        databaseReference.child(selectedCategoriesBranch + firebaseUser.getUid() + "/" + list.get(i).preference_id).setValue(compoundButton.getText());
                        checkboxStates.set(i, b);
                    }
                    else {
                        databaseReference.child(selectedCategoriesBranch + firebaseUser.getUid() + "/" + list.get(i).preference_id).removeValue();
                        checkboxStates.set(i, b);
                        compoundButton.setTextColor(getResources().getColor(R.color.peach));
                    }
                }
            });

            if (checkbox.isChecked()) {
                checkbox.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            else {
                checkbox.setTextColor(getResources().getColor(R.color.peach));
            }

            checkbox.setChecked(checkboxStates.get(i));
            checkbox.setText(list.get(i).preference_name);
            return view;
        }

    }

}
