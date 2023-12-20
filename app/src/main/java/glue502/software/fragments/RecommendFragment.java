package glue502.software.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import glue502.software.R;
import glue502.software.activities.travelRecord.TravelAlbumActivity;
import glue502.software.activities.travelRecord.travelRecordActivity;
import glue502.software.activities.travelRecord.TravelReviewActivity;
public class RecommendFragment extends Fragment {
    private Button createBtn;
    private Button reviewBtn;
    private Button topRightButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend,container,false);
        createBtn = view.findViewById(R.id.btn_Recording);
        reviewBtn = view.findViewById(R.id.bottomRightButton);
        topRightButton = view.findViewById(R.id.topRightButton);

//        // 获取 CalendarView 对象
//        CalendarView calendarView = view.findViewById(R.id.calendar_view);

        // 设置最小日期和最大日期范围
//        calendarView.setMinDate(minDateMillis); // minDateMillis为long类型的时间值表示最小日期
//        calendarView.setMaxDate(maxDateMillis); // maxDateMillis为long类型的时间值表示最大日期

        setlistener();
        date();
        return view;
    }

    private void date() {



    }

    public void setlistener(){
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), travelRecordActivity.class);
                startActivity(intent);
            }
        });
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TravelReviewActivity.class);
                startActivity(intent);
            }
        });
        topRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TravelAlbumActivity.class);
                startActivity(intent);
            }
        });

    }
}