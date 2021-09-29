package org.ezequiel.shortlink;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.WINDOW_SERVICE;
import static android.widget.Toast.LENGTH_LONG;
import static androidx.core.content.ContextCompat.getSystemService;

import static org.ezequiel.shortlink.MainActivity.hideKeybaord;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import org.ezequiel.shortlink.databinding.FragmentFirstBinding;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private InterstitialAd mInterstitialAd;


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
                Banner startAppBanner = new Banner(getActivity());
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

                            new Async(v).execute(url);

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


        binding.buttonShareQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareQrCode(binding.imageViewQrCode, getActivity());
            }
        });


        binding.buttonGoHistoric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getSharedPreferences(getActivity().getSharedPreferences(
                "savedUrl", Context.MODE_PRIVATE));


    }

    private void saveSharedPreferences(SharedPreferences preferences) {
        if (!binding.textInputUrl.getText().toString().isEmpty())
            preferences.edit().putString("longUrl", binding.textInputUrl.getText().toString()).apply();
        if (!binding.textviewFirst.getText().toString().isEmpty())
            preferences.edit().putString("shortUrl1", binding.textviewFirst.getText().toString()).apply();
        if (!binding.textViewSecond.getText().toString().isEmpty())
            preferences.edit().putString("shortUrl2", binding.textViewSecond.getText().toString()).apply();
        if (!binding.textViewThree.getText().toString().isEmpty())
            preferences.edit().putString("shortUrl3", binding.textViewThree.getText().toString()).apply();
        generateQrCode(binding.textInputUrl.getText().toString(), getActivity(), binding.imageViewQrCode, binding.buttonShareQrCode);
    }

    private void getSharedPreferences(SharedPreferences preferences) {
        binding.textInputUrl.setText(preferences.getString("longUrl", ""));
        binding.textviewFirst.setText(preferences.getString("shortUrl1", "Short link 1"));
        binding.textViewSecond.setText(preferences.getString("shortUrl2", "Short link 2"));
        binding.textViewThree.setText(preferences.getString("shortUrl3", "Short link 3"));

        String url = preferences.getString("longUrl", "");

        if (!url.isEmpty()) {
            generateQrCode(url, getActivity(), binding.imageViewQrCode, binding.buttonShareQrCode);
        }

    }

    public static void shareUrl(String url, Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>" + url + "</p>"));
        context.startActivity(Intent.createChooser(sharingIntent, url));

    }

    public static void copyUrl(String shortUrl, View v, Context context) {

        if (shortUrl.contains("Short link"))
            return;

        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Short url", shortUrl);
        clipboard.setPrimaryClip(clip);
        Snackbar.make(context, v, "url was copied", Snackbar.LENGTH_LONG).show();
    }

    public static void generateQrCode(String text, Context context, ImageView imageViewQrCode, Button buttonShareQrCode) {

        WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        QRGEncoder qrgEncoder;
        Bitmap bitmap;

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
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            imageViewQrCode.setImageBitmap(bitmap);
            buttonShareQrCode.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }


    }

    private void showInterstitial(){
        if (mInterstitialAd != null) {
            mInterstitialAd.show(getActivity());
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            StartAppAd.showAd(getActivity());
        }
    }

    private void intersticiaisAdLoad(){

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(),getString(R.string.intersticiais_ad_unit_id), adRequest,
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        saveSharedPreferences(getActivity().getSharedPreferences(
                "savedUrl", Context.MODE_PRIVATE));

        binding = null;

    }

    private class Async extends AsyncTask<String, String, String> {

        private GetShortLink shortLink;
        private View view;
        private String url;

        public Async(View view) {
            this.view = view;
        }

        @Override
        protected String doInBackground(String... params) {

            url = params[0];

            try {
                shortLink = new GetShortLink(url);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {

            try {

                if (shortLink != null)
                    if (shortLink.getShortlink().isOk()) {
                        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, getResources().getConfiguration().locale);
                        String date = df.format(Calendar.getInstance().getTime());

                        binding.textviewFirst.setText("shrtco.de/" + shortLink.getShortlink().getCode());
                        binding.textViewSecond.setText("9qr.de/" + shortLink.getShortlink().getCode());
                        binding.textViewThree.setText("shiny.link/" + shortLink.getShortlink().getCode());
                        saveSharedPreferences(getActivity().getSharedPreferences(
                                "savedUrl", Context.MODE_PRIVATE));
                        DataBase dataBase = new DataBase(getActivity());
                        dataBase.insert(shortLink.getShortlink().getCode(), date,
                                url);

                        generateQrCode(url, getActivity(), binding.imageViewQrCode, binding.buttonShareQrCode);

                        Snackbar.make(getActivity(), view, "Short url created", Snackbar.LENGTH_LONG).show();

                    } else {
                        Snackbar.make(getActivity(), view, shortLink.getShortlink().getErrorMensagem(), Snackbar.LENGTH_LONG).show();
                    }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

    }

}
