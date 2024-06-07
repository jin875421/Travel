package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import glue502.software.R;
import glue502.software.models.UserDailyTask;

public class UserDailyTaskAdapter extends RecyclerView.Adapter<UserDailyTaskAdapter.UserDailyTaskViewHolder> {
    private String url = "http://" + ip + "/travel";

    private List<UserDailyTask> userDailyTaskList;
    private Context context;

    public UserDailyTaskAdapter(Context context, List<UserDailyTask> userDailyTaskList) {
        this.context = context;
        this.userDailyTaskList = userDailyTaskList;
    }

    @NonNull
    @Override
    public UserDailyTaskAdapter.UserDailyTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_daily_task, null);
        return new UserDailyTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDailyTaskAdapter.UserDailyTaskViewHolder holder, int position) {
        UserDailyTask userDailyTask = userDailyTaskList.get(position);
        Glide.with(context)
                .load(url +"/"+ userDailyTask.getTaskIcon())
                .into(holder.taskImage);
        holder.taskName.setText(userDailyTask.getTaskName());
        holder.taskReward.setText(userDailyTask.getReward() + "");
        holder.taskProgress.setText(userDailyTask.getProgress() + "");
        holder.taskMaxProgress.setText("/"+userDailyTask.getMaxProgress() + "");
        holder.taskProgressBar.setProgress(userDailyTask.getProgress());
        holder.taskProgressBar.setMax(userDailyTask.getMaxProgress());
    }

    @Override
    public int getItemCount() {
        return userDailyTaskList.size();
    }

    public static class UserDailyTaskViewHolder extends RecyclerView.ViewHolder {
        ImageView taskImage;
        TextView taskName, taskReward;
        TextView taskProgress, taskMaxProgress;
        ProgressBar taskProgressBar;

        public UserDailyTaskViewHolder(View itemView) {
            super(itemView);
            taskImage = itemView.findViewById(R.id.task_image);
            taskName = itemView.findViewById(R.id.task_name);
            taskReward = itemView.findViewById(R.id.task_reward);
            taskProgress = itemView.findViewById(R.id.tv_progress);
            taskMaxProgress = itemView.findViewById(R.id.tv_max_progress);
            taskProgressBar = itemView.findViewById(R.id.bar_progress);
        }
    }
}
