package com.example.openseainfo;

import static com.teamxdevelopers.SuperChat.utils.ServiceHelper.playAudio;
import static com.teamxdevelopers.SuperChat.utils.ServiceHelper.stopAudio;

import com.google.gson.Gson;
import com.smartwalkie.voicepingdemo.DisconnectConfirmationDialog;
import com.smartwalkie.voicepingdemo.MyPrefs;
import com.smartwalkie.voicepingdemo.TrackerInit;
import com.smartwalkie.voicepingdemo.WeatherMaps;
import com.smartwalkie.voicepingsdk.VoicePing;
import com.smartwalkie.voicepingsdk.model.ChannelType;
import com.teamxdevelopers.SuperChat.activities.main.MainViewModel;
import com.teamxdevelopers.SuperChat.common.ViewModelFactory;
import com.teamxdevelopers.SuperChat.events.UpdateGroupEvent;
import com.teamxdevelopers.SuperChat.fragments.MoreFragment;
import com.teamxdevelopers.SuperChat.job.SaveTokenJob;
import com.teamxdevelopers.SuperChat.job.SetLastSeenJob;
import com.teamxdevelopers.SuperChat.model.constants.MessageType;
import com.teamxdevelopers.SuperChat.model.realms.Chat;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.number.Scale;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smartwalkie.voicepingsdk.VoicePingButton;
import com.teamxdevelopers.SuperChat.R;
import com.teamxdevelopers.SuperChat.activities.main.MainActivity;
import com.teamxdevelopers.SuperChat.activities.main.calls.CallsFragment;
import com.teamxdevelopers.SuperChat.activities.main.chats.FragmentChats;
import com.teamxdevelopers.SuperChat.fragments.MainSettingsFragment;
import com.teamxdevelopers.SuperChat.fragments.Ptt_View;
import com.teamxdevelopers.SuperChat.model.realms.Message;
import com.teamxdevelopers.SuperChat.model.realms.RealmLocation;
import com.teamxdevelopers.SuperChat.model.realms.User;
import com.teamxdevelopers.SuperChat.services.FCMRegistrationService;
import com.teamxdevelopers.SuperChat.services.InternetConnectedListener;
import com.teamxdevelopers.SuperChat.services.NetworkService;
import com.teamxdevelopers.SuperChat.utils.BuildVerUtil;
import com.teamxdevelopers.SuperChat.utils.ContactUtils;
import com.teamxdevelopers.SuperChat.utils.FireConstants;
import com.teamxdevelopers.SuperChat.utils.FireListener;
import com.teamxdevelopers.SuperChat.utils.MessageCreator;
import com.teamxdevelopers.SuperChat.utils.RealmHelper;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.MapView;
import com.teamxdevelopers.SuperChat.utils.ServiceHelper;
import com.teamxdevelopers.SuperChat.utils.SharedPreferencesManager;
import com.teamxdevelopers.SuperChat.utils.UnProcessedJobs;
import com.teamxdevelopers.SuperChat.utils.network.FireManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.RealmResults;

public class MainActivityOSM extends AppCompatActivity implements LifecycleObserver, SensorEventListener {

    private static final int LOCATION_REQUEST_CODE = 1, PERMISSION_REQUEST_CODE = 2;    private static final float DEFAULT_MARKER_SIZE = 1.0f; // Default size multiplier
    private static final float MIN_MARKER_SIZE = 0.1f; // Minimum size on zoom out
    private static final float MAX_MARKER_SIZE = 1.0f; // Maximum size (default size)
    private float currentMarkerSize = DEFAULT_MARKER_SIZE;
    private static final String TAG = "MainActivityOSM";
    private FireListener fireListener = null;

    private MapController mapController;
    private LocationManager locationManager;
    public Location currentLocation;
    private AisStreamWebSocketClient webSocketClient;
    private Handler handler;
    private Button chartplotter_bg, startRecordButton, back_warn_compass2,back_warn_compass;
     private final List<GeoPoint> routePoints = new ArrayList<>();
    private Polyline routePolyline;
    private Polyline userLocationPolyline;
    private boolean isRecording = false;
    private boolean CurrentLocation = false;
    private MapView mapView;
    private Marker marker, UsersOther, userMarker;
    private final Map<String, Marker> userMarkersMap = new HashMap<>(); // To store user markers by user ID
    public int speed;
    public CheckBox save_tracker;
    /////////////// PTT Button ////////////////////////
    private ImageButton Main_Btns, Main_Btn_Eff;
    private MediaPlayer beepSound = null;
    private int channelType = ChannelType.GROUP;
    public int Help_Distance = 50, Ais_Distance = 50;
    private DisconnectConfirmationDialog disconnectConfirmationDialog = null;
    private String receiverUserID;
    private static final int NOTIFICATION_ID = 123; // Notification ID
    private static final String CHANNEL_ID = "foreground_channel"; // Notification Channel ID
    public String PTTUserID = "3";
    public String groupId_ptt = "null";
    public VoicePingButton voicePingButton;
    public EditText editReceiverId, tracker_number;
    boolean isGroup = false;
    ////////////// PTT Button ////////////////////////
    private boolean markersVisible = true; // Track visibility state
    private ImageView Notification_calls;
    private ImageView Notification_msgs;
    private ImageView Notification_account;
    private TextView MsgCount;
    Drawable vectorDrawable ;
    private ImageView layout_main, Overlay, connect_status,rec_on,rec_off;
    Button layoutdragger, remove_UserLoc;
    public RelativeLayout loading_marispeak, input_tracker, get_directions_poly, show_compass_warning, Minimize_bar, layout2, layout3, layout4, show_weather_details, subscribe;
    private TextView connection_quality, OtherUserName, OtherUserName_mini, signal_strenth, temp2, record_plot;
    private Button back_get_sn_number, back_get_sn_number2, set_tracker_number, back_get_directions, back_get_directions2, get_directions_btn, close_subs, btn_subscribe, ptt_Tracker, CloseChat, sea_map_layers, replay_sound, CloseChat_mini, Ptt_Window_btn, getaisButtonOn, getaisButtonOff, getweather, getwindspeed;
    ImageButton Chat, Calls, Account, More, back_btn_weather;
    public User user;
    public RealmResults<User> usersOsm;
    public Chat chat;
    private MainViewModel viewModel;

    String uid;
    boolean exit;
    ValueAnimator animator;
    private TextView speedTextDisplay;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    double Temprature, windSpeedMain;
    String userId;
    public TextView tvResult;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appId = "c658dc66b7ede9267ef33be79330fea3";
    /// MApBox
    String accessToken = "pk.eyJ1IjoiZXZhbjAwNzAwNyIsImEiOiJjbTJoODhkMTcwOHVmMmxweHB3aGFpZmdwIn0.vRJlqTDDKA8ws-73slb2mA"; // Your Mapbox access token

    public DecimalFormat df = new DecimalFormat("#.#");
    TextView ptt_Timer, ptt_timer2;
    private Runnable runnable, areaUsersRunnable;
    private long startTimeInMillis;
    public RealmResults<Message> messageList;
    private boolean isTimerRunning, ShowSeaMaps;
    private boolean isLooping = true;  // A flag to control the loop
    private static final long MAX_TIME_IN_MILLIS = 5000; // 1 minute in milliseconds
    private ImageView getDirections;
    private SensorManager sensorManager;
    private float[] gravity;
    private float[] geomagnetic;
    private float currentDegree = 0f;
    Sensor accelerometer = null ;
    Sensor magneticField = null;
    FrameLayout weather_layers;
    public boolean weatherShown = false, Onlline = true;
    GeoPoint lastCenter;
    int lastZoomLevel;
    GestureDetector gestureDetector = null;
    private TilesOverlay openSeaMapOverlay, temperatureOverlay, rainOverlay, cloudsOverlay;
    private float azimuth = 0f; // Angle of rotation in radians
    double Otherlatitude = 0.0;
    double Otherlongitude = 0.0;
    int zoomLevel;
    String isFromMsgLocation, isFromMsgTracker = "No";
    private WebView webView;

    public FireManager fireManager = new FireManager();

    private ScaleGestureDetector scaleGestureDetector;
    private CompositeDisposable disposables = new CompositeDisposable();

    public MainActivityOSM() {
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Call your method here
            GetVesselsData();
        }
    };
    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_osm);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("GET_VESSELS_DATA"));

        mapView = findViewById(R.id.mapView);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER); // Remove zoom buttons
        mapView.setTileSource(TileSourceFactory.MAPNIK); // OpenStreetMap base layer
        mapController = (MapController) mapView.getController();
        Configuration.getInstance().setCacheMapTileCount((short) 90);
        Configuration.getInstance().setCacheMapTileOvershoot((short) 2);
        mapView.setTilesScaledToDpi(true);


        startRecordButton = findViewById(R.id.startRecordButton);
        Notification_account = findViewById(R.id.account_dot);
        Notification_calls = findViewById(R.id.calls_dot);
        Notification_msgs = findViewById(R.id.msgdot);
        Minimize_bar = findViewById(R.id.minimize_tablayout);
        layout_main = findViewById(R.id.relativeLayout);
        layout2 = findViewById(R.id.layout_main2);
        layout3 = findViewById(R.id.layout_main3);
        layout4 = findViewById(R.id.layout_main4);
        layoutdragger = findViewById(R.id.slider_updown);
        OtherUserName = findViewById(R.id.connected_username);
        OtherUserName_mini = findViewById(R.id.connected_username_2);
        CloseChat = findViewById(R.id.exit_chat);
        CloseChat_mini = findViewById(R.id.exit_chat_mini);
        MsgCount = findViewById(R.id.counter);
        Chat = findViewById(R.id.chat_btn);
        Calls = findViewById(R.id.call_btn);
        replay_sound = findViewById(R.id.replay_sound);
        Account = findViewById(R.id.account_tab);
        More = findViewById(R.id.more_homescreen);
        Ptt_Window_btn = findViewById(R.id.ptt_view_btn);
        Overlay = findViewById(R.id.overlay_ptt);
        Main_Btns = findViewById(R.id.Main_Btn);
        Main_Btn_Eff = findViewById(R.id.Main_Btn_effect);
        getaisButtonOff = findViewById(R.id.getaisButtonOff);
        getaisButtonOn = findViewById(R.id.getaisButtonOn);
        speedTextDisplay = findViewById(R.id.my_speedinknots);
        getwindspeed = findViewById(R.id.getwindspeed);
        getweather = findViewById(R.id.getweather);
        temp2 = findViewById(R.id.temp2);
        tvResult = findViewById(R.id.tvResults);
        show_weather_details = findViewById(R.id.show_weather_details);
        back_btn_weather = findViewById(R.id.back_btn_weather);
        weather_layers = findViewById(R.id.weather_layers);
        ptt_Timer = findViewById(R.id.ptt_Timer);
        ptt_timer2 = findViewById(R.id.textView10);
        userId = FireManager.Companion.getMyUid();  // Make sure this is a unique key (could be user ID, email, etc.)
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        voicePingButton = findViewById(R.id.vpp_button);
        editReceiverId = findViewById(R.id.sending_ptt_msg);
        sea_map_layers = findViewById(R.id.sea_map_layers);
        remove_UserLoc = findViewById(R.id.remove_UserLoc);
        rec_on = findViewById(R.id.rec_on);
        rec_off = findViewById(R.id.rec_off);
        record_plot = findViewById(R.id.record_plot);
        chartplotter_bg = findViewById(R.id.chartplotter_bg);
        vectorDrawable = VectorDrawableCompat.create(getResources(), R.drawable.my_boat3, null);
