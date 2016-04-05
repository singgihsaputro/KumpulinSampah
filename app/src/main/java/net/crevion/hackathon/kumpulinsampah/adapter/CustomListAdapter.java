package net.crevion.hackathon.kumpulinsampah.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import net.crevion.hackathon.kumpulinsampah.R;
import net.crevion.hackathon.kumpulinsampah.main.AppController;
import net.crevion.hackathon.kumpulinsampah.model.SampahModel;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    ArrayList<SampahModel> sampahItems;
    private ArrayList<SampahModel> filterList;
    CustomFilter filter;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, ArrayList<SampahModel> sampahItems) {
        this.activity = activity;
        this.sampahItems = sampahItems;
        this.filterList = sampahItems;
    }

    @Override
    public int getCount() {
        return sampahItems.size();
    }

    @Override
    public Object getItem(int location) {
        return sampahItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_object, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView name = (TextView) convertView.findViewById(R.id.textView);
        TextView worth = (TextView) convertView.findViewById(R.id.textView2);
        TextView year = (TextView) convertView.findViewById(R.id.textView4);

        // getting billionaires data for the row
        SampahModel m = sampahItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // name
        name.setText(m.getSampah());

//        source.setText("Wealth Source: " + String.valueOf(m.getIdSampah()));


        worth.setText(String.valueOf(m.getNama()));

        // release year
        year.setText(String.valueOf(m.getTanggal()));

        return convertView;
    }
    public Filter getFilter(){
        if(filter==null){
            filter = new CustomFilter();

        }
        return filter;
    }

    class CustomFilter extends Filter{
        protected FilterResults performFiltering(CharSequence constraint){
            FilterResults results = new FilterResults();
            if(constraint!=null&&constraint.length()>0){
                constraint=constraint.toString().toUpperCase();
                ArrayList<SampahModel> filters=new ArrayList<SampahModel>();
                for(int i = 0;i< filterList.size();i++){
                    if(filterList.get(i).getSampah().toUpperCase().contains(constraint)){
                        SampahModel w = new SampahModel();
                        w = filterList.get(i);
                        w.getThumbnailUrl();
                        w.getNama();
                        w.getTanggal();
                        w.getIdSampah();
                        filters.add(w);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            }else{
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }
        protected void publishResults(CharSequence constraint, FilterResults results){

            sampahItems =(ArrayList<SampahModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
