package glue502.software.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import glue502.software.R;
import glue502.software.activities.CreateRecordActivity;

public class RecommendFragment extends Fragment {
    private Button createBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend,container,false);
        createBtn = view.findViewById(R.id.create_record);
        setlistener();
        return view;
    }
    public void setlistener(){
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateRecordActivity.class);
                startActivity(intent);
            }
        });
    }
}