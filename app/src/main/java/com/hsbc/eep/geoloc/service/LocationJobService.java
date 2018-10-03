package com.hsbc.eep.geoloc.service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hsbc.eep.geoloc.geoloc.R;
import com.hsbc.eep.geoloc.model.Place;
import com.hsbc.eep.geoloc.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "Started job!");
        getCurrentPlace();
        Util.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    private void getCurrentPlace() {
        PlaceDetectionClient mPlaceDetectionClient =
                Places.getPlaceDetectionClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           Log.i(TAG, "No valid permissions granted!");
        } else {
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);

            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    final List<Place> places = new ArrayList<>();
                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                    if (likelyPlaces != null) {
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            final Place p = new Place(
                                    placeLikelihood.getPlace().getId(),
                                    placeLikelihood.getPlace().getName().toString(),
                                    placeLikelihood.getLikelihood(),
                                    placeLikelihood.getPlace().getAddress(),
                                    placeLikelihood.getPlace().getPhoneNumber()
                            );
                            places.add(p);
                        }
                        sendNotificationIntents(places);

                        likelyPlaces.release();
                    }
                }
            });
        }
    }

    private void sendNotificationIntents(List<Place> places) {
        //TODO send intent to processing service or do filtering here and send notifications
        for (Place p : places) {
            Log.i(TAG, "Got " + p.toString());
        }

        Map<String, String> offersMap = new HashMap<>();
        offersMap.put("ChIJSQEO26gCdkgRIkXQQ-i2MLU","10");
        offersMap.put("ChIJ-7DjHKkCdkgRES9oQjiuC1Q","10");

        String strNot1="There is flat ";
        String strNot2="% off";

        for(int i=0;i<places.size();i++){
            CharSequence id = places.get(i).getId();
            String offer= offersMap.get(id);
            if(offersMap.containsKey(id)) {
                Log.i(TAG, "Creating notification for "+id);
                CharSequence shopName = places.get(i).getName();
                String msg = strNot1 + offer + strNot2 + shopName;
                sendNotification(msg, shopName.toString());
            }
        }
    }

    private void sendNotification(String strNot, String shopName){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("HSBC Offers - "+ shopName)
                .setContentText(strNot)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);//to show content in lock screen

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(strNot.hashCode(), builder.build());
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}
