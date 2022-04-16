package org.ezequiel.shortlink;
//d:\android-sdk\platform-tools\adb.exe connect 192.168.0.36


import static org.ezequiel.shortlink.FirstFragment.generateQrCode;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.BannerListener;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import org.ezequiel.shortlink.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Banner startAppBanner;
    private TextView textInputUrl;
    private TextView textviewFirst;
    private TextView textViewSecond;
    private TextView textViewThree;
    private TextView textviewFour;
    private ImageView imageViewQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        try {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            StartAppSDK.init(this, getString(R.string.startapp_app_id), false);
            StartAppAd.disableSplash();
        }catch(NullPointerException e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        /*List<String> testDeviceIds = Arrays.asList("DB530A1BBBDBFE8567328113528A19EF");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);*/

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setLogo(R.drawable.icon_title);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        textInputUrl = findViewById(R.id.textInputUrl);
        textviewFirst = findViewById(R.id.textView_first);
        textViewSecond = findViewById(R.id.textView_second);
        textViewThree = findViewById(R.id.textView_three);
        textviewFour = findViewById(R.id.textView_four);
        imageViewQrCode = findViewById(R.id.imageViewQrCode);

    }

    public static void hideKeybaord(View v, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager)  context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_privacy) {
            Intent intent = new Intent(getBaseContext(), PrivacyPolicyHelp.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_help) {

            try {
                showHelp();
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }

            return true;
        }

        if (id == R.id.action_about) {
            about();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("ResourceType")
    private void showHelp() throws IllegalArgumentException {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.getPrimaryNavigationFragment();

        try {
            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_FirstFragment_to_HelpFragment);
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_SecondFragment_to_HelpFragment);


        }
    }

    private void about() {

        TextView message = new TextView(MainActivity.this);
        message.setPadding(10, 0, 0, 0);
        String version = "v";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version += pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String msg = getString(R.string.app_name) + " " + version + "\nDeveloper: Ezequiel A. Ribeiro" + "\nContact: "+getString(R.string.contact);
        final SpannableString s = new SpannableString(msg);
        Linkify.addLinks(s, Linkify.ALL);

        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setView(message)
                .setTitle("About");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 new StartAppAd(getBaseContext()).show();  ;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onResume(){
        super.onResume();
        receivedFromShare();
        try{
        loadAdmob();
        }catch (NullPointerException exception){
            exception.printStackTrace();
        }

    }

    public void loadAdmob(){

        AdView adView = findViewById(R.id.adView);
        LinearLayout linearLayoutAd = findViewById(R.id.linearLayoutAd);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                linearLayoutAd.removeAllViews();

                startAppBanner = new Banner(MainActivity.this, new BannerListener() {
                    @Override
                    public void onReceiveAd(View view) {

                    }

                    @Override
                    public void onFailedToReceiveAd(View view) {
                       linearLayoutAd.removeView(startAppBanner);
                    }

                    @Override
                    public void onImpression(View view) {

                    }

                    @Override
                    public void onClick(View view) {

                    }
                });

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }

    public void receivedFromShare() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.e("text",action);
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    Log.e("text",sharedText);
                    Snackbar.make(getWindow().getDecorView().getRootView(), "URL received", Snackbar.LENGTH_LONG).show();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textInputUrl.setText(sharedText);
                            textviewFirst.setText("Short link 1");
                            textViewSecond.setText("Short link 2");
                            textViewThree.setText("Short link 3");
                            textviewFour.setText("Short link 4");
                            imageViewQrCode.setImageBitmap(null);

                        }
                    });

                    if (!Patterns.WEB_URL.matcher(sharedText).matches()) {

                        Snackbar.make(MainActivity.this,getWindow().getDecorView()
                                .getRootView(), "url is invalid", Snackbar.LENGTH_INDEFINITE).show();

                        textInputUrl.setError("url is invalid");
                        textviewFirst.setError("error");
                        textViewSecond.setError("error");
                        textViewThree.setError("error");
                        textviewFour.setError("error");
                        imageViewQrCode.setImageResource(R.drawable.icon150);

                    }

                    getIntent().setData(null);

                }else{
                    Log.e("text","null");
                }

            }

        }
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      try {
          textInputUrl.setText(savedInstanceState.getString("longUrl"));
          textviewFirst.setText(savedInstanceState.getString("shortUrl1"));
          textViewSecond.setText(savedInstanceState.getString("shortUrl2"));
          textViewThree.setText(savedInstanceState.getString("shortUrl3"));
          textviewFour.setText(savedInstanceState.getString("shortUrl4"));
          imageViewQrCode.setImageBitmap(generateQrCode(savedInstanceState.getString("longUrl"), getBaseContext()));

          new StartAppAd(getBaseContext()).onRestoreInstanceState(savedInstanceState);
          super.onRestoreInstanceState(savedInstanceState);
      }catch (NullPointerException exception){
          exception.printStackTrace();
      }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        try {
            if (!textviewFirst.getText().equals("wait...") && !textviewFirst.getText().equals("error")) {
                if (!textInputUrl.getText().toString().isEmpty())
                    outState.putString("longUrl", textInputUrl.getText().toString());
                if (!textviewFirst.getText().toString().isEmpty())
                    outState.putString("shortUrl1", textviewFirst.getText().toString());
                if (!textViewSecond.getText().toString().isEmpty())
                    outState.putString("shortUrl2", textViewSecond.getText().toString());
                if (!textViewThree.getText().toString().isEmpty())
                    outState.putString("shortUrl3", textViewThree.getText().toString());
                if (!textviewFour.getText().toString().isEmpty())
                    outState.putString("shortUrl4", textviewFour.getText().toString());

            }
            new StartAppAd(getBaseContext()).onSaveInstanceState(outState);
            super.onSaveInstanceState(outState);
        }catch (NullPointerException exception){
            exception.printStackTrace();
        }
    }


}