package glue502.software.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.models.Follow;

public class FollowGroupListAdapter extends ArrayAdapter<String> {
    private Gson gson = new Gson();
    private Context context;
    private int layoutResource;
    private List<String> groupList;
    private List<Follow> followList;
    private  String followId;

    public FollowGroupListAdapter(Context context, int layoutResource, List<String> groupList,List<Follow> followList,String followId) {
        super(context, layoutResource, groupList);
        this.context = context;
        this.layoutResource = layoutResource;
        this.groupList = groupList;
        this.followList = followList;
        this.followId = followId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.groupName = convertView.findViewById(R.id.follow_group_item_name);
            viewHolder.itemCheckBox = convertView.findViewById(R.id.follow_group_item_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String groupName = groupList.get(position);
        viewHolder.groupName.setText(groupName);
        // 已存在于某个分组 则勾选
        if(groupName.equals("全部")){
            viewHolder.itemCheckBox.setChecked(true);
            viewHolder.itemCheckBox.setEnabled(false);
        }
        List<String> groupOfList = new ArrayList<>();
        for(Follow follow:followList){
            if(follow.getFollowId().equals(followId)){
                groupOfList = gson.fromJson(follow.getGroupOf(),List.class);
            }
        }
        if (groupOfList != null && groupOfList.contains(groupName)){
            viewHolder.itemCheckBox.setChecked(true);
        }

        viewHolder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO 收集勾选的分组 ?: 通过handler将收集到的分组信息组成List<String> 回传给activity activity根据回传的分组信息修改FollowList 并且传给服务器 更新数据
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
//                    Toast.makeText(context, "选中了" + groupName, Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(context, "取消了" + groupName, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView groupName;
        CheckBox itemCheckBox;
    }
}
