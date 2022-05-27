package com.example.a7_1p;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class oneitem extends AppCompatActivity {

    String title;
    String date;
    String loc;
    public DatabaseHelper db;
    public Cursor cursor;
    String apikey = "AIzaSyBpno1hJ0ZtFKw-BhZ4swwY30lkCmVgpgk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oneitem);
        Intent intent = getIntent();
        title = intent.getStringExtra("buttonText");
        TextView titleText = findViewById(R.id.textViewItemname);
        titleText.setText(title);

        Places.initialize(getApplicationContext(), apikey);

        PlacesClient placesClient = Places.createClient(this);

        db = new DatabaseHelper(this);
        cursor = db.fetchTitle(title);
        cursor.moveToFirst();
        date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        loc = cursor.getString(cursor.getColumnIndexOrThrow("loc"));

        TextView dateText = findViewById(R.id.textViewItemdate);
        TextView locText = findViewById(R.id.textViewItemlocation);

        dateText.setText(date);

        // Define a Place ID.
        final String placeId = loc;

        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            locText.setText(response.getPlace().getName());
            Log.i(TAG, "Place found: " + place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
            }
        });


    }

    public void onClickDelete(View view) {
        if (db.removeItem(title)) {
            Toast.makeText(getApplicationContext(), "Removed from database", Toast.LENGTH_SHORT).show();
            Intent newintent = new Intent(oneitem.this, showads.class);
            startActivity(newintent);
        }
    }
}