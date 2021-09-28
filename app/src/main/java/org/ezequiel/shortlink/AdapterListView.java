package org.ezequiel.shortlink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ping.shortlink.R;

import java.util.ArrayList;

public class AdapterListView extends ArrayAdapter<ShortAddress> {


    public AdapterListView(Context context, ArrayList<ShortAddress> urls) {
        super(context, 0, urls);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ShortAddress urls = getItem(position);
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
        TextView original_link = (TextView) convertView.findViewById(R.id.textViewOriginal_link);

        // Populate the data into the template view using the data object
        id.setText(String.valueOf(urls.getId()));
        date.setText(urls.getDate());
        link1.setText("shrtco.de/"+urls.getCode());
        link2.setText("9qr.de/" +urls.getCode());
        link3.setText("shiny.link/" +urls.getCode());
        original_link.setText(urls.getOriginal_link());

        // Return the completed view to render on screen
        return convertView;
    }












}
