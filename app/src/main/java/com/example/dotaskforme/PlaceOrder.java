package com.example.dotaskforme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class PlaceOrder extends Fragment {
    private Spinner assspinner, vivaspinner; // Spinner for Assignment Type
    private EditText etExactDeadline, etWhatsapp, etGoogleDriveLink; // EditText for Exact Deadline
    private Context context;
    Button btn_extra_small, btn_small, btn_large, btn_medium, btn_browse;
    TextView tv_example, tv_possible_deliverables;
    private static final int REQUEST_CODE_PICK_FILE = 1;
    private TextView fileNameTextView,pricevalue;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView ivmenu;
    FragmentManager manager;
    private Button btnSubmit;
    private Button selectedButton = null;

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

    @SuppressLint("MissingInflatedId")
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
        pricevalue = view.findViewById(R.id.pricevalue);

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
        etWhatsapp = view.findViewById(R.id.et_whatsapp); // Initialize WhatsApp EditText
        etGoogleDriveLink = view.findViewById(R.id.et_google_drive_link);

        // Button click listeners with inline tracking
        btn_extra_small.setOnClickListener(v -> {
            if (selectedButton != null) {
                selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.blue)); // Reset previous button color
            }
            selectedButton = btn_extra_small; // Update selected button
            selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.lightblue)); // Highlight selected button
            tv_example.setText("Up to 3 short practice problems or theoretical questions");
            tv_possible_deliverables.setText("• Simple computations\n• Short answers to questions");
            pricevalue.setText("$10");
        });

        btn_small.setOnClickListener(v -> {
            if (selectedButton != null) {
                selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.blue));
            }
            selectedButton = btn_small;
            selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.lightblue));
            tv_example.setText("Small assignment details");
            tv_possible_deliverables.setText("• Detailed report\n• Extended calculations");
            pricevalue.setText("$20");
        });

        btn_medium.setOnClickListener(v -> {
            if (selectedButton != null) {
                selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.blue));
            }
            selectedButton = btn_medium;
            selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.lightblue));
            tv_example.setText("Medium-sized project description");
            tv_possible_deliverables.setText("• Project analysis\n• Complex diagrams");
            pricevalue.setText("$50");
        });

        btn_large.setOnClickListener(v -> {
            if (selectedButton != null) {
                selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.blue));
            }
            selectedButton = btn_large;
            selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.lightblue));
            tv_example.setText("Large project scope");
            tv_possible_deliverables.setText("• Comprehensive report\n• Advanced analytics");
            pricevalue.setText("$100");
        });


        btn_browse.setOnClickListener(v -> openFilePicker());

        // Set OnClickListener to show Date and Time Picker
        etExactDeadline.setOnClickListener(v -> showDateTimePicker());

        btnSubmit = view.findViewById(R.id.submitButton);

        btnSubmit.setOnClickListener(v -> {
            String assignmentType = null;
            String exactDeadline = null;
            String price = null; // Variable to hold the price
            String vivaPreparation = null;
            String whatsappNumber; // Variable to hold WhatsApp number
            String googleDriveLink; // Variable to hold Google Drive link

            // Retrieve input from EditTexts
            whatsappNumber = etWhatsapp.getText().toString().trim();
            googleDriveLink = etGoogleDriveLink.getText().toString().trim();

            // Check if the required fields are filled
            if (assspinner.getSelectedItem() == null || vivaspinner.getSelectedItem() == null ||
                    etExactDeadline.getText().toString().isEmpty() || selectedButton == null ||
                    whatsappNumber.isEmpty() || googleDriveLink.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            } else if (!isValidGoogleDriveLink(googleDriveLink)) { // Check if the Google Drive link is valid
                Toast.makeText(getContext(), "Please enter a valid Google Drive link", Toast.LENGTH_SHORT).show();
            } else {
                // Get data from input fields
                assignmentType = assspinner.getSelectedItem().toString();
                vivaPreparation = vivaspinner.getSelectedItem().toString();
                exactDeadline = etExactDeadline.getText().toString();

                // Determine the price based on the selected button
                if (selectedButton == btn_extra_small) {
                    price = "$10";
                    pricevalue.setText("$10");// Set price for extra small
                } else if (selectedButton == btn_small) {
                    price = "$20";
                    pricevalue.setText("$20");// Set price for small
                } else if (selectedButton == btn_medium) {
                    price = "$50";  // Set price for medium
                    pricevalue.setText("$50");
                } else if (selectedButton == btn_large) {
                    price = "$100";  // Set price for large
                    pricevalue.setText("$100");
                }

                // Create Order data object with selected price and dynamic phone/link
                Order order = new Order(
                        assignmentType,        // assignment type
                        exactDeadline,         // deadline
                        "Pending",             // status
                        whatsappNumber,        // phone number from UI
                        googleDriveLink,       // link from UI
                        price,
                        vivaPreparation
                );

                // Send Order Data to Firestore
                sendOrderToFirestore(order);
            }
        });

    return view;
    }

        private boolean isValidGoogleDriveLink (String googleDriveLink){
            String driveRegex = "^(https:\\/\\/)?(drive\\.google\\.com\\/)(file\\/d\\/|open\\?id=|drive\\/folders\\/|drive\\/u\\/\\d\\/folders\\/|drive\\/u\\/\\d\\/file\\/d\\/).*";
            return !TextUtils.isEmpty(googleDriveLink) && googleDriveLink.matches(driveRegex);
        }



    // Modified sendOrderToFirestore method
    public void sendOrderToFirestore(Order order) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(context, "You must be logged in to place an order.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        order.setUserId(userId); // Attach userId to the order

        // Get a reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the "orders" collection
        CollectionReference ordersCollection = db.collection("orders");

        // Create a new document with the order data
        ordersCollection.add(order)
                .addOnSuccessListener(documentReference -> {
                    // Attach the Firestore document ID to the order object
                    order.setDocumentId(documentReference.getId());  // Set the generated document ID

                    // Success message
                    Toast.makeText(context, "Order submitted successfully!", Toast.LENGTH_SHORT).show();

                    // You can now use the order object with the documentId
                    // e.g., update status, fetch order, etc.
                })
                .addOnFailureListener(e -> {
                    // Log error for debugging
                    Log.e("FirestoreError", "Failed to submit order: " + e.getMessage());
                    Toast.makeText(context, "Failed to submit order", Toast.LENGTH_SHORT).show();
                });
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
