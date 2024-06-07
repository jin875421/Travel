package glue502.software.adapters;

import static glue502.software.activities.MainActivity.ip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import glue502.software.R;
import glue502.software.models.Achievement;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {
    private String url = "http://" + ip + "/travel";

    private List<Achievement> achievementList;
    private Context context;

    public AchievementAdapter(Context context, List<Achievement> achievementList) {
        this.context = context;
        this.achievementList = achievementList;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievementList.get(position);
        Glide.with(context)
                .load(url+"/"+achievement.getImageUrl())
                .into(holder.achievementImage);
        holder.achievementName.setText(achievement.getName());
        holder.achievementDescription.setText(achievement.getDescription());
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        ImageView achievementImage;
        TextView achievementName;
        TextView achievementDescription;

        AchievementViewHolder(View itemView) {
            super(itemView);
            achievementImage = itemView.findViewById(R.id.achievement_image);
            achievementName = itemView.findViewById(R.id.achievement_name);
            achievementDescription = itemView.findViewById(R.id.achievement_description);
        }
    }
}

