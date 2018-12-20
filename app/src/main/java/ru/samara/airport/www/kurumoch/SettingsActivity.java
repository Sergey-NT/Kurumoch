package ru.samara.airport.www.kurumoch;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import ru.samara.airport.www.kurumoch.Fragment.LanguageFragment;
import ru.samara.airport.www.kurumoch.Fragment.ThemeDialogFragment;

public class SettingsActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private static final int LAYOUT = R.layout.activity_settings;
    private static final int APP_THEME = R.style.AppDefault;

    private static final String PRODUCT_ID = "www.airport.samara.ru.ads.disable";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArhVXYpo3/I7o/ar/b/a3yDkyBZjLRpHivo+ED5ozZyGo+dkVAmQOCufHubSAFpyT18jet2KQzcTIJvw40axNcfVz+KVh522mr9KNpufDmKJNcXG5Vr1B0fmAxLblCgWtuHH4MxN+O5UElzV/V+2x2MM6m1yZ3WU5NBX2wpJobOCciLk9avzUx/etTPm0rgdj+lMhLHVZ+nFGqtcuILqNw7rgaF9Wh9G6anD74MPFlLCQzUd25QyZbKcdKca15nTUgv3FSPfYNkV7MeTdHRJUJBBdR4VcqujojTpPQQIB0bjX8pnu1XM9l/2uauT1+KcwvZ/sT1rlg2Uyps/ze+Ot4QIDAQAB";
    private static final String MERCHANT_ID = "09670604812027174402";

    private BillingProcessor bp;
    private Button btnAdsDisable;
    private SharedPreferences settings;

    private boolean readyToPurchase = false;


    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        settings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        int appTheme = settings.getInt(Constants.APP_PREFERENCES_APP_THEME, APP_THEME);
        setTheme(appTheme);

        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        // Google Analytics
        Tracker t = ((AppController) getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
        t.enableAdvertisingIdCollection(true);

        initToolbar();

        btnAdsDisable = findViewById(R.id.btnAdsDisable);
        Button btnLanguage = findViewById(R.id.btnLanguage);
        Button btnAdsDisableRestore = findViewById(R.id.btnAdsDisableRecovery);
        Button btnFeedback = findViewById(R.id.btnFeedback);
        CheckBox checkBoxUpdate = findViewById(R.id.checkBoxUpdate);
        CheckBox checkBoxActivateBackground = findViewById(R.id.checkBoxActivateBackground);

        Boolean update = settings.getBoolean(Constants.APP_PREFERENCES_CANCEL_CHECK_VERSION, false);
        Boolean activate = settings.getBoolean(Constants.APP_PREFERENCES_ACTIVATE_BACKGROUND, false);
        String price = settings.getString(Constants.APP_PREFERENCES_ADS_DISABLE_PRICE, "");
        String buttonPriceText = getString(R.string.button_ads_disable) + " " + price;
        String language = settings.getString(Constants.APP_PREFERENCES_LANGUAGE, "ru");
        if (language.equalsIgnoreCase("ru")) {
            String buttonLanguageText = getString(R.string.button_language) + " " + getString(R.string.check_box_language_ru);
            btnLanguage.setText(buttonLanguageText);
        } else {
            String buttonLanguageText = getString(R.string.button_language) + " " + getString(R.string.check_box_language_en);
            btnLanguage.setText(buttonLanguageText);
        }


        checkBoxUpdate.setChecked(update);
        checkBoxActivateBackground.setChecked(activate);

        btnAdsDisable.setText(buttonPriceText);

        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, this);
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(this);
        if(!isAvailable) {
            btnFeedback.setVisibility(View.GONE);
            btnAdsDisableRestore.setVisibility(View.GONE);
            btnAdsDisable.setVisibility(View.GONE);
        }

        checkBoxUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.APP_PREFERENCES_CANCEL_CHECK_VERSION, isChecked);
                editor.apply();
            }
        });

        checkBoxActivateBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.APP_PREFERENCES_ACTIVATE_BACKGROUND, isChecked);
                editor.apply();
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            toolbar.setSubtitle(R.string.menu_settings);
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

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
        getSkuDetails task = new getSkuDetails();
        task.execute();
    }

    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        showToast(getString(R.string.menu_ads_disable_toast));

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.APP_PREFERENCES_ADS_DISABLE, true);
        editor.apply();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {}

    @Override
    public void onPurchaseHistoryRestored() {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    @SuppressLint("StaticFieldLeak")
    private class getSkuDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            SkuDetails list = bp.getPurchaseListingDetails(PRODUCT_ID);

            if (list != null) {
                String price = String.valueOf(list.priceValue);
                String currency = list.currency;
                String textPrice = price + " " + currency;
                final String buttonText = getString(R.string.button_ads_disable) + " " + textPrice;

                if (!settings.getString(Constants.APP_PREFERENCES_ADS_DISABLE_PRICE, "").equals(textPrice)) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Constants.APP_PREFERENCES_ADS_DISABLE_PRICE, textPrice);
                    editor.apply();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnAdsDisable.setText(buttonText);
                        }
                    });
                }
            }
            return null;
        }
    }
    public void btnAdsDisableOnClick (View view) {
        // Google Analytics
        Tracker t = ((AppController) getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.analytics_category_button))
                .setAction(getString(R.string.analytics_action_ads_disable))
                .build());

        if (!readyToPurchase) {
            showToast(getString(R.string.menu_billing_not_initialized));
            return;
        }
        bp.purchase(this, PRODUCT_ID);
    }

    public void btnAdsDisableRecoveryOnClick (View view) {
        // Google Analytics
        Tracker t = ((AppController) getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.analytics_category_button))
                .setAction(getString(R.string.analytics_action_ads_disable_recovery))
                .build());

        if (!readyToPurchase) {
            showToast(getString(R.string.menu_billing_not_initialized));
            return;
        }
        bp.loadOwnedPurchasesFromGoogle();
        if (bp.isPurchased(PRODUCT_ID)) {
            // Сохраняем в настройках
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.APP_PREFERENCES_ADS_DISABLE, true);
            editor.apply();

            showToast(getString(R.string.menu_ads_ads_disable_recovery_true));
        } else {
            // Сохраняем в настройках
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.APP_PREFERENCES_ADS_DISABLE, false);
            editor.apply();

            showToast(getString(R.string.menu_ads_ads_disable_recovery_false));
        }
    }

    public void btnFeedbackOnClick (View view) {
        // Google Analytics
        Tracker t = ((AppController) getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.analytics_category_button))
                .setAction(getString(R.string.analytics_action_feedback))
                .build());
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void btnLanguageOnClick (View view) {
        FragmentManager manager = getSupportFragmentManager();
        LanguageFragment dialogFragment = new LanguageFragment();
        dialogFragment.show(manager, "dialog");
    }

    public void btnThemeOnClick (View view) {
        FragmentManager manager = getSupportFragmentManager();
        ThemeDialogFragment dialogFragment = new ThemeDialogFragment();
        dialogFragment.show(manager, "dialog");
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}