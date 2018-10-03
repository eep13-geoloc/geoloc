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
        Log.i(TAG, "Got " + places.size());

        Map<String, String> offersMap = new HashMap<String, String>();
        offersMap.put("Nike","10");
        offersMap.put("Adidas","10");
        offersMap.put("TGIF","15");
        offersMap.put("Apple Store","5");
        offersMap.put("Samsung","8");

        String strNot1="There is flat ";
        String strNot2="% off on ";
        String strNot3=" Store Near You. Ejoy!!!";
        String finalNotification="";
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<places.size();i++){
            String offer= offersMap.get(places.get(i).getName());

            sb = sb.append(strNot1).append(offer).append(strNot2).append(places.get(i).getName()).append(strNot3);
            sb.append(System.getProperty("line.separator"));
            //finalNotification = finalNotification.concat(strNot1+offer+strNot2+places.get(i).getName()+strNot3+"\n");
        }

        finalNotification=sb.toString();
        sendNotification(finalNotification);
        finalNotification="";

    }

    private void sendNotification(String strNot){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("HSBC Offers")
                .setContentText(strNot)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);//to show content in lock screen

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, builder.build());
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}
