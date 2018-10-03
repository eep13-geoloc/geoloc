package com.hsbc.eep.geoloc.geoloc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hsbc.eep.geoloc.util.Util;

//TODO move it to service
public class MainActivity extends AppCompatActivity {

    private final int gpsPermissionsReturnCode = 7;
    private final String TAG = "GeoLoc";

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case gpsPermissionsReturnCode: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start();
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
            final String missingPermissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, missingPermissions, gpsPermissionsReturnCode);
        } else {
            start();
        }
    }

    private void start() {
        Util.scheduleJob(getApplicationContext());
    }
}
