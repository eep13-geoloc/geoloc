package com.hsbc.eep.geoloc.geoloc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hsbc.eep.geoloc.model.Place;

//TODO move it to service
public class MainActivity extends AppCompatActivity {

    private final int gpsPermissionsReturnCode = 7;
    private final String TAG = "GeoLoc";

    private void getCurrentPlace() {
        PlaceDetectionClient mPlaceDetectionClient =
                Places.getPlaceDetectionClient(this);

        Task<PlaceLikelihoodBufferResponse> placeResult =
                mPlaceDetectionClient.getCurrentPlace(null);

        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                if (likelyPlaces != null) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        Place p = new Place(placeLikelihood.getPlace().getName().toString(), placeLikelihood.getLikelihood());
                        createNotification(p);
                    }
                    likelyPlaces.release();
                }
            }
        });
    }

    private void createNotification(Place place) {
        Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                place.getName(),
                place.getLikehood()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case gpsPermissionsReturnCode: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentPlace();
                } else {
                    Log.i("LOC", "Failed to grant permission!");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("LOC", "permissions for geo location are not granted");
            final String missingPerimissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, missingPerimissions, gpsPermissionsReturnCode);
        } else {
            getCurrentPlace();
        }
    }
}