// Subscribe Panel Functions
        ptt_Tracker = findViewById(R.id.ptt_Tracker);
        subscribe = findViewById(R.id.subscribe);
        btn_subscribe = findViewById(R.id.btn_subscribe);
        close_subs = findViewById(R.id.close_subs);
        /// Loading Src
        loading_marispeak = findViewById(R.id.loading_marispeak);
        loading_marispeak.setVisibility(View.VISIBLE);

        loading_marispeak.setAlpha(1f); // Start with fully visible
        loading_marispeak.animate()
                .alpha(0f)
                .setDuration(3000) // Duration of the scaling down animation
                .start();

// Compass Functions
        back_warn_compass = findViewById(R.id.back_warn_compass);
        back_warn_compass2 = findViewById(R.id.back_warn_compass2);
        show_compass_warning = findViewById(R.id.show_compass_warning);

// Directions Functions
        get_directions_poly = findViewById(R.id.get_directions_poly);
        back_get_directions = findViewById(R.id.back_get_directions);
        back_get_directions2 = findViewById(R.id.back_get_directions2);
        get_directions_btn = findViewById(R.id.get_directions_btn);

// Tracker Functions
        input_tracker = findViewById(R.id.input_tracker);
        tracker_number = findViewById(R.id.tracker_number);
        back_get_sn_number = findViewById(R.id.back_get_sn_number);
        back_get_sn_number2 = findViewById(R.id.back_get_sn_number2);
        set_tracker_number = findViewById(R.id.set_tracker_number);
        save_tracker = findViewById(R.id.save_tracker);

        mapView.setMultiTouchControls(true); // Disable pinch-to-zoom by default

        back_get_sn_number.setOnClickListener(view -> {
            input_tracker.setVisibility(View.GONE);
        });
        back_get_sn_number2.setOnClickListener(view -> {
            input_tracker.setVisibility(View.GONE);
        });

     ///////// Scale Bar //////////////////////////
        final DisplayMetrics dm = this.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
//play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(mScaleBarOverlay);

        ////////////////// Radius Range ///////////////////
        SharedPreferences prefs = this.getSharedPreferences("Help_Distance", Context.MODE_PRIVATE);
        Help_Distance = prefs.getInt("Help_Distance", 50); // The second parameter is the default value if no UID is found

        Log.d("Help_Distance", String.valueOf(Help_Distance));

        ////////////////// Ais Range ///////////////////
        SharedPreferences aisprefs = this.getSharedPreferences("Ais_Distance", Context.MODE_PRIVATE);
        Ais_Distance = aisprefs.getInt("Ais_Distance", 50); // The second parameter is the default value if no UID is found

        Log.d("Ais_Distance", String.valueOf(Ais_Distance));
// distance checker km and meters
      /////////////// dumbed

        save_tracker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences save_tracker = getSharedPreferences("save_tracker", Context.MODE_PRIVATE);
                    SharedPreferences.Editor SaveOsmState = save_tracker.edit();
                    SaveOsmState.putBoolean("save_tracker", true);
                    SaveOsmState.apply();
                    // save the state check box
                }
                else {
                    SharedPreferences save_tracker = getSharedPreferences("save_tracker", Context.MODE_PRIVATE);
                    SharedPreferences.Editor SaveOsmState = save_tracker.edit();
                    SaveOsmState.putBoolean("save_tracker", false);
                    SaveOsmState.apply();
                }
            }
        });

        set_tracker_number.setOnClickListener(view -> {

            SharedPreferences save_tracker_numbers = getSharedPreferences("save_tracker_number", Context.MODE_PRIVATE);
            SharedPreferences.Editor saves_number = save_tracker_numbers.edit();
            saves_number.putString("save_tracker_number", tracker_number.getText().toString());
            saves_number.apply();
            Log.d("saved_tracker_number", String.valueOf(tracker_number));
            input_tracker.setVisibility(View.GONE);
            Intent intent = new Intent(this, TrackerInit.class);
            startActivity(intent);
            // Track Now
        });

        back_get_directions.setOnClickListener(view -> {
            get_directions_poly.setVisibility(View.GONE);
        });
        back_get_directions2.setOnClickListener(view -> {
            get_directions_poly.setVisibility(View.GONE);
        });

        get_directions_btn.setOnClickListener(view -> {
            get_directions_poly.setVisibility(View.GONE);
            RecheckLocationMsg();
        });

        back_warn_compass.setOnClickListener(view -> {
            show_compass_warning.setVisibility(View.GONE);
        }); back_warn_compass2.setOnClickListener(view -> {
            show_compass_warning.setVisibility(View.GONE);
        });
        ////////////////////// TRACKER WINDOW ///////////////////////////////
        ptt_Tracker.setOnClickListener(view -> {
            subscribe.setVisibility(View.VISIBLE);
        });

        btn_subscribe.setOnClickListener(view -> {
            subscribe.setVisibility(View.GONE);
            SharedPreferences save_tracker = getSharedPreferences("save_tracker", Context.MODE_PRIVATE);
            boolean save_tracker_saved = save_tracker.getBoolean("save_tracker", false);

            if(!save_tracker_saved){
                input_tracker.setVisibility(View.VISIBLE); }
            else{
                Intent intent = new Intent(this, TrackerInit.class);
                startActivity(intent);
            }
        });

        close_subs.setOnClickListener(view -> {
            subscribe.setVisibility(View.GONE);

            // Track Now
        });

////


/// /trying to stop repeating map ///
       /* mapView.setHorizontalMapRepetitionEnabled(false);
        mapView.setVerticalMapRepetitionEnabled(false);
        mapView.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(), MapView.getTileSystem().getMinLatitude(), 0);
*/
      /*  MutePTT = findViewById(R.id.mute_ptt);
        UnMutePTT = findViewById(R.id.unmute_ptt);

         MutePTT.setOnClickListener((v -> {
          muteChannel();
        }));

        UnMutePTT.setOnClickListener((v -> {
          unmuteChannel();
        }));
        */
        remove_UserLoc.setOnClickListener(view -> {
            updateIntent();
        });
        MyPrefs myPrefs = new MyPrefs(MyPrefs.Companion);

        editReceiverId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called to notify you that the text is about to change.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called to notify you that the text has changed.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String receiverId = editable.toString().trim();
                voicePingButton.setReceiverId(receiverId);

                beepSound = MediaPlayer.create(getApplicationContext(), R.raw.pttpress);
                if (!receiverId.isEmpty()) {

                } else {

                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enableGps();
        }

        //////////////////// Handlers ////////////////////////////////////////
        // Set up periodic polling for map changes
        handler = new Handler(Looper.getMainLooper());

        //////////////////////////// MariSpeak Tracker Boats ////////////
        // Set up the periodic polling tasks

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                handleGetLocationButton();
            }
        }, 6000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAreaUsers();
            }
        }, 18000);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSeaMap();
            }
        }, 6000);



////////////////// WEATHER UPDATE //////////////////////

     /*   weatherMap.setOnClickListener((view -> {
            Intent intent = new Intent(this, WeatherMaps.class);

            intent.putExtra("latitude", currentLocation.getLatitude());
            intent.putExtra("longitude", currentLocation.getLongitude());

            startActivity(intent);
        }));*/
        getweather.setOnClickListener((view -> {
//// 1 Big 0 Small Tab
            TabController(0);

            show_weather_details.setAlpha(0f); // Start with fully transparent
            show_weather_details.setVisibility(View.VISIBLE); // Make the view visible
             show_weather_details.animate()
                    .alpha(1f)
                    .setDuration(300) // Duration of the scaling down animation
                    .start();


             /////////// Web View Settings  /////////////////////


            if(webView == null){
            webView = findViewById(R.id.webView);
            // Initialize WebView settings
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // Set up WebView client
            webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setBuiltInZoomControls(false);
                webView.getSettings().setDisplayZoomControls(false);
                webView.getSettings().setSupportZoom(false);
            // Load your HTML file
            webView.loadUrl("file:///android_asset/MapViews.html");

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateLocationInWebView();
                }
            }, 15000);

            }
            else{
                webView.onResume();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateLocationInWebView();
                    }
                }, 6000);
            }
        }));


/*
        sea_map_layers.setOnClickListener(view -> {

            if(!ShowSeaMaps) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ShowSeaMaps = true;
                        showSeaMap();
                    }
                }, 500);
            }

        });*/

        back_btn_weather.setOnClickListener((view -> {

//// 1 Big 0 Small Tab
            webView.onPause();
            TabController(1);
            show_weather_details.setAlpha(1f);
            show_weather_details.animate()
                    .alpha(0f)
                    .setDuration(300) // Duration of the scaling down animation
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            show_weather_details.setVisibility(View.GONE);
                        /*    if (!Objects.equals(uid, "defaultValue") && Minimize_bar.getVisibility() == View.VISIBLE)
                                TabFunc();*/
                        }
                    })
                    .start();// Start with fully transparent

        }));
////////////////// Singals Strenth //////////////////
        signal_strenth = findViewById(R.id.signal_strenth);
        connect_status = findViewById(R.id.connect_status);
        connection_quality = findViewById(R.id.connection_quality);
    //    allConnectionData();
       /////////////// AIS init ///////////

