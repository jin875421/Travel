package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import glue502.software.R;
import glue502.software.models.travelRecord;

public class TravelDetailAdapter extends BaseAdapter {
    private Context context;
    private List<travelRecord> travelRecords;
    private String url = "http://"+ip+"/travel/";
    private int layoutId;
    public TravelDetailAdapter(Context context, List<travelRecord> travelRecords, int layoutId) {
        this.context = context;
        this.travelRecords = travelRecords;
        this.layoutId = layoutId;
    }
    @Override
    public int getCount() {
        return travelRecords.size();
    }

    @Override
    public Object getItem(int i) {
        return travelRecords.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(layoutId,null);
        TextView travelName = v.findViewById(R.id.travel_place);
        LinearLayout imageContainer = v.findViewById(R.id.image_container);
        for(String path:travelRecords.get(i).getImage()){
            if(path!=null){
                ImageView imageView = new ImageView(context);
                Glide.with(context).load(url+path).into(imageView);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);
                imageView.setLayoutParams(params);
                imageContainer.addView(imageView);
            }
        }
        return v;
    }
}
