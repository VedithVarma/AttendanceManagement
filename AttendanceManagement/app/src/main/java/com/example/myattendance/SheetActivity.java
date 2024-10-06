package com.example.myattendance;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class SheetActivity extends AppCompatActivity {

    long[] idArray;

    String[] rollArray;
    String[] nameArray;
    String month;

    String className;

    String subjectName;
    Toolbar toolbar;
    Button buttonExportExcel;
    String FileNameSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sheet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        idArray = getIntent().getLongArrayExtra("idArray");
        rollArray = getIntent().getStringArrayExtra("rollArray");
        nameArray = getIntent().getStringArrayExtra("nameArray");
        month = getIntent().getStringExtra("month");
        className = getIntent().getStringExtra("className");
        subjectName = getIntent().getStringExtra("subjectName");
        setToolbar();
        showTable();
        buttonExportExcel = findViewById(R.id.buttonExportExcel);
        buttonExportExcel.setOnClickListener(v -> exportToExcel());
        buttonExportExcel = findViewById(R.id.buttonExportExcel);
        buttonExportExcel.setOnClickListener(v -> exportToExcel());
        Button buttonSendEmail = findViewById(R.id.buttonSendEmail);
        buttonSendEmail.setOnClickListener(v -> sendEmailWithAttachment());
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);
        save.setVisibility(View.INVISIBLE);
        title.setText(className + " " + subjectName);
        Log.i("ClassName", className + "2");
        String month_val = month.substring(0, 2);
        String month_name;
        if (Integer.parseInt(month_val) == 1) {
            month_name = "January";
        } else if (Integer.parseInt(month_val) == 2) {
            month_name = "February";
        } else if (Integer.parseInt(month_val) == 3) {
            month_name = "March";
        } else if (Integer.parseInt(month_val) == 4) {
            month_name = "April";
        } else if (Integer.parseInt(month_val) == 5) {
            month_name = "May";
        } else if (Integer.parseInt(month_val) == 6) {
            month_name = "June";
        } else if (Integer.parseInt(month_val) == 7) {
            month_name = "July";
        } else if (Integer.parseInt(month_val) == 8) {
            month_name = "August";
        } else if (Integer.parseInt(month_val) == 8) {
            month_name = "September";
        } else if (Integer.parseInt(month_val) == 8) {
            month_name = "October";
        } else if (Integer.parseInt(month_val) == 8) {
            month_name = "November";
        } else if (Integer.parseInt(month_val) == 8) {
            month_name = "December";
        } else {
            month_name = "Months";
        }
        subtitle.setText(month_name);
