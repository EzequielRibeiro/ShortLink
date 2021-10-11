package org.ezequiel.shortlink;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.BannerListener;

import org.ezequiel.shortlink.databinding.FragmentSecondBinding;


public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private Banner startAppBanner;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DataBase dataBase = new DataBase(getActivity());

        AdapterListView adapter = new AdapterListView(getActivity(), dataBase.readData());
        binding.listViewHistoric.setAdapter(adapter);


        startAppBanner = new Banner(getActivity(), new BannerListener() {
            @Override
            public void onReceiveAd(View view) {

            }

            @Override
            public void onFailedToReceiveAd(View view) {

                binding.linearLayoutAdSecond.removeView(startAppBanner);
            }

            @Override
            public void onImpression(View view) {

            }

            @Override
            public void onClick(View view) {

            }
        });
        binding.linearLayoutAdSecond.addView(startAppBanner);




       /* binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}