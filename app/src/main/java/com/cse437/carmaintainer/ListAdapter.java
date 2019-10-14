package com.cse437.carmaintainer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Car> {


    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int textViewResourceId, List<Car> items) {
        super(context, textViewResourceId, items);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listitem, null);
        }

        Car c = getItem(position);

        if (c != null) {
            TextView makeView = v.findViewById(R.id.makeView);
            TextView modelView = v.findViewById(R.id.modelView);
            TextView yearView = v.findViewById(R.id.yearView);
            TextView volumeView = v.findViewById(R.id.volumeView);
            TextView estMileage = v.findViewById(R.id.estMilage);
            TextView estMaintFixed = v.findViewById(R.id.estMaintView);
            TextView estMaint = v.findViewById(R.id.estMaint);
            String[] stateColors = v.getResources().getStringArray(R.array.textColors);

            if (makeView != null) {
                makeView.setText(c.make);
            }
            if (modelView != null) {
                modelView.setText(c.model);
            }
            if (yearView != null) {
                yearView.setText(String.valueOf(c.year));
            }
            if (volumeView != null) {
                volumeView.setText(String.valueOf(c.volume)+"cc");
            }
            if (estMileage != null) {
                estMileage.setText(String.valueOf(c.estMileage) + " km");
            }
            if (estMaint != null) {
                estMaint.setText(String.valueOf(c.estDistanceToMaint) + " km");
                estMaint.setTextColor(Color.parseColor(stateColors[c.maintState]));
            }
            if(estMaintFixed != null){
                estMaintFixed.setTextColor(Color.parseColor(stateColors[c.maintState]));
            }
        }

        return v;
    }

}
