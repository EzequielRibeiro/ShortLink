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
import android.graphics.BitmapFactory;
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
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;
import org.ezequiel.shortlink.databinding.FragmentFirstBinding;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private InterstitialAd mInterstitialAd;
    private Async async;
    public static final String URLSHRTCO = "https://api.shrtco.de/v2/shorten?url=";
    public static final String URLISGD = "https://is.gd/create.php?format=json&url=";
    public static final String URLVGD = "https://v.gd/create.php?format=json&url=";
    private final String CHECKCUSTOMURLISGD = "https://is.gd/forward.php?format=json&shorturl=";
    private final String CHECKCUSTOMURLVGD = "https://v.gd/forward.php?format=json&shorturl=";
    private final String URLSHORTNAME = "&shorturl=";
    public static final String URLSTATS = "&logstats=1";
    private View view;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        view = inflater.inflate(R.layout.fragment_first, container, false);
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        binding.textViewTap.setVisibility(View.INVISIBLE);
        intersticiaisAdLoad();
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.checkBoxVGD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b)
                   binding.checkBoxISGD.setChecked(false);
                else
                    binding.checkBoxISGD.setChecked(true);


            }
        });

        binding.checkBoxISGD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b)
                    binding.checkBoxVGD.setChecked(false);
                else
                    binding.checkBoxVGD.setChecked(true);

            }
        });


        binding.buttonGeneraterShort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    hideKeybaord(v, getActivity());

                    if (!binding.textInputUrl.getText().toString().isEmpty()) {

                        String url = binding.textInputUrl.getText().toString();
                        url = url.replace(" ", "");

                        if (Patterns.WEB_URL.matcher(url).matches()) {

                            cancelAsync();
                            async = new Async(v);
                            async.execute(url);
                            startProgress();
                            binding.textInputUrl.setError(null);


                        } else {

                            binding.textInputUrl.setError("url is invalid");
                            binding.textViewFirst.setText("error");
                            binding.textViewSecond.setText("error");
                            binding.textViewThree.setText("error");
                            binding.textViewFour.setText("error");
                            binding.imageViewQrCode.setImageResource(R.drawable.icon50);
                            binding.textViewTap.setVisibility(View.INVISIBLE);

                        }
                    } else {
                        binding.textInputUrl.setError("Please enter a valid URL");
                        binding.textViewTap.setVisibility(View.INVISIBLE);
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });


        binding.buttonCopy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(binding.textViewFirst.getText().toString(), v, getActivity());
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
                copyUrl(binding.textViewFour.getText().toString(), v, getActivity());
            }
        });

        binding.buttonShare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(binding.textViewFirst.getText().toString(), getActivity());
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
                shareUrl(binding.textViewFour.getText().toString(), getActivity());
            }
        });

        binding.buttonGoHistoric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAsync();
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
             }
        });

        binding.imageViewQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.textInputUrl.getText().toString().isEmpty())
                    new ShareQrCode(binding.textInputUrl.getText().toString(), getActivity());
            }
        });

        binding.textViewTap.setVisibility(View.INVISIBLE);



    }

    private void cancelAsync() {
        if (async != null) {
            if (async.getStatus() == AsyncTask.Status.RUNNING) {
                async.cancel(true);
                Log.i("AsyncTask", "canceled");
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelAsync();
    }

   /* private void saveSharedPreferences(SharedPreferences preferences) {

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

            if (!binding.textInputUrl.getText().toString().isEmpty())
                binding.imageViewQrCode.setImageBitmap(generateQrCode(binding.textInputUrl.getText().toString(), getActivity()));

        }
    }

    private void getSharedPreferences(SharedPreferences preferences) {
        binding.textInputUrl.setText(preferences.getString("longUrl", ""));
        binding.textviewFirst.setText(preferences.getString("shortUrl1", "Short link 1"));
        binding.textViewSecond.setText(preferences.getString("shortUrl2", "Short link 2"));
        binding.textViewThree.setText(preferences.getString("shortUrl3", "Short link 3"));
        binding.textviewFour.setText(preferences.getString("shortUrl4", "Short link 4"));

        String url = preferences.getString("longUrl", "");

        if (url.length() > 0) {
            binding.imageViewQrCode.setImageBitmap(generateQrCode(url, getActivity()));
            binding.textViewTap.setVisibility(View.VISIBLE);
        }
    }*/

    public static void shareUrl(String url, Context context) {

        if (url.contains("Short link"))
            return;

        if (url.equals("wait...") || url.equals("error"))
            return;

        if (!(url.contains("https://") || url.contains("https://"))) {
            url = "https://" + url;
        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>" + url + "</p>"));
        context.startActivity(Intent.createChooser(sharingIntent, url));


    }

    public static void copyUrl(String shortUrl, View v, Context context) {

        if (shortUrl.contains("Short link"))
            return;
        if (shortUrl.equals("wait...") || shortUrl.equals("error"))
            return;

        if (!(shortUrl.contains("https://") || shortUrl.contains("https://"))) {
            shortUrl = "https://" + shortUrl;
        }

        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Short url", shortUrl);
        clipboard.setPrimaryClip(clip);
        Snackbar.make(context, v, "url was copied", Snackbar.LENGTH_LONG).show();

    }

    public static Bitmap generateQrCode(String text, Context context) {

        Bitmap bitmap;

        if (text == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon50);
            return bitmap;
        }

        WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        QRGEncoder qrgEncoder;


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
        dimen = (dimen * 3) / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(text, null, QRGContents.Type.TEXT, dimen);

        // getting our qrcode in the form of bitmap.
        bitmap = qrgEncoder.getBitmap();

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
        String id = getString(R.string.intersticiais_ad_unit_id);
            //   id = "ca-app-pub-3940256099942544/1033173712"; //id to test ad

        InterstitialAd.load(getContext(),id, adRequest,
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

        binding.textViewFirst.setText("wait...");
        binding.textViewSecond.setText("wait...");
        binding.textViewThree.setText("wait...");
        binding.textViewFour.setText("wait...");
        binding.progressBar1.setVisibility(VISIBLE);
        binding.progressBar2.setVisibility(VISIBLE);
        binding.progressBar3.setVisibility(VISIBLE);
        binding.progressBar4.setVisibility(VISIBLE);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

    private class Async extends AsyncTask<String, String, ShortLink> {

        private GetShortLink getShortLink;

        private View view;
        private String url, urlToCompare;

        public Async(View view) {
            this.view = view;
        }

        @Override
        protected ShortLink doInBackground(String... params) {

            ShortLink shortLink = null;
            urlToCompare = params[0];
            url = params[0];

            try {
                getShortLink = new GetShortLink();
                getShortLink.requestShortlink(URLSHRTCO + params[0]);

                if (!binding.textInputUrlCustomName.getText().toString().isEmpty()) {

                    url = url + URLSHORTNAME + binding.textInputUrlCustomName.getText().toString();
                }

                if (binding.checkBoxStatistic.isChecked() && binding.checkBoxVGD.isChecked())
                    getShortLink.requestShortlink(URLVGD + url + URLSTATS);
                else if (!binding.checkBoxStatistic.isChecked() && binding.checkBoxVGD.isChecked())
                    getShortLink.requestShortlink(URLVGD + url);
                else if (binding.checkBoxStatistic.isChecked() && binding.checkBoxISGD.isChecked())
                    getShortLink.requestShortlink(URLISGD + url + URLSTATS);
                else if (!binding.checkBoxStatistic.isChecked() && binding.checkBoxISGD.isChecked())
                    getShortLink.requestShortlink(URLISGD + url);

                shortLink = getShortLink.getShortlink();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return shortLink;
        }

        protected void onPostExecute(ShortLink result) {

            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, getActivity().getResources().getConfiguration().locale);
            String date = df.format(Calendar.getInstance().getTime());

            try {

                ShortLink shortlink = new ShortLink();

                if (result != null)
                    if (result.getIsOkUrl1()) {

                        binding.textViewFirst.setText("shrtco.de/" + result.getCode1());
                        binding.textViewFirst.setError(null);
                        binding.textViewSecond.setText("9qr.de/" + result.getCode1());
                        binding.textViewSecond.setError(null);
                        binding.textViewThree.setText("shiny.link/" + result.getCode1());
                        binding.textViewThree.setError(null);

                        DataBase dataBase = new DataBase(getActivity());

                        if (dataBase.selectUrlExists(result.getOriginalLink()) > 0) {
                            dataBase.updateShortUrl1(result.getCode1(), date, urlToCompare);

                        } else {
                            dataBase.insertShortUrl1(result.getCode1(), date, urlToCompare);
                        }
                        binding.textInputUrl.setError(null);
                        binding.textViewTap.setVisibility(VISIBLE);

                    } else {
                        binding.textViewFirst.setText("error");
                        binding.textViewFirst.setError("");
                        binding.textViewSecond.setText("error");
                        binding.textViewSecond.setError("");
                        binding.textViewThree.setText("error");
                        binding.textViewThree.setError("");
                        binding.textInputUrl.setError(result.getErrorMensagem1());
                        binding.textViewTap.setVisibility(View.INVISIBLE);
                    }

                if (result != null)
                    if (result.getIsOkUrl2()) {

                        binding.textViewFour.setText(result.getCode2().replace("https://", ""));
                        saveInDataBase2(result.getCode2(), date, urlToCompare);
                        binding.textInputUrlCustomName.setError(null);
                        binding.textViewFour.setError(null);

                    } else if (result.getError_code2().equals("2")) {
                        //error 2 is reference for custom name exist
                        String custom = binding.textInputUrlCustomName.getText().toString();

                        GetShortLink getShortLink = new GetShortLink();

                        if (result.getUrlApi().contains("https://v.gd/"))
                            getShortLink.requestShortlink(CHECKCUSTOMURLVGD + custom);
                        else
                            getShortLink.requestShortlink(CHECKCUSTOMURLISGD + custom);

                        shortlink = getShortLink.getShortlink();

                        String url1, url2;
                        url1 = urlToCompare;
                        url2 = shortlink.getUrl();
                        

                        if (url1.contains("http://")) {
                            url1 = url1.replace("http://", "");
                        }
                        if (url1.contains("https://")) {
                            url1 = url1.replace("https://", "");
                        }
                        if (url2.contains("http://")) {
                            url2 = url2.replace("http://", "");
                        }
                        if (url2.contains("https://")) {
                            url2 = url2.replace("https://", "");
                        }

                        if (url1.equals(url2)) {

                            String temp;

                            if (result.getUrlApi().contains("https://v.gd/"))
                                temp = "v.gd/" + custom;
                            else
                                temp = "is.gd/" + custom;
                            binding.textViewFour.setText(temp);
                            saveInDataBase2(temp, date, urlToCompare);
                            shortlink.setIsOkUr2(true);
                            binding.textInputUrlCustomName.setError(null);
                            binding.textViewFour.setError(null);
                        } else {
                            showError(result);
                        }

                    } else {
                        showError(result);
                    }

                binding.imageViewQrCode.setImageBitmap(generateQrCode(result.getUrl(), getActivity()));
                binding.textViewTap.setVisibility(View.VISIBLE);

                if (result.getIsOkUrl1() == true && shortlink.getIsOkUrl2() == true) {
                    Snackbar.make(getActivity(), view, "Success! ", Snackbar.LENGTH_LONG).show();

                }


            } catch (IllegalArgumentException e) {
                Snackbar.make(getActivity(), view, "Failed to create a short link! ", Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (NullPointerException e) {
                Snackbar.make(getActivity(), view, "Failed to create a short link! ", Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Snackbar.make(getActivity(), view, "Failed to create a short link! ", Snackbar.LENGTH_LONG).show();
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

            showInterstitial();

        }

        private void showError(ShortLink shortlink) {

            binding.textInputUrlCustomName.setError(shortlink.getErrorMensagem2());
            binding.textViewFour.setText("error");
            binding.textViewFour.setError("");


        }

        private void saveInDataBase2(String code, String date, String url) {

            DataBase dataBase = new DataBase(getActivity());

            if (dataBase.selectUrlExists(url) > 0) {
                dataBase.updateShortUrl2(code, date, url);
                Log.e("code", code);

            } else {
                dataBase.insertShortUrl2(code, date, url);
            }

        }

    }

}