/*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentLocation != null)
                    initializeWebSocketClient();
            }
        },3000);*/

        ////////////////////////////
        getaisButtonOff.setOnClickListener(view -> {

                webSocketClient.clearAllMarkers();
                webSocketClient.closeConnection(0, "closed connection");
                getaisButtonOff.setVisibility(View.GONE);
                getaisButtonOn.setVisibility(View.VISIBLE);

        });

       // initializeWebSocketClient();

        getaisButtonOn.setOnClickListener(view -> {


                initializeWebSocketClient();
            //    GeoPoint userLocation = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                // Calculate the bounding box with a 50km radius
            //    BoundingBox boundingBox = getBoundingBoxForLocation(userLocation); // Adjusted to 50km

              /*  handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Initialize WebSocket client or send subscription request
                        webSocketClient.sendSubscriptionRequest(boundingBox);
                    }
                }, 2800);*/

            getaisButtonOn.setVisibility(View.GONE);
            getaisButtonOff.setVisibility(View.VISIBLE);

        });


        //////////////////////// Sensor for Compass ////////////////////////

        getDirections = findViewById(R.id.getDirections);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        SharedPreferences CompassWarn = getSharedPreferences("CompassWarn", Context.MODE_PRIVATE);
        boolean isCompassWarn = CompassWarn.getBoolean("CompassWarn", false);


        if (!isSensorAvailable(Sensor.TYPE_MAGNETIC_FIELD) && !isCompassWarn || !isSensorAvailable(Sensor.TYPE_ACCELEROMETER) && !isCompassWarn) {
            SharedPreferences.Editor SaveOsmState = CompassWarn.edit();
            SaveOsmState.putBoolean("CompassWarn", true);
            SaveOsmState.apply();

            show_compass_warning.setAlpha(0f); // Start with fully transparent
            show_compass_warning.setVisibility(View.VISIBLE); // Make the view visible
            show_compass_warning.animate()
                    .alpha(1f)
                    .setDuration(300) // Duration of the scaling down animation
                    .start();
            Log.d("No Sensors", "Yes");
            Toast.makeText(MainActivityOSM.this, "ACCELEROMETER and Magnetic Sensors not available Using GPS Direction Instead! That May Not Accurately Work", Toast.LENGTH_LONG).show();
        }
        else {

            Log.d("Have Sensors", "Yes");
            magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        //////////////////////////////////////////////////
        isActivityRunning = true;

        Ptt_Window_btn.setOnClickListener(view -> {



            Ptt_View myFragment = new Ptt_View();
            // Create a Bundle to hold your data
            Bundle args = new Bundle();
            args.putString("ptt_time", (String) ptt_Timer.getText()); // Replace with your actual key-value pairs
            myFragment.setArguments(args);
            // Add the fragment to the activity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.other_frags, myFragment) // R.id.fragment_container is the ID of your FrameLayout or other container
                    .addToBackStack(null) // Add this line
                    .commit();
        });


        SharedPreferences OnOsmPage = this.getSharedPreferences("fromOSMscreen", Context.MODE_PRIVATE);
        SharedPreferences.Editor SaveOsmState = OnOsmPage.edit();
        SaveOsmState.putBoolean("fromOSMscreen", true);
        SaveOsmState.apply();


        Chat.setOnClickListener(view -> {

            Calls.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
            Chat.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.grey));
            Account.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
            More.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
             /////////////// For old view False
            SharedPreferences sharedPrefs = this.getSharedPreferences("fromOSM", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor3 = sharedPrefs.edit();
            editor3.putBoolean("fromOSM", false);
            editor3.apply();   /////////////// For old view False

            Intent intent = new Intent(this, MainActivity.class);
           // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        Calls.setOnClickListener(view -> {

            Calls.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.grey));
            Chat.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
            Account.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
            More.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
///////////// For New View
                SharedPreferences fromOSM = getSharedPreferences("fromOSM", MODE_PRIVATE);
                SharedPreferences.Editor editor = fromOSM.edit();
                editor.putBoolean("fromOSM", true);
                editor.apply();
  ////////////////////////
            // Create a new instance of the fragment
                CallsFragment myFragment = new CallsFragment();

//// 1 Big 0 Small Tab if Connected with any user other wise stays same coonnection state only
       //     TabController(0);
                // Add the fragment to the activity
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.call_frags, myFragment) // R.id.fragment_container is the ID of your FrameLayout or other container
                        .addToBackStack(null) // Add this line
                        .commit();
                //  layout2.setVisibility(View.GONE);
        });
        Account.setOnClickListener(view -> {
            Calls.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
            Chat.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
            Account.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.grey));
            More.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));

///////////// For New View true
            SharedPreferences fromOSM = getSharedPreferences("fromOSM", MODE_PRIVATE);
            SharedPreferences.Editor editor = fromOSM.edit();
            editor.putBoolean("fromOSM", true);
            editor.apply();

                MainSettingsFragment myFragment = new MainSettingsFragment();

                // Add the fragment to the activity
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.other_frags, myFragment) // R.id.fragment_container is the ID of your FrameLayout or other container
                        .addToBackStack(null) // Add this line
                        .commit();
        });

        More.setOnClickListener(view -> {
            Calls.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
            Chat.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
            Account.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
            More.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.grey));

///////////// For New View true
            SharedPreferences fromOSM = getSharedPreferences("fromOSM", MODE_PRIVATE);
            SharedPreferences.Editor editor = fromOSM.edit();
            editor.putBoolean("fromOSM", true);
            editor.apply();

            MoreFragment myFragment = new MoreFragment();

            // Add the fragment to the activity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.other_frags, myFragment) // R.id.fragment_container is the ID of your FrameLayout or other container
                    .addToBackStack(null) // Add this line
                    .commit();

        });


        CloseChat.setOnClickListener(view -> {
            Disconnect();
        });
        CloseChat_mini.setOnClickListener(view -> {
           Disconnect();
        });

        TabController(1);

        layoutdragger.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        TabFunc();
                        return true;
                }
                return false;
            }
        });

        beepSound = MediaPlayer.create(getApplicationContext(), R.raw.pttpress);

        Main_Btns.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               SharedPreferences pre = getSharedPreferences("UID", MODE_PRIVATE);
                uid = pre.getString("UID", "defaultValue");

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//////////////////////// View Swtiching ///////////////////////////
                        SharedPreferences sharedPrefs = getSharedPreferences("fromOSM", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor3 = sharedPrefs.edit();
                        editor3.putBoolean("fromOSM", true);
                        editor3.apply();
                            // Create a new instance of the fragment
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    String fragmentTag = "MyFragmentTag"; // Unique tag for your fragment

// Check if the fragment is already in the fragment manager
                                    Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
                                    if (existingFragment != null && existingFragment.isVisible()) {
                                        // The fragment is already opened and visible
                                        Log.d("FragmentCheck", "Fragment is already opened.");
                                    } else {
                                        // Create a new instance of the fragment
                                        FragmentChats myFragment = new FragmentChats();

                                        // Replace the fragment if it's not already visible
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.call_frags, myFragment, fragmentTag) // Use 'replace' instead of 'add'
                                                .addToBackStack(null) // Add to back stack for navigation
                                                .commit();
                                    }
                                }
                            }, 20);

                        return true;
                }
                return false;
            }
        });
////////////// Adjusting Zoom to map view or boat icons //////////////////
        if (mapView != null && mapView.getScaleY() >= 1.1f) {
            // Adjust the scale of the MapView based on zoom level
            float scaleFactor = (float) (14 * 0.15);
            mapView.setScaleX(scaleFactor);
            mapView.setScaleY(scaleFactor);
            Log.d("MapView Scaling", "Scale Factor: " + scaleFactor);
            // Refresh the map view to apply changes
        } else {
            if (mapView != null) {
                mapView.setScaleX(1.0f);
                mapView.setScaleY(1.0f);
            }
        }

        startRecordButton.setOnClickListener(view -> {
          //  subscribe.setVisibility(View.VISIBLE);
            startRecordingRoute();
        }) ;
        // Set minimum and maximum zoom levels
        mapView.setMinZoomLevel(5.0);  // Example minimum zoom level
        mapView.setMaxZoomLevel(18.0); // Example maximum zoom level
/////////////////////// Setting zoom level icons ///////////////////////////////

//////////////// Notification check ///////////////////////////////
        Checknotification();
        // speed testing
//////////////////// Replay Button /////////////
/*

        replay_sound.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (!Objects.equals(uid, "defaultValue")) {

                    switch (motionEvent.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            replay_sound.animate()
                                    .scaleX(1.1f)
                                    .scaleY(1.1f)
                                    .setDuration(500)
                                    .start();
                            chat.getChatId().lastIndexOf(String.valueOf(messageList));
                            playAudio(MainActivityOSM.this, chat.getLastMessage().getMessageId(), chat.getLastMessage().getLocalPath(), chat.getChatId().lastIndexOf(String.valueOf(messageList)), 1);
                            Log.d("Msgs list", String.valueOf(chat.getChatId().lastIndexOf(String.valueOf(messageList))));
                            return true;

                        case MotionEvent.ACTION_UP:
                            replay_sound.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(500)
                                    .start();
                        case MotionEvent.ACTION_CANCEL:
                            stopAudio(MainActivityOSM.this); // Stop playback
                    }
                }
                return false;
            }
        });

*/

        File file = new File(getFilesDir(), "route_points.json");
        if (!file.exists()) {
            chartplotter_bg.setVisibility(View.GONE);
            replay_sound.setVisibility(View.GONE);
        }
        else {
            chartplotter_bg.setVisibility(View.VISIBLE);
            replay_sound.setVisibility(View.VISIBLE);
        }
replay_sound.setOnClickListener(view -> {
    if (!file.exists()) {
        chartplotter_bg.setVisibility(View.GONE);
        replay_sound.setVisibility(View.GONE);
        Toast.makeText(this, "No route points to display.", Toast.LENGTH_LONG).show();
        return; // File does not exist, so no points to load
    }
    else {
        loadRoutePointsAndDisplayOnMap(mapView);
    }
});
        ////////////////////////
       // resetRotationButton.setOnClickListener(v -> mapView.resetRotation());


        Button getLocationButton = findViewById(R.id.getLocationButton);
        getLocationButton.setOnClickListener(v -> handleGetLocationButton());

        // Initialize map tracking variables
        lastCenter = (GeoPoint) mapView.getMapCenter();
        lastZoomLevel = mapView.getZoomLevel();

        voicePingButton.setListener(new VoicePingButton.Listener() {
            @Override
            public void onStarted() {
                handleVoicePingStarted();
            }

            @Override
            public void onStopped() {
                handleVoicePingStopped();
            }

            @Override
            public void onError(String errorMessage) {
                handleVoicePingError(errorMessage);
            }
        });



//////////// Requesting weather until location available
      //  RequestWeather();
       /* mapUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mapView != null) {
                    checkMapChanges();
                }
                handler.postDelayed(this, POLL_INTERVAL_MS);
            }
        };
        handler.post(mapUpdateRunnable);*/

   //     GestureDetector();
// Set up the touch listener for the MapView
  /*      mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event); // Pass touch events to the GestureDetector
                return false; // Let other events be handled normally
            }
        });*/
/////////////////// Backend and Back ground services //////////////////////////
        fireListener = new FireListener();

        startServices();

        viewModel = new ViewModelProvider(
                this,
                new ViewModelFactory(getApplication())
        ).get(MainViewModel.class);

        usersOsm = RealmHelper.getInstance().getListOfUsers();


        fetchStatuses();
        /////////////// end create view ////////////////////

    }

    private void startServices() {
        if (!BuildVerUtil.isOreoOrAbove()) {
            // Starting services on devices below Oreo
            startService(new Intent(this, NetworkService.class));
            startService(new Intent(this, InternetConnectedListener.class));
            startService(new Intent(this, FCMRegistrationService.class));
        } else {
            // Starting jobs on Oreo and above
            if (!SharedPreferencesManager.isTokenSaved()) {
                SaveTokenJob.schedule(this, null);
            }
            SetLastSeenJob.schedule(this);
            UnProcessedJobs.process(this);
        }

        // Sync contacts for the first time if needed
        if (!SharedPreferencesManager.isContactSynced()) {
            syncContacts();
        } else {
            // Sync contacts daily if needed
            if (SharedPreferencesManager.needsSyncContacts()) {
                syncContacts();
            }
        }


    }

    /////////////////////// WebView Map /////////////////////////////////
    private void updateLocationInWebView() {
        if (currentLocation.getLatitude() != 0.0 && currentLocation.getLongitude() != 0.0) {
            String javascript = "updateMapWithDefaultLocation(" + currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + ")";
            webView.evaluateJavascript(javascript, null);
        }
    }




