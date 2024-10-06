package com.example.myattendance;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    ArrayList<StudentItem> studentItems;
    Context context;

    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener=onItemClickListener;
    }
    public StudentAdapter(Context context, ArrayList<StudentItem> studentItems) {
        this.studentItems = studentItems;
        this.context=context;
    }


    public static class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        TextView roll;
        TextView name;
        TextView status;

        CardView cardview;

        RelativeLayout sitem;
        public StudentViewHolder(@NonNull View itemView,OnItemClickListener onItemClickListener){

            super(itemView);
            roll=itemView.findViewById(R.id.roll);
            name = itemView.findViewById(R.id.name);
            status= itemView.findViewById(R.id.status);
            cardview=itemView.findViewById(R.id.cardview);
            sitem=itemView.findViewById(R.id.sitem);
            itemView.setOnClickListener(v->onItemClickListener.onClick(getAdapterPosition()));
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(),0,0,"Edit");
            menu.add(getAdapterPosition(),1,0,"Delete");
        }
    }

    @NonNull
    @Override
    public StudentAdapter.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item,parent,false);
        return new StudentViewHolder(itemView,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.roll.setText(studentItems.get(position).getRoll());
        holder.name.setText(studentItems.get(position).getName());
        holder.status.setText(studentItems.get(position).getStatus());
        holder.sitem.setBackground(getDrawableForStatus(position));
    }

    private Drawable getDrawableForStatus(int position) {
        String status = studentItems.get(position).getStatus();
        if (status.equals("P")) {
            // Return color drawable for "P" status
            return ContextCompat.getDrawable(context, R.drawable.present_gradient);
        } else if (status.equals("A")) {
            // Return color drawable for "A" status
            return ContextCompat.getDrawable(context, R.drawable.absent_gradient);
        } else {
            // Return drawable for "normal" status
            return ContextCompat.getDrawable(context, R.drawable.sunrise_blue_student);
        }
    }


    @Override
    public int getItemCount() {
              return studentItems.size();

    }
}

//package com.example.myapplication;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
//    private ArrayList<StudentItem> studentItems;
//    private Context context;
//
//    public StudentAdapter(Context context, ArrayList<StudentItem> studentItems) {
//        this.context = context;
//        this.studentItems = studentItems;
//    }
//
//    public static class StudentViewHolder extends RecyclerView.ViewHolder {
//        TextView roll;
//        TextView name;
//        TextView status;
//
//        public StudentViewHolder(@NonNull View itemView) {
//            super(itemView);
//            roll = itemView.findViewById(R.id.roll);
//            name = itemView.findViewById(R.id.name);
//            status = itemView.findViewById(R.id.status);
//        }
//    }
//
//    @NonNull
//    @Override
//    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
//        return new StudentViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
//        StudentItem studentItem = studentItems.get(position);
//        holder.roll.setText(studentItem.getRoll());
//        holder.name.setText(studentItem.getName());
//        holder.status.setText(studentItem.getStatus());
//    }
//
//    @Override
//    public int getItemCount() {
//        return studentItems.size();
//    }
//}
