package org.ezequiel.shortlink;

import static org.ezequiel.shortlink.FirstFragment.copyUrl;
import static org.ezequiel.shortlink.FirstFragment.generateQrCode;
import static org.ezequiel.shortlink.FirstFragment.shareUrl;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class AdapterListView extends ArrayAdapter<ShortLink> {

    private ArrayList<ShortLink> urlsList;

    public AdapterListView(Context context, ArrayList<ShortLink> urls) {
        super(context, 0, urls);
        urlsList = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ShortLink urls = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_short_url, parent, false);
        }
        // Lookup view for data population
        TextView id = (TextView) convertView.findViewById(R.id.textViewId);
        TextView date = (TextView) convertView.findViewById(R.id.textViewShortDate);
        TextView link1 = (TextView) convertView.findViewById(R.id.textViewShort1);
        TextView link2 = (TextView) convertView.findViewById(R.id.textViewShort2);
        TextView link3 = (TextView) convertView.findViewById(R.id.textViewShort3);
        TextView link4 = (TextView) convertView.findViewById(R.id.textViewShort4);
        TextView original_link = (TextView) convertView.findViewById(R.id.textViewOriginal_link);
        Button buttonShareQrCode = (Button) convertView.findViewById(R.id.buttonShareQrCodeHistoric);
        ImageView imageViewQrCode = (ImageView) convertView.findViewById(R.id.imageViewQrCodeHistoric);

        // Populate the data into the template view using the data object
        id.setText(String.valueOf(urls.getId()));
        date.setText(urls.getDate());
        if(!urls.getCode1().equals("error")) {
            link1.setText("shrtco.de/" + urls.getCode1());
            link2.setText("9qr.de/" + urls.getCode1());
            link3.setText("shiny.link/" + urls.getCode1());
        }
        if(!urls.getCode2().equals("error"))
           link4.setText(urls.getCode2().replace("https://",""));

        original_link.setText(urls.getOriginal_link());

        imageViewQrCode.setImageBitmap(generateQrCode(original_link.getText().toString(),
                getContext()));
        buttonShareQrCode.setVisibility(View.VISIBLE);

        buttonShareQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) getContext();
                new ShareQrCode(urls.getOriginal_link(),activity);
            }
        });

        Button buttonCopyHistoric1 = (Button) convertView.findViewById(R.id.buttonCopyHistoric1);
        Button buttonCopyHistoric2 = (Button) convertView.findViewById(R.id.buttonCopyHistoric2);
        Button buttonCopyHistoric3 = (Button) convertView.findViewById(R.id.buttonCopyHistoric3);
        Button buttonCopyHistoric4 = (Button) convertView.findViewById(R.id.buttonCopyHistoric4);

        Button buttonShareHistoric1 = (Button) convertView.findViewById(R.id.buttonShareHistoric1);
        Button buttonShareHistoric2 = (Button) convertView.findViewById(R.id.buttonShareHistoric2);
        Button buttonShareHistoric3 = (Button) convertView.findViewById(R.id.buttonShareHistoric3);
        Button buttonShareHistoric4 = (Button) convertView.findViewById(R.id.buttonShareHistoric4);

        ImageButton buttonDeleteHistoric = (ImageButton) convertView.findViewById(R.id.imageButtonDeleteHistoric);
        buttonDeleteHistoric.setTag(urls.getId());

        buttonCopyHistoric1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(link1.getText().toString(),v, getContext());
            }
        });
        buttonCopyHistoric2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(link2.getText().toString(),v, getContext());
            }
        });
        buttonCopyHistoric3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(link3.getText().toString(),v, getContext());
            }
        });

        buttonCopyHistoric4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrl(link4.getText().toString(),v, getContext());
            }
        });

        buttonShareHistoric1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(link1.getText().toString(),getContext());
            }
        });
        buttonShareHistoric2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(link2.getText().toString(),getContext());
            }
        });
        buttonShareHistoric3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(link3.getText().toString(),getContext());
            }
        });

        buttonShareHistoric4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(link4.getText().toString(),getContext());
            }
        });

        buttonDeleteHistoric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildShowDialodDelete(urls,v);

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }


    private void buildShowDialodDelete(ShortLink urls, View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Delete Short Link");

        builder.setMessage("Short Link will be permanently deleted !");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DataBase dataBase = new DataBase(getContext());
                if(dataBase.delete( String.valueOf(urls.getId())) > 0){

                    Snackbar.make(getContext(), v, "deleted value", Snackbar.LENGTH_LONG).show();
                    urlsList.remove(urls);
                    notifyDataSetChanged();

                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }










}