//////////////////////////////////////////////////////////////////////////

    public void GetTimer(String value){
        ptt_Timer.setText(value);
        ptt_timer2.setText(value);
    }
/*public void GestureDetector() {
// Create a custom GestureDetector to handle pinch zoom
    gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Check for multi-touch
            if (e1.getPointerCount() > 1 && e2.getPointerCount() > 1) {
                adjustMarkerSize(); // Adjust marker size only during pinch zoom
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    });
}*/
    //////////////// GPS Heading ////////////////////
    // Call this method whenever you receive a new heading

    public void Disconnect(){
        SharedPreferences prefs = getSharedPreferences("UID", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("UID", "defaultValue");
        editor.apply();

        resetBottomBarBtns();
//// 1 Big 0 Small Tab if Connected with any user other wise stays same coonnection state only
        TabController(0);
    }


    // Method to handle the start of voice ping
    public void handleVoicePingStarted() {
        startCountUpTimer();
      //  startRepeatingTask();

        // Create and start the scale animation
        animator = ValueAnimator.ofFloat(1.7f, 1.9f);
        animator.setDuration(300); // Duration for one scale change
        animator.setRepeatCount(ValueAnimator.INFINITE); // Repeat indefinitely
        animator.setRepeatMode(ValueAnimator.REVERSE); // Reverse back to original
        animator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            Main_Btn_Eff.setScaleX(scale);
            Main_Btn_Eff.setScaleY(scale);
        });
        animator.start();

        // Scale up the button
        voicePingButton.animate()
                .scaleX(1.4f)
                .scaleY(1.4f)
                .setDuration(300) // Duration of the scaling up animation
                .start();

        Overlay.setVisibility(View.VISIBLE);
        vibrateDevice();
        Log.d("VoicePingButton", "PTT onStarted");

        if (beepSound != null) {
            beepSound.start();
        }
    }

    // Method to handle the stop of voice ping
    public void handleVoicePingStopped() {
        stopTimer();
        stopRepeatingTask();
        Log.d("fromOSM3", uid);

        // Reset scale with animation
        if (animator != null) {
            animator.cancel();
        }

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(Main_Btn_Eff, "scaleX", 1.8f, 0.9f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(Main_Btn_Eff, "scaleY", 1.8f, 0.9f);
        scaleX.setDuration(300); // Set duration of the animation
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();

        // Scale down the button
        voicePingButton.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300) // Duration of the scaling down animation
                .start();

        Overlay.setVisibility(View.GONE);
        Log.d("VoicePingButton", "PTT onStopped");

        if (beepSound != null) {
            String text = "I just sent PTT Voice";
            sendMessage(text);
            beepSound.seekTo(0);
        }
    }
//////////// Custom Looper //////////////////////////////
    public void RecheckLocationMsg(){

        Intent intent = getIntent();


        Log.d("FromMsgLocRecheck", String.valueOf(isFromMsgLocation));

        if (intent != null) {
            if (Objects.equals(isFromMsgLocation, "Yes")) {
                GetUserMsgLocation();
            }
        }
        Checknotification();
    }
    // Method to handle errors during voice ping
    private void handleVoicePingError(String errorMessage) {
        stopTimer();
        stopRepeatingTask();
       Log.d("Selected Channel", String.valueOf(voicePingButton.getChannelType()));
        Log.d("VoicePingButton, PTT error: ", errorMessage);

        String receiverId = editReceiverId.getText().toString().trim();
        if (receiverId.isEmpty()) {
            editReceiverId.setError(getString(R.string.cannot_be_blank));
        }
    }

    // Method to handle device vibration
    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);  // For older versions
        }
    }


 ///////////////////// Network stats looping /////////////
/* private void startRepeatingTask() {
     handler.postDelayed(new Runnable() {
         @Override
         public void run() {
             if (!isLooping) return; // Exit the loop if isLooping is set to false

             // Call your method that performs the task
             allConnectionData();

             // Re-run this block every second (1000 milliseconds)
             handler.postDelayed(this, 1000);
         }
     }, 1000); // Initial delay of 1 second
 }*/

    private void stopRepeatingTask() {
        isLooping = false; // Stop the loop by setting this flag to false
        handler.removeCallbacksAndMessages(null); // Remove any pending callbacks
    }

    ////////////// PTT Timer /////////////////////////

    ////////////////////////

    // Start the timer with seconds and milliseconds display
    private void startCountUpTimer() {
        if (isTimerRunning) return; // Prevent multiple timers

        isTimerRunning = true;
        startTimeInMillis = System.currentTimeMillis();

        runnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTimeInMillis = System.currentTimeMillis() - startTimeInMillis;

                // Convert elapsed time to seconds and milliseconds
                long secondsElapsed = (elapsedTimeInMillis / 1000) % 60;
                long milliseconds = (elapsedTimeInMillis % 1000) / 10; // Get milliseconds as two digits

                // Format time as "SS:SS"
                @SuppressLint("DefaultLocale")
                String timeFormatted = String.format("%02d:%02d", secondsElapsed, milliseconds);
                ptt_Timer.setText(timeFormatted);
                ptt_timer2.setText(timeFormatted);


                // If 60 seconds (1 minute) is reached, trigger the event and stop the timer
                if (elapsedTimeInMillis >= MAX_TIME_IN_MILLIS) {
                    voicePingButton.getListener().onStopped(); // Trigger the stop recording method
                    stopTimer();
                    Toast.makeText(MainActivityOSM.this, "5 Seconds Reached! Please Subscribe To Extand Time", Toast.LENGTH_LONG).show();
                    return;
                }

                // Continue running the handler every 10 milliseconds
                handler.postDelayed(this, 10);
            }
        };

        // Start the timer with the handler
        handler.post(runnable);
    }

    // Stop the timer and reset state
    private void stopTimer() {
        if (isTimerRunning) {
            handler.removeCallbacks(runnable);
            isTimerRunning = false;
        }
    }
    /////////////
//////////// Every 1 min Call //////////////////////////
    public void allConnectionData(){
        // Check for network connection before attempting latency measurement
        if (isNetworkAvailable()) {

            if(Onlline){
                disposables.add(fireManager.setOnlineStatus().subscribe());}
            else{
                disposables.add(fireManager.setLastSeen().subscribe());}

            new MeasureNetworkLatencyTask(this, connection_quality, signal_strenth)
                    .execute("https://www.google.com");
        } else {

            disposables.add(fireManager.setLastSeen().subscribe());
            Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
        }

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        // Create a PhoneStateListener to listen for signal strength changes
        phoneStateListener = new PhoneStateListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);

                // Depending on the API level, you may need to check signal strength differently
                int level = signalStrength.getLevel();  // Range: 0 (poor) to 4 (excellent)
                //  Log.d("SignalStrength", "Signal strength level: " + level);

                if (level >= 0 && level < 2) connect_status.setImageResource(R.drawable.connection);

                else if (level == 2) connect_status.setImageResource(R.drawable.connection_med);

                else if (level > 2) connect_status.setImageResource(R.drawable.connection_full);

            }
        };

        // Start listening for signal strength changes
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

/*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                allConnectionData();
            }
        }, 60000); // each Minute*/

    }
    private void StartPTT(){

        if (Objects.equals(PTTUserID, "3")) {
            editReceiverId.setText("3");
           // editReceiverId.setVisibility(View.VISIBLE);
        }
        else if (Objects.equals(groupId_ptt, "null")) {
            editReceiverId.setText(PTTUserID);
            channelType = ChannelType.PRIVATE;
            voicePingButton.setChannelType(ChannelType.PRIVATE);
            Log.d("isPrivate ","Yes");
            Log.d("groupId_ptt ", String.valueOf(groupId_ptt));
        }
        else
        {
             editReceiverId.setText(groupId_ptt);
            Log.d("isGroup Yes", String.valueOf(groupId_ptt));
            channelType = ChannelType.GROUP;
            voicePingButton.setChannelType(ChannelType.GROUP);
           // editReceiverId.setVisibility(View.VISIBLE);
        }

        channelType = ChannelType.PRIVATE;
        Log.d("getChannelType ", String.valueOf(voicePingButton.getChannelType()));
    }


    //send text message
    public void sendMessage(String text) {

        if (text.trim().isEmpty())
            return;

        int length = text.getBytes().length;
        if (length > FireConstants.MAX_SIZE_STRING) {
            Toast.makeText(MainActivityOSM.this, R.string.message_is_too_long, Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = new MessageCreator.Builder(user, MessageType.SENT_TEXT).quotedMessage(getQuotedMessage()).text(text).build();
        if (message != null) {
            ServiceHelper.startNetworkRequest(this, message.getMessageId(), message.getChatId());
        }

    }

    private Message getQuotedMessage() {  return null;  }

    ///////////// keeping the activity in bg on until killed//////////////////
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        SharedPreferences sharedPrefs = getSharedPreferences("isFromMsgTracker", Context.MODE_PRIVATE);
        isFromMsgTracker = sharedPrefs.getString("isFromMsgTracker", "default_value");

     /*   SharedPreferences prefs = this.getSharedPreferences("Help_Distance", Context.MODE_PRIVATE);
        Help_Distance = prefs.getInt("Help_Distance", 50); // The second parameter is the default value if no UID is found
        ////////////////// Ais Range ///////////////////
        SharedPreferences aisprefs = this.getSharedPreferences("Ais_Distance", Context.MODE_PRIVATE);
        Ais_Distance = aisprefs.getInt("Ais_Distance", 50); // The second parameter is the default value if no UID is found
*/
        Log.d("Ais_Distance", String.valueOf(Ais_Distance));
        Log.d("Got to intent", "yes");
        Log.d("Help_Distance", String.valueOf(Help_Distance));

        /////////////////////////// Location Of Tracker In Map /////////////////////////////////
        if(Objects.equals(isFromMsgTracker, "Yes")) {
            SharedPreferences sharedPrefslat = getSharedPreferences("Otherlatitude", Context.MODE_PRIVATE);
            double Otherlatitudes = Double.parseDouble(sharedPrefslat.getString("Otherlatitude", "default_value"));

            SharedPreferences sharedPrefslong = getSharedPreferences("Otherlongitude", Context.MODE_PRIVATE);
            double Otherlongitudes = Double.parseDouble(sharedPrefslong.getString("Otherlongitude", "default_value"));

            Otherlatitude = Otherlatitudes;
            Otherlongitude = Otherlongitudes;

            SharedPreferences DeviceNamesharedPrefs = getSharedPreferences("DeviceName", Context.MODE_PRIVATE);
            String DeviceName = DeviceNamesharedPrefs.getString("DeviceName", "default_value");

            GetTrackerLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), Otherlatitude, Otherlongitude, DeviceName);
           }
           else {
            isFromMsgLocation = intent.getStringExtra("isFromMsgLocation");

            if(Objects.equals(isFromMsgLocation, "Yes")) {
                String OtherUsername = intent.getStringExtra("OtherUsername");
                // Retrieve the extras
                double Otherlatitude = intent.getDoubleExtra("Otherlatitude", 0.0);
                double Otherlongitude = intent.getDoubleExtra("Otherlongitude", 0.0);

                marker = null;
                UsersOther = null;
                mapView.getOverlays().remove(marker);
                mapView.getOverlays().remove(UsersOther);

                GetTrackerLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), Otherlatitude, Otherlongitude, OtherUsername);
                /////////////////////////// Location Of Others In Map /////////////////////////////////
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //   RecheckLocationMsg();
                        get_directions_poly.setAlpha(0f); // Start with fully transparent
                        get_directions_poly.setVisibility(View.VISIBLE); // Make the view visible
                        get_directions_poly.animate()
                                .alpha(1f)
                                .setDuration(300) // Duration of the scaling down animation
                                .start();
                    }
                }, 3000);
            }
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
        }
        resetBottomBarBtns();
    }

 private void resetBottomBarBtns(){

     Calls.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
     Chat.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
     Account.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));
     More.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_blue));

 }
