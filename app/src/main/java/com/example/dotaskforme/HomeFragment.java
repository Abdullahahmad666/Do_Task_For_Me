package com.example.dotaskforme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class HomeFragment extends Fragment {
    ImageView iv_icon,imageinside0;
    Context context;
    Button completeassignmnent,getquickassistance,placeorderbutton;
    FragmentManager manager;
    private DrawerListener drawerListener;
    FloatingActionButton fabWhatsapp;
    TextView tvbanner,bannertext1,bannertext2,bannertext3,bannertext4,bannertext5;

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
        placeorderbutton = view.findViewById(R.id.placeOrderButton);
        iv_icon = view.findViewById(R.id.iv_icon);
        iv_icon.setOnClickListener(v -> {
            if (drawerListener != null) {
                drawerListener.openDrawer();  // Call to open the drawer
            }
        });


        tvbanner = view.findViewById(R.id.bannerText);
        bannertext1 = view.findViewById(R.id.bannerText1);
        bannertext2 = view.findViewById(R.id.bannerText2);
        bannertext3 = view.findViewById(R.id.bannerText3);
        bannertext4 = view.findViewById(R.id.bannerText4);
        bannertext5 = view.findViewById(R.id.bannerText5);
        imageinside0 = view.findViewById(R.id.imageInsideO);
        Animation textAnimation = AnimationUtils.loadAnimation(context, R.anim.text_animation);
        imageinside0.startAnimation(textAnimation);
        tvbanner.startAnimation(textAnimation);
        bannertext1.startAnimation(textAnimation);
        bannertext2.startAnimation(textAnimation);
        bannertext3.startAnimation(textAnimation);
        bannertext4.startAnimation(textAnimation);
        bannertext5.startAnimation(textAnimation);


        completeassignmnent = view.findViewById(R.id.btnCompleteAssignment);
        getquickassistance = view.findViewById(R.id.btnQuickAssistance);
        completeassignmnent.setOnClickListener(view1 -> {

            PlaceOrder placeOrderFragment = new PlaceOrder();

            // Use FragmentManager to replace HomeFragment with PlaceOrderFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, placeOrderFragment) // R.id.fragment_container is the container where your fragments are hosted
                    .addToBackStack(null) // Adds the transaction to the back stack so you can navigate back
                    .commit();// Close the current Activity hosting the Fragment, if needed

        });
        getquickassistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactUsFragment contactus = new ContactUsFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, contactus) // R.id.fragment_container is the container where your fragments are hosted
                        .addToBackStack(null) // Adds the transaction to the back stack so you can navigate back
                        .commit();
            }
        });
        placeorderbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceOrder placeorder = new PlaceOrder();
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, placeorder) // fragment_container is your container ID
                        .addToBackStack(null) // Optional: Allows the user to press back and return to the previous fragment
                        .commit();
            }
        });
        fabWhatsapp = view.findViewById(R.id.fabWhatsapp);
        fabWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "923324331776";
                String url = "https://wa.me/" + phoneNumber;

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                // Verify WhatsApp is installed
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}