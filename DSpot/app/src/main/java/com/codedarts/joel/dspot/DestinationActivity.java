package com.codedarts.joel.dspot;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
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

public class DestinationActivity extends AppCompatActivity {

    public static String LATITUDE = "LATITUDE";
    public static String LONGITUDE = "LONGITUDE";
    public static String SELECTED_DESTINATION = "SELECTED_DESTINATION";

    private double latitude;
    private double longitude;

    private TextView title, date, time, website, description, address, location;
    private ImageView imageView;
    private RatingBar ratingBar;

    private String parentNode;
    private String ratingNode;
    private String destinationListNode;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private int numberOf5StarRatings,numberOf4StarRatings,numberOf3StarRatings,numberOf2StarRatings,numberOf1StarRatings;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        parentNode = getResources().getString(R.string.saved_destinations);
        ratingNode = getResources().getString(R.string.destination_rating);
        destinationListNode = getResources().getString(R.string.destinations_list);

        intent = getIntent();

        if (intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_TITLE) != null) {
            actionBar.setTitle(intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_TITLE));
        }
        else {
            actionBar.setTitle("Selected Destination");
        }

        imageView = (ImageView) findViewById(R.id.roundFrameImage);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        title = (TextView)findViewById(R.id.eventTitleTextView);
        date = (TextView)findViewById(R.id.dateTextView);
        time = (TextView)findViewById(R.id.hoursTextView);
        //website = (TextView)findViewById(R.id.websiteTextView);
        description = (TextView)findViewById(R.id.descriptionTextView);
        //address = (TextView)findViewById(R.id.addressTextView);
        location = (TextView)findViewById(R.id.locationTextView);

        title.setText(String.valueOf(intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_TITLE)));
        date.setText(intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_DATE));
        time.setText(intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_TIME));
        //website.setText(intent.getStringExtra(FeedsFragment.SELECTED_EVENT_WEBSITE));
        description.setText(intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_DESCRIPTION));
        //address.setText(intent.getStringExtra(FeedsFragment.SELECTED_EVENT_DESCRIPTION));
        location.setText(intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_LOCATION));

        latitude = intent.getDoubleExtra(DestinationListActivity.SELECTED_LATITUDE, 0);
        longitude = intent.getDoubleExtra(DestinationListActivity.SELECTED_LONGITUDE, 0);

        Picasso.with(this).load(intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_COVER_URL)).into(imageView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void openMapWithLocation(View view) {
        Intent intent = new Intent(this, MapsActivity.class);

        intent.putExtra(LATITUDE, latitude);
        intent.putExtra(LONGITUDE, longitude);

        startActivity(intent);
    }

    public void viewOrBuyTicket (View view) {

    }

    public void loadWebsite(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_WEBSITE))));
    }

    public void saveDestination(View view) {
        DestinationData destinationData = new DestinationData(
                intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_LOCATION),
                intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_COVER_URL),
                intent.getStringExtra(DestinationListActivity.SELECTED_FACEBOOK),
                intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_DESCRIPTION),
                intent.getDoubleExtra(DestinationListActivity.SELECTED_LATITUDE, 100),
                intent.getDoubleExtra(DestinationListActivity.SELECTED_LONGITUDE, 100),
                intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_TITLE),
                intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_DATE),
                intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_TIME),
                intent.getIntExtra(DestinationListActivity.SELECTED_RATING, 5),
                null,
                intent.getStringExtra(DestinationListActivity.SELECTED_TWITTER),
                intent.getStringExtra(DestinationListActivity.SELECTED_EVENT_WEBSITE)
        );

        databaseReference.child(parentNode + firebaseUser.getUid() + "/" + intent.getStringExtra(DestinationListActivity.SELECTED_KEY)).setValue(destinationData);
    }

    public void openEventsListActivity (View view) {
        startActivity(new Intent(this, EventsListActivity.class));
    }

    public void rateDestination (View view) {
        //databaseReference.child(destinationListNode + intent.getStringExtra(DestinationListActivity.SELECTED_CATEGORY_KEY) + intent.getStringExtra(DestinationListActivity.SELECTED_KEY) + ratingNode).setValue()
    }

    private float calculateWeightMean () {
        return ((numberOf1StarRatings) + (numberOf2StarRatings * 2) + (numberOf3StarRatings * 3) + (numberOf4StarRatings * 4) + (numberOf5StarRatings * 5)) /
                (numberOf1StarRatings + numberOf2StarRatings + numberOf3StarRatings + numberOf4StarRatings + numberOf5StarRatings);
    }

}