//// Enabler GPs
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enableGps() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ANSWER_PHONE_CALLS}, PERMISSION_REQUEST_CODE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
        }

        @SuppressLint("VisibleForTests") LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnCompleteListener(task -> {
                    try {
                        task.getResult(ApiException.class);
                    } catch (ApiException exception) {
                        if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(MainActivityOSM.this, LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException |
                                     ClassCastException ignored) {
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            if (resultCode == Activity.RESULT_OK) {
                requestCurrentLocation();
            } else {
                Toast.makeText(getApplicationContext(), "GPS permission denied, GPS is Turned Off", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }

        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];

            if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float degree = (float) Math.toDegrees(orientation[0]); // orientation[0] is the azimuth
                degree = (degree + 360) % 360; // Normalize to 0-360

                updateCompass(degree);
            }
        }
    }

    public void updateCompass(float degree) {
        if(accelerometer != null) {
            RotateAnimation rotateAnimation = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation.setDuration(210);
            rotateAnimation.setFillAfter(true);
            getDirections.startAnimation(rotateAnimation);
            currentDegree = -degree; // Update current degree
        }
        else{
            Log.d("degree", String.valueOf(degree));
         //   Toast.makeText(getApplicationContext(), "degree :" + String.valueOf(degree), Toast.LENGTH_SHORT).show();
            getDirections.setRotation(degree);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // You can ignore this for basic compass functionality
    }

    public class MeasureNetworkLatencyTask extends AsyncTask<String, Void, Long> {

        private TextView connection_quality;
        private final TextView signal_strength;
        private Context context; // Store context for Toast

        public MeasureNetworkLatencyTask(Context context, TextView connectionQuality, TextView signalStrength) {
            this.context = context;
            this.connection_quality = connectionQuality;
            this.signal_strength = signalStrength;
        }

        @Override
        protected Long doInBackground(String... urls) {
            if (urls == null || urls.length == 0) {
                return -1L; // Handle error
            }

            String url = urls[0];
            return getConnectionLatency(url);
        }

        @Override
        protected void onPostExecute(Long latency) {
            super.onPostExecute(latency);
            if (latency != -1) {
                if (latency >= 0 && latency <= 150) {
                    connection_quality.setText("Good");
                    connection_quality.setTextColor(ContextCompat.getColor(context, R.color.green));
                } else if (latency > 150 && latency < 300) {
                    connection_quality.setText("Avg");
                    connection_quality.setTextColor(ContextCompat.getColor(context, R.color.yellow));
                } else if (latency >= 300) {
                    connection_quality.setText("Poor");
                    connection_quality.setTextColor(ContextCompat.getColor(context, R.color.red));
                }

                signal_strength.setText(latency + " ms");
                Log.d("NetworkLatency", "Latency: " + latency + " ms");
            } else {
                Log.e("NetworkLatency", "Failed to measure latency");
                Toast.makeText(context, "Latency measurement failed", Toast.LENGTH_SHORT).show();
            }
        }

        private long getConnectionLatency(String urlString) {
            long startTime, endTime;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000); // 5 seconds timeout
                connection.setReadTimeout(5000);    // 5 seconds read timeout
                connection.setRequestProperty("User-Agent", "YourAppName/1.0");

                startTime = System.currentTimeMillis();
                connection.connect();  // Attempt to connect to the URL
                endTime = System.currentTimeMillis();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return endTime - startTime;  // Latency in ms
                } else {
                    Log.e("NetworkLatency", "Server responded with code: " + connection.getResponseCode());
                    return -1;  // Failure, invalid response
                }

            } catch (MalformedURLException e) {
                Log.e("NetworkLatency", "MalformedURLException: Invalid URL", e);
            } catch (IOException e) {
                Log.e("NetworkLatency", "IOException: Network issue", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();  // Close connection
                }
            }
            return -1; // Return failure
        }

    }

    // Method to check network availability
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    /////// Weather Details
    public void getWeatherDetails(double Latitude, double Longitude) {
        String tempUrl = url + "?lat=" + Latitude + "&lon=" + Longitude + "&appid=" + appId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onResponse(String rs) {
                String output = "";
                try {
                    JSONObject jsonResponse = new JSONObject(rs);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonWeather = jsonArray.getJSONObject(0);
                    String description = jsonWeather.getString("description");
                    String main = jsonWeather.getString("main");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    float visibility = jsonResponse.getInt("visibility");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    float windSpeed = jsonObjectWind.getInt("speed");
                    float deg = jsonObjectWind.getInt("deg");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonResponse.getString("name");
                    JSONObject jsonObjectCords = jsonResponse.getJSONObject("coord");
                    float lon = (float) jsonObjectCords.getDouble("lon");
                    float lat = (float) jsonObjectCords.getDouble("lat");

                    float toKphWind = windSpeed * 4.8f;

                    String NotFetchWind;
                    if(toKphWind < 1) {
                        NotFetchWind = "N/A";
                        getwindspeed.setText(NotFetchWind);
                    }
                    else {
                        DecimalFormat windf = new DecimalFormat("#.#");
                        windSpeedMain = Double.parseDouble(windf.format(toKphWind));
                        getwindspeed.setText(windSpeedMain + "Km/h");
                    }

                    Temprature = Double.parseDouble(df.format(temp));
                    temp2.setText(String.valueOf(Temprature + "°"));
                    getweather.setText(String.valueOf(Temprature + "°"));
                    Log.d("windSpeed , Weather", String.valueOf(windSpeed + "," + getweather.getText()));

                    output +=
                            "\n Location: " + cityName + ", " + countryName +
                                    "\n Description: " + main + ", " + description +
                                    "\n Clouds: " + clouds +
                                    "\n Wind Speed: " + getwindspeed.getText() +
                                    "\n Feels like: " + df.format(feelsLike) + " C" +
                                    "\n Temp: " + df.format(temp) + " C" +
                                    "\n Pressure: " + df.format(pressure) + " hPa" +
                                    "\n Visibility: " + visibility / 1000 + " Km" +
                                    "\n Humidity: " + humidity + '%' +
                                    "\n Wind Direction: " + df.format(deg) + " Degree" +
                                    "\n Current Longitude: " + lon +
                                    "\n Current Latitude: " + lat;

                    tvResult.setText(output);
                    tvResult.setTextColor(Color.BLACK);
                    // OpenMapsVisiblity.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        // Check if there are any fragments in the stack

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.other_frags);
        if (fragment instanceof Ptt_View) {
            ((Ptt_View) fragment).sendTimerData();
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            SharedPreferences sharedPrefs = this.getSharedPreferences("fromOSM", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor3 = sharedPrefs.edit();
            editor3.putBoolean("fromOSM", false);
            editor3.apply();
            // Pop the top fragment off the stack
            getSupportFragmentManager().popBackStack();

            resetBottomBarBtns();
        } else {
            // Move app to background instead of closing it
            moveTaskToBack(true);
            // No fragments left, call finishAffinity
          //  finishAffinity();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateGroupEvent(UpdateGroupEvent event) {
        groupId_ptt = event.getGroupId();
        Log.d("Group ID", String.valueOf(groupId_ptt));
        StartPTT();
        joinGroup(groupId_ptt);
    }

    private void joinGroup(String groupId) {
        if (groupId.isEmpty()) {
            editReceiverId.setError(getString(R.string.cannot_be_blank));
            editReceiverId.requestFocus();
            return;
        }
        Log.d("joinGroup, group ID: " , groupId);
        VoicePing.INSTANCE.joinGroup(groupId);
    }

    private void leaveGroup(String groupId) {

        if (groupId.isEmpty()) {
            editReceiverId.setError(getString(R.string.cannot_be_blank));
            editReceiverId.requestFocus();
            return;
        }

        Log.d("leaveGroup, group ID: ", groupId);
        VoicePing.INSTANCE.leaveGroup(groupId);

    }

    ////////////////////////// PTT Ends ////////////////////////////


public void Checknotification(){
    String chatcounts = String.valueOf(RealmHelper.getInstance().getUnreadChatsCount());
    if (!chatcounts.equals("0")) {
        MsgCount.setText(chatcounts);
        Notification_msgs.setVisibility(View.VISIBLE);
    } else {
        MsgCount.setText(" ");
        Notification_msgs.setVisibility(View.GONE);
    }
}

    public void TabController(int value){
        SharedPreferences prefs = getSharedPreferences("UID", MODE_PRIVATE);
        uid = prefs.getString("UID", "defaultValue");

        if(!Objects.equals(uid, "defaultValue")) {

            user = RealmHelper.getInstance().getUser(uid);

            if (value == 0) {
                // 0 for Small 1 for Big Tab
                layout2.setVisibility(View.VISIBLE);
                Minimize_bar.setVisibility(View.GONE);
            } else {
                layout2.setVisibility(View.GONE);
                Minimize_bar.setVisibility(View.VISIBLE);
            }
        }
        TabFunc();
    }
    public void TabFunc() {


        if (!Objects.equals(uid, "defaultValue")) {
            voicePingButton.setVisibility(View.VISIBLE);
            Main_Btns.setVisibility(View.GONE);
            OtherUserName.setText(user.getUserName());
            chat = RealmHelper.getInstance().getChat(uid);

            groupId_ptt = "null";
            isGroup = false;
            isGroup = user.isGroupBool();
            PTTUserID = user.getUid();

            if(isGroup){
                updateGroupEvent(new UpdateGroupEvent(user.getUid()));}
            else {
                StartPTT();
            }
                Log.d("isGroup Bool", String.valueOf(isGroup));

            if (Minimize_bar.getVisibility() == View.GONE) {
                OtherUserName_mini.setText(user.getUserName());
                int heightInDp = 150;
                float scale = getResources().getDisplayMetrics().density;
                int heightInPx = (int) (heightInDp * scale + 0.5f);
                ViewGroup.LayoutParams params = layout_main.getLayoutParams();
                params.height = heightInPx;
                //layout_main.setLayoutParams(params);

                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
                layout4.setVisibility(View.GONE);
                layout_main.setTranslationY(layout_main.getLayoutParams().height); // Start from bottom of the view
                layout_main.animate()
                        .translationY(0)
                        .setDuration(600)
                        .withEndAction(() -> {
                            Minimize_bar.setVisibility(View.VISIBLE);
                            params.height = heightInPx;
                            layout_main.setLayoutParams(params);
                        });
            } else {
                int heightInDp = 300;
                float scale = getResources().getDisplayMetrics().density;
                int heightInPx = (int) (heightInDp * scale + 0.5f);
                ViewGroup.LayoutParams params = layout_main.getLayoutParams();
                ViewGroup.LayoutParams params2 = layout2.getLayoutParams();
                params.height = heightInPx;
                params2.height = heightInPx;

                layout_main.setTranslationY(layout_main.getHeight()); // Start from bottom of the view
                layout_main.animate()
                        .translationY(0)  // Slide it into its position
                        .setDuration(500)
                        .withEndAction(() -> {
                            layout2.setVisibility(View.VISIBLE);
                            layout_main.setLayoutParams(params);
                            Minimize_bar.setVisibility(View.GONE);
                            layout3.setVisibility(View.VISIBLE);
                            layout2.setLayoutParams(params2);
                            layout4.setVisibility(View.VISIBLE);
                        });
            }
        } else {

            Main_Btns.setVisibility(View.VISIBLE);
            voicePingButton.setVisibility(View.GONE);
            int heightInDp = 190;
            float scale = getResources().getDisplayMetrics().density;
            int heightInPx = (int) (heightInDp * scale + 0.5f);
            ViewGroup.LayoutParams params = layout_main.getLayoutParams();
            ViewGroup.LayoutParams params2 = layout2.getLayoutParams();
            params.height = heightInPx;
            params2.height = heightInPx;

            layout_main.setTranslationY(layout_main.getHeight()); // Start from bottom of the view
            layout_main.animate()
                    .translationY(0)  // Slide it into its position
                    .setDuration(600)
                    .withEndAction(() -> {
                        layout2.setLayoutParams(params2);
                        layout_main.setLayoutParams(params);
                        layout2.setVisibility(View.VISIBLE);
                        Minimize_bar.setVisibility(View.GONE);
                        layout4.setVisibility(View.GONE);
                        layout3.setVisibility(View.GONE);
                    });
        }
    }

    ///////////////////////////////////
    private static final double KM_PER_DEGREE_LATITUDE = 111.0;

    public BoundingBox getBoundingBoxForLocation(GeoPoint currentLocation) {
        double lat = currentLocation.getLatitude();
        double lon = currentLocation.getLongitude();

        // Calculate deltas
        double deltaLat = Ais_Distance / KM_PER_DEGREE_LATITUDE;
        double deltaLon = Ais_Distance / (KM_PER_DEGREE_LATITUDE * Math.cos(Math.toRadians(lat)));

        // Compute bounding box
        double minLat = lat - deltaLat;
        double maxLat = lat + deltaLat;
        double minLon = lon - deltaLon;
        double maxLon = lon + deltaLon;

        return new BoundingBox(maxLat, maxLon, minLat, minLon);
    }


    public void GetVesselsData() {

            GeoPoint userLocation = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
            // Calculate the bounding box with a 50km radius
            BoundingBox boundingBox = getBoundingBoxForLocation(userLocation); // Adjusted to 50km
            // Initialize WebSocket client or send subscription request
            webSocketClient.sendSubscriptionRequest(boundingBox);
            Log.d("coordinates", "Bounding box: " + boundingBox);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(webSocketClient.isOpen())
                        GetVesselsData();
                }
            }, 1800000); // 30 Mins
    }

    private void initializeWebSocketClient() {
        try {
            webSocketClient = new AisStreamWebSocketClient(new URI("wss://stream.aisstream.io/v0/stream"), "333ddd265b5725996cc64817994095f60e133d5c", this);
            webSocketClient.setMapView(mapView);
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error initializing WebSocket client", e);
        }
    }
    private void handleGetLocationButton() {
        if(currentLocation  != null){
            animateMapToMarker(currentLocation.getLatitude(), currentLocation.getLongitude());
            CurrentLocation = true;
            RequestWeather();
           // mapView.getController().setZoom(8.0);

            GeoPoint userLocation = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());

            if (marker == null) {
                // Drawable drawable = ContextCompat.getDrawable(this, R.drawable.my_boat_svg);
                marker = new Marker(mapView);
                marker.setIcon(vectorDrawable); // Adjust marker size based on zoom level
              //  marker.setIcon(ContextCompat.getDrawable(this, R.drawable.my_boat_svg)); // Ensure drawable resource exists
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                mapView.getOverlays().add(marker);
                marker.setTitle("You");
                mapView.invalidate(); // Refresh the map to show the new marker
            }
            marker.setPosition(userLocation);
        }
        else{
            if (ContextCompat.checkSelfPermission(MainActivityOSM.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                requestCurrentLocation();
                CurrentLocation = true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handleGetLocationButton();
                    }
                }, 2000);
            } else {
                ActivityCompat.requestPermissions(MainActivityOSM.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }
    private void startRecordingRoute() {
        if (!isRecording) {
                    record_plot.setText("ON");
         //   startRecordButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
          //  startRecordButton.setTextColor(getResources().getColor(R.color.white));
            record_plot.setTextColor(getResources().getColor(R.color.green));
            rec_on.setVisibility(View.GONE);
            rec_off.setVisibility(View.GONE);

// Duration of the animation in milliseconds
            animator = null;
            animator = ValueAnimator.ofFloat(1.0f, 0.0f);
            animator.setDuration(500); // Duration for one cycle
            animator.setRepeatCount(ValueAnimator.INFINITE); // Repeat indefinitely
            animator.setRepeatMode(ValueAnimator.REVERSE); // Reverse back to original

            animator.addUpdateListener(animation -> {
                // Get the animated value (alpha effect or any other)
                float value = (float) animation.getAnimatedValue();

                // Control the visibility of the view
                if (value > 0.5f) {
                    rec_on.setVisibility(View.VISIBLE); // Visible when value is above 0.5
                } else {
                    rec_on.setVisibility(View.GONE); // Gone when value is below 0.5
                }
            });

            animator.start(); // Start the animation
           // startRecordButton.setBackgroundResource(R.drawable.tracker);
            isRecording = true;
            //startRecordButton.setText("Stop Record");
            // Clear previous polyline if exists
            if (routePolyline != null) {
                mapView.getOverlays().remove(routePolyline);
            }
            routePolyline = new Polyline(); // Create a new Polyline
            routePolyline.setColor(Color.RED); // Set the color for the polyline
            routePolyline.setWidth(5); // Set the width of the polyline
            mapView.getOverlays().add(routePolyline); // Add polyline to the map

            chartplotter_bg.setVisibility(View.GONE);
            replay_sound.setVisibility(View.GONE);
            mapView.invalidate(); // Refresh the map
             Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } else {
// To stop the animation at any point
            animator.cancel();
            record_plot.setText("OFF");
         //   startRecordButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            record_plot.setTextColor(getResources().getColor(R.color.red));
         //   startRecordButton.setTextColor(getResources().getColor(R.color.colorDarkGray));
            rec_on.setVisibility(View.GONE);
            rec_off.setVisibility(View.VISIBLE);
            chartplotter_bg.setVisibility(View.VISIBLE);
            replay_sound.setVisibility(View.VISIBLE);
            isRecording = false;
            //startRecordButton.setText("Start Record");
           // startRecordButton.setBackgroundResource(R.drawable.tracker_red);
            routePolyline.setPoints(routePoints); // Set the points for the polyline
            // Save the route points for later use
            saveRoutePoints();
         //   replay_sound.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.transparent)));
           // replay_sound.isClickable();
            mapView.invalidate(); // Refresh the map
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        }
    }
/*
    private void checkMapChanges() {
        GeoPoint currentCenter = (GeoPoint) mapView.getMapCenter();
        int currentZoomLevel = mapView.getZoomLevel();

        if (!currentCenter.equals(lastCenter) || currentZoomLevel != lastZoomLevel) {
            Log.d(TAG, "Map changed: Center = " + currentCenter + ", Zoom Level = " + currentZoomLevel);

            if(webSocketClient != null && currentZoomLevel != lastZoomLevel)
                webSocketClient.setZoomLevel(currentZoomLevel);

            lastCenter = currentCenter;
            lastZoomLevel = currentZoomLevel;
         //   updateBoundingBoxSubscription();

        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDetach(); // Detach and cleanup mapView
            mapView = null;
        }
        if (webView != null) {
            webView.destroy();  // Ensure WebView is destroyed when the activity is destroyed
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void requestCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isLocationEnabled()) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener, Looper.getMainLooper());
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener, Looper.getMainLooper());
            } else {
                Toast.makeText(this, "Please enable location services.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Location permission is required to get the current location.", Toast.LENGTH_LONG).show();
        }
    }

    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            currentLocation = location; // Save the current location
            speed = (int) ((int) location.getSpeed() * 1.94384f); // in knots from kph

            speedTextDisplay.setText(String.valueOf(speed));
            if (accelerometer == null && location.hasBearing()) {
                float heading = location.getBearing();
                float Degrees = 360 - heading; // Adjust for correct orientation
                updateCompass(Degrees);

            }

            if (currentLocation == null)
                handler.postDelayed(() -> onLocationChanged(location), 500);
            else updateMapWithCurrentLocation();
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            handleGetLocationButton();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };
    public void RequestWeather(){
        if(currentLocation != null) {
            handler.postDelayed(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    getWeatherDetails(currentLocation.getLatitude(), currentLocation.getLongitude());

                }
            }, 1000);
        }
        else handler.postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                RequestWeather();
            }
        }, 1000);
    }
    private void updateMapWithCurrentLocation() {
        if (currentLocation != null) {

            GeoPoint userLocation = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users").child(FireManager.Companion.getMyUid());

            // Create User Data Map
            Map<String, Object> userData = new HashMap<>();
            userData.put("latitude", currentLocation.getLatitude());
            userData.put("longitude", currentLocation.getLongitude());

            // Save User Data under the userId
            ref.updateChildren(userData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) Log.d("Update Data", "Successfully Added Long Lat");
                else Log.d("Update Data", "Failed to save data", task.getException());
            });

            //  Log.d("Get Latitude", String.valueOf(user.getLatitude()));


            if (marker == null) {
               // Drawable drawable = ContextCompat.getDrawable(this, R.drawable.my_boat_svg);
                marker = new Marker(mapView);
                marker.setIcon(vectorDrawable);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                mapView.getOverlays().add(marker);
                marker.setTitle("You");
                mapView.invalidate(); // Refresh the map to show the new marker
            }

            if (isRecording) {
                mapController.setCenter(userLocation);
                mapView.getController().setZoom(14.0); // Set the zoom level
                routePoints.add(userLocation);
                if (routePolyline != null) {
                    routePolyline.setPoints(routePoints);
                }
            }
/////////////// Current Location Zoom
            if (!isRecording && CurrentLocation) {
                CurrentLocation = false;
                mapController.setCenter(userLocation);

                mapView.getController().setZoom(8.0);
            }
            marker.setPosition(userLocation);


        } else Toast.makeText(this, "Getting Your Location.", Toast.LENGTH_LONG).show();

    }

    public void sendMessageToGroupWithinRadius(String messageContent) {
        String groupId = "-O1lQDHIOPtMTZAkSQXB";
     //   String groupId = "-O28b3b8pBnFG4i2NE-l";
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference groupRef = database.getReference("groups").child(groupId).child("users");

        List<String> usersWithinRadius = new ArrayList<>();

        // Retrieve all users from the group
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Use a CountDownLatch to wait for all user location requests to complete
                    int totalUsers = (int) dataSnapshot.getChildrenCount();
                    CountDownLatch latch = new CountDownLatch(totalUsers); // Initialize latch with the number of users
                    Log.d("User Count", "Total users in group: " + totalUsers);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userId = snapshot.getKey();
                        Log.d("User Loop", "Processing user ID: " + userId);

                        // Get the user's location
                        DatabaseReference userLocationRef = database.getReference("users").child(userId);
                        userLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                if (userSnapshot.exists()) {
                                    Double latitude = userSnapshot.child("latitude").getValue(Double.class);
                                    Double longitude = userSnapshot.child("longitude").getValue(Double.class);
                                    Log.d("Distance", latitude + "," + longitude);

                                    if (latitude != null && longitude != null && currentLocation != null) {
                                        double distance = calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), latitude, longitude);

                                        Log.d("Distance Calculation", "Distance to user " + userId + ": " + distance + " km");

                                        // Check if the user is within the 20 km radius
                                        if (distance <= Help_Distance) {  // Ensure this is the correct distance check
                                            usersWithinRadius.add(userId);
                                        }
                                    }
                                }
                                latch.countDown(); // Decrement the latch count when done
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                latch.countDown(); // Ensure latch is decremented even on error
                            }
                        });
                    }

                    // Wait for all location requests to complete
                    new Thread(() -> {
                        try {
                            latch.await(); // This blocks until all requests are complete
                            // After checking all users, send the message if there are any users within the radius
                            if (!usersWithinRadius.isEmpty()) {
                              //  usersWithinRadius.remove(FireManager.Companion.getMyUid());

                                  String[] selectedUserIds = {usersWithinRadius.toString()};  // Only these friends can see the message
                                sendMessage(groupId, FireManager.Companion.getMyUid(), "1", messageContent, selectedUserIds);
                                Log.d("selectedUserIds", Arrays.toString(selectedUserIds));
                            } else {
                                Log.d("Message", "No users within the specified radius.");
                            }
                        } catch (InterruptedException e) {
                            Log.e("Latch Error", "Interrupted while waiting for user locations: " + e.getMessage());
                        }
                    }).start(); // Start a new thread to wait for the latch
                } else {
                    Log.d("Group Error", "No users found in the group.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Group Error", "Failed to retrieve group users: " + databaseError.getMessage());
            }
        });
    }

////////// PTT and HELP Group CONNECTION ///////////////////////

    // Method to send a message to selected users in the group
    public void sendMessage(String groupId, String senderId, String type, String messageText, String[] selectedUserIds) {
        List<String> selectedUserIdsList = Arrays.asList(selectedUserIds);

        // Create a unique message ID
        String messageId = FirebaseDatabase.getInstance().getReference("groupsMessages").child(groupId).push().getKey();

     //   if(Objects.equals(type, "1")) {
            // Type declear the msg in context

            // Create a message map with the required parameters

            Map<String, Object> locationMap = new HashMap<>();
            locationMap.put("lat", currentLocation.getLatitude());    // Replace 'latitude' with the actual latitude value (Double)
            locationMap.put("lng", currentLocation.getLongitude());  // Replace 'longitude' with the actual longitude value (Double)
            locationMap.put("name", "");    // Replace 'locationName' with the actual name (String)
            locationMap.put("address", "");      // Replace 'address' with the actual address (String)
            // Send the message to Firebase

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("fromId", senderId);            // The sender's ID
            messageMap.put("type", 18);                      // The Type of Msg [1: Text, 2: Image Metadata, 18: Location, etc.]
            messageMap.put("toId", groupId);               // The group's ID
            messageMap.put("content", messageText);        // The message content
            messageMap.put("timestamp", System.currentTimeMillis()); // The current timestamp
            messageMap.put("seenBy", selectedUserIdsList); // List of selected user IDs who can see the message
            messageMap.put("location", locationMap);               // The group's ID



/*

        Map<String, Boolean> seenByMap = new HashMap<>();
        seenByMap.put(FireManager.Companion.getMyUid(), true);
        messageMap.put("seenBy", seenByMap);*/

            sendMessageToFirebase(messageMap);
           // sendMessage(groupId, FireManager.Companion.getMyUid(), "18", messageText, selectedUserIds);
    //    }
      /*  else {

            //Map<String, Object> locationMap = null;


            // Create a message map with the required parameters

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("fromId", senderId);            // The sender's ID
            messageMap.put("type", Integer.parseInt(type));                      // The Type of Msg [1: Text, 2: Image Metadata, 18: Location, etc.]
            messageMap.put("toId", groupId);               // The group's ID
            messageMap.put("timestamp", System.currentTimeMillis()); // The current timestamp
            messageMap.put("seenBy", selectedUserIdsList); // List of selected user IDs who can see the message

            sendMessageToFirebase(messageMap);
           // sendMessageToFirebase(locationMap);

        }*/
    }


    // Method to push the message to Firebase under a group node
    private void sendMessageToFirebase(Map<String, Object> messageMap) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference groupMessagesRef = database.getReference("groupsMessages").child(messageMap.get("toId").toString());

        // Push the message to the Firebase Database (this generates a unique key for each message)
        groupMessagesRef.push().setValue(messageMap)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    System.out.println("Message sent successfully to selected users in the group!");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    System.err.println("Error sending message: " + e.getMessage());
                });
    }



