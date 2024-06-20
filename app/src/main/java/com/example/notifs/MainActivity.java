package com.example.notifs;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "your_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_LIKE = "com.example.notifs.ACTION_LIKE";
    private static final String[] GAME_ITEMS = {
            "Staff of Purity", "Axe of Ragnarok", "Sword of Destiny", "Shield of Valor",
            "Bow of Eternity", "Helmet of Wisdom", "Armor of Fortitude", "Boots of Speed",
            "Gloves of Strength", "Ring of Power", "Amulet of Protection", "Cape of Invisibility",
            "Orb of Knowledge", "Dagger of Stealth", "Lance of Justice", "Crown of Kings",
            "Gem of Insight", "Scepter of Command", "Tome of Secrets", "Pendant of Courage"
    };
    private static final int SHAKE_INTERVAL = 1000;

    private ImageView imageView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        handler = new Handler();
        handler.post(shakeRunnable);
    }

    private final Runnable shakeRunnable = new Runnable() {
        @Override
        public void run() {
            shakeImage();
            handler.postDelayed(this, SHAKE_INTERVAL);
        }
    };

    private void shakeImage() {
        ObjectAnimator shakeAnimator = ObjectAnimator.ofFloat(imageView, "translationX", 0f, 25f, -25f, 25f, -25f, 0f);
        shakeAnimator.setDuration(500);
        shakeAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(shakeRunnable);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Your Channel Name";
            String description = "Your Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void triggerNotification(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Random random = new Random();
        String gameItem = GAME_ITEMS[random.nextInt(GAME_ITEMS.length)];

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Intent likeIntent = new Intent(this, NotificationActionReceiver.class);
        likeIntent.setAction(ACTION_LIKE);
        PendingIntent likePendingIntent = PendingIntent.getBroadcast(this, 0, likeIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("YOU GOT A")
                .setContentText(gameItem)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_like, "Like", likePendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Notification permission is required for this feature.", Toast.LENGTH_SHORT).show();
                }
            });
}
