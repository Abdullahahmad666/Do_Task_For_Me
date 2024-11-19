package com.example.dotaskforme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class PlaceOrder extends Fragment {
    private Spinner assspinner,vivaspinner; // Spinner for Assignment Type
    private EditText etExactDeadline; // EditText for Exact Deadline
    private Context context;
    Button btn_extra_small,btn_small,btn_large,btn_medium;
    TextView tv_example,tv_possible_deliverables;

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
        tv_example = view.findViewById(R.id.tv_example);
        tv_possible_deliverables = view.findViewById(R.id.tv_possible_deliverables);

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
}