////////// One 2 One Live Connect ///////////////////////
public void checkAreaUsers() {
    // Initialize Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");

    // Retrieve all user data
    ref.addListenerForSingleValueEvent(new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                List<String> usersWithinRadius = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey(); // Retrieve userId

                    // Retrieve latitude and longitude for each user
                    Double latitude = userSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = userSnapshot.child("longitude").getValue(Double.class);

                    if (latitude != null && longitude != null && currentLocation != null) {
                        double distance = calculateDistance(
                                currentLocation.getLatitude(),
                                currentLocation.getLongitude(),
                                latitude,
                                longitude
                        );

                        Log.d("I am in list?", String.valueOf(usersWithinRadius.contains(FireManager.Companion.getMyUid())));


                        Log.d("I am in list?", String.valueOf(usersWithinRadius.contains(FireManager.Companion.getMyUid())));

                        if (distance <= Help_Distance) { // 15 km in this case
                            usersWithinRadius.add(userId);
                            Log.d("User Added", "User " + userId + " is within the radius");

                            usersWithinRadius.remove(FireManager.Companion.getMyUid());


                            updateUserMarker(userId, latitude, longitude);
                        }
                    } else {
                        Log.d("Data Error", "Missing latitude or longitude for user " + userId);
                    }
                }

                Log.d("UsersWithinRadius", "Total users within radius: " + usersWithinRadius.size());

            } else {
                Log.d("Data Error", "No users found.");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("Database Error", "Failed to retrieve data: " + databaseError.getMessage());
        }
    });

    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            checkAreaUsers();
        }
    }, 300000);////// 5 Mins // 60000 1 min
}


