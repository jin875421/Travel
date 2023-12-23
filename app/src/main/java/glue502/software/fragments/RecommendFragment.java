package glue502.software.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;

import glue502.software.R;
import glue502.software.activities.travelRecord.TravelAlbumActivity;
import glue502.software.activities.travelRecord.travelRecordActivity;
import glue502.software.activities.travelRecord.TravelReviewActivity;
import glue502.software.utils.MyViewUtils;

public class RecommendFragment extends Fragment {
    private Button createBtn;
    private Button reviewBtn;
    private LinearLayout rltlCreate;
    private LinearLayout rltlFootprint;
    private LinearLayout rltlPhoto;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend,container,false);
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.top),true);
        rltlCreate = view.findViewById(R.id.lrlt_create);
        rltlFootprint = view.findViewById(R.id.lrlt_footprint);
        rltlPhoto=view.findViewById(R.id.lrlt_photo);
        setlistener();
        date();
        return view;
    }

    private void date() {



    }

    public void setlistener(){
        rltlCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), travelRecordActivity.class);
                startActivity(intent);
            }
        });
        rltlFootprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TravelReviewActivity.class);
                startActivity(intent);
            }
        });
        rltlPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TravelAlbumActivity.class);
                startActivity(intent);
            }
        });

    }

    //生命周期管理
    @Override
    public void onResume() {
        super.onResume();
        //沉浸式状态栏
        MyViewUtils.setImmersiveStatusBar(getActivity(),view.findViewById(R.id.top),true);
    }
}