package com.example.user.dooropenservice.app.DoorOpenService;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.user.dooropenservice.app.Model.CompanyVO;
import com.example.user.dooropenservice.app.ShakeAlgorithm.ShakeService;

import java.util.ArrayList;

/*
 * DoorOpenService
 * 앱이 백그라운드에서 유지되기 위한 주 서비스클래스
 * function : GPS값을 주기적으로 받아 위치확인 후 ShakeCallback 을 이용해 리스너를 관리
 * 상호작용 : ShakeService(IShakeCallback)
 * @Author : 조재영
 */
public class DoorOpenService extends Service {

    static final int DOOR_OPEN_SERVICE_ID = 1;
    private static ShakeService shakeService;

    double[] distance;

    private static final String TAG = "TEST_GPS";
    private LocationManager locationManager = null;
    private static final long LOCATION_INTERVAL = 1000 * 10;
    private static final long LOCATION_DISTANCE = 10;

    boolean isNetworkEnabled = false;
    boolean isGPSEnabled = false;
    boolean isGetLocation = false;

    private ArrayList<CompanyVO> companyVOArrayList;

    Location mLocation;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class LocationListener implements android.location.LocationListener {

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLocation = new Location(provider);

        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.e(TAG, "onLocationChanged : " + location);

                mLocation.set(location);
                Log.i(TAG, "GPS : " + location.getLatitude() + "/" + location.getLongitude() + "");

                callShakeAlgorithm();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged : " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled : " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled : " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER), new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        removeListeners();
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onCreate() {
        initializeLocationManager();

        setNotification(); //백그라운드 서비스를 유지하기위한 설정 (이거는 유지 onCreate에 있어야 함)

        getLocationManager();
    }

    public void getLocationManager() {
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (!isGPSEnabled && !isNetworkEnabled) {
            //둘다 안될 때 구현
        } else {
            isGetLocation = true;

            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[1]);

                if (locationManager != null) {
                    mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (isGPSEnabled) {
                    if (mLocation == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[0]);
                        if (locationManager != null) {
                            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        companyVOArrayList = (ArrayList<CompanyVO>) intent.getSerializableExtra("ArrayList");

        return START_STICKY;
    }

    //Notification을 추가함으로써 ForeGround에 Service가 실행되고있음을 보임으로써 Service 종료 방지
    public void setNotification() {
//        Intent notificationIntent = new Intent(this,MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,0);
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification.Builder builder = new Notification.Builder(this).setSmallIcon(R.mipmap.jaeho).setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.jaeho)).setTicker("앙 설명띠").setContentTitle("앙 제목띠").setContentText("앙 내용띠").setContentIntent(pendingIntent);
//        notificationManager.notify(0,builder.build());
        startForeground(DOOR_OPEN_SERVICE_ID, new Notification());
    }

    public double getDistance(double targetLat, double targetLon, double myLat, double myLon) {
        double distance = 0;

        Location locationA = new Location("targetGPS");
        locationA.setLatitude(targetLat);
        locationA.setLongitude(targetLon);

        Location locationB = new Location("myGPS");
        locationB.setLatitude(myLat);
        locationB.setLongitude(myLon);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    public void callShakeAlgorithm() {
        distance = new double[companyVOArrayList.size()];

        for (int i = 0; i < companyVOArrayList.size(); i++) {
            distance[i] = getDistance(companyVOArrayList.get(i).getLatitude(), companyVOArrayList.get(i).getLongitude(), mLocation.getLatitude(), mLocation.getLongitude());

            Log.e(TAG, "Distance : " + distance[i] + "");

            if (distance[i] <= companyVOArrayList.get(i).getScope()) {
                shakeService = ShakeService.getInstance(getApplicationContext());
                shakeService.registerListener();

            } else {   //범위 밖으로 벗어난 경우
                if (shakeService != null) {
                    if (shakeService.isListenerSet()) {
                        shakeService.removeListener(); //거리 밖으로 오면 이코드 추가 3줄 다.
                    }
                }
            }
        }
    }

    public void removeListeners() {
        //ShakeService 의 메모리 해제
        if (shakeService != null) {
            if (shakeService.isListenerSet()) {
                shakeService.removeListener();
                shakeService = null;
            }
        }

        if (locationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    locationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception e) {
                    Log.i(TAG, "fail to remove location listeners, ignore", e);
                }
            }
        }
    }
}