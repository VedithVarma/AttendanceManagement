package com.example.myattendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {
    Toolbar toolbar;
    private String className;
    private String subjectName;

    private int position;

    StudentAdapter studentAdapter;
    RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;
    ArrayList<StudentItem> studentItems = new ArrayList<>();

    private DBHelper dbHelper;

    private long cid;
    private MyCalendar calendar;

    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        calendar = new MyCalendar();
        dbHelper = new DBHelper(this);
        Intent intent = getIntent();
        className=intent.getStringExtra("className");
        subjectName=intent.getStringExtra("subjectName");
        position = intent.getIntExtra("position",-1);
        cid=intent.getLongExtra("cid",-1);

        setToolbar();
        loadData();

        recyclerView = findViewById(R.id.student_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(this,studentItems);
        recyclerView.setAdapter(studentAdapter);
        studentAdapter.setOnItemClickListener(position->changeStatus(position));
        loadStatusData();
    }

    private void loadData() {
        Log.i("hello","getting cid: "+String.valueOf(cid));
        Cursor cursor = dbHelper.getStudentTable(cid);
        Log.i("hello","getting cid: "+String.valueOf(cid));
        studentItems.clear();
        while(cursor.moveToNext())
        {
            @SuppressLint("Range") long sid = cursor.getLong(cursor.getColumnIndex(DBHelper.S_ID));
            @SuppressLint("Range") String roll = cursor.getString(cursor.getColumnIndex(DBHelper.STUDENT_ROLL_KEY));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DBHelper.STUDENT_NAME_KEY));
            studentItems.add(new StudentItem(sid,roll,name));
        }
        cursor.close();
    }

    private void changeStatus(int position) {
        String status = studentItems.get(position).getStatus();

        if(status.equals("P")) status ="A";
        else status ="P";

        studentItems.get(position).setStatus(status);
        studentAdapter.notifyDataSetChanged();
    }

    private void setToolbar()
    {

        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
//        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);
        save.setOnClickListener(v->saveStatus());

        title.setText(className);
        subtitle.setText(subjectName+" | "+calendar.getDate());
//        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                setEnabled(false);
//                getOnBackPressedDispatcher().onBackPressed();
//                setEnabled(true);
//            }
//        };

        back.setOnClickListener(v->getOnBackPressedDispatcher().onBackPressed());
        toolbar.inflateMenu(R.menu.student_menu);
        toolbar.setOnMenuItemClickListener(menuItem->onMenuItemClick(menuItem));
    }

    private void saveStatus() {
        for(StudentItem studentItem:studentItems){
            String status = studentItem.getStatus();
            if(status!="P")status = "A";
            long value=dbHelper.addStatus(studentItem.getSid(),cid,calendar.getDate(),status);
            if(value==-1)dbHelper.updateStatus(studentItem.getSid(),calendar.getDate(),status);
        }
    }
    private void loadStatusData(){
        for(StudentItem studentItem: studentItems){
            String status = dbHelper.getStatus(studentItem.getSid(),calendar.getDate());
            if(status!=null)studentItem.setStatus(status);
            else studentItem.setStatus("");
        }
        studentAdapter.notifyDataSetChanged();;
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId()==R.id.add_student)
        {
            showAddStudentDialog();
        }
        else if(menuItem.getItemId()==R.id.show_Calendar)
        {
            showCalendar();
        }
        else if(menuItem.getItemId()==R.id.show_attendance_sheet)
        {
            openSheetList();
        }
        return true;
    }

    private void openSheetList() {
        long[] idArray = new long[studentItems.size()];
        String [] rollArray = new String[studentItems.size()];
        String [] nameArray = new String[studentItems.size()];
        for(int i=0;i<idArray.length;i++)
        {
            idArray[i]= studentItems.get(i).getSid();
        }
        for(int i=0;i<rollArray.length;i++)
        {
            rollArray[i]= studentItems.get(i).getRoll();
        }
        for(int i=0;i<nameArray.length;i++)
        {
            nameArray[i]= studentItems.get(i).getName();
        }
        Intent intent = new Intent(this,SheetListActivity.class);
        intent.putExtra("cid",cid);
        intent.putExtra("idArray",idArray);
        intent.putExtra("rollArray",rollArray);
        intent.putExtra("nameArray",nameArray);
        intent.putExtra("className",className);
        intent.putExtra("subjectName",subjectName);

        startActivity(intent);
    }

    private void showCalendar() {

        calendar.show(getSupportFragmentManager(),"");
        calendar.setOnCalendarOkClickListener(this::onCalendarOkClicked);
    }

    private void onCalendarOkClicked(int year, int month, int day) {
        calendar.setDate(year,month,day);
        subtitle.setText(calendar.getDate());
        loadStatusData();
    }

    private void showAddStudentDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.STUDENT_ADD_DIALOG);
        dialog.setListener((roll,name)->addStudent(roll,name));

    }

    private void addStudent(String roll, String name) {
        Log.i("hello",name);
        long sid = dbHelper.addStudent(cid,roll,name);
        StudentItem studentItem = new StudentItem(sid,roll,name);
        studentItems.add(studentItem);
        studentAdapter.notifyDataSetChanged();
    }

    private void showUpdateStudentDialog(int position) {
        MyDialog dialog = new MyDialog(studentItems.get(position).getRoll(),studentItems.get(position).getName());
        dialog.show(getSupportFragmentManager(),MyDialog.STUDENT_UPDATE_DIALOG);
        dialog.setListener((roll,name)->updateStudent(position,name));

    }

    private void updateStudent(int position, String name) {
        dbHelper.updateStudent(studentItems.get(position).getSid(),name);
        studentItems.get(position).setName(name);

        studentAdapter.notifyItemChanged(position);
        Log.i("hello",String.valueOf(studentItems.size()));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:
                showUpdateStudentDialog(item.getGroupId());
                break;
            case 1:
                deleteStudent(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }



    private void deleteStudent(int position){
        dbHelper.deleteStudent(studentItems.get(position).getSid());
        studentItems.remove(position);
        studentAdapter.notifyItemRemoved(position);
    }
}

//package com.example.myapplication;
//
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//public class StudentActivity extends AppCompatActivity {
//    private RecyclerView recyclerView;
//    private StudentAdapter adapter;
//    private ArrayList<StudentItem> studentItems;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_student);
//
//        recyclerView = findViewById(R.id.student_recycler);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        studentItems = new ArrayList<>();
//        // Populate studentItems with dummy data (replace with your actual data)
//        studentItems.add(new StudentItem("1", "John Doe"));
//        studentItems.add(new StudentItem("2", "Jane Smith"));
//        studentItems.add(new StudentItem("3", "Bob Johnson"));
//
//        adapter = new StudentAdapter(this, studentItems);
//        recyclerView.setAdapter(adapter);
//    }
//}
