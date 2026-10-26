package com.example.frensia_avey_012202500012_is1_midexam;
import android.os.Bundle;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etStudentId;
    private CheckBox cbEnrolled;
    private Button btnAdd, btnEdit, btnDelete, btnView;
    private TableLayout tableStudents;
    private DatabaseHelper databaseHelper;

    private String selectedStudentId = "";
    private String lastClickedId = "";
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etStudentId = findViewById(R.id.etStudentId);
        cbEnrolled = findViewById(R.id.cbEnrolled);

        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnView = findViewById(R.id.btnView);

        tableStudents = findViewById(R.id.tableStudents);

        databaseHelper = new DatabaseHelper(this);

        btnAdd.setOnClickListener(v -> addStudent());
        btnEdit.setOnClickListener(v -> editStudent());
        btnDelete.setOnClickListener(v -> deleteStudent());
        btnView.setOnClickListener(v -> viewStudents());

        viewStudents();
    }

    private void addStudent() {
        String name = etName.getText().toString().trim();
        String id = etStudentId.getText().toString().trim();
        boolean enrolled = cbEnrolled.isChecked();

        if (name.isEmpty() || id.isEmpty()) {
            showMessage("Please fill in Student Name and Student ID");
            return;
        }

        boolean success = databaseHelper.addStudent(id, name, enrolled);

        if (success) {
            showMessage("Student added successfully");
            clearForm();
            viewStudents();
        } else {
            showMessage("Failed to add. Student ID may already exist.");
        }
    }

    private void editStudent() {
        String id = selectedStudentId.isEmpty()
                ? etStudentId.getText().toString().trim()
                : selectedStudentId;

        String name = etName.getText().toString().trim();
        boolean enrolled = cbEnrolled.isChecked();

        if (id.isEmpty() || name.isEmpty()) {
            showMessage("Select a student and enter u name");
            return;
        }

        boolean success = databaseHelper.updateStudent(
                id, name, enrolled
        );

        if (success) {
            showMessage("Student updated successfully");
            clearForm();
            viewStudents();
        } else {
            showMessage("Student ID not found");
        }
    }

    private void deleteStudent() {
        String id = selectedStudentId.isEmpty()
                ? etStudentId.getText().toString().trim()
                : selectedStudentId;

        if (id.isEmpty()) {
            showMessage("Select a student to delete");
            return;
        }

        boolean success = databaseHelper.deleteStudent(id);

        if (success) {
            showMessage("Student deleted successfully");
            clearForm();
            viewStudents();
        } else {
            showMessage("Student ID not found");
        }
    }

    private void viewStudents() {
        // Keep the first row as the table header.
        if (tableStudents.getChildCount() > 1) {
            tableStudents.removeViews(
                    1, tableStudents.getChildCount() - 1
            );
        }

        Cursor cursor = databaseHelper.getAllStudents();

        if (cursor == null) {
            showMessage("Unable to load student records");
            return;
        }

        try {
            if (cursor.getCount() == 0) {
                TableRow emptyRow = new TableRow(this);
                TextView emptyCell = createCell("No student records found");
                emptyRow.addView(emptyCell);
                tableStudents.addView(emptyRow);
                return;
            }

            while (cursor.moveToNext()) {
                String id = cursor.getString(
                        cursor.getColumnIndexOrThrow("student_id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("student_name")
                );

                int enrolledValue = cursor.getInt(
                        cursor.getColumnIndexOrThrow("enrolled")
                );

                String status = enrolledValue == 1
                        ? "Enrolled" : "Not Enrolled";

                TableRow row = new TableRow(this);
                row.setPadding(4, 4, 4, 4);
                row.setBackgroundColor(Color.WHITE);

                row.addView(createCell(id));
                row.addView(createCell(name));
                row.addView(createCell(status));

                row.setOnClickListener(v -> {
                    long now = System.currentTimeMillis();

                    boolean doubleClick =
                            id.equals(lastClickedId)
                                    && now - lastClickTime < 500;

                    selectedStudentId = id;

                    if (doubleClick) {
                        etStudentId.setText(id);
                        etName.setText(name);
                        cbEnrolled.setChecked(enrolledValue == 1);

                        showMessage("Student loaded into form");
                    } else {
                        showMessage(
                                "Selected: " + name
                                        + ". Double tap to open data."
                        );
                    }

                    lastClickedId = id;
                    lastClickTime = now;
                });

                tableStudents.addView(row);
            }
        } finally {
            cursor.close();
        }
    }

    private TextView createCell(String value) {
        TextView cell = new TextView(this);

        cell.setText(value);
        cell.setTextColor(Color.rgb(23, 43, 77));
        cell.setTextSize(13);
        cell.setPadding(12, 14, 12, 14);
        cell.setGravity(Gravity.CENTER_VERTICAL);

        return cell;
    }

    private void clearForm() {
        etName.setText("");
        etStudentId.setText("");
        cbEnrolled.setChecked(false);

        selectedStudentId = "";
        lastClickedId = "";
        lastClickTime = 0;
    }

    private void showMessage(String message) {
        Toast.makeText(
                this, message, Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
