package com.example.tdtuapp.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tdtuapp.R;
import com.example.tdtuapp.TrainEvalActivity;

public class TrainingEvaluationFragment extends Fragment implements View.OnClickListener{
    private CardView academic, activities, volunteer;
    private Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_evaluation, container, false);
        academic = view.findViewById(R.id.academic);
        activities = view.findViewById(R.id.activities);
        volunteer = view.findViewById(R.id.volunteer);

        academic.setOnClickListener(this);
        activities.setOnClickListener(this);
        volunteer.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        String field = "Học thuật";
        switch (v.getId()){
            case R.id.academic:
                field = "Học thuật";
                intent = new Intent(getContext(), TrainEvalActivity.class);
                intent.putExtra("field", field);
                getContext().startActivity(intent);
                break;
            case R.id.activities:
                field = "Hoạt động";
                intent = new Intent(getContext(), TrainEvalActivity.class);
                intent.putExtra("field", field);
                getContext().startActivity(intent);
                break;
            case R.id.volunteer:
                field = "Tình nguyện";
                intent = new Intent(getContext(), TrainEvalActivity.class);
                intent.putExtra("field", field);
                getContext().startActivity(intent);
                break;
        }

    }
}