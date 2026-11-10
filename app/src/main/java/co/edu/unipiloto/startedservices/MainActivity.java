
package co.edu.unipiloto.startedservices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private EditText messageEditText;
    private Button startServiceButton;
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {

                if (granted) {
                    iniciarServicio();
                } else {
                    Toast.makeText(this, "Se necesita permiso para mostrar notificaciones",
                            Toast.LENGTH_LONG).show();
                }

            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageEditText = findViewById(R.id.messageEditText);
        startServiceButton = findViewById(R.id.startServiceButton);

        startServiceButton.setOnClickListener(v -> solicitarPermiso());

    }

    private void solicitarPermiso() {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

        } else {
            iniciarServicio();
        }

    }

    private void iniciarServicio() {

        String message = messageEditText.getText().toString().trim();

        if (message.isEmpty()) {
            message = "El profesor ha publicado una nueva calificación.";
        }

        Intent intent = new Intent(this, DelayedMessageService.class);
        intent.putExtra(DelayedMessageService.EXTRA_MESSAGE, message);
        startService(intent);

        Toast.makeText(this, "Notificación programada para dentro de 10 segundos", Toast.LENGTH_SHORT).show();

    }

}