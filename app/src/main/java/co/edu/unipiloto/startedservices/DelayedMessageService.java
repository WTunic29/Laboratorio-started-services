package co.edu.unipiloto.startedservices;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DelayedMessageService extends Service {

    public static final String EXTRA_MESSAGE = "message";
    private static final String TAG = "DelayedMessageService";
    private static final String CHANNEL_ID = "academic_channel";
    private static final int NOTIFICATION_ID = 1001;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {

        super.onCreate();
        crearCanalNotificaciones();
        Log.d(TAG, "Servicio creado");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String message = intent != null ? intent.getStringExtra(EXTRA_MESSAGE) : "Nueva notificación académica";

        executor.execute(() -> {

            try {

                // Simular procesamiento durante 10 segundos.
                Thread.sleep(10000);
                Log.d(TAG, "Mensaje recibido: " + message);
                mostrarNotificacion(message);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                Log.e(TAG, "Proceso interrumpido", e);

            } finally {
                stopSelf(startId);
            }

        });

        return START_NOT_STICKY;

    }

    private void crearCanalNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notificaciones académicas", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notificaciones de calificaciones y actividades");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

    }

    private void mostrarNotificacion(String message) {

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Nueva calificación disponible")
                .setContentText(message)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        executor.shutdownNow();
        Log.d(TAG, "Servicio destruido");
        super.onDestroy();

    }

}