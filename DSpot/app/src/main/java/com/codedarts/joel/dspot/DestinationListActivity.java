package com.codedarts.joel.dspot;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codedarts.joel.dspot.FirebaseDataObjects.DestinationData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DestinationListActivity extends AppCompatActivity {

    public static String SELECTED_EVENT_TITLE = "SELECTED_EVENT_TITLE";
    public static String SELECTED_EVENT_DATE = "SELECTED_EVENT_DATE";
    public static String SELECTED_EVENT_DESCRIPTION = "SELECTED_EVENT_DESCRIPTION";
    public static String SELECTED_EVENT_COVER_URL = "SELECTED_EVENT_COVER_URL";
    public static String SELECTED_EVENT_TIME = "SELECTED_EVENT_TIME";
    public static String SELECTED_EVENT_WEBSITE = "SELECTED_EVENT_WEBSITE";
    public static String SELECTED_EVENT_LOCATION = "SELECTED_EVENT_LOCATION";

    public static String SELECTED_LATITUDE = "SELECTED_LATITUDE";
    public static String SELECTED_LONGITUDE = "SELECTED_LONGITUDE";
    public static String SELECTED_RATING = "SELECTED_RATING";

    public static String SELECTED_FACEBOOK = "SELECTED_FACEBOOK";
    public static String SELECTED_TWITTER = "SELECTED_TWITTER";

    public static String SELECTED_KEY = "SELECTED_KEY";
    public static String SELECTED_CATEGORY_KEY = "SELECTED_CATEGORY";

    private ListView listView;
    private TextView categoryTextView;
    private TextView locationTextView;
    private String selectedCategory;
    private Intent intent;
    private CustomListAdapter listAdapter;
    private Bundle bundle;

    private String databasePath = "Destination List/";
    private int selectedLocationIndex;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_list);

        databasePath = getResources().getString(R.string.destinations_list);

        bundle = savedInstanceState;
        if (bundle == null) {
            bundle = new Bundle();
        }

        intent = getIntent();
        selectedCategory = intent.getStringExtra(HomeSelectedCategoriesActivity.SELECTED_CATEGORY_STRING);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(selectedCategory);
        }

        listView = (ListView) findViewById(R.id.listView);
        categoryTextView = (TextView) findViewById(R.id.categoryTextView);
        locationTextView = (TextView) findViewById(R.id.textView3);

        categoryTextView.setText(selectedCategory);

        //Whereas we can setOnClickListeners inside the BaseAdapter, which we did previously,
        //We need to get the specific index of the selected item, not the recycled index.
        //So when views are recycled, they recycle their indexes as well.
        //the setOnItemClickListener keeps track of all items in the list and thus, returns the correct index when an item is selected.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DestinationActivity.class);

                intent.putExtra(SELECTED_EVENT_TITLE, listAdapter.destinationData.get(i).name);
                intent.putExtra(SELECTED_EVENT_DATE, listAdapter.destinationData.get(i).open_days);
                intent.putExtra(SELECTED_EVENT_DESCRIPTION, listAdapter.destinationData.get(i).info);
                intent.putExtra(SELECTED_EVENT_COVER_URL, listAdapter.destinationData.get(i).cover_photo);
                intent.putExtra(SELECTED_EVENT_TIME, listAdapter.destinationData.get(i).open_hours);

                intent.putExtra(SELECTED_EVENT_WEBSITE, listAdapter.destinationData.get(i).website);
                intent.putExtra(SELECTED_FACEBOOK, listAdapter.destinationData.get(i).facebook_link);
                intent.putExtra(SELECTED_TWITTER, listAdapter.destinationData.get(i).twitter_link);

                intent.putExtra(SELECTED_EVENT_LOCATION, listAdapter.destinationData.get(i).address);

                intent.putExtra(SELECTED_LATITUDE, listAdapter.destinationData.get(i).latitude);
                intent.putExtra(SELECTED_LONGITUDE, listAdapter.destinationData.get(i).longitude);
                intent.putExtra(SELECTED_RATING, listAdapter.destinationData.get(i).rating);

                intent.putExtra(SELECTED_KEY, listAdapter.keys.get(i));
                intent.putExtra(SELECTED_CATEGORY_KEY, selectedCategory);

                startActivity(intent);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();

        setItemDetails();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        //getMenuInflater().inflate(R.menu.account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_settings) {
            //startActivity(new Intent(this, com.codedarts.joel.dspot.SettingsActivity.class));
        }
        if (menuItem.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    private void setItemDetails () {
        listAdapter = new CustomListAdapter();

        databaseReference.child(databasePath + selectedCategory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    DestinationData value = childSnapshot.getValue(DestinationData.class);
                    listAdapter.destinationData.add(value);
                    listAdapter.keys.add(childSnapshot.getKey());
                    listAdapter.notifyDataSetChanged();
                }
                listView.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openCountrySelectionList (View view) {
        final String[] listItems = getResources().getStringArray(R.array.CountryList);
        //boolean[] checkedItems = new boolean[listItems.length];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Area");
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedLocationIndex = i;
            }
        });
        builder.setCancelable(true);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                locationTextView.setText(listItems[selectedLocationIndex]);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

       public class CustomListAdapter extends BaseAdapter {
        public ArrayList<String> keys = new ArrayList<>();
        public ArrayList<DestinationData> destinationData = new ArrayList<>();

        private Boolean has0Passed;

        @Override
        public int getCount() {
            return destinationData.size();
        }

        @Override
        public Object getItem(int i) {
            return destinationData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ImageView coverImageView;
            TextView titleTextView;
            TextView daysTextView;

            if (destinationData.size() > 0) {
                has0Passed = true;
            }

            if (view == null) {
                if (i % 2 == 0) {
                    view = getLayoutInflater().inflate(R.layout.destination_list_item, viewGroup, false);
                }
                else {
                    view = getLayoutInflater().inflate(R.layout.destination_list_item_inverted, viewGroup, false);
                }
            }

            daysTextView = (TextView) view.findViewById(R.id.textView13);
            titleTextView = (TextView)view.findViewById(R.id.textView8);
            coverImageView = (ImageView)view.findViewById(R.id.imageView7);

            Picasso.with(getApplicationContext()).load(destinationData.get(i).cover_photo).into(coverImageView);
            titleTextView.setText(destinationData.get(i).name);
            daysTextView.setText(destinationData.get(i).open_days);

            return view;
        }
    }
}
