package org.ezequiel.shortlink;
//d:\android-sdk\platform-tools\adb.exe connect 192.168.0.36


import static org.ezequiel.shortlink.FirstFragment.generateQrCode;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.android.gms.ads.RequestConfiguration;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import org.ezequiel.shortlink.databinding.ActivityMainBinding;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnFailureListener;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private TextView textInputUrl;
    private TextView textviewFirst;
    private ImageView imageViewQrCode;
    private NavController navController;
    private BillingClient billingClient;
    private ProductDetails productDetails;
    private SharedPreferences sharedPreferences;
    public static boolean ENABLEAD = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedPreferences = getSharedPreferences("noad", MODE_PRIVATE);

        if (!sharedPreferences.contains("enableAd")) {
            sharedPreferences.edit().putBoolean("enableAd", true).apply();
        }

        try {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

       /* List<String> testDeviceIds = Arrays.asList("D9AA814C0E496E3B75381AF8514DC61F");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);*/

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setLogo(R.drawable.icon_title);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        textInputUrl = findViewById(R.id.textInputUrl);
        textviewFirst = findViewById(R.id.textView_first);
        imageViewQrCode = findViewById(R.id.imageViewQrCode);

    }

    public static void hideKeybaord(View v, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
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
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

            return true;
        }

        if (id == R.id.action_about) {
            about();
            return true;
        }

        if (id == R.id.action_remove) {
            try {
                makePurchase();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this,"an error has occurred.",Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.action_rate) {
            rateApp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

        if (Objects.requireNonNull(navController.getCurrentDestination()).getLabel().equals(getString(R.string.app_name))) {
            confirmExit();
        } else
            super.onBackPressed();

    }


    private void rateAppOnPlayStore() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void rateApp() {


        final ReviewManager reviewManager = ReviewManagerFactory.create(MainActivity.this);
        //reviewManager = new FakeReviewManager(this);
        com.google.android.play.core.tasks.Task<ReviewInfo> request = reviewManager.requestReviewFlow();

        request.addOnCompleteListener(new com.google.android.play.core.tasks.OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull com.google.android.play.core.tasks.Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    com.google.android.play.core.tasks.Task<Void> flow = reviewManager.launchReviewFlow(MainActivity.this, reviewInfo);
                    flow.addOnCompleteListener(new com.google.android.play.core.tasks.OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.play.core.tasks.Task<Void> task) {
                            Log.i("Rate Flow", "Sucess");
                        }
                    });

                    flow.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            rateAppOnPlayStore();
                            Log.i("Rate Flow", "Fail");
                            e.printStackTrace();
                        }
                    });

                } else {
                    try {
                        String reviewErrorCode = task.getException().getMessage();
                        Log.d("Rate Task Fail", "cause: " + reviewErrorCode);
                        rateAppOnPlayStore();
                    } catch (NullPointerException | ClassCastException e) {
                        rateAppOnPlayStore();
                        e.printStackTrace();
                    }
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Log.d("Rate Request", "Fail");
                rateAppOnPlayStore();
            }
        });

    }


    public void confirmExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Close the application ?")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(1);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("ResourceType")
    private void showHelp() throws IllegalArgumentException {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.getPrimaryNavigationFragment();

        try {
            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_FirstFragment_to_HelpFragment);
        } catch (IllegalArgumentException e) {
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

        String msg = getString(R.string.app_name) + " " + version + "\nDeveloper: Ezequiel A. Ribeiro" + "\nContact: " + getString(R.string.contact);
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

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();

        receivedFromShare();

        try {
            billingSetup();
            ENABLEAD = sharedPreferences.getBoolean("enableAd",true);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    private void billingSetup() {

        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {

                                          @Override
                                          public void onBillingSetupFinished(
                                                  @NonNull BillingResult billingResult) {

                                              if (billingResult.getResponseCode() ==
                                                      BillingClient.BillingResponseCode.OK) {
                                                  Log.i("ShortLink", "OnBillingSetupFinish connected");
                                                  queryProduct();
                                              } else {
                                                  Log.i("ShortLink", "OnBillingSetupFinish failed");
                                              }
                                          }

                                          @Override
                                          public void onBillingServiceDisconnected() {
                                              try {
                                                  billingSetup();
                                                  Log.i("ShortLink", "onBillingServiceDisconnected trying reconnection");
                                              } catch (IllegalStateException e) {
                                                  e.printStackTrace();
                                                  Log.i("ShortLink", "onBillingServiceDisconnected fail connection lost");
                                              }
                                          }
                                      }

        );
    }



    private void queryProduct() {

        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("noadshort")
                                                .setProductType(
                                                        BillingClient.ProductType.INAPP)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(
                            @NonNull BillingResult billingResult,
                            @NonNull List<ProductDetails> productDetailsList) {

                        if (!productDetailsList.isEmpty()) {
                            productDetails = productDetailsList.get(0);

                        } else {
                            Log.i("ShortLink", "onProductDetailsResponse: No products");
                        }
                    }
                }
        );
    }

    public void makePurchase() {

        BillingFlowParams billingFlowParams =
                BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                                ImmutableList.of(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                                .setProductDetails(productDetails)
                                                .build()
                                )
                        )
                        .build();

        billingClient.launchBillingFlow(this, billingFlowParams);
    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult,
                                   List<Purchase> purchases) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i("billingResultCan", "canceled");
            sharedPreferences.edit().putBoolean("enableAd", true).apply();
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.i("billingResultCan", "ITEM_ALREADY_OWNED");
            sharedPreferences.edit().putBoolean("enableAd", false).apply();
        }
    }


    private void handlePurchase(Purchase purchase) {

        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        sharedPreferences.edit().putBoolean("enableAd", false).apply();
                        Log.i("billingResult", "ITEM_OWNED PURCHASED");
                    }
                } else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                    sharedPreferences.edit().putBoolean("enableAd", true).apply();
                    Log.i("billingResult", "ITEM_NOT_OWNED UNSPECIFIED_STATE");

                } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                    sharedPreferences.edit().putBoolean("enableAd", true).apply();
                    Log.i("billingResult", "ITEM_NOT_OWNED PurchaseState.PENDING");
                }
            }


        };
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }else{
            sharedPreferences.edit().putBoolean("enableAd", true).apply();
            Log.i("billingResult", "ITEM_NOT_OWNED");
        }
    }




    public void receivedFromShare() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.e("text", action);
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    Log.e("text", sharedText);
                    Snackbar.make(getWindow().getDecorView().getRootView(), "URL received", Snackbar.LENGTH_LONG).show();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textInputUrl.setText(sharedText);
                            textviewFirst.setText("Short link 1");
                            imageViewQrCode.setImageBitmap(null);

                        }
                    });

                    if (!Patterns.WEB_URL.matcher(sharedText).matches()) {

                        Snackbar.make(MainActivity.this, getWindow().getDecorView()
                                .getRootView(), "url is invalid", Snackbar.LENGTH_INDEFINITE).show();

                        textInputUrl.setError("url is invalid");
                        textviewFirst.setError("error");
                        imageViewQrCode.setImageResource(R.drawable.icon150);

                    }

                    getIntent().setData(null);

                } else {
                    Log.e("text", "null");
                }

            }

        }
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        try {
            textInputUrl.setText(savedInstanceState.getString("longUrl"));
            textviewFirst.setText(savedInstanceState.getString("shortUrl1"));
            imageViewQrCode.setImageBitmap(generateQrCode(savedInstanceState.getString("longUrl"), getBaseContext()));
            super.onRestoreInstanceState(savedInstanceState);
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        try {
            if (!textviewFirst.getText().equals("wait...") && !textviewFirst.getText().equals("error")) {
                if (!textInputUrl.getText().toString().isEmpty())
                    outState.putString("longUrl", textInputUrl.getText().toString());
            }

            super.onSaveInstanceState(outState);
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }
    }


}