//        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                setEnabled(false);
//                getOnBackPressedDispatcher().onBackPressed();
//                setEnabled(true);
//            }
//        };

        back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

    }

    private void showTable() {
        DBHelper dbHelper = new DBHelper(this);
        TableLayout tableLayout = findViewById(R.id.tableLayout);


        int DAY_IN_MONTH = getDayInMonth(month);


        //row setup
        int rowSize = idArray.length + 1;
        TableRow[] rows = new TableRow[idArray.length + 1];
        TextView[] roll_tvs = new TextView[rowSize];
        TextView[] name_tvs = new TextView[rowSize];
        TextView[][] status_tvs = new TextView[rowSize][DAY_IN_MONTH + 1];

        for (int i = 0; i < rowSize; i++) {
            roll_tvs[i] = new TextView(this);
            name_tvs[i] = new TextView(this);
            for (int j = 0; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j] = new TextView(this);

            }
        }

        //header
        roll_tvs[0].setText("Roll");
        roll_tvs[0].setTypeface(roll_tvs[0].getTypeface(), Typeface.BOLD);
        name_tvs[0].setText("Name");
        name_tvs[0].setTypeface(name_tvs[0].getTypeface(), Typeface.BOLD);
        for (int i = 1; i <= DAY_IN_MONTH; i++) {
            status_tvs[0][i].setText(String.valueOf(i));
            status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(), Typeface.BOLD);
        }

        for (int i = 1; i < rowSize; i++) {
            assert rollArray != null;
            roll_tvs[i].setText(rollArray[i - 1]);
            assert nameArray != null;
            name_tvs[i].setText(nameArray[i - 1]);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                String day = String.valueOf(j);
                if (day.length() == 1) day = "0" + day;
                String date = day + "." + month;
                String status = dbHelper.getStatus(idArray[i - 1], date);
                status_tvs[i][j].setText(status);
                // Set background color based on status
                if ("P".equals(status)) {
                    status_tvs[i][j].setBackgroundColor(getResources().getColor(R.color.light_blue));
                } else if ("A".equals(status)) {
                    status_tvs[i][j].setBackgroundColor(getResources().getColor(R.color.light_red));
                }
            }
        }
        for (int i = 0; i < rowSize; i++) {
            rows[i] = new TableRow(this);
            if (i % 2 == 0) {
                rows[i].setBackgroundColor(Color.parseColor("#EEEEEE"));
            } else {
                rows[i].setBackgroundColor(Color.parseColor("#E4E4E4"));
            }

            roll_tvs[i].setPadding(16, 16, 16, 16);
            name_tvs[i].setPadding(16, 16, 16, 16);
            rows[i].addView(roll_tvs[i]);
            rows[i].addView(name_tvs[i]);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j].setPadding(16, 16, 16, 16);
                rows[i].addView(status_tvs[i][j]);
            }
            tableLayout.addView(rows[i]);
        }
        tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);

    }

    private int getDayInMonth(String month) {
        int monthIndex = Integer.valueOf(month.substring(0, 1));
        int year = Integer.valueOf(month.substring(4));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthIndex);
        calendar.set(Calendar.YEAR, year);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private String getMonthName(int monthVal) {
        switch (monthVal) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "Months";
        }
    }

    private void exportToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Roll");
        headerRow.createCell(1).setCellValue("Name");
        int DAY_IN_MONTH = getDayInMonth(month);
        for (int i = 1; i <= DAY_IN_MONTH; i++) {
            headerRow.createCell(i + 1).setCellValue(String.valueOf(i));
        }

        // Populate data
        for (int i = 0; i < idArray.length; i++) {
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(rollArray[i]);
            dataRow.createCell(1).setCellValue(nameArray[i]);

            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                String day = String.valueOf(j);
                if (day.length() == 1) day = "0" + day;
                String date = day + "." + month;
                String status = new DBHelper(this).getStatus(idArray[i], date);
                dataRow.createCell(j + 1).setCellValue(status);
            }
        }

        // Save the workbook
        if (saveWorkbookToDownloads(workbook)) {
            Toast.makeText(this, "Excel file exported to Downloads folder", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error exporting Excel file", Toast.LENGTH_SHORT).show();
        }

        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean saveWorkbookToDownloads(Workbook workbook) {
        boolean success = false;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();
            String fileName = "Attendance_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".xlsx";
            FileNameSave = fileName;
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

            try {
                if (uri != null) {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        workbook.write(outputStream);
                        outputStream.close();
                        success = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // For Android 9 and below, use traditional file path
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String fileName = "Attendance_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".xlsx";
            FileNameSave = fileName;
            File file = new File(downloadsDir, fileName);

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    private File getExcelFile() {

        String fileName = FileNameSave;

        // Get the public Downloads directory
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (downloadsDir != null) {
            // Ensure that the Downloads directory exists
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            // Create the Excel file in the Downloads directory
            return new File(downloadsDir, fileName);
        } else {
            return null;
        }
    }




    private void sendEmailWithAttachment() {
        exportToExcel();
        File file = getExcelFile(); // Use the same method to get the file

        if (file.exists()) {
            Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"anurag.cs22@bmsce.ac.in","vedithvarma.cs22@bmsce.ac.in","sarthakgupta.cs22@bmsce.ac.in","ashish.cs22@bmsce.ac.in"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Attendance "+className+" "+subjectName);
            intent.putExtra(Intent.EXTRA_TEXT, "Kindly find Attendance Sheet for "+className+" "+subjectName+" attached below.");
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Send email..."));
        } else {
            Toast.makeText(this, "File not found. Please export to Excel first.", Toast.LENGTH_SHORT).show();
        }
    }




}


//    private void exportToExcel() {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Attendance");
//
//        // Create header row
//        Row headerRow = sheet.createRow(0);
//        headerRow.createCell(0).setCellValue("Roll");
//        headerRow.createCell(1).setCellValue("Name");
//        int DAY_IN_MONTH = getDayInMonth(month);
//        for (int i = 1; i <= DAY_IN_MONTH; i++) {
//            headerRow.createCell(i + 1).setCellValue(String.valueOf(i));
//        }
//
//        // Populate data
//        for (int i = 0; i < idArray.length; i++) {
//            Row dataRow = sheet.createRow(i + 1);
//            dataRow.createCell(0).setCellValue(rollArray[i]);
//            dataRow.createCell(1).setCellValue(nameArray[i]);
//
//            for (int j = 1; j <= DAY_IN_MONTH; j++) {
//                String day = String.valueOf(j);
//                if (day.length() == 1) day = "0" + day;
//                String date = day + "." + month;
//                String status = new DBHelper(this).getStatus(idArray[i], date);
//                dataRow.createCell(j + 1).setCellValue(status);
//            }
//        }
//
//        // Save the workbook
//        File file = getExcelFile();
//        if (file != null) {
//            try (FileOutputStream outputStream = new FileOutputStream(file)) {
//                workbook.write(outputStream);
//                Toast.makeText(this, "Excel file exported to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Error exporting Excel file", Toast.LENGTH_SHORT).show();
//            } finally {
//                try {
//                    workbook.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private File getExcelFile() {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        String fileName = "Attendance_" + timeStamp + ".xlsx";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
//        if (storageDir != null) {
//            return new File(storageDir, fileName);
//        } else {
//            return null;
//        }
//    }
//}




//BACKUP  CODE
//package com.example.myapplication;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
//
//
//
//public class SheetActivity extends AppCompatActivity {
//
//    long [] idArray;
//
//    String [] rollArray;
//    String[] nameArray;
//    String month;
//
//    String className;
//
//    String subjectName;
//    Toolbar toolbar;
//    Button buttonExportExcel;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_sheet);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        idArray = getIntent().getLongArrayExtra("idArray");
//        rollArray = getIntent().getStringArrayExtra("rollArray");
//        nameArray =getIntent().getStringArrayExtra("nameArray");
//        month = getIntent().getStringExtra("month");
//        className = getIntent().getStringExtra("className");
//        subjectName=getIntent().getStringExtra("subjectName");
//        setToolbar();
//        showTable();
//        buttonExportExcel = findViewById(R.id.buttonExportExcel);
//        buttonExportExcel.setOnClickListener(v -> exportToExcel());
//    }
//    private void setToolbar() {
//        toolbar = findViewById(R.id.toolbar);
//        TextView title = toolbar.findViewById(R.id.title_toolbar);
//        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
//        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
//        ImageButton back = toolbar.findViewById(R.id.back);
//        ImageButton save = toolbar.findViewById(R.id.save);
//        save.setVisibility(View.INVISIBLE);
//        title.setText(className+" "+subjectName);
//        Log.i("ClassName",className+"2");
//        String month_val=month.substring(0,2);
//        String month_name;
//        if(Integer.parseInt(month_val)==1){
//            month_name ="January";
//        }
//        else if(Integer.parseInt(month_val)==2){
//            month_name ="February";
//        }
//        else if(Integer.parseInt(month_val)==3){
//            month_name ="March";
//        }
//        else if(Integer.parseInt(month_val)==4){
//            month_name ="April";
//        }
//        else if(Integer.parseInt(month_val)==5){
//            month_name ="May";
//        }
//        else if(Integer.parseInt(month_val)==6){
//            month_name ="June";
//        }
//        else if(Integer.parseInt(month_val)==7){
//            month_name ="July";
//        }
//        else if(Integer.parseInt(month_val)==8){
//            month_name ="August";
//        }
//        else if(Integer.parseInt(month_val)==8){
//            month_name ="September";
//        }
//        else if(Integer.parseInt(month_val)==8){
//            month_name ="October";
//        }
//        else if(Integer.parseInt(month_val)==8){
//            month_name ="November";
//        }
//        else if(Integer.parseInt(month_val)==8){
//            month_name ="December";
//        }
//        else{
//            month_name="Months";
//        }
//        subtitle.setText(month_name);
////        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
////            @Override
////            public void handleOnBackPressed() {
////                setEnabled(false);
////                getOnBackPressedDispatcher().onBackPressed();
////                setEnabled(true);
////            }
////        };
//
//        back.setOnClickListener(v->getOnBackPressedDispatcher().onBackPressed());
//
//    }
//
//    private void showTable() {
//        DBHelper dbHelper= new DBHelper(this);
//        TableLayout tableLayout = findViewById(R.id.tableLayout);
//
//
//        int DAY_IN_MONTH = getDayInMonth(month);
//
//
//        //row setup
//        int rowSize =idArray.length+1;
//        TableRow[] rows = new TableRow[idArray.length+1];
//        TextView[] roll_tvs= new TextView[rowSize];
//        TextView[] name_tvs = new TextView[rowSize];
//        TextView[][] status_tvs = new TextView[rowSize][DAY_IN_MONTH+1];
//
//        for(int i=0;i<rowSize;i++){
//            roll_tvs[i]=new TextView(this);
//            name_tvs[i]=new TextView(this);
//            for(int j=0;j<=DAY_IN_MONTH;j++){
//                status_tvs[i][j]= new TextView(this);
//            }
//        }
//
//        //header
//        roll_tvs[0].setText("Roll");
//        roll_tvs[0].setTypeface(roll_tvs[0].getTypeface(), Typeface.BOLD);
//        name_tvs[0].setText("Name");
//        name_tvs[0].setTypeface(name_tvs[0].getTypeface(), Typeface.BOLD);
//        for(int i=1;i<=DAY_IN_MONTH;i++){
//            status_tvs[0][i].setText(String.valueOf(i));
//            status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(),Typeface.BOLD);
//        }
//
//        for(int i=1;i<rowSize;i++){
//            assert rollArray != null;
//            roll_tvs[i].setText(rollArray[i-1]);
//            assert nameArray != null;
//            name_tvs[i].setText(nameArray[i-1]);
//            for(int j=1;j<=DAY_IN_MONTH;j++){
//                String day = String.valueOf(j);
//                if(day.length()==1) day="0"+day;
//                String date = day+"."+month;
//                String status = dbHelper.getStatus(idArray[i-1],date);
//                status_tvs[i][j].setText(status);
//            }
//        }
//        for(int i=0;i<rowSize;i++){
//            rows[i]= new TableRow(this);
//            if(i%2==0){
//                rows[i].setBackgroundColor(Color.parseColor("#EEEEEE"));}
//            else{
//                rows[i].setBackgroundColor(Color.parseColor("#E4E4E4"));}
//
//            roll_tvs[i].setPadding(16,16,16,16);
//            name_tvs[i].setPadding(16,16,16,16);
//            rows[i].addView(roll_tvs[i]);
//            rows[i].addView(name_tvs[i]);
//            for(int j=1;j<=DAY_IN_MONTH;j++){
//                status_tvs[i][j].setPadding(16,16,16,16);
//                rows[i].addView(status_tvs[i][j]);
//            }
//            tableLayout.addView(rows[i]);
//        }
//        tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);
//
//    }
//
//    private int getDayInMonth(String month) {
//        int monthIndex = Integer.valueOf(month.substring(0,1));
//        int year = Integer.valueOf(month.substring(4));
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.MONTH,monthIndex);
//        calendar.set(Calendar.YEAR,year);
//        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//    }
//
//    private String getMonthName(int monthVal) {
//        switch (monthVal) {
//            case 1: return "January";
//            case 2: return "February";
//            case 3: return "March";
//            case 4: return "April";
//            case 5: return "May";
//            case 6: return "June";
//            case 7: return "July";
//            case 8: return "August";
//            case 9: return "September";
//            case 10: return "October";
//            case 11: return "November";
//            case 12: return "December";
//            default: return "Months";
//        }
//    }
//
//
//    private void exportToExcel() {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Attendance");
//
//        // Create header row
//        Row headerRow = sheet.createRow(0);
//        headerRow.createCell(0).setCellValue("Roll");
//        headerRow.createCell(1).setCellValue("Name");
//        int DAY_IN_MONTH = getDayInMonth(month);
//        for (int i = 1; i <= DAY_IN_MONTH; i++) {
//            headerRow.createCell(i + 1).setCellValue(String.valueOf(i));
//        }
//
//        // Populate data
//        for (int i = 0; i < idArray.length; i++) {
//            Row dataRow = sheet.createRow(i + 1);
//            dataRow.createCell(0).setCellValue(rollArray[i]);
//            dataRow.createCell(1).setCellValue(nameArray[i]);
//
//            for (int j = 1; j <= DAY_IN_MONTH; j++) {
//                String day = String.valueOf(j);
//                if (day.length() == 1) day = "0" + day;
//                String date = day + "." + month;
//                String status = new DBHelper(this).getStatus(idArray[i], date);
//                dataRow.createCell(j + 1).setCellValue(status);
//            }
//        }
//
//        // Save the workbook
//        File file = getExcelFile();
//        if (file != null) {
//            try (FileOutputStream outputStream = new FileOutputStream(file)) {
//                workbook.write(outputStream);
//                Toast.makeText(this, "Excel file exported to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Error exporting Excel file", Toast.LENGTH_SHORT).show();
//            } finally {
//                try {
//                    workbook.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private File getExcelFile() {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        String fileName = "Attendance_" + timeStamp + ".xlsx";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
//        if (storageDir != null) {
//            return new File(storageDir, fileName);
//        } else {
//            return null;
//        }
//    }
//}