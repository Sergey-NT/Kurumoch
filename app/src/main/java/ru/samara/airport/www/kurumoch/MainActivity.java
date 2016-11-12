package ru.samara.airport.www.kurumoch;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import ru.samara.airport.www.kurumoch.Adapter.TabsPagerFragmentAdapter;
import ru.samara.airport.www.kurumoch.Fragment.InfoDialogFragment;
import ru.samara.airport.www.kurumoch.Fragment.UpdateDialogFragment;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int APP_THEME = R.style.AppDefault;
    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private ViewPager viewPager;
    private Drawer drawerResult;
    private Drawer drawerResultRight;
    private AdView adView;
    private SharedPreferences settings;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String planeNumber;
    private String direction;
    private int versionGooglePlay = 0;
    private int appTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        appTheme = settings.getInt(Constants.APP_PREFERENCES_APP_THEME, APP_THEME);
        setTheme(appTheme);

        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        // Google Analytics
        Tracker t = ((AppController) getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
        t.enableAdvertisingIdCollection(true);

        boolean adDisable = settings.getBoolean(Constants.APP_PREFERENCES_ADS_DISABLE, false);

        if (!adDisable) {
            MobileAds.initialize(getApplicationContext(), getString(R.string.app_identifier));
            initAd(R.id.main_activity_layout);
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {}
        };

        checkPlayServices();

        if (!getSettingsParams(Constants.APP_PREFERENCES_SHOW_DIALOG)) {
            FragmentManager manager = getSupportFragmentManager();
            InfoDialogFragment dialogFragment = new InfoDialogFragment();
            dialogFragment.show(manager, "dialog");
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            direction = extras.getString("direction");
            planeNumber = extras.getString("planeNumber");
        }

        initToolbar(R.string.app_name);
        initTabs();
        initNavigationDrawer();

        if (!getSettingsParams(Constants.APP_PREFERENCES_CANCEL_CHECK_VERSION)) {
            getVersionFromGooglePlay();
        }

        if (direction != null) {
            if (direction.equals("arrival")) {
                viewPager.setCurrentItem(Constants.TAB_ONE);
            } else {
                viewPager.setCurrentItem(Constants.TAB_TWO);
            }
        }
    }

    private void initToolbar(int title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsPagerFragmentAdapter adapter = new TabsPagerFragmentAdapter(getApplicationContext(), planeNumber, direction, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (drawerResult != null && viewPager != null) {
                    drawerResult.setSelection(viewPager.getCurrentItem());
                }
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void initNavigationDrawer() {
        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new SectionDrawerItem()
                                .withName(R.string.menu_title),
                        new PrimaryDrawerItem()
                                .withName(R.string.tabs_item_arrival)
                                .withIcon(GoogleMaterial.Icon.gmd_flight_land)
                                .withIdentifier(0),
                        new PrimaryDrawerItem()
                                .withName(R.string.tabs_item_departure)
                                .withIcon(GoogleMaterial.Icon.gmd_flight_takeoff)
                                .withIdentifier(1),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName(R.string.tabs_item_search)
                                .withIcon(GoogleMaterial.Icon.gmd_search),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_other_airports)
                                .withIcon(GoogleMaterial.Icon.gmd_airplanemode_active),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_settings)
                                .withIcon(GoogleMaterial.Icon.gmd_settings),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_about)
                                .withIcon(GoogleMaterial.Icon.gmd_info_outline)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        if (drawerView == drawerResultRight.getSlider() & getSettingsParams(Constants.APP_PREFERENCES_SHOW_RIGHT_DRAWER)) {
                            setSettingsParams(Constants.APP_PREFERENCES_SHOW_RIGHT_DRAWER, false);

                            if (drawerResult != null && viewPager != null) {
                                drawerResult.setSelection(viewPager.getCurrentItem());
                            }
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        if (drawerView == drawerResult.getSlider() & getSettingsParams(Constants.APP_PREFERENCES_SHOW_RIGHT_DRAWER)) {
                            drawerResultRight.openDrawer();
                        } else if (drawerView == drawerResult.getSlider() & getSettingsParams(Constants.APP_PREFERENCES_SETTINGS)) {
                            setSettingsParams(Constants.APP_PREFERENCES_SETTINGS, false);
                            Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intentSettings);
                        } else if (drawerView == drawerResult.getSlider() & getSettingsParams(Constants.APP_PREFERENCES_ABOUT)) {
                            setSettingsParams(Constants.APP_PREFERENCES_ABOUT, false);
                            Intent intentAbout = new Intent(MainActivity.this, AboutActivity.class);
                            startActivity(intentAbout);
                        } else if (drawerView == drawerResult.getSlider() & getSettingsParams(Constants.APP_PREFERENCES_SEARCH)) {
                            setSettingsParams(Constants.APP_PREFERENCES_SEARCH, false);
                            Intent intentSearch = new Intent(MainActivity.this, SearchActivity.class);
                            startActivity(intentSearch);
                        }
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {}
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem iDrawerItem) {
                        switch (position) {
                            case 1:
                                drawerResult.closeDrawer();
                                viewPager.setCurrentItem(Constants.TAB_ONE);
                                return true;
                            case 2:
                                drawerResult.closeDrawer();
                                viewPager.setCurrentItem(Constants.TAB_TWO);
                                return true;
                            case 4:
                                setSettingsParams(Constants.APP_PREFERENCES_SEARCH, true);
                                drawerResult.closeDrawer();
                                return true;
                            case 5:
                                setSettingsParams(Constants.APP_PREFERENCES_SHOW_RIGHT_DRAWER, true);
                                drawerResult.closeDrawer();
                                return true;
                            case 7:
                                setSettingsParams(Constants.APP_PREFERENCES_SETTINGS, true);
                                drawerResult.closeDrawer();
                                return true;
                            case 8:
                                setSettingsParams(Constants.APP_PREFERENCES_ABOUT, true);
                                drawerResult.closeDrawer();
                                return true;
                        }
                        return false;
                    }
                })
                .build();
        drawerResult.setSelection(0);

        drawerResultRight = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        new SectionDrawerItem()
                                .withName(R.string.menu_title_right),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_koltsovo)
                                .withDescription(R.string.menu_koltsovo_subtitle)
                                .withIcon(GoogleMaterial.Icon.gmd_airplanemode_active),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_rostov_on_don)
                                .withIcon(GoogleMaterial.Icon.gmd_airplanemode_active),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_strigino)
                                .withDescription(R.string.menu_strigino_subtitle)
                                .withIcon(GoogleMaterial.Icon.gmd_airplanemode_active)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                drawerResultRight.closeDrawer();
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getString(R.string.package_koltsovo))));
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getString(R.string.package_koltsovo))));
                                }
                                return true;
                            case 2:
                                drawerResultRight.closeDrawer();
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getString(R.string.package_rostov))));
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getString(R.string.package_rostov))));
                                }
                                return true;
                            case 3:
                                drawerResultRight.closeDrawer();
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getString(R.string.package_strigino))));
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getString(R.string.package_strigino))));
                                }
                                return true;
                        }
                        return false;
                    }
                })
                .withDrawerGravity(Gravity.END)
                .append(drawerResult);
    }

    @Override
    public void onBackPressed() {
        int version = 0;

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (drawerResult != null && drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else if (drawerResultRight != null && drawerResultRight.isDrawerOpen()) {
            drawerResultRight.closeDrawer();
        } else if (version != 0 & versionGooglePlay != 0 & versionGooglePlay > version & !getSettingsParams(Constants.APP_PREFERENCES_CANCEL_CHECK_VERSION)) {
            FragmentManager manager = getSupportFragmentManager();
            UpdateDialogFragment dialogFragment = new UpdateDialogFragment();
            dialogFragment.show(manager, "dialog");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // No call for super(). Bug on API Level > 11.
    }

    @SuppressWarnings("ConstantConditions")
    public void initAd(int layoutId) {
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.ad_view_banner));
        adView.setAdSize(AdSize.SMART_BANNER);

        LinearLayout layout = (LinearLayout)findViewById(layoutId);
        layout.addView(adView);

        AdRequest request = new AdRequest.Builder()
                // Nexus 5
                .addTestDevice("4B954499F159024FD4EFD592E7A5F658")
                // Nexus 4 4.4.4
                .addTestDevice("769FA0ABAACE6F42A12E2AF6BA03F1FC")
                // Samsung GT-P5200
                .addTestDevice("36F1281CF85BE19471A7B8BD82141BDF")
                // Nexus 7
                .addTestDevice("07B4BB1F6E99054B7ED99CF142644BBD")
                .build();

        adView.loadAd(request);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (appTheme != settings.getInt(Constants.APP_PREFERENCES_APP_THEME, APP_THEME)) {
            changeActivityAppTheme();
        }
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        if (drawerResult != null && viewPager != null) {
            drawerResult.setSelection(viewPager.getCurrentItem());
        }
        if (adView != null && getSettingsParams(Constants.APP_PREFERENCES_ADS_DISABLE)) {
            adView.setVisibility(View.GONE);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Constants.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void changeActivityAppTheme() {
        finish();
        final Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean getSettingsParams(String params) {
        return settings.getBoolean(params, false);
    }

    private void setSettingsParams(String params, boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(params, value);
        editor.apply();
    }

    private void getVersionFromGooglePlay() {
        String url = "http://www.avtovokzal.org/php/app_kurumoch/requestVersionCode.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject dataJsonObject = new JSONObject(response);
                        versionGooglePlay = dataJsonObject.getInt("version_app");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        // Установливаем TimeOut, Retry
        strReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Добавляем запрос в очередь
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode)) {
                api.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                if (Constants.LOG_ON) {
                    Log.i(TAG, "This device is not supported.");
                }
                finish();
            }
            return false;
        }
        return true;
    }
}