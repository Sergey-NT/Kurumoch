package ru.samara.airport.www.kurumoch;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

import java.util.HashMap;
import java.util.Locale;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    public static final String CHANNEL_ID = "KUMUMOCH";

    private RequestQueue mRequestQueue;
    private Locale locale = null;
    private SharedPreferences settings;

    private static AppController mInstance;

    // Google Analytics
    private static final String PROPERTY_ID = "UA-25056266-7";

    public enum TrackerName {
        APP_TRACKER, GLOBAL_TRACKER,
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    public AppController() {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
                    .newTracker(R.xml.app_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
                    .newTracker(PROPERTY_ID) : analytics
                    .newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        configureCrashReporting();
        mInstance = this;

        settings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.APP_PREFERENCES_UPDATE_LIST_FLAG, false);
        editor.apply();

        FixNoClassDefFoundError81083();
        setLocale();

        if (!settings.getBoolean(Constants.APP_PREFERENCES_CHANNEL_CREATE_FLAG, false)) {
            editor.putBoolean(Constants.APP_PREFERENCES_CHANNEL_CREATE_FLAG, true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.channel_name);
                String description = getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_HIGH;

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.enableLights(true);
                channel.setLightColor(Color.GREEN);
                channel.enableVibration(true);
                channel.setSound(defaultSoundUri, audioAttributes);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }
        }
    }

    private void configureCrashReporting() {
        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());
    }

    private void setLocale() {
        String language = settings.getString(Constants.APP_PREFERENCES_LANGUAGE, "ru");

        locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(config, locale);
            updateConfiguration(config);
        }else{
            setSystemLocaleLegacy(config, locale);
            updateConfigurationLegacy(config);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (locale != null) {
            Configuration config = new Configuration(newConfig);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setSystemLocale(config, locale);
                Locale.setDefault(locale);
                updateConfiguration(newConfig);
            }else{
                setSystemLocaleLegacy(config, locale);
                Locale.setDefault(locale);
                updateConfigurationLegacy(newConfig);
            }
        }
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    private void FixNoClassDefFoundError81083() {
        try {
            Class.forName("android.os.AsyncTask");
        }
        catch(Throwable ignore) {}
    }

    @SuppressWarnings("deprecation")
    public void setSystemLocaleLegacy(Configuration config, Locale locale){
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void setSystemLocale(Configuration config, Locale locale){
        config.setLocale(locale);
    }

    @SuppressWarnings("deprecation")
    private void updateConfigurationLegacy(Configuration config) {
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void updateConfiguration(Configuration config) {
        getBaseContext().createConfigurationContext(config);
    }
}