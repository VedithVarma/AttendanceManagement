package com.example.myattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder> {
    private Context context;
    private ArrayList<FacultyItem> facultyList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FacultyAdapter(Context context, ArrayList<FacultyItem> facultyList) {
        this.context = context;
        this.facultyList = facultyList;
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faculty_item, parent, false);
        return new FacultyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        FacultyItem currentItem = facultyList.get(position);
        holder.nameTextView.setText(currentItem.getName());
        holder.emailTextView.setText(currentItem.getEmail());
        holder.departmentTextView.setText(currentItem.getDepartment());
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }

    public static class FacultyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView emailTextView;
        public TextView departmentTextView;

        public FacultyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.faculty_name);
            emailTextView = itemView.findViewById(R.id.faculty_email);
            departmentTextView = itemView.findViewById(R.id.faculty_department);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
