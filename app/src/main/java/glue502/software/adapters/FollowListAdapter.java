package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import glue502.software.models.Follow;

public class FollowListAdapter extends BaseAdapter {

    Context context;
    int adapter_fellow_item;
    List<Follow> followList;

    private String url = "http://"+ip+"/travel/";

    public FollowListAdapter(Context context, int adapter_fellow_item, List<Follow> followList) {
        this.context = context;
        this.followList = followList;
        this.adapter_fellow_item = adapter_fellow_item;
    }

    @Override
    public int getCount() {
        return followList.size();
    }

    @Override
    public Object getItem(int position) {
        return followList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, adapter_fellow_item, null);
        }

        return convertView;
    }
}
