package com.example.a7_1p;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class newad extends AppCompatActivity {

    public DatabaseHelper db;
    String locID;
    String locName;

    String apikey = "AIzaSyBpno1hJ0ZtFKw-BhZ4swwY30lkCmVgpgk";



    public PlacesClient placesClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newad);

        db = new DatabaseHelper(this);
        locV = findViewById(R.id.editTextLoc);

        Places.initialize(getApplicationContext(), apikey);
        placesClient = Places.createClient(this);

        Intent intent = getIntent();
        locID = intent.getStringExtra("placeid");
        locName = intent.getStringExtra("placename");
        locV.setText(locName);
    }

    EditText nameV;
    EditText phV;
    EditText decV;
    EditText dateV;
    EditText locV;

    RadioButton lostbut;
    RadioButton foundbut;

    String name;
    String phone;
    String dec;
    String date;
    String location;

    public void onClickPlace(View view) {
        Intent newintent = new Intent(newad.this, placeActivity.class);
        startActivity(newintent);
    }

    public void onClicksave(View view) {
        nameV = findViewById(R.id.editTextTextPersonName);
        phV = findViewById(R.id.editTextPhone);
        decV = findViewById(R.id.editTextDesc);
        dateV = findViewById(R.id.editTextDate);
        locV = findViewById(R.id.editTextLoc);

        lostbut = findViewById(R.id.radioButtonLost);
        foundbut = findViewById(R.id.radioButtonFound);

        if (lostbut.isChecked())
            name = "Lost " + nameV.getText().toString();
        else if (foundbut.isChecked())
            name = "Found " + nameV.getText().toString();


        phone = phV.getText().toString();
        dec = decV.getText().toString();
        date = dateV.getText().toString();
        location = locID;
        if (location != null) {
            if (db.insert(name, dec, date, phone, location) > 0) {
                Toast.makeText(getApplicationContext(), "Saved to database", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Location ID is null", Toast.LENGTH_SHORT).show();
        }

        Intent newintent = new Intent(newad.this, MainActivity.class);
        startActivity(newintent);
    }

    public void onClickgrab(View view) {

// Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ID);

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    double likelihood = -1;
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        if (placeLikelihood.getLikelihood() > likelihood) {
                            likelihood = placeLikelihood.getLikelihood();
                            locID = placeLikelihood.getPlace().getId();
                            locName = placeLikelihood.getPlace().getName();
                        }
                    }
                    locV.setText(locName);
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Location services not enabled", Toast.LENGTH_SHORT).show();

        }


    }

}