/////////////////// icons reposition of exisitng users ///////////////////
public void updateUserMarker(String userId, double latitude, double longitude) {
    GeoPoint userLocation = new GeoPoint(latitude, longitude);


    // Check if it's your own ID and skip adding/updating your marker
    String myUserId = FireManager.Companion.getMyUid();
    if (userId.equals(myUserId)) {
        // Remove your own marker if it exists
        if (userMarkersMap.containsKey(myUserId)) {
            Marker myMarker = userMarkersMap.get(myUserId);
            mapView.getOverlays().remove(myMarker);  // Remove from map overlay
            userMarkersMap.remove(myUserId);  // Remove from marker map
        }
        return;  // Exit the method as you don't want to add or update your marker
    }


    if(!userId.isEmpty() && !userId.equals(String.valueOf(FireManager.Companion.getMyUid()))) {
        // Update or create a marker for other users
        if (userMarkersMap.containsKey(userId)) {
            // If it exists, update its position
            userMarker = userMarkersMap.get(userId);
            assert userMarker != null;
            userMarker.setPosition(userLocation); // Update position
          //  Toast.makeText(this, "Updating User's Location", Toast.LENGTH_LONG).show();
        } else {
          user = RealmHelper.getInstance().getUser(userId);
          Log.d("placing marker", userId);
            // If it doesn't exist, create a new marker
            userMarker = new Marker(mapView);
// Check if the username is not null
            if (user != null && user.getUserName() != null) {
                Log.d("user when placing marker", String.valueOf(user.getUserName()));
                userMarker.setTitle(String.valueOf(user.getPhone()));
            }
            else
                userMarker.setTitle(userId);
           // userMarker.setTitle(String.valueOf(RealmHelper.getInstance().getUser(userId).getUserName()));
            userMarker.setIcon(vectorDrawable); // Ensure you have the drawable
            userMarker.setPosition(userLocation);
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
            userMarkersMap.put(userId, userMarker); // Add to the map
            mapView.getOverlays().add(userMarker); // Add marker to the map
        }
    }
    if(mapView != null){
    // Refresh the map to show the updated marker
    mapView.invalidate();
    }
}


    public void clearAllUserMarkers() {
        // Remove all user markers from the map and the map of user markers
        for (Marker marker : userMarkersMap.values()) {
            mapView.getOverlays().remove(marker);
        }
        userMarkersMap.clear(); // Clear the map if you don't need it anymore
        mapView.invalidate(); // Refresh the map
    }
    /////////////// Earth Radius CalCulator
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Radius of the Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // returns distance in kilometers
    }
    ////////// One 2 One Live Connect ///////////////////////
