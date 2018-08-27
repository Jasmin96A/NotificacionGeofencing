package com.notificaciones.uteq;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.notificaciones.uteq.Mensaje;
//import com.notificaciones.uteq.Notification;

public class GeofenceTransitionService extends IntentService {

    private static final String TAG = GeofenceTransitionService.class.getSimpleName();
    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    public GeofenceTransitionService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        FirebaseMessaging.getInstance().subscribeToTopic("news"); // instancia para el firebase

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Manejador de  errors
        if (geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;    }
        // Obtiene el tipo de transicion
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Verificar si el tipo de transición es de interés
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence QUE SE ACTIVO
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Obtiene los detalles de la transicion en tipo string
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geoFenceTransition, triggeringGeofences );
            // ENVIAR  DETALLE DE notification COMO UN String
           // sendNotification( geofenceTransitionDetails );
        }
    }

    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }



    private String getGeofenceTransitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {

        FirebaseMessaging.getInstance().subscribeToTopic("news"); // instancia para el firebase
        // obtener la identificación de cada geofence desencadenada
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }
        String status = null, cuerpo = null;
        String usu = Geo.txtuser.getText().toString();
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
            status = usu + " esta Entrando en: ";
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = usu + "esta Saliendo de: ";


        com.notificaciones.uteq.Notification notification =
                new com.notificaciones.uteq.Notification(); // instacion para la notificacion

        //** Se construye el mensaje:
        notification.setTitle(status); // titulo del mensaje firebase(usuario + suceso (entrada o salida))
        cuerpo = TextUtils.join( ", ", triggeringGeofencesList);
        notification.setBody(cuerpo); // cuerpo del mensaje firebase ( lugar)
        notification.getClickAction("TOP_STORY_ACTIVITY");
        Mensaje mensaje1 = new Mensaje(); // instancia para el mensaje
        mensaje1.setTo("/topics/news");
        mensaje1.setNotification(notification);
        sent(mensaje1);

        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }

    // declaracion de la key de la aplicacion
    OkHttpClient client = new OkHttpClient.Builder().writeTimeout(10, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
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

    ServiceApi restClient = retrofit.create(ServiceApi.class);


    // funcion que toma el mensaje y lo presenta como notificacion
    public void sent(Mensaje mensaje){
        Call<Mensaje> call = restClient.create(mensaje);
        call.enqueue(new Callback<Mensaje>() {
            @Override
            public void onResponse(Call<Mensaje> call, retrofit2.Response<Mensaje> response) {
                if (response.code() == 200) {

                }
            }
            @Override
            public void onFailure(Call<Mensaje> call, Throwable t) {
            }
        });
    }

}
