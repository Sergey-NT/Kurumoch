package ru.samara.airport.www.kurumoch.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import ru.samara.airport.www.kurumoch.Adapter.ObjectPlaneAdapter;
import ru.samara.airport.www.kurumoch.AppController;
import ru.samara.airport.www.kurumoch.Constants;
import ru.samara.airport.www.kurumoch.InfoActivity;
import ru.samara.airport.www.kurumoch.ObjectPlane;
import ru.samara.airport.www.kurumoch.R;

public class Fragment extends android.support.v4.app.Fragment {

    private static final int LAYOUT = R.layout.fragment;
    private static final String TAG = "Fragment";

    private List<ObjectPlane> list;
    private ListView listView;
    private TextView textView;
    private Button btnRepeat;
    private ImageButton btnClearEditText;
    private ProgressDialog progressDialog;
    private String direction;
    private String language;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText editText;
    private ObjectPlaneAdapter adapter;
    private SharedPreferences settings;
    private FloatingActionsMenu floatingActionsMenu;
    private List<String> dates;

    public static Fragment getInstance(String direction, String planeNumber) {
        Bundle args = new Bundle();
        Fragment fragment = new Fragment();
        args.putString("direction", direction);
        args.putString("planeNumber", planeNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String planeNumber;

        View view = inflater.inflate(LAYOUT, container, false);

        // Google Analytics
        Tracker t = ((AppController) getActivity().getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
        t.enableAdvertisingIdCollection(true);

        list = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.listView);
        textView = (TextView) view.findViewById(R.id.tvNoInternet);
        btnRepeat = (Button) view.findViewById(R.id.btnRepeat);
        btnClearEditText = (ImageButton) view.findViewById(R.id.btnClearEditText);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_swipe_refresh);
        editText = (EditText) view.findViewById(R.id.searchListView);
        direction = getArguments().getString("direction");
        planeNumber = getArguments().getString("planeNumber");
        settings = getActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        language = settings.getString(Constants.APP_PREFERENCES_LANGUAGE, "ru");
        floatingActionsMenu = (FloatingActionsMenu) view.findViewById(R.id.fam);

        clearEditTextListener();
        editTextListeners();
        listViewListeners();
        refreshListener();
        uploadListView();

        if (planeNumber != null) {
            editText.setText(planeNumber);
        }

        return view;
    }

