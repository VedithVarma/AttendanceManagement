package com.example.myattendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FacultyLandingActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FacultyAdapter facultyAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<FacultyItem> facultyItems = new ArrayList<>();

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_landing);

        loadFacultyData();
        setToolbar();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        facultyAdapter = new FacultyAdapter(this, facultyItems);
        recyclerView.setAdapter(facultyAdapter);
        facultyAdapter.setOnItemClickListener(position -> promptPassword(position));
    }

    private void loadFacultyData() {
        // Hardcoded faculty data
        facultyItems.add(new FacultyItem("Prof. Swathi Sridharan", "password123", "profswathi@bmsce.ac.in", "Computer Science"));
        facultyItems.add(new FacultyItem("Prof. Chandrasekhara", "password456", "profchandrasekhara@bmsce.ac.in", "Mathematics"));
        facultyItems.add(new FacultyItem("Prof. Suresha", "password789", "profsuresha@bmsce.ac.in", "Mechanical"));
    }

    private void promptPassword(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password, null);
        builder.setView(dialogView);

        final EditText inputPassword = dialogView.findViewById(R.id.input_password);

        builder.setTitle("Enter Password")
                .setPositiveButton("OK", (dialog, which) -> {
                    String enteredPassword = inputPassword.getText().toString();
                    if (enteredPassword.equals(facultyItems.get(position).getPassword())) {
                        Intent intent = new Intent(FacultyLandingActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(FacultyLandingActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        title.setText("BMSCE Attendance App");
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
    }
}
