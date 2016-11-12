package ar.edu.utn.frba.coeliacs.coeliacapp.models.components;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frba.coeliacs.coeliacapp.R;
import ar.edu.utn.frba.coeliacs.coeliacapp.communication.BusProvider;
import ar.edu.utn.frba.coeliacs.coeliacapp.communication.ItemSelectedEvent;
import ar.edu.utn.frba.coeliacs.coeliacapp.utils.TextUtils;

/**
 * Created by mberetta on 27/10/16.
 */
public class FilterIconArrayAdapter<T extends IconArrayAdapterModel> extends ArrayAdapter<T> {
    private static final String TAG = FilterIconArrayAdapter.class.getSimpleName();
    final private Bus bus = BusProvider.getInstance();

    private Context context;
    private List<T> objects;
    private List<T> filteredObjects;

    public FilterIconArrayAdapter(Context context, List<T> objects) {
        super(context, -1, objects);
        this.context = context;
        this.objects = objects;
        this.filteredObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.icon_array_adapter, parent, false);
        TextView titleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
        TextView subtitleTextView = (TextView) rowView.findViewById(R.id.subtitleTextView);
        ImageView iconImageView = (ImageView) rowView.findViewById(R.id.iconImageView);
        Log.d(TAG, "getView - size:" + objects.size() + " position:" + String.valueOf(position));
        final T object = filteredObjects.get(position);
        titleTextView.setText(object.getTitle());
        subtitleTextView.setText(object.getSubtitle());
        iconImageView.setImageResource(object.getIconResId());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.post(new ItemSelectedEvent(object));
            }
        });
        return rowView;
    }

    @Override
    public int getCount() {
        return filteredObjects.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                String cleanConstraint = TextUtils.getRideOfAccents(constraint.toString().trim().toLowerCase());
                ArrayList<T> tempList = new ArrayList<T>();

                if(constraint != null && objects != null) {

                    for(int i = 0; i < objects.size(); i ++) {
                        T item = objects.get(i);

                        String cleanTitle = TextUtils.getRideOfAccents(item.getTitle().toLowerCase());
                        Log.d(TAG, "Comparing: " + cleanConstraint + " and " + cleanTitle);
                        if (cleanTitle.contains(cleanConstraint)) {
                            tempList.add(item);
                        }
                    }

                    //following two lines are for publishResults method
                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }
                return filterResults;
            }


            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                FilterIconArrayAdapter.this.filteredObjects = (ArrayList<T>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

}
