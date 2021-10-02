package org.ezequiel.shortlink;

import static android.content.Context.WINDOW_SERVICE;
import static android.view.View.VISIBLE;

import static org.ezequiel.shortlink.MainActivity.hideKeybaord;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.BannerListener;

import org.ezequiel.shortlink.databinding.FragmentFirstBinding;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private InterstitialAd mInterstitialAd;
    private Banner startAppBanner;
    private Async async;
    private final String URL1 = "https://api.shrtco.de/v2/shorten?url=";
    private final String URL2 = "https://is.gd/create.php?format=json&url=";
    // for custom name link
    private final String URLSHORTNAME = "&shorturl=";


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        intersticiaisAdLoad();

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                binding.linearLayoutAd.removeView(binding.adView);

                startAppBanner = new Banner(getActivity(), new BannerListener() {
                    @Override
                    public void onReceiveAd(View view) {

                    }

                    @Override
                    public void onFailedToReceiveAd(View view) {
                        startAppBanner = null;
                    }

                    @Override
                    public void onImpression(View view) {

                    }

                    @Override
                    public void onClick(View view) {

                    }
                });

                if (startAppBanner != null)
                    binding.linearLayoutAd.addView(startAppBanner);

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


        binding.buttonGeneraterShort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showInterstitial();

                try {

                    hideKeybaord(v, getActivity());

                    if (!binding.textInputUrl.getText().toString().isEmpty()) {

                        String url = binding.textInputUrl.getText().toString();


                        if (Patterns.WEB_URL.matcher(url).matches()) {

                            cancelAsync();
                            async = new Async(v);
                            async.execute(url);
                            startProgress();

                        } else {
                            Snackbar.make(getActivity(), v, "url is invalid", Snackbar.LENGTH_LONG).show();

                        }
                    } else {

                        Snackbar.make(getActivity(), v, "Please enter a valid URL", Snackbar.LENGTH_LONG).show();

                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });


        binding.buttonCopy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(binding.textviewFirst.getText().toString(), v, getActivity());
            }
        });

        binding.buttonCopy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(binding.textViewSecond.getText().toString(), v, getActivity());
            }
        });

        binding.buttonCopy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(binding.textViewThree.getText().toString(), v, getActivity());
            }
        });

        binding.buttonCopy4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(binding.textviewFour.getText().toString(), v, getActivity());
            }
        });

        binding.buttonShare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(binding.textviewFirst.getText().toString(), getActivity());
            }
        });
        binding.buttonShare2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(binding.textViewSecond.getText().toString(), getActivity());
            }
        });
        binding.buttonShare3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(binding.textViewThree.getText().toString(), getActivity());
            }
        });

        binding.buttonShare4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(binding.textviewFour.getText().toString(), getActivity());
            }
        });


        binding.buttonShareQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareQrCode(binding.textInputUrl.getText().toString(), getActivity());
            }
        });


        binding.buttonGoHistoric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAsync();
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
                showInterstitial();
            }
        });




    }

    private void cancelAsync() {
        if (async != null) {
            if (async.getStatus() == AsyncTask.Status.RUNNING) {
                async.cancel(true);
                Log.i("AsyncTask", "canceled");
            }
        }
    }

    private void receivedFromShare(){
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {

                    Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), "URL received", Snackbar.LENGTH_LONG).show();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.textInputUrl.setText(sharedText);

                        }
                    });

                    cancelAsync();
                    async = new Async(getActivity().getWindow().getDecorView().getRootView());
                    async.execute(sharedText);
                    startProgress();
                    getActivity().getIntent().setData(null);

                }

            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onStop() {
        super.onStop();
        cancelAsync();
    }

    @Override
    public void onStart() {
        super.onStart();
        getSharedPreferences(getActivity().getSharedPreferences(
                "savedUrl", Context.MODE_PRIVATE));

        receivedFromShare();


    }

    private void saveSharedPreferences(SharedPreferences preferences) {

        if (!binding.textviewFirst.getText().equals("wait...") && !binding.textviewFirst.getText().equals("error")) {
            if (!binding.textInputUrl.getText().toString().isEmpty())
                preferences.edit().putString("longUrl", binding.textInputUrl.getText().toString()).apply();
            if (!binding.textviewFirst.getText().toString().isEmpty())
                preferences.edit().putString("shortUrl1", binding.textviewFirst.getText().toString()).apply();
            if (!binding.textViewSecond.getText().toString().isEmpty())
                preferences.edit().putString("shortUrl2", binding.textViewSecond.getText().toString()).apply();
            if (!binding.textViewThree.getText().toString().isEmpty())
                preferences.edit().putString("shortUrl3", binding.textViewThree.getText().toString()).apply();
            if (!binding.textviewFour.getText().toString().isEmpty())
                preferences.edit().putString("shortUrl4", binding.textviewFour.getText().toString()).apply();

            binding.imageViewQrCode.setImageBitmap(generateQrCode(binding.textInputUrl.getText().toString(), getActivity()));
            binding.buttonShareQrCode.setVisibility(VISIBLE);
        }
    }

    private void getSharedPreferences(SharedPreferences preferences) {
        binding.textInputUrl.setText(preferences.getString("longUrl", ""));
        binding.textviewFirst.setText(preferences.getString("shortUrl1", "Short link 1"));
        binding.textViewSecond.setText(preferences.getString("shortUrl2", "Short link 2"));
        binding.textViewThree.setText(preferences.getString("shortUrl3", "Short link 3"));
        binding.textviewFour.setText(preferences.getString("shortUrl4", "Short link 4"));

        String url = preferences.getString("longUrl", "");

        if (!url.isEmpty()) {
            binding.imageViewQrCode.setImageBitmap(generateQrCode(url, getActivity()));
            binding.buttonShareQrCode.setVisibility(View.VISIBLE);
        }

    }

    public static void shareUrl(String url, Context context) {

        if (url.contains("Short link"))
            return;

        if (url.equals("wait...") || url.equals("error"))
            return;

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>" + url + "</p>"));
        context.startActivity(Intent.createChooser(sharingIntent, "https://"+url));


    }

    public static void copyUrl(String shortUrl, View v, Context context) {

        if (shortUrl.contains("Short link"))
            return;
        if (shortUrl.equals("wait...") || shortUrl.equals("error"))
            return;


        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Short url", "https://"+shortUrl);
        clipboard.setPrimaryClip(clip);
        Snackbar.make(context, v, "url was copied", Snackbar.LENGTH_LONG).show();

    }

    public static Bitmap generateQrCode(String text, Context context) {

        WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        QRGEncoder qrgEncoder;
        Bitmap bitmap = null;

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(text, null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();

        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }

        return bitmap;

    }

    private void showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(getActivity());
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");

        }
    }

    private void intersticiaisAdLoad() {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(), getString(R.string.intersticiais_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("InterstitialAd", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("InterstitialAd", loadAdError.getMessage());
                        mInterstitialAd = null;

                    }
                });


    }

    private void startProgress() {

        binding.textviewFirst.setText("wait...");
        binding.textViewSecond.setText("wait...");
        binding.textViewThree.setText("wait...");
        binding.textviewFour.setText("wait...");
        binding.progressBar1.setVisibility(VISIBLE);
        binding.progressBar2.setVisibility(VISIBLE);
        binding.progressBar3.setVisibility(VISIBLE);
        binding.progressBar4.setVisibility(VISIBLE);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        saveSharedPreferences(getActivity().getSharedPreferences(
                "savedUrl", Context.MODE_PRIVATE));

        binding = null;

    }

    private class Async extends AsyncTask<String, String, String> {

        private GetShortLink getShortLink;
        private ShortLink    shortLink;
        private View view;
        private String url;
        private String urlTemp;

        public Async(View view) {
            this.view = view;
        }

        @Override
        protected String doInBackground(String... params) {

            url = params[0];
            urlTemp = url;

            try {
                getShortLink = new GetShortLink();
                getShortLink.requestShortlink(URL1+url);

                if(!binding.textInputUrlCustomName.getText().toString().isEmpty()) {

                    url = url + URLSHORTNAME+binding.textInputUrlCustomName.getText().toString();
                }

                getShortLink.requestShortlink(URL2+url);
                shortLink = getShortLink.getShortlink();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {

            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, getActivity().getResources().getConfiguration().locale);
            String date = df.format(Calendar.getInstance().getTime());

            try {

                if (shortLink != null)
                    if (shortLink.getIsOkUrl1()) {

                        binding.textviewFirst.setText("shrtco.de/" + shortLink.getCode1());
                        binding.textViewSecond.setText("9qr.de/" + shortLink.getCode1());
                        binding.textViewThree.setText("shiny.link/" + shortLink.getCode1());
                        saveSharedPreferences(getActivity().getSharedPreferences(
                                "savedUrl", Context.MODE_PRIVATE));

                        DataBase dataBase = new DataBase(getActivity());

                        if(dataBase.selectUrlExists(urlTemp) > 0){
                            dataBase.updateShortUrl1(shortLink.getCode1(),date,urlTemp);

                        }else{
                            dataBase.insertShortUrl1(shortLink.getCode1(), date,urlTemp);
                        }


                        Snackbar.make(getActivity(), view, "Success! ", Snackbar.LENGTH_LONG).show();

                    } else {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.textviewFirst.setText("error");
                                binding.textViewSecond.setText("error");
                                binding.textViewThree.setText("error");

                            }
                        });
                        Snackbar.make(getActivity(), view, shortLink.getErrorMensagem1(), Snackbar.LENGTH_INDEFINITE).show();
                    }

                if (shortLink != null)
                    if (shortLink.getIsOkUrl2()) {

                        binding.textviewFour.setText(shortLink.getCode2().replace("https://",""));
                        saveSharedPreferences(getActivity().getSharedPreferences(
                                "savedUrl", Context.MODE_PRIVATE));
                        DataBase dataBase = new DataBase(getActivity());

                        if(dataBase.selectUrlExists(urlTemp) > 0){
                            dataBase.updateShortUrl2(shortLink.getCode2(),date,urlTemp);

                        }else{
                            dataBase.insertShortUrl2(shortLink.getCode2(), date,urlTemp);
                        }
                        Snackbar.make(getActivity(), view, "Success! ", Snackbar.LENGTH_LONG).show();

                    } else {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              binding.textviewFour.setText("error");

                            }
                        });
                        Snackbar.make(getActivity(), view, shortLink.getErrorMensagem2(), Snackbar.LENGTH_INDEFINITE).show();
                    }


                binding.imageViewQrCode.setImageBitmap(generateQrCode(url, getActivity()));
                binding.buttonShareQrCode.setVisibility(View.VISIBLE);


            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.progressBar1.setVisibility(View.GONE);
                    binding.progressBar2.setVisibility(View.GONE);
                    binding.progressBar3.setVisibility(View.GONE);
                    binding.progressBar4.setVisibility(View.GONE);
                }
            });

        }

    }

}
