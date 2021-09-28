package org.ezequiel.shortlink;

import static android.content.Context.WINDOW_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.zxing.WriterException;

import org.ping.shortlink.R;
import org.ping.shortlink.databinding.FragmentFirstBinding;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;


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


        binding.buttonGeneraterShort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (!binding.textInputUrl.getText().toString().isEmpty()) {
                        String url = binding.textInputUrl.getText().toString();
                        url = url.replace(" ", "");
                        GetShortLink shortLink = new GetShortLink(url);

                        if (shortLink.getShortlink().isOk()) {

                            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, getResources().getConfiguration().locale);
                            String date = df.format(Calendar.getInstance().getTime());

                            binding.textviewFirst.setText("shrtco.de/" + shortLink.getShortlink().getCode());
                            binding.textViewSecond.setText("9qr.de/" + shortLink.getShortlink().getCode());
                            binding.textViewThree.setText("shiny.link/" + shortLink.getShortlink().getCode());
                            binding.textViewError.setText("");
                            saveSharedPreferences(getActivity().getSharedPreferences(
                                    "savedUrl", Context.MODE_PRIVATE));
                            DataBase dataBase = new DataBase(getActivity());
                            dataBase.insert(shortLink.getShortlink().getCode(), date,
                                    url);

                            generateQrCode(binding.textviewFirst.getText().toString());

                        } else {
                            binding.textViewError.setText(shortLink.getShortlink().getErrorMensagem());

                        }

                    } else {
                        binding.textViewError.setText("Please enter a valid URL");
                    }

                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });


        binding.buttonCopy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(binding.textviewFirst.getText().toString());
            }
        });

        binding.buttonCopy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(binding.textViewSecond.getText().toString());
            }
        });

        binding.buttonCopy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(binding.textViewThree.getText().toString());
            }
        });

        binding.buttonShare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(binding.textviewFirst.getText().toString());
            }
        });
        binding.buttonShare2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(binding.textViewSecond.getText().toString());
            }
        });
        binding.buttonShare3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(binding.textViewThree.getText().toString());
            }
        });




        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        getSharedPreferences(getActivity().getSharedPreferences(
                "savedUrl", Context.MODE_PRIVATE));

    }

    private void saveSharedPreferences(SharedPreferences preferences){
        if(!binding.textInputUrl.getText().toString().isEmpty())
            preferences.edit().putString("longUrl",binding.textInputUrl.getText().toString()).apply();
        if(!binding.textviewFirst.getText().toString().isEmpty())
            preferences.edit().putString("shortUrl1",binding.textviewFirst.getText().toString()).apply();
        if(!binding.textViewSecond.getText().toString().isEmpty())
            preferences.edit().putString("shortUrl2",binding.textViewSecond.getText().toString()).apply();
        if(!binding.textViewThree.getText().toString().isEmpty())
            preferences.edit().putString("shortUrl3",binding.textViewThree.getText().toString()).apply();
    }

    private void getSharedPreferences(SharedPreferences preferences){
        binding.textInputUrl.setText(preferences.getString("longUrl",""));
        binding.textviewFirst.setText(preferences.getString("shortUrl1","Short link 1"));
        binding.textViewSecond.setText(preferences.getString("shortUrl2","Short link 2"));
        binding.textViewThree.setText(preferences.getString("shortUrl3","Short link 3"));

    }

    private void shareUrl(String url){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>"+url+"</p>"));
        startActivity(Intent.createChooser(sharingIntent, "Share using"));

    }

    private void copyUrl(String shortUrl) {

        if(shortUrl.contains("Short link"))
            return;

        ClipboardManager clipboard = (ClipboardManager)
                getParentFragment().getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Short url", shortUrl);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), "url was copied", Toast.LENGTH_LONG).show();
    }

    public void generateQrCode(String text){

        WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);

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
             binding.imageViewQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        saveSharedPreferences(getActivity().getSharedPreferences(
                "savedUrl", Context.MODE_PRIVATE));

        binding = null;

    }

}
