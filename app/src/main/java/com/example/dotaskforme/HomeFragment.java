package com.example.dotaskforme;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class HomeFragment extends Fragment {
    ImageView iv_icon;
    private DrawerListener drawerListener;

    public interface DrawerListener {
        void openDrawer();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
    }
}