public void ClearShips(){
    if (webSocketClient != null)
        webSocketClient.clearAllMarkers();
}
public void ShowShips(){
    if (webSocketClient != null)
        webSocketClient.ShowShipsMarkers();
    }
private void saveRoutePoints() {

        File file = new File(getFilesDir(), "route_points.json");
        try (FileWriter writer = new FileWriter(file)) {
            JSONArray jsonArray = new JSONArray();
            for (GeoPoint point : routePoints) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Savelatitude", point.getLatitude());
                jsonObject.put("Savelongitude", point.getLongitude());
                jsonArray.put(jsonObject);
            }
            writer.write(jsonArray.toString());
            routePoints.clear(); // Clear previous route points if any
            Toast.makeText(this, "Location Points Saved!.", Toast.LENGTH_LONG).show();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    ///////////// Retrieve route points .//////////////
    @SuppressLint("UseCompatLoadingForDrawables")
    private void loadRoutePointsAndDisplayOnMap(MapView map) {
        File file = new File(getFilesDir(), "route_points.json");
        if (!file.exists()) {
            Toast.makeText(this, "No saved route points found.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<GeoPoint> routePoints = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            double latitude = 0;
            double longitude = 0;
            // Parse JSON and extract coordinates
            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                latitude = jsonObject.getDouble("Savelatitude");
                longitude = jsonObject.getDouble("Savelongitude");
                routePoints.add(new GeoPoint(latitude, longitude));
            }

            // Draw the polyline on the map
            Polyline routePolyline = new Polyline();
            routePolyline.setPoints(routePoints);
            routePolyline.setColor(Color.BLUE);  // Optional: Set line color
            routePolyline.setWidth(5.0f);        // Optional: Set line width
            map.getOverlayManager().add(routePolyline);

            // Zoom to fit the route
          //  map.zoomToBoundingBox(routePolyline.getBounds(), true);
            // Animate the map to the last point
            if (!routePoints.isEmpty()) {
                // Starting Point Marker (Red)
                GeoPoint firstPoint = routePoints.get(0);
                Marker startMarker = new Marker(mapView);
                startMarker.setPosition(firstPoint);
                startMarker.setTitle("Starting Point");

                Drawable redIcon = getResources().getDrawable(org.osmdroid.library.R.drawable.marker_default).mutate();
                DrawableCompat.setTint(redIcon, getResources().getColor(android.R.color.holo_red_dark)); // Tint to red
                startMarker.setIcon(redIcon);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                mapView.getOverlays().add(startMarker);

                // Ending Point Marker (Green)
                GeoPoint lastPoint = routePoints.get(routePoints.size() - 1);
                Marker endMarker = new Marker(mapView);
                endMarker.setPosition(lastPoint);
                endMarker.setTitle("End Point");

                Drawable greenIcon = getResources().getDrawable(org.osmdroid.library.R.drawable.marker_default).mutate();
                DrawableCompat.setTint(greenIcon, getResources().getColor(android.R.color.holo_green_dark)); // Tint to green
                endMarker.setIcon(greenIcon);
                endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                mapView.getOverlays().add(endMarker);

                // Optionally animate the map to the starting point
                animateMapToMarker(firstPoint.getLatitude(), firstPoint.getLongitude());
                mapView.getController().setZoom(16.0);

                mapView.invalidate();
            }


            Toast.makeText(this, "Route points loaded successfully!", Toast.LENGTH_LONG).show();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load route points.", Toast.LENGTH_SHORT).show();
        }
    }
    /*private void updateBoundingBoxSubscription() {
        // Here you should handle the visibility of the Ships button or subscription logic.
        if (webSocketClient != null) {
            ShipsBtn.setVisibility(View.VISIBLE);
        } else {
            ShipsBtn.setVisibility(View.VISIBLE);
        }
    }*/
private boolean isLocationEnabled() {
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            // Provider not enabled
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            // Provider not enabled
        }

        return gpsEnabled || networkEnabled;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if(fireListener != null)
            fireListener.cleanup();

        if (accelerometer != null && magneticField != null)
            sensorManager.unregisterListener(this);
        if (webView != null) {
            webView.onPause();
        }
        if(mapView != null ) mapView.onPause();
        //  handler.removeCallbacks(mapUpdateRunnable); // Stop polling
        // Stop location updates to save battery
        if (isLocationEnabled())
            locationManager.removeUpdates(locationListener);

        // Close WebSocket connection if necessary
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (accelerometer != null && magneticField != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_UI);
        }

        Checknotification();

        if(mapView != null )
                    mapView.onResume();

        Checknotification();

        if (webView != null) {
            webView.onResume();
        }

        // Resume the tasks by reposting them
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAreaUsers();
            }
        }, 8000);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {

                    requestCurrentLocation();
                } else {
                    Toast.makeText(this, "Please enable location services.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private float getScaleFactorForZoom(int zoomLevel) {
        if (zoomLevel <= 10) {
            return 0.5f;
        } else if (zoomLevel <= 15) {
            return 0.75f;
        } else {
            return 1.0f;
        }
    }

    public void showSeaMap(){
        // OpenSeaMap tile source configuration
// Create the tile source
        String baseUrl = "https://tiles.openseamap.org/seamark/";
        ITileSource openSeaMapTileSource = new XYTileSource(
                "OpenSeaMap",
                5,  // min zoom level
                15, // max zoom level
                256, // tile size
                ".png",
                new String[]{baseUrl}
        );

// Create the tile provider and overlay
        MapTileProviderBase tileProvider = new MapTileProviderBasic(getApplicationContext());
        tileProvider.setTileSource(openSeaMapTileSource);
        openSeaMapOverlay = new TilesOverlay(tileProvider, getApplicationContext());

// Add the overlay to the map
        mapView.getOverlays().add(openSeaMapOverlay);
    }
 /*   public void showSeaDepth(){
        CustomWMSTileSource wmsTileSource = new CustomWMSTileSource(12);  // Set your desired zoom level
        mapView.setTileSource(wmsTileSource);
    }
*/

/*
    // Set the marker with the initial size
    private void setMarkerSize(Marker marker, float sizeMultiplier) {
        Drawable drawable = marker.getIcon();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                    (int) (bitmap.getWidth() * sizeMultiplier),
                    (int) (bitmap.getHeight() * sizeMultiplier),
                    false);
            marker.setIcon(new BitmapDrawable(getResources(), scaledBitmap));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // Adjust anchor as needed
        }
    }*/

    // Method to adjust marker size based on zoom level
 /*   private void adjustMarkerSize() {
        float currentZoomLevel = (float) mapView.getZoomLevelDouble();
        float sizeMultiplier = Math.max(MIN_MARKER_SIZE,
                Math.min(MAX_MARKER_SIZE, DEFAULT_MARKER_SIZE / currentZoomLevel));

        if (currentMarkerSize != sizeMultiplier) {
            setMarkerSize(marker, sizeMultiplier);
            currentMarkerSize = sizeMultiplier;
        }
    }*/


    // Function to animate the marker position
    public void animateMapToMarker(double newLatitude, double newLongitude) {
        GeoPoint currentCenter = (GeoPoint) mapView.getMapCenter();
        GeoPoint end = new GeoPoint(newLatitude, newLongitude);

        // Duration of the animation in milliseconds
        long duration = 500; // Adjust this for speed

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            double lat = currentCenter.getLatitude() + (end.getLatitude() - currentCenter.getLatitude()) * fraction;
            double lon = currentCenter.getLongitude() + (end.getLongitude() - currentCenter.getLongitude()) * fraction;

            GeoPoint newPosition = new GeoPoint(lat, lon);

            // Center the map on the new position
            mapView.getController().setCenter(newPosition);
            mapView.invalidate();
        });
        animator.start();
    }

    public static boolean isActivityRunning = false;
    private boolean isSensorAvailable(int sensorType) {
        return sensorManager.getDefaultSensor(sensorType) != null;
    }

    private void updateIntent() {
        remove_UserLoc.setVisibility(View.GONE);
        mapView.getOverlays().remove(userLocationPolyline);
        handleGetLocationButton();
    }





///////////////////////////////////////////// Location Get ////////////////////////////////////////////
public void GetUserMsgLocation(){

    // Retrieve the latitude and longitude from the Intent
    Intent intent = getIntent();
    if (intent != null) {
        isFromMsgLocation = "No";
        intent.putExtra("isFromMsgLocation", "No");
       if (Otherlatitude != 0 || Otherlongitude != 0) {
            showUserLocationOnMap(Otherlatitude, Otherlongitude, user.getUserName());
            remove_UserLoc.setVisibility(View.VISIBLE);
        }
    }
    else{
        remove_UserLoc.setVisibility(View.GONE);
    }

}
    private void showUserLocationOnMap(double latitude, double longitude, String label) {
        if(currentLocation != null){
            drawPolyline(currentLocation.getLatitude(), currentLocation.getLongitude(), latitude, longitude, label);
        }
      // mapView.getController().animateTo(geoPoint); // Center the map on the location

    }

    private void drawPolyline(double startLat, double startLon, double destLat, double destLon, String label) {

        // Create a GeoPoint for the destination
        GeoPoint startPoint = new GeoPoint(startLat, startLon);
        GeoPoint endPoint = new GeoPoint(destLat, destLon);

        // Create a polyline
        userLocationPolyline = new Polyline();
        userLocationPolyline.addPoint(startPoint);
        userLocationPolyline.addPoint(endPoint);

        // Set the style of the polyline
        userLocationPolyline.setColor(Color.RED);
        userLocationPolyline.setWidth(5f);

        // Add the polyline to the map
        mapView.getOverlays().add(userLocationPolyline);
        mapView.invalidate();

    }

    public void fetchStatuses() {
        if (usersOsm != null) {
            viewModel.fetchStatuses(usersOsm);
        }
    }

    
    // Method to sync contacts
    private void syncContacts() {
        disposables.add(ContactUtils.syncContacts()
                .subscribe()
        );
    }
    private void GetTrackerLocation(double startLat, double startLon, double destLat, double destLon, String label) {

        SharedPreferences sharedPrefs = getSharedPreferences("isFromMsgTracker", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor3 = sharedPrefs.edit();
        editor3.putString("isFromMsgTracker", "No");
        editor3.apply();

        // Create a GeoPoint for the destination
        GeoPoint startPoint = new GeoPoint(startLat, startLon);
        GeoPoint endPoint = new GeoPoint(destLat, destLon);

///////////// My User Marker //////////////////
        marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setTitle("You");
        marker.setIcon(vectorDrawable);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
        animateMapToMarker(destLat, destLon);
        mapView.getOverlays().add(marker);

///////////// Other User Marker //////////////////
        UsersOther = new Marker(mapView);
        marker.setPosition(endPoint);
        marker.setTitle(label);
        marker.setIcon(vectorDrawable);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
        animateMapToMarker(destLat, destLon);
        mapView.getOverlays().add(UsersOther);
        mapView.getController().setZoom(16.0);
        mapView.invalidate();

    }
}
