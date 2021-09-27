package org.ezequiel.shortlink;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.ping.shortlink.R;
import org.ping.shortlink.databinding.FragmentFirstBinding;

import java.io.IOException;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;


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
                            binding.textviewFirst.setText("shrtco.de/" + shortLink.getShortlink().getCode());
                            binding.textViewSecond.setText("9qr.de/" + shortLink.getShortlink().getCode());
                            binding.textViewThree.setText("shiny.link/" + shortLink.getShortlink().getCode());
                            binding.textViewError.setText("");
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
        SharedPreferences preferences =  getActivity().getSharedPreferences(
                "savedUrl", Context.MODE_PRIVATE);

            binding.textInputUrl.setText(preferences.getString("longUrl",""));
            binding.textviewFirst.setText(preferences.getString("shortUrl1",""));
            binding.textViewSecond.setText(preferences.getString("shortUrl2",""));
            binding.textViewThree.setText(preferences.getString("shortUrl3",""));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SharedPreferences preferences =  getActivity().getSharedPreferences(
                "savedUrl", Context.MODE_PRIVATE);
        if(!binding.textInputUrl.getText().toString().isEmpty())
            preferences.edit().putString("longUrl",binding.textInputUrl.getText().toString()).apply();
            if(!binding.textviewFirst.getText().toString().isEmpty())
                preferences.edit().putString("shortUrl1",binding.textviewFirst.getText().toString()).apply();
                if(!binding.textViewSecond.getText().toString().isEmpty())
                    preferences.edit().putString("shortUrl2",binding.textViewSecond.getText().toString()).apply();
                    if(!binding.textViewThree.getText().toString().isEmpty())
                        preferences.edit().putString("shortUrl3",binding.textViewThree.getText().toString()).apply();

        binding = null;

    }

}
