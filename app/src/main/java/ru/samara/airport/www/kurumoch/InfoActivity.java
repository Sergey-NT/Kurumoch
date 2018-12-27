package ru.samara.airport.www.kurumoch;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class InfoActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_info;

    private SharedPreferences settings;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);
        int appTheme = settings.getInt(Constants.APP_PREFERENCES_APP_THEME, Constants.APP_THEME);

        if (Build.VERSION.SDK_INT >= 23) {
            onApplyThemeResource(getTheme(), appTheme, false);
        } else {
            setTheme(appTheme);
        }

        super.onCreate(savedInstanceState);

        setContentView(LAYOUT);

        // Google Analytics
        Tracker t = ((AppController) getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
        t.enableAdvertisingIdCollection(true);

        String planeFlight = getIntent().getStringExtra("planeFlight");
        String planeDirection = getIntent().getStringExtra("planeDirection");
        String planeType = getIntent().getStringExtra("planeType");
        String planeRoute = getIntent().getStringExtra("planeRoute");
        String planeRouteStatus = getIntent().getStringExtra("planeRouteStatus");
        String planeCombination = getIntent().getStringExtra("planeCombination");
        String planeAirline = getIntent().getStringExtra("planeAirline");
        String baggageStatus = getIntent().getStringExtra("baggageStatus");
        String checkInBegin = getIntent().getStringExtra("checkInBegin");
        String checkInEnd = getIntent().getStringExtra("checkInEnd");
        String checkIn = getIntent().getStringExtra("checkIn");
        String checkInStatus = getIntent().getStringExtra("checkInStatus");
        String boardingEnd = getIntent().getStringExtra("boardingEnd");
        String boardingGate = getIntent().getStringExtra("boardingGate");
        String boardingStatus = getIntent().getStringExtra("boardingStatus");

        String subtitle = getString(R.string.menu_info_subtitle) + " "  + planeFlight + " " + planeDirection;

        initToolbar(subtitle);

        LinearLayout linearLayoutRoute = findViewById(R.id.linearLayoutRoute);
        CardView cardViewCombination = findViewById(R.id.cardViewCombination);
        CardView cardViewBaggageStatus = findViewById(R.id.cardViewBaggage);
        CardView cardViewCheckIn = findViewById(R.id.cardViewCheckIn);
        CardView cardViewBoarding = findViewById(R.id.cardViewBoarding);
        CardView cardViewAirline = findViewById(R.id.cardViewAirline);
        CardView cardViewRoute = findViewById(R.id.cardViewRoute);

        if (planeRoute == null || planeRouteStatus == null) {
            cardViewRoute.setVisibility(View.GONE);
        } else {
            String[] subStringRoute = planeRoute.split(";");
            String[] subStringRouteStatus = planeRouteStatus.split(";");
            addRouteInfoToView(linearLayoutRoute, subStringRoute, subStringRouteStatus);
        }

        TextView tvPlaneType = findViewById(R.id.tvType);
        tvPlaneType.setText(planeType);

        if (planeAirline == null || planeAirline.length() < 2) {
            cardViewAirline.setVisibility(View.GONE);
        } else {
            TextView tvPlaneAirline = findViewById(R.id.tvPlaneAirline);
            tvPlaneAirline.setText(planeAirline);
        }

        if (planeCombination == null || planeCombination.length() < 2) {
            cardViewCombination.setVisibility(View.GONE);
        } else {
            TextView tvCombination = findViewById(R.id.tvPlaneCombination);
            tvCombination.setText(planeCombination);
        }

        if (baggageStatus == null || baggageStatus.length() < 2) {
            cardViewBaggageStatus.setVisibility(View.GONE);
        } else {
            TextView tvBaggageStatus = findViewById(R.id.tvBaggage);
            tvBaggageStatus.setText(baggageStatus);
        }

        if (checkInBegin == null || checkInBegin.length() < 2 || checkInEnd == null || checkIn == null || checkInStatus == null) {
            cardViewCheckIn.setVisibility(View.GONE);
        } else {
            TextView tvCheckInBegin = findViewById(R.id.tvCheckInBegin);
            TextView tvCheckInEnd = findViewById(R.id.tvCheckInEnd);
            TextView tvCheckIn = findViewById(R.id.tvCheckIn);
            TextView tvCheckInStatus = findViewById(R.id.tvCheckInStatus);
            TextView descCheckIn = findViewById(R.id.tvCheckInRegDesc);
            TextView descCheckInStatus = findViewById(R.id.tvCheckInStatusDesc);
            if (checkIn.length() < 2) {
                descCheckIn.setVisibility(View.GONE);
                tvCheckIn.setVisibility(View.GONE);
            } else {
                tvCheckIn.setText(checkIn);
            }
            if (checkInStatus.length() < 2) {
                descCheckInStatus.setVisibility(View.GONE);
                tvCheckInStatus.setVisibility(View.GONE);
            } else {
                tvCheckInStatus.setText(checkInStatus);
            }
            tvCheckInBegin.setText(checkInBegin);
            tvCheckInEnd.setText(checkInEnd);
        }

        if (boardingEnd == null || boardingEnd.length() < 2 || boardingGate == null || boardingStatus == null) {
            cardViewBoarding.setVisibility(View.GONE);
        } else {
            TextView tvBoardingEnd = findViewById(R.id.tvBoardingEnd);
            TextView tvBoardingGate = findViewById(R.id.tvBoardingGate);
            TextView tvBoardingStatus = findViewById(R.id.tvBoardingStatus);
            tvBoardingEnd.setText(boardingEnd);
            tvBoardingGate.setText(boardingGate);
            tvBoardingStatus.setText(boardingStatus);
        }
    }

    private void addRouteInfoToView(LinearLayout linearLayout, String[] subStringRoute, String[] subStringRouteStatus) {
        int countRoute = subStringRoute.length;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;
        float smallestWidth = Math.min(widthDp, heightDp);

        for (int i=0; i<countRoute; i++) {
            String[] subRouteStatus = subStringRouteStatus[i].split("(_!_)");
            TextView tvRoute = new TextView(this);
            tvRoute.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvRoute.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryText));
            tvRoute.setText(subStringRoute[i]);
            if (smallestWidth >= 720) {
                tvRoute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            } else if (smallestWidth >= 600) {
                tvRoute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else {
                tvRoute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }
            linearLayout.addView(tvRoute);
            for (String item : subRouteStatus) {
                TextView tvRouteStatus = new TextView(this);
                tvRouteStatus.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                item = item.replace(" )(",")*").replace(" )",")*").replace(")О","О").replace(")П","П").replace("  "," ").replace(")A","A").replace(")D","D");
                tvRouteStatus.setText(item);
                tvRouteStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondaryText));
                if (smallestWidth >= 720) {
                    tvRouteStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                } else if (smallestWidth >= 600) {
                    tvRouteStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                } else  {
                    tvRouteStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
                linearLayout.addView(tvRouteStatus);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbar(String subTitle) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.menu_info_title);
            toolbar.setSubtitle(subTitle);
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