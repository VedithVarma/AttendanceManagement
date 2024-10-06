package com.example.myattendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ClassItem> classItems = new ArrayList<>();
    Toolbar toolbar;
    DBHelper dbHelper;

    TextClock textClock;
    TextView dateText;


    private int cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textClock = findViewById(R.id.dynamicClock);
        textClock.setTextColor(getResources().getColor(R.color.white)); // Change color dynamically if needed
        textClock.setTextSize(24);


        dateText = findViewById(R.id.dateText);
        updateDateTime();

        dbHelper = new DBHelper(this);
        setToolbar();
        fab=findViewById(R.id.fab_main);
        fab.setBackgroundResource(R.drawable.sunrise_gradient_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation scaleAnimation = new ScaleAnimation(1f, 0.8f, 1f, 0.8f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(200);
                v.startAnimation(scaleAnimation);
                showDialog();
                // Add your click logic here
                // For example, launch a new activity or show a dialog
            }
        });

        loadData();

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FacultyLandingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the current activity
        });


        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this,classItems);
        recyclerView.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(position->gotoItemActivity(position));
        

    }

    private void loadData() {
        Cursor cursor = dbHelper.getClassTable();

        classItems.clear();
        while(cursor.moveToNext())
        {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DBHelper.C_ID));
            @SuppressLint("Range") String className = cursor.getString(cursor.getColumnIndex(DBHelper.CLASS_NAME_KEY));
            @SuppressLint("Range") String subjectName = cursor.getString(cursor.getColumnIndex(DBHelper.SUBJECT_NAME_KEY));
            classItems.add(new ClassItem(id,className,subjectName));
        }

    }

    private void setToolbar()
    {

        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        title.setText("Attendance App");
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.INVISIBLE);

        save.setVisibility(View.INVISIBLE);
    }

    private void gotoItemActivity(int position) {
        Intent intent = new Intent(this,StudentActivity.class);
        intent.putExtra("className",classItems.get(position).getClassName());
        intent.putExtra("subjectName",classItems.get(position).getSubjectName());
        intent.putExtra("position",position);
        Log.i("hello","putting:"+String.valueOf(classItems.get(position).getCid()));
        intent.putExtra("cid",classItems.get(position).getCid());
        startActivity(intent);
    }

    private void showDialog(){
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.CLASS_ADD_DIALOG);
        dialog.setListener((className,subjectName)->addClass(className,subjectName));
    }

    private void addClass(String className,String subjectName){
        long cid = dbHelper.addClass(className,subjectName);
        ClassItem classItem = new ClassItem(cid,className,subjectName);
        classItems.add(new ClassItem(className,subjectName));
        classAdapter.notifyDataSetChanged();

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        Log.i("hello",String.valueOf(item.getItemId()));
        switch (item.getItemId()){
            case 0:
                showUpdateDialog(item.getGroupId());
                break;
            case 1:
                Log.i("hello",String.valueOf(item.getGroupId())+"0");
                deleteClass(item.getGroupId());

        }
        return super.onContextItemSelected(item);

    }

    private void showUpdateDialog(int position) {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.CLASS_UPDATE_DIALOG);
        dialog.setListener((className,subjectName)->updateClass(position,className,subjectName));
    }

    private void updateClass(int position, String className, String subjectName) {
        dbHelper.updateClass(classItems.get(position).getCid(),className,subjectName);
        classItems.get(position).setClassName(className);
        classItems.get(position).setSubjectName(subjectName);
        classAdapter.notifyItemChanged(position);

    }


    private void deleteClass(int position) {
        Log.i("hello",String.valueOf(position)+"0");
        Log.i("hello",String.valueOf(classItems.get(position).getCid())+"1");
        dbHelper.deleteClass(classItems.get(position).getCid());
        classItems.remove(position);
        classAdapter.notifyItemRemoved(position);

    }

    @SuppressLint("ResourceAsColor")
    private void updateDateTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

        String currentDate = dateFormat.format(new Date());
        String currentDay = dayFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());

        String combinedText = String.format("%s %s\n", currentDate, currentDay);
        dateText.setText(combinedText);
        textClock.setFormat24Hour(currentTime);


    }




}