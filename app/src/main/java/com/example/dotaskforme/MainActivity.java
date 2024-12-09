package com.example.dotaskforme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;



public class MainActivity extends AppCompatActivity implements HomeFragment.DrawerListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        String targetFragment = getIntent().getStringExtra("targetFragment");

        // Load the appropriate fragment
        if (savedInstanceState == null) {
            if ("ManageOrderFragment".equals(targetFragment)) {
                loadFragment(new ManageOrderFragment());
                navigationView.setCheckedItem(R.id.nav_manage_orders);
            } else {
                loadFragment(new HomeFragment());
                navigationView.setCheckedItem(R.id.nav_home);
            }
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_about_us) {
                selectedFragment = new AboutUsFragment();
            } else if (id == R.id.nav_services) {
                selectedFragment = new ServicesFragment();
            } else if (id == R.id.nav_projects) {
                selectedFragment = new ProjectsFragment();
            } else if (id == R.id.nav_contact_us) {
                selectedFragment = new ContactUsFragment();
            } else if (id == R.id.nav_login) {
                loadFragment(new ManageOrderFragment());
            } else if (id == R.id.nav_assignment) {
                loadFragment(new PlaceOrder());
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Handle close button in the header
        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.close_button).setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void synchronizeDrawer(String targetFragment) {
        NavigationView navigationView = findViewById(R.id.navigation_view);

        if (navigationView == null) {
            throw new IllegalStateException("NavigationView is null. Check your layout.");
        }

        if ("ManageOrderFragment".equals(targetFragment)) {
            navigationView.setCheckedItem(R.id.nav_manage_orders);
        } else if ("HomeFragment".equals(targetFragment)) {
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

    }
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        // Check if the current fragment is not the HomeFragment
        if (currentFragment != null && !(currentFragment instanceof HomeFragment)) {
            // Navigate to the HomeFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            // Default back press behavior
            super.onBackPressed();
        }
    }
}

