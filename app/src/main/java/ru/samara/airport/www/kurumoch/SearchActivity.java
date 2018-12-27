package ru.samara.airport.www.kurumoch;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.identification.SdkConfig;
import ru.aviasales.template.ui.fragment.AviasalesFragment;

public class SearchActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_search;
    private final static String TRAVEL_PAYOUTS_MARKER = "64818";
    private final static String TRAVEL_PAYOUTS_TOKEN = "56006c981e1ebbcff3430c6ef2519b1f";
    private final static String SDK_HOST = "www.travel-api.pw";

    private SharedPreferences settings;
    private AviasalesFragment aviasalesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);
        int appTheme = settings.getInt(Constants.APP_PREFERENCES_APP_THEME, Constants.APP_THEME);

        if (Build.VERSION.SDK_INT >= 23) {
            onApplyThemeResource(getTheme(), appTheme, false);
        } else {
            setTheme(appTheme);
        }

        super.onCreate(savedInstanceState);

        AviasalesSDK.getInstance().init(this, new SdkConfig(TRAVEL_PAYOUTS_MARKER, TRAVEL_PAYOUTS_TOKEN, SDK_HOST));
        setContentView(LAYOUT);

        // Google Analytics
        Tracker t = ((AppController) getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
        t.enableAdvertisingIdCollection(true);

        init();
    }

    private void init() {
        initFragment();
        initToolbar();
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        aviasalesFragment = (AviasalesFragment) fm.findFragmentByTag(AviasalesFragment.TAG);

        if (aviasalesFragment == null) {
            aviasalesFragment = (AviasalesFragment) AviasalesFragment.newInstance();
        }

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, aviasalesFragment, AviasalesFragment.TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (!aviasalesFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.APP_PREFERENCES_UPDATE_LIST_FLAG, false);
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }
}