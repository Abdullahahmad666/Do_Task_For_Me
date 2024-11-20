package com.example.dotaskforme;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class PlaceOrder extends Fragment {
    private Spinner assspinner, vivaspinner; // Spinner for Assignment Type
    private EditText etExactDeadline; // EditText for Exact Deadline
    private Context context;
    Button btn_extra_small, btn_small, btn_large, btn_medium, btn_browse;
    TextView tv_example, tv_possible_deliverables;
    private static final int REQUEST_CODE_PICK_FILE = 1;
    private TextView fileNameTextView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView ivmenu;
    FragmentManager manager;

    public PlaceOrder() {
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
        View view = inflater.inflate(R.layout.fragment_place_order, container, false);

        // Initialize Spinner
        assspinner = view.findViewById(R.id.spinner_assignment_type);
        vivaspinner = view.findViewById(R.id.spinner_viva_preparation);


        // Initialize DrawerLayout and NavigationView
        drawerLayout = view.findViewById(R.id.draw_header);
        navigationView = view.findViewById(R.id.order_nav);
        ivmenu = view.findViewById(R.id.iv_icon);

        ivmenu.setOnClickListener(v -> {
            // Open the drawer when the icon is clicked
            openDrawer();
        });

        // Set up the navigation item click listener
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                HomeFragment HomeFragment = new HomeFragment();

                // Use FragmentManager to replace the current fragment
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                       manager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment) // fragment_container is your container ID
                        .addToBackStack(null) // Optional: Allows the user to press back and return to the previous fragment
                        .commit();
            } else if (id == R.id.nav_how_we_work) {
                AboutUsFragment AboutusFragment = new AboutUsFragment();

                // Use FragmentManager to replace the current fragment
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, AboutusFragment) // fragment_container is your container ID
                        .addToBackStack(null) // Optional: Allows the user to press back and return to the previous fragment
                        .commit();
            } else if (id == R.id.nav_services) {
                ServicesFragment ServicesFragment = new ServicesFragment();

                // Use FragmentManager to replace the current fragment
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, ServicesFragment) // fragment_container is your container ID
                        .addToBackStack(null) // Optional: Allows the user to press back and return to the previous fragment
                        .commit();
            } else if (id == R.id.nav_contact_us) {
                ContactUsFragment ContactUsFragment = new ContactUsFragment();

                // Use FragmentManager to replace the current fragment
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, ContactUsFragment) // fragment_container is your container ID
                        .addToBackStack(null) // Optional: Allows the user to press back and return to the previous fragment
                        .commit();
            } else if (id == R.id.nav_manage_orders) {
                ManageOrderFragment ManageOrderFragment = new ManageOrderFragment();

                // Use FragmentManager to replace the current fragment
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, ManageOrderFragment) // fragment_container is your container ID
                        .addToBackStack(null) // Optional: Allows the user to press back and return to the previous fragment
                        .commit();
            } else if (id == R.id.nav_order_now) {
                Toast.makeText(getActivity(), "Order Now Selected", Toast.LENGTH_SHORT).show();
                // Handle Order Now navigation
            }

            // Close the drawer after item selection
            closeDrawer();
            return true;
        });
        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.close_button).setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        // Set up ArrayAdapter for Spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.assignment_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assspinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> vivaadapter = ArrayAdapter.createFromResource(context,
                R.array.viva, android.R.layout.simple_spinner_item);
        vivaadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vivaspinner.setAdapter(vivaadapter);

        // Initialize other UI components
        etExactDeadline = view.findViewById(R.id.et_exact_deadline);
        btn_extra_small = view.findViewById(R.id.btn_extra_small);
        btn_small = view.findViewById(R.id.btn_small);
        btn_medium = view.findViewById(R.id.btn_medium);
        btn_large = view.findViewById(R.id.btn_large);
        btn_browse = view.findViewById(R.id.btn_browse);
        tv_example = view.findViewById(R.id.tv_example);
        tv_possible_deliverables = view.findViewById(R.id.tv_possible_deliverables);
        fileNameTextView = view.findViewById(R.id.tv_file_name);

        btn_extra_small.setOnClickListener(v -> {
            tv_example.setText("Up to 3 short practice problems or theoretical questions");
            tv_possible_deliverables.setText("• Simple computations\n• Short answers to questions");
        });

        btn_small.setOnClickListener(v -> {
            tv_example.setText("Small assignment details");
            tv_possible_deliverables.setText("• Detailed report\n• Extended calculations");
        });

        btn_medium.setOnClickListener(v -> {
            tv_example.setText("Medium-sized project description");
            tv_possible_deliverables.setText("• Project analysis\n• Complex diagrams");
        });

        btn_large.setOnClickListener(v -> {
            tv_example.setText("Large project scope");
            tv_possible_deliverables.setText("• Comprehensive report\n• Advanced analytics");
        });

        btn_browse.setOnClickListener(v -> openFilePicker());

        // Set OnClickListener to show Date and Time Picker
        etExactDeadline.setOnClickListener(v -> showDateTimePicker());

        return view;
    }

    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }



    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        // DatePickerDialog to select the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    // Once the date is selected, show TimePickerDialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                            (timeView, hourOfDay, minute) -> {
                                String formattedDateTime = String.format("%02d/%02d/%d %02d:%02d",
                                        dayOfMonth, month + 1, year, hourOfDay, minute);
                                etExactDeadline.setText(formattedDateTime);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allows both images and PDFs
        String[] mimeTypes = {"application/pdf", "image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    String fileName = getFileName(fileUri);
                    fileNameTextView.setText(fileName != null ? fileName : "Unknown file");
                    Toast.makeText(getContext(), "File Selected: " + fileName, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            int lastSeparator = path.lastIndexOf('/');
            return lastSeparator != -1 ? path.substring(lastSeparator + 1) : path;
        }
        return null;
    }
}
