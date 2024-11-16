package com.example.dotaskforme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class HomeFragment extends Fragment {
    ImageView iv_icon;
    Context context;
    Button completeassignmnent;
    FragmentManager manager;
    private DrawerListener drawerListener;

    public interface DrawerListener {
        void openDrawer();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof DrawerListener) {
            drawerListener = (DrawerListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DrawerListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iv_icon = view.findViewById(R.id.iv_icon);
        iv_icon.setOnClickListener(v -> {
            if (drawerListener != null) {
                drawerListener.openDrawer();  // Call to open the drawer
            }
        });

        TextView tvbanner;
        tvbanner = view.findViewById(R.id.bannerText);
        Animation textAnimation = AnimationUtils.loadAnimation(context, R.anim.text_animation);
        tvbanner.startAnimation(textAnimation);

        completeassignmnent = view.findViewById(R.id.btnCompleteAssignment);
        completeassignmnent.setOnClickListener(view1 -> {

            PlaceOrder placeOrderFragment = new PlaceOrder();

            // Use FragmentManager to replace HomeFragment with PlaceOrderFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, placeOrderFragment) // R.id.fragment_container is the container where your fragments are hosted
                    .addToBackStack(null) // Adds the transaction to the back stack so you can navigate back
                    .commit();// Close the current Activity hosting the Fragment, if needed

        });
    }

}