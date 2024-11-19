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
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class PlaceOrder extends Fragment {
    private Spinner assspinner,vivaspinner; // Spinner for Assignment Type
    private EditText etExactDeadline; // EditText for Exact Deadline
    private Context context;
    Button btn_extra_small,btn_small,btn_large,btn_medium,btn_browse;
    TextView tv_example,tv_possible_deliverables;
    private static final int REQUEST_CODE_PICK_FILE = 1;
    private TextView fileNameTextView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public PlaceOrder() {
        // Required empty public constructor
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

        // Set up the ArrayAdapter for Spinner Assignmnet type
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.assignment_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> vivaadapter = ArrayAdapter.createFromResource(context,
                R.array.viva, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach the adapter to the Spinner
        assspinner.setAdapter(adapter);

        vivaspinner.setAdapter(vivaadapter);

        // Initialize Exact Deadline EditText
        etExactDeadline = view.findViewById(R.id.et_exact_deadline);
        btn_extra_small = view.findViewById(R.id.btn_extra_small);
        btn_small = view.findViewById(R.id.btn_small);
        btn_medium = view.findViewById(R.id.btn_medium);
        btn_large = view.findViewById(R.id.btn_large);
        btn_browse = view.findViewById(R.id.btn_browse);
        tv_example = view.findViewById(R.id.tv_example);
        tv_possible_deliverables = view.findViewById(R.id.tv_possible_deliverables);
        fileNameTextView = view.findViewById(R.id.tv_file_name);

        btn_extra_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_example.setText("Up to 3 short practice problems or theoretical questions");
                tv_possible_deliverables.setText("• Simple computations\n• Short answers to questions");
            }
        });
        btn_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_example.setText("Small assignment details");
                tv_possible_deliverables.setText("• Detailed report\n• Extended calculations");
            }
        });
        btn_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_example.setText("Medium-sized project description");
                tv_possible_deliverables.setText("• Project analysis\n• Complex diagrams");
            }
        });
        btn_large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_example.setText( "Large project scope");
                tv_possible_deliverables.setText("• Comprehensive report\n• Advanced analytics");
            }
        });

        btn_browse.setOnClickListener(v -> openFilePicker());

        // Set OnClickListener to show Date and Time Picker
        etExactDeadline.setOnClickListener(v -> showDateTimePicker());

        return view;
    }

    private void showDateTimePicker() {
        // Get Current Date and Time
        Calendar calendar = Calendar.getInstance();

        // DatePickerDialog to select the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    // Once the date is selected, show TimePickerDialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                            (timeView, hourOfDay, minute) -> {
                                // Format and set the selected date and time in the EditText
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
