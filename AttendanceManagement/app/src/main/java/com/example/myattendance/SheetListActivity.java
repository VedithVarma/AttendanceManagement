package com.example.myattendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SheetListActivity extends AppCompatActivity {
    Toolbar toolbar;
    private String className;
    private String subjectName;

    private int position;

    private long cid;
    private ListView sheetlist;

    private ArrayList<String> listItems=new ArrayList();
    private ArrayAdapter listAdapter;
    long [] idArray;
    String [] rollArray;
    String[] nameArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sheet_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cid= getIntent().getLongExtra("cid",-1);
        className=getIntent().getStringExtra("className");
        subjectName=getIntent().getStringExtra("subjectName");
        setToolbar();
        loadListItems();
        sheetlist=findViewById(R.id.sheetList);
        listAdapter = new ArrayAdapter(this,R.layout.sheet_list,R.id.date_list_item,listItems);
        sheetlist.setAdapter(listAdapter);
        idArray = getIntent().getLongArrayExtra("idArray");
        rollArray = getIntent().getStringArrayExtra("rollArray");
        nameArray =getIntent().getStringArrayExtra("nameArray");
        sheetlist.setOnItemClickListener((parent,view,position,id)->openSheetActivity(position));
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);
        save.setVisibility(View.INVISIBLE);
        title.setText(className+" "+subjectName);
        Log.i("ClassName",className+"2");
        subtitle.setText("Months");
//        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                setEnabled(false);
//                getOnBackPressedDispatcher().onBackPressed();
//                setEnabled(true);
//            }
//        };

        back.setOnClickListener(v->getOnBackPressedDispatcher().onBackPressed());

    }

    private void openSheetActivity(int position) {

        Log.i("idArray",String.valueOf(idArray[0]));
        Log.i("idArray",String.valueOf(idArray[1]));
        Intent intent = new Intent(this,SheetActivity.class);
        intent.putExtra("idArray",idArray);
        intent.putExtra("rollArray",rollArray);
        intent.putExtra("nameArray",nameArray);
        intent.putExtra("month",listItems.get(position));
        intent.putExtra("className",className);
        intent.putExtra("subjectName",subjectName);
        startActivity(intent);
    }

//    private void loadListItems(){
//        Cursor cursor = new DBHelper(this).getDistinctMonths(cid);
//        while(cursor.moveToNext()){
//            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DBHelper.DATE_KEY));
//            Log.i("hello","Date = "+date);
//            listItems.add(date.substring(3));
//            Log.i("hello","listItems = "+listItems.get(0));
//
//        }
//    }

    private void loadListItems(){
        Cursor cursor = new DBHelper(this).getDistinctMonths(cid);

        // Check if cursor has data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DBHelper.DATE_KEY));
                Log.i("hello", "Date = " + date);

                // Check if date is not null and has at least 3 characters
                if (date != null && date.length() > 3) {
                    // Add substring to listItems
                    listItems.add(date.substring(3));
                } else {
                    // Handle invalid date format
                    Log.e("hello", "Invalid date format: " + date);
                }
            } while (cursor.moveToNext());

            // Close cursor after use
            cursor.close();
        } else {
            // Handle empty cursor
            Log.e("hello", "Cursor is empty or null");
        }
    }
}