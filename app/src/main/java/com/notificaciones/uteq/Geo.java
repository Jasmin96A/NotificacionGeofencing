package com.notificaciones.uteq;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import com.google.android.gms.dynamic.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Geo extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        ResultCallback<Status> {


        //Variables
        private static final String TAG = MainActivity.class.getSimpleName();
        private GoogleMap map;
        private GoogleApiClient googleApiClient;
        private Location lastLocation;
        Double l1;
        Double l2;

        //Variables Layout
        private TextView textLat, textLong;
        public static TextView txtuser;
        private MapFragment mapFragment;

        public Button btenviar;

        //ajustes de geofence
        private static final long GEO_DURATION = 60 * 60 * 1000;
        private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
        // Crear un Intent enviado por la notificación

        public static Intent makeNotificationIntent(Context context, String msg) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(NOTIFICATION_MSG, msg);
                return intent;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_geo);
                textLat = (TextView) findViewById(R.id.lat);
                textLong = (TextView) findViewById(R.id.lon);
                txtuser = (TextView) findViewById(R.id.name);
                btenviar = (Button) this.findViewById(R.id.btenviar);

                Bundle bundle = this.getIntent().getExtras();
                txtuser.setText(bundle.getString("NOMBRE"));

               // FirebaseMessaging.getInstance().subscribeToTopic("news");

                // initialize GoogleMaps
                initGMaps();

                // create GoogleApiClient
                createGoogleApi();
        }


        // Create GoogleApiClient instance
        private void createGoogleApi() {
                Log.d(TAG, "createGoogleApi()");
                if (googleApiClient == null) {
                        googleApiClient = new GoogleApiClient.Builder(this)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();
                }
        }

        @Override
        protected void onStart() {
                super.onStart();
                // Call GoogleApiClient connection when starting the Activity
                googleApiClient.connect();
        }

        @Override
        protected void onStop() {
                super.onStop();
                // Disconnect GoogleApiClient when stopping Activity
                googleApiClient.disconnect();
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
                Log.i(TAG, "onConnected()");
                getLastKnownLocation();

                //Toast.makeText(this, "DENTRO DEL ONCONNECTED", Toast.LENGTH_SHORT).show();

                //parametros de configracion de geofencing
                String GEOFENCE_REQ_ID; //NOMBRE
                float GEOFENCE_RADIUS = 50.0f;//RADIO DE GEOFENCE


                GEOFENCE_REQ_ID = "Instituto de Informatica";
                GEOFENCE_RADIUS = 25.0f;
                l1 = Double.parseDouble("-1.0125938");
                l2 = Double.parseDouble("-79.470618");

                //Toast.makeText(this, "nombre "+GEOFENCE_REQ_ID, Toast.LENGTH_SHORT).show();

                markerForGeofence(new LatLng(l1, l2), GEOFENCE_REQ_ID, GEOFENCE_RADIUS);
                // Toast.makeText(this, "GEOFENCING "+GEOFENCE_REQ_ID+" CREADO", Toast.LENGTH_SHORT).show();


                GEOFENCE_REQ_ID = "Facultad Empresariales";
                GEOFENCE_RADIUS = 25.0f;
                Double l1 = Double.parseDouble("-1.0121647");
                Double l2 = Double.parseDouble("-79.470063");

                markerForGeofence(new LatLng(l1, l2), GEOFENCE_REQ_ID, GEOFENCE_RADIUS);
                //Toast.makeText(this, "GEOFENCING "+GEOFENCE_REQ_ID+" CREADO", Toast.LENGTH_SHORT).show();


                GEOFENCE_REQ_ID = "Facultad Agrarias";
                GEOFENCE_RADIUS = 25.0f;
                l1 = Double.parseDouble("-1.0129049");
                l2 = Double.parseDouble("-79.469296");

                markerForGeofence(new LatLng(l1, l2), GEOFENCE_REQ_ID, GEOFENCE_RADIUS);
                //Toast.makeText(this, "GEOFENCING "+GEOFENCE_REQ_ID+" CREADO", Toast.LENGTH_SHORT).show();


                GEOFENCE_REQ_ID = "Facultad Ambientales";
                GEOFENCE_RADIUS = 25.0f;
                l1 = Double.parseDouble("-1.0126903");
                l2 = Double.parseDouble("-79.471026");

                markerForGeofence(new LatLng(l1, l2), GEOFENCE_REQ_ID, GEOFENCE_RADIUS);
                //Toast.makeText(this, "GEOFENCING "+GEOFENCE_REQ_ID+" CREADO", Toast.LENGTH_SHORT).show();

                GEOFENCE_REQ_ID = "Casa";
                GEOFENCE_RADIUS = 25.0f;
                l1 = Double.parseDouble("-1.015827");
                l2 = Double.parseDouble("-79.603692");

                markerForGeofence(new LatLng(l1, l2), GEOFENCE_REQ_ID, GEOFENCE_RADIUS);
                //Toast.makeText(this, "GEOFENCING " + GEOFENCE_REQ_ID + " CREADO", Toast.LENGTH_SHORT).show();

        }

        // Initialize GoogleMaps
        private void initGMaps() {
                mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync((OnMapReadyCallback) this);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "onMapReady()");
                map = googleMap;

                //TIPO DE VISTA
                //map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                map.getUiSettings().setZoomControlsEnabled(true);
        }