    private void clearEditTextListener() {
        btnClearEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Google Analytics
                Tracker t = ((AppController) getActivity().getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_category_button))
                        .setAction(getString(R.string.analytics_action_clear_text))
                        .build());

                editText.setText("");
                hideSoftKeyboard();
            }
        });
    }

    private void editTextListeners() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null) {
                    adapter.getFilter().filter(s.toString());
                }
                if (s.length() > 0) {
                    btnClearEditText.setImageResource(R.mipmap.ic_clear_green_24dp);
                } else {
                    btnClearEditText.setImageResource(R.mipmap.ic_clear_black_24dp);
                }
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideSoftKeyboard();
                }
                return false;
            }
        });
    }

    private void listViewListeners() {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                hideSoftKeyboard();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Google Analytics
                Tracker t = ((AppController) getActivity().getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_category_button))
                        .setAction(getString(R.string.analytics_action_plane_info))
                        .build());

                RelativeLayout rl = (RelativeLayout) view;
                TextView tvPlaneFlight = (TextView) rl.getChildAt(0);
                TextView tvPlaneDirection = (TextView) rl.getChildAt(1);
                TextView tvPlaneCombination = (TextView) rl.getChildAt(4);
                TextView tvPlaneType = (TextView) rl.getChildAt(6);
                TextView tvPlaneTimePlan = (TextView) rl.getChildAt(8);
                TextView tvPlaneTimeFact = (TextView) rl.getChildAt(10);
                TextView tvPlaneStatus = (TextView) rl.getChildAt(12);
                TextView tvBaggageStatus = (TextView) rl.getChildAt(14);
                TextView tvCheckInBegin = (TextView) rl.getChildAt(16);
                TextView tvCheckInEnd = (TextView) rl.getChildAt(18);
                TextView tvCheckIn = (TextView) rl.getChildAt(20);
                TextView tvGate = (TextView) rl.getChildAt(22);

                String planeCombination = null;
                String checkInBegin = null;
                String checkInEnd = null;
                String checkIn = null;
                String checkInStatus = null;
                String baggageStatus = null;
                String boardingEnd = null;
                String boardingGate = null;
                String boardingStatus = null;

                String planeFlight = tvPlaneFlight.getText().toString();
                String planeDirection = tvPlaneDirection.getText().toString();
                String planeType = tvPlaneType.getText().toString();

                String planeRoute = tvPlaneFlight.getTag().toString();
                String planeRouteStatus = tvPlaneDirection.getTag().toString();
                String planeAirline = tvPlaneStatus.getTag().toString();

                if (tvPlaneCombination != null) {
                    planeCombination = tvPlaneCombination.getText().toString();
                }
                if (tvCheckInBegin != null) {
                    checkInBegin = tvCheckInBegin.getText().toString();
                }
                if (tvCheckInEnd != null) {
                    checkInEnd = tvCheckInEnd.getText().toString();
                }
                if (tvCheckIn != null) {
                    checkIn = tvCheckIn.getText().toString();
                }
                if (tvPlaneType.getTag() != null) {
                    checkInStatus = tvPlaneType.getTag().toString();
                }
                if (tvBaggageStatus != null) {
                    baggageStatus = tvBaggageStatus.getText().toString();
                }
                if (tvPlaneTimePlan.getTag() != null) {
                    boardingEnd = tvPlaneTimePlan.getTag().toString();
                }
                if (tvGate != null) {
                    boardingGate = tvGate.getText().toString();
                }
                if (tvPlaneTimeFact.getTag() != null) {
                    boardingStatus = tvPlaneTimeFact.getTag().toString();
                }

                Intent intent = new Intent(getActivity(), InfoActivity.class);
                intent.putExtra("planeFlight", planeFlight);
                intent.putExtra("planeDirection", planeDirection);
                intent.putExtra("planeRoute", planeRoute);
                intent.putExtra("planeRouteStatus", planeRouteStatus);
                intent.putExtra("planeCombination", planeCombination);
                intent.putExtra("planeType", planeType);
                intent.putExtra("planeAirline", planeAirline);
                intent.putExtra("baggageStatus", baggageStatus);
                intent.putExtra("checkInBegin", checkInBegin);
                intent.putExtra("checkInEnd", checkInEnd);
                intent.putExtra("checkIn", checkIn);
                intent.putExtra("checkInStatus", checkInStatus);
                intent.putExtra("boardingEnd", boardingEnd);
                intent.putExtra("boardingGate", boardingGate);
                intent.putExtra("boardingStatus", boardingStatus);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Google Analytics
                Tracker t = ((AppController) getActivity().getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_category_button))
                        .setAction(getString(R.string.analytics_action_plane_tacking))
                        .build());

                RelativeLayout rl = (RelativeLayout) view;
                TextView tvPlaneFlight = (TextView) rl.getChildAt(0);
                TextView tvPlaneDirection = (TextView) rl.getChildAt(1);
                TextView tvPlaneTimePlan = (TextView) rl.getChildAt(8);
                TextView tvPlaneTimeFact = (TextView) rl.getChildAt(10);
                TextView tvPlaneStatus = (TextView) rl.getChildAt(12);

                String planeFlight = tvPlaneFlight.getText().toString();
                String planeDirection = tvPlaneDirection.getText().toString();
                String planeTimePlan = tvPlaneTimePlan.getText().toString();
                String planeTimeFact = tvPlaneTimeFact.getText().toString();
                String planeStatus = tvPlaneStatus.getText().toString();

                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);

                switch (planeStatus) {
                    case "Прибыл":
                        showToast(getString(R.string.toast_plane_arrive));
                        break;
                    case "Вылетел":
                        showToast(getString(R.string.toast_plane_departure));
                        break;
                    default:
                        String token = settings.getString(Constants.APP_TOKEN, "");

                        if (!adapter.getInfoTracking(position)) {
                            showToast(getString(R.string.toast_plane_tracking));
                            adapter.setInfoTracking(position);
                            sendQueryToDb(token, direction, planeFlight, planeDirection, planeTimePlan, planeTimeFact, planeStatus);
                        } else {
                            showToast(getString(R.string.toast_cancel_plane_tracking));
                            adapter.setInfoTracking(position);
                            sendDeleteQueryToDb(token, direction, planeFlight, planeTimePlan);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void sendDeleteQueryToDb(String... params) {
        String timePlane = Uri.encode(params[3]);
        String url = "http://www.avtovokzal.org/php/app_kurumoch/deleteQuery.php?token="+params[0]+"&direction="+params[1]+"&flight="+params[2]+"&time_plan="+timePlane;

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        // Установливаем TimeOut, Retry
        strReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Добавляем запрос в очередь
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void sendQueryToDb(String... params) {
        String planeDirection = Uri.encode(params[3]);
        String timePlane = Uri.encode(params[4]);
        String timeFact = Uri.encode(params[5]);
        String status = Uri.encode(params[6]);
        String url = "http://www.avtovokzal.org/php/app_kurumoch/query.php?token="+params[0]+"&direction="+params[1]+"&flight="+params[2]+"&plane_direction="+planeDirection+"&time_plan="+timePlane+"&time_fact="+timeFact+"&status="+status+"&language="+language;

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {}
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
            });
            // Установливаем TimeOut, Retry
            strReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Добавляем запрос в очередь
            AppController.getInstance().addToRequestQueue(strReq);
    }

    private void hideSoftKeyboard () {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        editText.clearFocus();
    }

    private void refreshListener() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccentOrange, R.color.colorAccentBlue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Google Analytics
                Tracker t = ((AppController) getActivity().getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_category_button))
                        .setAction(getString(R.string.analytics_action_refresh))
                        .build());

                uploadListView();
                progressDialogDismiss();
            }
        });
    }

    private void uploadListView() {
        if (isOnline()) {
            textView.setVisibility(View.GONE);
            btnRepeat.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            getXML(direction.substring(0,1));
        } else {
            setErrorTextAndButton();
        }
    }

    private void progressDialogDismiss(){
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            progressDialog = null;
        }
    }


    private void getXML(final String direction) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.main_load_dialog));
        progressDialog.setCancelable(true);
        progressDialog.show();

        String url = "http://www.airport.samara.ru/1linetablo.card.5.19.php?0&0&"+direction;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    response = response.substring(81);
                    response = response.replace("</teaxtarea>","");
                    parsingXML task = new parsingXML();
                    task.execute(response, direction);
                } else {
                    progressDialogDismiss();
                    setErrorTextAndButton();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(Constants.LOG_ON) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());}
                progressDialogDismiss();
                setErrorTextAndButton();
            }
        });
        // Установливаем TimeOut, Retry
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Добавляем запрос в очередь
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getQueryFromServer() {
        String token = settings.getString(Constants.APP_TOKEN, "");

        if (token.length() > 0) {
            String url = "http://www.avtovokzal.org/php/app_kurumoch/requestQuery.php?token="+token;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null && response.length() > 0) {
                        try {
                            JSONObject dataJsonObject = new JSONObject(response);
                            JSONArray arrayJson = dataJsonObject.getJSONArray("query_info");

                            if (arrayJson.length() > 0) {
                                for (int i = 0; i < arrayJson.length(); i++) {
                                    JSONObject oneObject = arrayJson.getJSONObject(i);

                                    String directionFromServer = oneObject.getString("direction");
                                    String planeFlight = oneObject.getString("flight");
                                    String planeDirection = oneObject.getString("plane_direction");
                                    String planeTimePlan = oneObject.getString("time_plan");

                                    if (directionFromServer.equals(direction)) {
                                        adapter.setTrackingInfoFromServer(planeFlight, planeDirection, planeTimePlan);
                                    }
                                }
                            }
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Добавляем запрос в очередь
            AppController.getInstance().addToRequestQueue(stringRequest);
        }
    }

    private class parsingXML extends AsyncTask<String, Void, List<ObjectPlane>> {
        @Override
        protected List<ObjectPlane> doInBackground(String... params) {
            String planeFlight = null;
            String planeDestination = null;
            String planeType = null;
            String planeTimePlan = null;
            String planeTimeFact = null;
            String planeStatus = null;
            String planeRoute = null;
            String planeRouteStatus = null;
            String planeCombination = null;
            String planeAirline = null;
            String baggageStatus = null;
            String registrationBegin = null;
            String registrationEnd = null;
            String gate = null;
            String checkIn = null;
            String checkInStatus = null;
            String boardingStatus = null;
            String boardingEnd = null;
            dates = new ArrayList<>();

            list.clear();

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser parser = factory.newPullParser();
                StringReader reader = new StringReader(params[0]);
                parser.setInput(reader);
                while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                    switch (parser.getEventType()) {
                        case XmlPullParser.START_TAG:
                            if (params[1].equals("a")) {
                                if (parser.getName().compareTo("flight") == 0) {
                                    String planeTypeArriveRU = parser.getAttributeValue(null, "tws_arrive");
                                    String planeTypeArriveEN = parser.getAttributeValue(null, "tws_arrive_eng");
                                    String planeDestinationRU = parser.getAttributeValue(null, "daname");
                                    String planeDestinationEN = parser.getAttributeValue(null, "daname_eng");
                                    String flightName = parser.getAttributeValue(null, "rf");
                                    String flightNumber = parser.getAttributeValue(null, "flt");
                                    String planeTimePlanRU = parser.getAttributeValue(null, "dp");
                                    String planeTimePlanEN = parser.getAttributeValue(null, "dp_eng");
                                    String planeTimeFactRU = parser.getAttributeValue(null, "dr");
                                    String planeTimeFactEN = parser.getAttributeValue(null, "dr_eng");
                                    String planeStatusRU = parser.getAttributeValue(null, "statuzz");
                                    String planeStatusEN = parser.getAttributeValue(null, "statuzz_eng");
                                    String planeCombinationRU = parser.getAttributeValue(null, "sovm");
                                    String planeCombinationEN = parser.getAttributeValue(null, "sovm_eng");
                                    String planeAirlineRU = parser.getAttributeValue(null, "m2");
                                    String planeAirlineEN = parser.getAttributeValue(null, "m2_eng");
                                    planeFlight = flightName + "-" + flightNumber;
                                    if (language.equalsIgnoreCase("en")) {
                                        planeDestination = planeDestinationEN;
                                        planeType = planeTypeArriveEN;
                                        planeTimePlan = planeTimePlanEN;
                                        planeTimeFact = planeTimeFactEN;
                                        planeStatus = planeStatusEN;
                                        planeCombination = planeCombinationEN;
                                        planeAirline = planeAirlineEN;
                                    } else {
                                        planeDestination = planeDestinationRU;
                                        planeType = planeTypeArriveRU;
                                        planeTimePlan = planeTimePlanRU;
                                        planeTimeFact = planeTimeFactRU;
                                        planeStatus = planeStatusRU;
                                        planeCombination = planeCombinationRU;
                                        planeAirline = planeAirlineRU;
                                    }
                                } else if (parser.getName().compareTo("route") == 0) {
                                    String planeRouteRU = parser.getAttributeValue(null, "name");
                                    String planeRouteEN = parser.getAttributeValue(null, "name_eng");
                                    String planeRouteStatusRU = parser.getAttributeValue(null, "status");
                                    String planeRouteStatusEN = parser.getAttributeValue(null, "status_eng");
                                    if (language.equalsIgnoreCase("en")) {
                                        planeRoute = planeRouteEN;
                                        planeRouteStatus = planeRouteStatusEN;
                                    } else {
                                        planeRoute = planeRouteRU;
                                        planeRouteStatus = planeRouteStatusRU;
                                    }
                                } else if (parser.getName().compareTo("baggage") == 0) {
                                    String baggageStatusRU = parser.getAttributeValue(null, "status");
                                    String baggageStatusEN = parser.getAttributeValue(null, "status_eng");
                                    if (language.equalsIgnoreCase("en")) {
                                        baggageStatus = baggageStatusEN;
                                    } else {
                                        baggageStatus = baggageStatusRU;
                                    }
                                }
                                break;
                            } else {
                                if (parser.getName().compareTo("flight") == 0) {
                                    String planeTypeDepartureRU = parser.getAttributeValue(null, "tws_depart");
                                    String planeTypeDepartureEN = parser.getAttributeValue(null, "tws_depart_eng");
                                    String planeDestinationRU = parser.getAttributeValue(null, "daname");
                                    String planeDestinationEN = parser.getAttributeValue(null, "daname_eng");
                                    String flightName = parser.getAttributeValue(null, "rf");
                                    String flightNumber = parser.getAttributeValue(null, "flt");
                                    String planeTimePlanRU = parser.getAttributeValue(null, "dp");
                                    String planeTimePlanEN = parser.getAttributeValue(null, "dp_eng");
                                    String planeTimeFactRU = parser.getAttributeValue(null, "dr");
                                    String planeTimeFactEN = parser.getAttributeValue(null, "dr_eng");
                                    String planeStatusRU = parser.getAttributeValue(null, "statuzz");
                                    String planeStatusEN = parser.getAttributeValue(null, "statuzz_eng");
                                    String planeCombinationRU = parser.getAttributeValue(null, "sovm");
                                    String planeCombinationEN = parser.getAttributeValue(null, "sovm_eng");
                                    String planeAirlineRU = parser.getAttributeValue(null, "m2");
                                    String planeAirlineEN = parser.getAttributeValue(null, "m2_eng");
                                    planeFlight = flightName + "-" + flightNumber;
                                    if (language.equalsIgnoreCase("en")) {
                                        planeDestination = planeDestinationEN;
                                        planeType = planeTypeDepartureEN;
                                        planeTimePlan = planeTimePlanEN;
                                        planeTimeFact = planeTimeFactEN;
                                        planeStatus = planeStatusEN;
                                        planeCombination = planeCombinationEN;
                                        planeAirline = planeAirlineEN;
                                    } else {
                                        planeDestination = planeDestinationRU;
                                        planeType = planeTypeDepartureRU;
                                        planeTimePlan = planeTimePlanRU;
                                        planeTimeFact = planeTimeFactRU;
                                        planeStatus = planeStatusRU;
                                        planeCombination = planeCombinationRU;
                                        planeAirline = planeAirlineRU;
                                    }
                                } else if (parser.getName().compareTo("route") == 0) {
                                    String planeRouteRU = parser.getAttributeValue(null, "name");
                                    String planeRouteEN = parser.getAttributeValue(null, "name_eng");
                                    String planeRouteStatusRU = parser.getAttributeValue(null, "status");
                                    String planeRouteStatusEN = parser.getAttributeValue(null, "status_eng");
                                    if (language.equalsIgnoreCase("en")) {
                                        planeRoute = planeRouteEN;
                                        planeRouteStatus = planeRouteStatusEN;
                                    } else {
                                        planeRoute = planeRouteRU;
                                        planeRouteStatus = planeRouteStatusRU;
                                    }
                                } else if (parser.getName().compareTo("check-in") == 0) {
                                    String checkInStatusRU = parser.getAttributeValue(null, "status");
                                    String checkInStatusEN = parser.getAttributeValue(null, "status_eng");
                                    String checkInRU = parser.getAttributeValue(null, "checkins");
                                    String checkInEN = parser.getAttributeValue(null, "checkins_eng");
                                    registrationBegin = parser.getAttributeValue(null, "dt_b");
                                    registrationEnd = parser.getAttributeValue(null, "dt_e");
                                    if (language.equalsIgnoreCase("en")) {
                                        checkInStatus = checkInStatusEN;
                                        checkIn = checkInEN;
                                    } else {
                                        checkInStatus = checkInStatusRU;
                                        checkIn = checkInRU;
                                    }
                                } else if (parser.getName().compareTo("boarding") == 0) {
                                    String boardingStatusRU = parser.getAttributeValue(null, "status");
                                    String boardingStatusEN = parser.getAttributeValue(null, "status_eng");
                                    boardingEnd = parser.getAttributeValue(null, "dt_e");
                                    String gateRU = parser.getAttributeValue(null, "gate");
                                    String gateEN = parser.getAttributeValue(null, "gate_eng");
                                    if (language.equalsIgnoreCase("en")) {
                                        boardingStatus = boardingStatusEN;
                                        gate = gateEN;
                                    } else {
                                        boardingStatus = boardingStatusRU;
                                        gate = gateRU;
                                    }
                                }
                                break;
                            }
                        case XmlPullParser.END_TAG:
                            if (parser.getName().compareTo("flight") == 0) {
                                String planeDate = planeTimePlan.substring(0, 6);
                                dates.add(planeDate);

                                list.add(new ObjectPlane(planeFlight, planeDestination, planeType, planeTimePlan, planeTimeFact, planeStatus, false, baggageStatus, gate, checkIn, planeCombination, planeRoute, planeRouteStatus, registrationBegin, registrationEnd, checkInStatus, boardingEnd, boardingStatus, planeAirline));
                            }
                            break;

                        default:
                            break;
                    }
                    parser.next();
                }
            } catch (XmlPullParserException | IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialogDismiss();
                        setErrorTextAndButton();
                    }
                });
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<ObjectPlane> list) {
            super.onPostExecute(list);

            if(!isAdded()) {
                return;
            }

            if (list == null || list.size() == 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialogDismiss();
                        setErrorTextAndButton();
                    }
                });
            } else {
                if (adapter == null) {
                    adapter = new ObjectPlaneAdapter(getActivity().getApplicationContext(), list);
                    listView.setAdapter(adapter);
                    adapter.getFilter().filter(editText.getText().toString());
                    getQueryFromServer();
                } else {
                    adapter.notifyDataSetChanged();
                    getQueryFromServer();
                }
            }
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            progressDialogDismiss();
            addFilterButtons();
        }
    }

    private void addFilterButtons() {
        final String[] unique = new HashSet<>(dates).toArray(new String[0]);
        Arrays.sort(unique);
        for (final String title : unique) {
            final FloatingActionButton fab = new FloatingActionButton(getActivity().getApplication());
            fab.setColorNormalResId(R.color.colorPrimary);
            fab.setColorPressedResId(R.color.colorPrimaryDark);
            fab.setTitle(title);

            fab.setIconDrawable(new IconicsDrawable(getActivity().getApplication())
                    .icon(GoogleMaterial.Icon.gmd_date_range)
                    .color(Color.WHITE)
                    .sizeDp(24));
            floatingActionsMenu.addButton(fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showToast(fab.getTitle());
                    if (adapter != null) {
                        editText.setText(title);
                    }
                    floatingActionsMenu.collapse();
                }
            });
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void setErrorTextAndButton(){
        textView.setVisibility(View.VISIBLE);
        btnRepeat.setVisibility(View.VISIBLE);
        if (listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    // Google Analytics
                    Tracker t = ((AppController) getActivity().getApplication()).getTracker(AppController.TrackerName.APP_TRACKER);
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_category_button))
                            .setAction(getString(R.string.analytics_action_repeat))
                            .build());

                    textView.setVisibility(View.GONE);
                    btnRepeat.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    getXML(direction.substring(0,1));
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity().getApplication(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        progressDialogDismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        progressDialogDismiss();
    }
}