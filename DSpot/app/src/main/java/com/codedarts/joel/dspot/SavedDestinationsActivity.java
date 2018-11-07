package com.codedarts.joel.dspot;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SavedDestinationsActivity extends AppCompatActivity {

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

    private ListView listView;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private SavedDestinationListAdapter savedDestinationListAdapter;

    private String databasePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_destinations);

        databasePath = getResources().getString(R.string.saved_destinations);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = (ListView)findViewById(R.id.listView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Whereas we can setOnClickListeners inside the BaseAdapter, which we did previously,
        //We need to get the specific index of the selected item, not the recycled index.
        //So when views are recycled, they recycle their indexes as well.
        //the setOnItemClickListener keeps track of all items in the list and thus, returns the correct index when an item is selected.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DestinationActivity.class);

                intent.putExtra(SELECTED_EVENT_TITLE, savedDestinationListAdapter.destinationData.get(i).name);
                intent.putExtra(SELECTED_EVENT_DATE, savedDestinationListAdapter.destinationData.get(i).open_days);
                intent.putExtra(SELECTED_EVENT_DESCRIPTION, savedDestinationListAdapter.destinationData.get(i).info);
                intent.putExtra(SELECTED_EVENT_COVER_URL, savedDestinationListAdapter.destinationData.get(i).cover_photo);
                intent.putExtra(SELECTED_EVENT_TIME, savedDestinationListAdapter.destinationData.get(i).open_hours);

                intent.putExtra(SELECTED_EVENT_WEBSITE, savedDestinationListAdapter.destinationData.get(i).website);
                intent.putExtra(SELECTED_FACEBOOK, savedDestinationListAdapter.destinationData.get(i).facebook_link);
                intent.putExtra(SELECTED_TWITTER, savedDestinationListAdapter.destinationData.get(i).twitter_link);

                intent.putExtra(SELECTED_EVENT_LOCATION, savedDestinationListAdapter.destinationData.get(i).address);

                intent.putExtra(SELECTED_LATITUDE, savedDestinationListAdapter.destinationData.get(i).latitude);
                intent.putExtra(SELECTED_LONGITUDE, savedDestinationListAdapter.destinationData.get(i).longitude);
                intent.putExtra(SELECTED_RATING, savedDestinationListAdapter.destinationData.get(i).rating);

                //intent.putExtra(SELECTED_KEY, savedDestinationListAdapter.keys.get(i));

                startActivity(intent);
            }
        });

        displaySaved();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void displaySaved () {
        savedDestinationListAdapter = new SavedDestinationListAdapter();

        databaseReference.child(databasePath + firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    DestinationData value = childSnapshot.getValue(DestinationData.class);
                    savedDestinationListAdapter.destinationData.add(value);
                    savedDestinationListAdapter.notifyDataSetChanged();
                }
                listView.setAdapter(savedDestinationListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class SavedDestinationListAdapter extends BaseAdapter {

        public ArrayList<DestinationData> destinationData = new ArrayList<>();

        @Override
        public int getCount () {
            return destinationData.size();
        }

        @Override
        public Object getItem(int item) {
            return destinationData.get(item);
        }

        @Override
        public long getItemId(int item) {
            return item;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ImageView coverImageView;
            TextView titleTextView;
            TextView daysTextView;

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
