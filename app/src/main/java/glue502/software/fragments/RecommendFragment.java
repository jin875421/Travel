package glue502.software.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import glue502.software.R;
import glue502.software.activities.travelRecord.travelRecordActivity;
import glue502.software.activities.travelRecord.travelReviewActivity;
import glue502.software.adapters.TravelReviewAdapter;
import glue502.software.utils.Carousel;

public class RecommendFragment extends Fragment {
    private Button createBtn;
    private Button reviewBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend,container,false);
        createBtn = view.findViewById(R.id.btn_Recording);
        reviewBtn = view.findViewById(R.id.bottomRightButton);
        setlistener();
        Carousel carousel = new Carousel(getContext(), view.findViewById(R.id.recommend_lbt_dot), view.findViewById(R.id.recommend_lbt_image));
        List<String> paths = new ArrayList<>();
        paths.add("images/cat1_1.jpg");
        paths.add("images/cat1_2.jpg");
        paths.add("images/cat1_3.jpg");
        paths.add("images/cat2_1.jpg");
        paths.add("images/cat2_2.jpg");
        carousel.initViewsLBT(paths);
        return view;
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
                Intent intent = new Intent(getActivity(), travelReviewActivity.class);
                startActivity(intent);
            }
        });
    }
}