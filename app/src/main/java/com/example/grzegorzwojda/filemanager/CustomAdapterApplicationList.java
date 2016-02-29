package com.example.grzegorzwojda.filemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Grzegorz Wojda on 2016-02-09.
 */

public class CustomAdapterApplicationList extends ArrayAdapter<Application> {

    Context context;
    List<Application> applications;

    public CustomAdapterApplicationList(Context context, List<Application> applications) {
        super(context, R.layout.dialog_fragment_row, applications);
        this.applications = applications;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater
                    = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dialog_fragment_row, null);
        } else view = convertView;

        ImageView applicationIcon = (ImageView) view.findViewById(R.id.application_icon);
        TextView applicationName = (TextView) view.findViewById(R.id.application_name);

        applicationIcon.setImageDrawable(applications.get(position).getIcon());
        applicationName.setText(applications.get(position).getLabel());
        return view;
    }
}
