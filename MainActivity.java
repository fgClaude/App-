package com.aaappps.sigaraicme;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {
    
    private TextView todayCountText, messageText, moneyText, lastTimeText, weeklyAvgText, healthText;
    private TextView streakText, motivationText, timeWithoutText;
    private Button smokingButton, statisticsButton, achievementsButton;
    private ProgressBar dailyProgressBar, healthProgressBar;
    private ImageView smokingIcon, healthIcon, moneyIcon, trendIcon;
    private LinearLayout mainContainer, statsContainer;
    private SharedPreferences prefs;
    private Handler notificationHandler, animationHandler;
    private static final String CHANNEL_ID = "SigaraChannel";
    private static final int NOTIFICATION_DELAY = 30 * 60 * 1000; // 30 dakika
    private long lastSmokeTime = 0;
    
    // Zenginleştirilmiş mesajlar
    private String[] funnyMessages = {
        "🔥 Yine ciğerleri közelledik baba!",
        "💨 Bu duman da nereden çıkıyor böyle?",
        "🫁 Akciğerler: 'Artık bırak şunu!'",
        "🚨 Duman alarmı çaldı gene!",
        "💸 Cebindeki para da gitti böyle...",
        "⚠️ Sağlık barın azalıyor dikkat!",
        "🎯 Bu da bugünün kaçıncısı acaba?",
        "🤔 Başka hobi bulamadık mı hala?",
        "📉 Grafiklerde kötü haber var!",
        "🍃 Temiz hava özledik galiba..."
    };
    
    private String[] motivationMessages = {
        "💪 Her gün biraz daha az içmeye çalış!",
        "🌟 Sigarasız bir gün hedefle!",
        "💚 Sağlığın için küçük adımlar at!",
        "🎯 Bugün dünden az içersen kazanırsın!",
        "🏆 Her azaltma bir başarı!",
        "🌱 Yeni bir başlangıç her zaman mümkün!",
        "⭐ Sen yapabilirsin, inan kendine!",
        "🚀 Hedefine doğru ilerle!"
    };
    
    private String[] achievementMessages = {
        "🏅 2 saat sigara içmedin! Harika!",
        "🎖️ 4 saat temiz kaldın! Süper!",
        "🏆 6 saat boyunca sigarasız! Mükemmel!",
        "⭐ 8 saat! Akciğerlerin teşekkür ediyor!",
        "🌟 12 saat! Bu bir rekor olabilir!",
        "💎 1 gün tamamlandı! Efsane!",
        "👑 2 gün! Sen bir kahramsın!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        initPreferences();
        createNotificationChannel();
        setupAnimations();
        updateUI();
        startTimers();
        checkAchievements();
    }
    
    private void initViews() {
        // TextViews
        todayCountText = findViewById(R.id.todayCountText);
        messageText = findViewById(R.id.messageText);
        moneyText = findViewById(R.id.moneyText);
        lastTimeText = findViewById(R.id.lastTimeText);
        weeklyAvgText = findViewById(R.id.weeklyAvgText);
        healthText = findViewById(R.id.healthText);
        streakText = findViewById(R.id.streakText);
        motivationText = findViewById(R.id.motivationText);
        timeWithoutText = findViewById(R.id.timeWithoutText);
        
        // Buttons
        smokingButton = findViewById(R.id.smokingButton);
        statisticsButton = findViewById(R.id.statisticsButton);
        achievementsButton = findViewById(R.id.achievementsButton);
        
        // Progress Bars
        dailyProgressBar = findViewById(R.id.dailyProgressBar);
        healthProgressBar = findViewById(R.id.healthProgressBar);
        
        // ImageViews
        smokingIcon = findViewById(R.id.smokingIcon);
        healthIcon = findViewById(R.id.healthIcon);
        moneyIcon = findViewById(R.id.moneyIcon);
        trendIcon = findViewById(R.id.trendIcon);
        
        // Layouts
        mainContainer = findViewById(R.id.mainContainer);
        statsContainer = findViewById(R.id.statsContainer);
        
        setupClickListeners();
        setupButtonStyles();
    }
    
    private void setupClickListeners() {
        smokingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSmoking();
                animateButton(v);
            }
        });
        
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailedStats();
                animateButton(v);
            }
        });
        
        achievementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAchievements();
                animateButton(v);
            }
        });
    }
    
    private void setupButtonStyles() {
        // Buton stilleri App Builder tarafından XML'den alınacak
        // Gradient background'lar drawable klasöründen otomatik yüklenecek
    }
    
    private void setupAnimations() {
        // Ana container'a giriş animasyonu
        mainContainer.setAlpha(0f);
        mainContainer.animate().alpha(1f).setDuration(1000).start();
        
        // İkonlara döndürme animasyonu
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(smokingIcon, "rotation", 0f, 360f);
        rotateAnim.setDuration(2000);
        rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnim.start();
    }
    
    private void animateButton(View button) {
        // Buton animasyonu
        button.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction(new Runnable() {
                @Override
                public void run() {
                    button.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .setInterpolator(new BounceInterpolator())
                        .start();
                }
            }).start();
    }
    
    private void initPreferences() {
        prefs = getSharedPreferences("SigaraDataAdvanced", Context.MODE_PRIVATE);
        lastSmokeTime = prefs.getLong("last_smoke_time", 0);
    }
    
    private void addSmoking() {
        String today = getCurrentDate();
        int todayCount = prefs.getInt("count_" + today, 0) + 1;
        int totalCount = prefs.getInt("total_count", 0) + 1;
        long currentTime = System.currentTimeMillis();
        
        // Verileri kaydet
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("count_" + today, todayCount);
        editor.putInt("total_count", totalCount);
        editor.putString("last_time", getCurrentTime());
        editor.putLong("last_smoke_time", currentTime);
        editor.apply();
        
        lastSmokeTime = currentTime;
        
        // Animasyonlu mesaj göster
        showAnimatedMessage();
        
        // Sağlık barını güncelle
        updateHealthBar(todayCount);
        
        // Progress bar animasyonu
        animateProgressBar(todayCount);
        
        // UI güncelle
        updateUI();
        
        // Özel toast
        showCustomToast("🚬 Sigara eklendi! Sağlık: -" + (todayCount * 2) + "%");
        
        // Trend ikonu güncelle
        updateTrendIcon(todayCount);
    }
    
    private void showAnimatedMessage() {
        Random random = new Random();
        String message = funnyMessages[random.nextInt(funnyMessages.length)];
        
        messageText.setText(message);
        messageText.setAlpha(0f);
        messageText.animate()
            .alpha(1f)
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(300)
            .withEndAction(new Runnable() {
                @Override
                public void run() {
                    messageText.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .start();
                }
            }).start();
    }
    
    private void updateHealthBar(int todayCount) {
        int healthPercent = Math.max(0, 100 - (todayCount * 8)); // Her sigara %8 azaltır
        
        ObjectAnimator animator = ObjectAnimator.ofInt(healthProgressBar, "progress", 
                                                     healthProgressBar.getProgress(), healthPercent);
        animator.setDuration(500);
        animator.start();
        
        // Sağlık durumu mesajı
        String healthStatus;
        if (healthPercent > 80) {
            healthStatus = "🟢 İyi Durumda";
        } else if (healthPercent > 50) {
            healthStatus = "🟡 Dikkat Et";
        } else if (healthPercent > 20) {
            healthStatus = "🟠 Tehlike";
        } else {
            healthStatus = "🔴 Kritik!";
        }
        
        healthText.setText("Sağlık: " + healthPercent + "% " + healthStatus);
    }
    
    private void animateProgressBar(int todayCount) {
        int maxDaily = 20; // Günlük maksimum limit
        int progress = Math.min(100, (todayCount * 100) / maxDaily);
        
        ObjectAnimator animator = ObjectAnimator.ofInt(dailyProgressBar, "progress", 
                                                     dailyProgressBar.getProgress(), progress);
        animator.setDuration(800);
        animator.start();
    }
    
    private void updateTrendIcon(int todayCount) {
        String yesterday = getDateBefore(1);
        int yesterdayCount = prefs.getInt("count_" + yesterday, 0);
        
        if (todayCount > yesterdayCount) {
            trendIcon.setImageResource(android.R.drawable.arrow_up_float); // Artış
            trendIcon.setColorFilter(Color.parseColor("#F44336")); // Kırmızı
        } else if (todayCount < yesterdayCount) {
            trendIcon.setImageResource(android.R.drawable.arrow_down_float); // Azalış
            trendIcon.setColorFilter(Color.parseColor("#4CAF50")); // Yeşil
        } else {
            trendIcon.setImageResource(android.R.drawable.ic_menu_sort_by_size); // Sabit
            trendIcon.setColorFilter(Color.parseColor("#9E9E9E")); // Gri
        }
    }
    
    private void updateUI() {
        String today = getCurrentDate();
        int todayCount = prefs.getInt("count_" + today, 0);
        int totalCount = prefs.getInt("total_count", 0);
        String lastTime = prefs.getString("last_time", "Henüz içilmedi");
        
        // Ana sayaçlar
        todayCountText.setText("🚬 Bugün: " + todayCount + " Sigara");
        
        // Para hesabı (daha detaylı)
        double todayMoney = todayCount * 4.25;
        double totalMoney = totalCount * 4.25;
        double weeklyMoney = calculateWeeklyMoney();
        
        moneyText.setText(String.format("💸 Bugün: %.2f TL\n📊 Haftalık: %.2f TL\n💰 Toplam: %.2f TL", 
                         todayMoney, weeklyMoney, totalMoney));
        
        lastTimeText.setText("⏰ Son: " + lastTime);
        
        // Haftalık ortalama
        double weeklyAvg = calculateWeeklyAverage();
        weeklyAvgText.setText(String.format("📈 Haftalık Ort: %.1f sigara/gün", weeklyAvg));
        
        // Sigarasız geçen süre
        updateTimeWithoutSmoking();
        
        // Motivasyon mesajı
        showRandomMotivation();
        
        // Streak hesapla
        updateStreak();
    }
    
    private void updateTimeWithoutSmoking() {
        if (lastSmokeTime == 0) {
            timeWithoutText.setText("🌟 Henüz sigara içilmedi!");
            return;
        }
        
        long timeDiff = System.currentTimeMillis() - lastSmokeTime;
        long hours = timeDiff / (1000 * 60 * 60);
        long minutes = (timeDiff % (1000 * 60 * 60)) / (1000 * 60);
        
        timeWithoutText.setText(String.format("🕐 Sigarasız: %d saat %d dakika", hours, minutes));
    }
    
    private void showRandomMotivation() {
        Random random = new Random();
        String motivation = motivationMessages[random.nextInt(motivationMessages.length)];
        motivationText.setText(motivation);
    }
    
    private void updateStreak() {
        // Başarı serisi hesapla (günlük azalma)
        int streak = calculateStreak();
        if (streak > 0) {
            streakText.setText("🔥 " + streak + " gün azalış trendi!");
            streakText.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            streakText.setText("📈 Azaltma trendine başla!");
            streakText.setTextColor(Color.parseColor("#FF9800"));
        }
    }
    
    private int calculateStreak() {
        int streak = 0;
        String today = getCurrentDate();
        int todayCount = prefs.getInt("count_" + today, 0);
        int previousCount = todayCount;
        
        for (int i = 1; i <= 7; i++) {
            String date = getDateBefore(i);
            int count = prefs.getInt("count_" + date, 0);
            
            if (count > 0 && count <= previousCount) {
                streak++;
                previousCount = count;
            } else {
                break;
            }
        }
        
        return streak;
    }
    
    private double calculateWeeklyAverage() {
        int totalWeek = 0;
        int daysWithData = 0;
        
        for (int i = 0; i < 7; i++) {
            String date = getDateBefore(i);
            int count = prefs.getInt("count_" + date, 0);
            totalWeek += count;
            if (count > 0) daysWithData++;
        }
        
        return daysWithData > 0 ? (double) totalWeek / 7 : 0;
    }
    
    private double calculateWeeklyMoney() {
        int totalWeek = 0;
        for (int i = 0; i < 7; i++) {
            String date = getDateBefore(i);
            totalWeek += prefs.getInt("count_" + date, 0);
        }
        return totalWeek * 4.25;
    }
    
    private void showDetailedStats() {
        StringBuilder stats = new StringBuilder("📊 DETAYLI İSTATİSTİKLER 📊\n\n");
        
        // Günlük trend
        stats.append("📅 SON 7 GÜN TRENDİ:\n");
        for (int i = 6; i >= 0; i--) {
            String date = getDateBefore(i);
            int count = prefs.getInt("count_" + date, 0);
            double money = count * 4.25;
            
            String dayName = i == 0 ? "BUGÜN" : i + " gün önce";
            String trend = "";
            
            if (i < 6) {
                String prevDate = getDateBefore(i + 1);
                int prevCount = prefs.getInt("count_" + prevDate, 0);
                if (count > prevCount) trend = " ↗️";
                else if (count < prevCount) trend = " ↘️";
                else trend = " ➡️";
            }
            
            stats.append(String.format("%s: %d sigara - %.2f TL%s\n", 
                        dayName, count, money, trend));
        }
        
        // Genel istatistikler
        int totalCount = prefs.getInt("total_count", 0);
        double totalMoney = totalCount * 4.25;
        double weeklyAvg = calculateWeeklyAverage();
        int streak = calculateStreak();
        
        stats.append(String.format("\n🎯 ÖZET:\n"));
        stats.append(String.format("📊 Haftalık Ortalama: %.1f/gün\n", weeklyAvg));
        stats.append(String.format("💰 Toplam Harcama: %.2f TL\n", totalMoney));
        stats.append(String.format("🔥 Azalış Trendi: %d gün\n", streak));
        
        // Sağlık tahmini
        if (weeklyAvg > 15) {
            stats.append("\n⚠️ Çok yüksek! Azaltmaya odaklan!");
        } else if (weeklyAvg > 10) {
            stats.append("\n🟡 Orta risk. Biraz daha azalt!");
        } else if (weeklyAvg > 5) {
            stats.append("\n🟢 İyi gidiyor! Devam et!");
        } else {
            stats.append("\n⭐ Harika! Bu tempoda devam!");
        }
        
        showCustomToast(stats.toString());
    }
    
    private void showAchievements() {
        StringBuilder achievements = new StringBuilder("🏆 BAŞARILARINIZ 🏆\n\n");
        
        long timeSinceLastSmoke = lastSmokeTime > 0 ? 
                                 System.currentTimeMillis() - lastSmokeTime : 0;
        long hoursWithout = timeSinceLastSmoke / (1000 * 60 * 60);
        
        // Zaman bazlı başarılar
        if (hoursWithout >= 2) achievements.append("🏅 2+ Saat Sigarasız!\n");
        if (hoursWithout >= 4) achievements.append("🎖️ 4+ Saat Temiz!\n");
        if (hoursWithout >= 8) achievements.append("🏆 8+ Saat Süper!\n");
        if (hoursWithout >= 24) achievements.append("⭐ 1+ Gün Mükemmel!\n");
        if (hoursWithout >= 48) achievements.append("💎 2+ Gün Efsane!\n");
        
        // Trend bazlı başarılar
        int streak = calculateStreak();
        if (streak >= 3) achievements.append("🔥 3+ Gün Azalış Trendi!\n");
        if (streak >= 7) achievements.append("👑 1 Hafta Sürekli Azalış!\n");
        
        // Günlük başarılar
        String today = getCurrentDate();
        int todayCount = prefs.getInt("count_" + today, 0);
        String yesterday = getDateBefore(1);
        int yesterdayCount = prefs.getInt("count_" + yesterday, 0);
        
        if (todayCount < yesterdayCount && yesterdayCount > 0) {
            achievements.append("📉 Dünden Az İçtin!\n");
        }
        
        double weeklyAvg = calculateWeeklyAverage();
        if (weeklyAvg < 5) achievements.append("🌟 Haftalık 5'in Altında!\n");
        if (weeklyAvg < 3) achievements.append("✨ Haftalık 3'ün Altında!\n");
        
        if (achievements.length() == 20) { // Sadece başlık varsa
            achievements.append("🎯 Henüz başarı yok.\n");
            achievements.append("💪 İlk hedefin: 2 saat sigarasız!");
        }
        
        achievements.append("\n🚀 Devam et, daha büyük başarılar seni bekliyor!");
        
        showCustomToast(achievements.toString());
    }
    
    private void checkAchievements() {
        if (lastSmokeTime == 0) return;
        
        long timeDiff = System.currentTimeMillis() - lastSmokeTime;
        long hours = timeDiff / (1000 * 60 * 60);
        
        // Başarı bildirimleri
        if (hours == 2 || hours == 4 || hours == 8 || hours == 24) {
            String message = "";
            for (String achievement : achievementMessages) {
                if (achievement.contains(hours + " saat") || 
                    (hours == 24 && achievement.contains("1 gün"))) {
                    message = achievement;
                    break;
                }
            }
            if (!message.isEmpty()) {
                sendAchievementNotification(message);
            }
        }
    }
    
    private void startTimers() {
        notificationHandler = new Handler();
        animationHandler = new Handler();
        
        // Bildirim zamanlayıcısı
        Runnable notificationRunnable = new Runnable() {
            @Override
            public void run() {
                sendSmartNotification();
                notificationHandler.postDelayed(this, NOTIFICATION_DELAY);
            }
        };
        notificationHandler.postDelayed(notificationRunnable, NOTIFICATION_DELAY);
        
        // UI güncelleme zamanlayıcısı
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimeWithoutSmoking();
                checkAchievements();
                animationHandler.postDelayed(this, 60000); // Her dakika
            }
        };
        animationHandler.postDelayed(updateRunnable, 60000);
    }
    
    private void sendSmartNotification() {
        Random random = new Random();
        String[] smartMessages = {
            "🚬 30 dakika geçti! Nasıl gidiyor?",
            "💭 Sigara aklına geldi mi şimdi?",
            "🌟 Bir sigara atla, sağlığın için!",
            "💪 Güçlüsün, dayanabilirsin!",
            "🎯 Bugünkü hedefini hatırla!",
            "🌱 Her dayanış bir kazanç!",
            "⏰ Zaman geçiyor, sen nasılsın?"
        };
        
        String message = smartMessages[random.nextInt(smartMessages.length)];
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🔔 Duman Dedektörü")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setColor(Color.parseColor("#FF6B6B"));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(random.nextInt(1000), builder.build());
    }
    
    private void sendAchievementNotification(String achievement) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.btn_star_big_on)
                .setContentTitle("🏆 Başarı Kazandın!")
                .setContentText(achievement)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColor(Color.parseColor("#4ECDC4"));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(9999, builder.build());
    }
    
    private void showCustomToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
    
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "Sigara Takip Sistemi",
            NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Akıllı sigara takip ve motivasyon sistemi");
        
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
    
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    private String getDateBefore(int daysBefore) {
        long timeInMillis = System.currentTimeMillis() - (daysBefore * 24 * 60 * 60 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(timeInMillis));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationHandler != null) {
            notificationHandler.removeCallbacksAndMessages(null);
        }
        if (animationHandler != null) {
            animationHandler.removeCallbacksAndMessages(null);
        }
    }
}