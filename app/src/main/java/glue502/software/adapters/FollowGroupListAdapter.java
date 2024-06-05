package glue502.software.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
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
    private Handler adapterHandler;
    // 选中分组
    private List<String> selectGroupList;

    public List<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }

    public List<String> getSelectGroupList() {
        return selectGroupList;
    }

    public FollowGroupListAdapter(Context context, int layoutResource, List<String> groupList, List<Follow> followList, String followId, Handler adapterHandler) {
        super(context, layoutResource, groupList);
        this.context = context;
        this.layoutResource = layoutResource;
        this.groupList = groupList;
        this.followList = followList;
        this.followId = followId;
        this.adapterHandler = adapterHandler;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        selectGroupList = new ArrayList<>();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemRootLayout = convertView.findViewById(R.id.follow_group_item_root);
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
        } else {
            viewHolder.itemCheckBox.setEnabled(true);
            viewHolder.itemCheckBox.setChecked(false);
        }
        selectGroupList = groupOfList;
        viewHolder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
//                    Toast.makeText(context, "选中了" + groupName, Toast.LENGTH_SHORT).show();
                    selectGroupList.add(groupName);
                } else {
//                    Toast.makeText(context, "取消了" + groupName, Toast.LENGTH_SHORT).show();
                    selectGroupList.remove(groupName);
                }
            }
        });

        // 长按item删除
        viewHolder.itemRootLayout.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                // 回传用户信息并在activity中弹出抽屉
                Message message = adapterHandler.obtainMessage();
                message.what = 1;
                message.arg1 = position;
                message.obj = groupName;
                adapterHandler.sendMessage(message);
                return true;
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        RelativeLayout itemRootLayout;
        TextView groupName;
        CheckBox itemCheckBox;
    }
}
