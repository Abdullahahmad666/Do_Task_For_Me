package com.example.dotaskforme;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ProjectsFragment extends Fragment {
    Context context;
    ImageView image1,image2,image3,image4,image5,image6,image7;


    public ProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        image1 = view.findViewById(R.id.image1);
        image2 = view.findViewById(R.id.image2);
        image3 = view.findViewById(R.id.image3);
        image4 = view.findViewById(R.id.image4);
        image5 = view.findViewById(R.id.image5);
        image6 = view.findViewById(R.id.image6);
        image7 = view.findViewById(R.id.image7);

        Animation textAnimation = AnimationUtils.loadAnimation(context, R.anim.text_animation);
        Animation logoAnimation = AnimationUtils.loadAnimation(context, R.anim.logo_animation);

        image1.startAnimation(textAnimation);
        image2.startAnimation(textAnimation);
        image3.startAnimation(textAnimation);
        image4.startAnimation(logoAnimation);
        image5.startAnimation(textAnimation);
        image6.startAnimation(textAnimation);
        image7.startAnimation(logoAnimation);

    }
}