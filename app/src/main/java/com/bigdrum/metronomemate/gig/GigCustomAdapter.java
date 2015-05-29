package com.bigdrum.metronomemate.gig;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigdrum.metronomemate.R;

/**
 * Created by andrew on 28/05/15.
 */
public class GigCustomAdapter extends ArrayAdapter<Gig> {

    private final Activity context;

    static class ViewHolder {
        protected TextView gigName;
        protected TextView gigDate;
    }


    /**
     *
     * @param context
     * @param resource
     * @param textViewResourceId
     */
    public GigCustomAdapter(Activity context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);

        this.context = context;
    }


    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = null;
        ViewHolder viewHolder;

        if (convertView == null) {

            view = context.getLayoutInflater().inflate(R.layout.gig_row_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.gigName = (TextView)view.findViewById(R.id.gig_name);
            viewHolder.gigDate = (TextView)view.findViewById(R.id.gig_date);

            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.gigName.setText(getItem(position).getName());
        viewHolder.gigDate.setText(getItem(position).getDate());

        return view;
    }
}
