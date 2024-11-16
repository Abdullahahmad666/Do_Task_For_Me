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

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Set the default fragment to HomeFragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId(); // Get the clicked item's ID

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
                Intent i = new Intent(MainActivity.this, Login.class);
                startActivity(i);
                finish();
            } else if (id == R.id.nav_assignment) {
                PlaceOrder placeOrderFragment = new PlaceOrder();

                // Use FragmentManager to replace the current fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, placeOrderFragment) // fragment_container is your container ID
                        .addToBackStack(null) // Optional: Allows the user to press back and return to the previous fragment
                        .commit();
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

    // Method to load fragments
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }



    }
}