//PERMISOS----------------------------------------------

        private final int REQ_PERMISSION = 999;

        // Check for permission to access Location
        private boolean checkPermission() {
                Log.d(TAG, "checkPermission()");
                // Ask for permission if it wasn't granted yet
                return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED);
        }

        // Asks for permission
        private void askPermission() {
                Log.d(TAG, "askPermission()");
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQ_PERMISSION
                );
        }

        // Verify user's response of the permission requested
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                Log.d(TAG, "onRequestPermissionsResult()");
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                switch (requestCode) {
                        case REQ_PERMISSION: {
                                if (grantResults.length > 0
                                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                        // Permission granted
                                        getLastKnownLocation();
                                } else {
                                        // Permission denied
                                        permissionsDenied();
                                }
                                break;
                        }
                }
        }

        // App cannot work without the permissions
        private void permissionsDenied() {
                Log.w(TAG, "permissionsDenied()");
                // TODO close app and warn user
        }


        private LocationRequest locationRequest;
        // Defined in mili seconds.
        // This number in extremely low, and should be used only for debug
        private final int UPDATE_INTERVAL = 1000;
        private final int FASTEST_INTERVAL = 900;

        // Start location Updates
        private void startLocationUpdates() {
                Log.i(TAG, "startLocationUpdates()");
                locationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(UPDATE_INTERVAL)
                        .setFastestInterval(FASTEST_INTERVAL);
                if (checkPermission())
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

        @Override
        public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged [" + location + "]");
                lastLocation = location;
                writeActualLocation(location);
        }

        @Override
        public void onConnectionSuspended(int i) {
                Log.w(TAG, "onConnectionSuspended()");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.w(TAG, "onConnectionFailed()");
        }

        @Override
        public void onResult(@NonNull Status status) {
                Log.i(TAG, "onResult: " + status);
        }

        // Get last known location
        private void getLastKnownLocation() {
                Log.d(TAG, "getLastKnownLocation()");
                if (checkPermission()) {
                        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                        if (lastLocation != null) {
                                Log.i(TAG, "LasKnown location. " +
                                        "Long: " + lastLocation.getLongitude() +
                                        " | Lat: " + lastLocation.getLatitude());
                                writeLastLocation();
                                startLocationUpdates();
                        } else {
                                Log.w(TAG, "No location retrieved yet");
                                startLocationUpdates();
                        }
                } else askPermission();
        }

        //UBICACION
        private void writeActualLocation(Location location) {
                textLat.setText("Lat: " + location.getLatitude());
                textLong.setText("Long: " + location.getLongitude());

                //Toast.makeText(this, "Aqui"+location.getLatitude(), Toast.LENGTH_SHORT).show();
                markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        private void writeLastLocation() {
                writeActualLocation(lastLocation);
        }

        //PARA MARKER DE MI UBICACION
        private Marker locationMarker;

        private void markerLocation(LatLng latLng) {
                Log.i(TAG, "markerLocation(" + latLng + ")");
                String title = "Mi Ubicacion es: " + latLng.latitude + ", " + latLng.longitude;
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(title);
                if (map != null) {
                        if (locationMarker != null)
                                locationMarker.remove();
                        locationMarker = map.addMarker(markerOptions);
                        float zoom = 16f;
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
                        map.animateCamera(cameraUpdate);
                }
        }

        //marker para geofence
        private Marker geoFenceMarker;

        private void markerForGeofence(LatLng latLng, String nombregeofen, float radio) {
                Log.i(TAG, "markerForGeofence(" + latLng + ")");
                String title = "" + nombregeofen + ":" + latLng.latitude + ", " + latLng.longitude;
                // Define marker options
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .title(title);
                if (map != null) {
                        // Remove last geoFenceMarker
                        //if (geoFenceMarker != null)
                        //    geoFenceMarker.remove();
                        geoFenceMarker = map.addMarker(markerOptions);
                        drawGeofence(geoFenceMarker, radio);
                        startGeofence(latLng, nombregeofen, radio);
                        //Toast.makeText(this, "Marker de Geofencing creado", Toast.LENGTH_SHORT).show();
                }
        }

        // Start Geofence creation process
        private void startGeofence(LatLng latLng, String nombregeofen, float radio) {
                Log.i(TAG, "startGeofence()");
                if (geoFenceMarker != null) {
                        //Geofence geofence = createGeofence( new LatLng(-1.0126968, -79.4695095999998), GEOFENCE_RADIUS );
                        Geofence geofence = createGeofence(latLng, nombregeofen, radio);
                        GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
                        addGeofence(geofenceRequest);
                } else {
                        Log.e(TAG, "Geofence marker is null");
                }
        }

        // Create a Geofence
        private Geofence createGeofence(LatLng latLng, String nombregeo, float radius) {
                Log.d(TAG, "createGeofence");
                return new Geofence.Builder()
                        .setRequestId(nombregeo)
                        .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                        .setExpirationDuration(GEO_DURATION)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build();
        }

        // Create a Geofence Request
        private GeofencingRequest createGeofenceRequest(Geofence geofence) {
                Log.d(TAG, "createGeofenceRequest");
                return new GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofence(geofence)
                        .build();
        }

        private PendingIntent geoFencePendingIntent;
        private final int GEOFENCE_REQ_CODE = 0;

        private PendingIntent createGeofencePendingIntent() {
                Log.d(TAG, "createGeofencePendingIntent");
                if (geoFencePendingIntent != null)
                        return geoFencePendingIntent;

                Intent intent = new Intent(this, GeofenceTransitionService.class);
                return PendingIntent.getService(
                        this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Add the created GeofenceRequest to the device's monitoring list
        private void addGeofence(GeofencingRequest request) {
                Log.d(TAG, "addGeofence");
                if (checkPermission())
                        LocationServices.GeofencingApi.addGeofences(
                                googleApiClient,
                                request,
                                createGeofencePendingIntent()
                        ).setResultCallback(this);
        }

        //graficar limite
// Draw Geofence circle on GoogleMap
        private Circle geoFenceLimits;

        private void drawGeofence(Marker geoFenceMarker, float radio) {
                Log.d(TAG, "drawGeofence()");

                //if ( geoFenceLimits != null )
                //    geoFenceLimits.remove();

                CircleOptions circleOptions = new CircleOptions()
                        .center(geoFenceMarker.getPosition())
                        .strokeColor(Color.argb(50, 70, 70, 70))
                        .fillColor(Color.argb(100, 150, 150, 150))
                        .radius(radio);
                geoFenceLimits = map.addCircle(circleOptions);
        }


        //****************************CODIGO FIREBASE

       /* OkHttpClient client = new OkHttpClient.Builder().writeTimeout(10, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization",
                                        "key=AAAA5sMQTZg:APA91bEUVFoI4POvKowXleQvUDRwicGWwcv2gVVD364Plx829bQry1zRMVO-WmKmGEKECXuI2tQ8gxjrRm5ePwbEJ5e1MJ7SajDmxem65bdFt0Q3KkbOmpeEbDp8sTRg_Pi4p91NwlkN")
                                .addHeader("Content-Type", "application/json")
                                .build();
                        return chain.proceed(newRequest);
                }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://fcm.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceApi restClient = retrofit.create(ServiceApi.class);*/


}
