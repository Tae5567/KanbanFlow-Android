package com.example.kanban_firebase_projmanagement

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val taskTitleTextView: TextView =itemView.findViewById(R.id.taskTitleTextView)
    private val taskDescriptionTextView: TextView = itemView.findViewById(R.id.taskDescriptionTextView)

    fun bind(task: Task){
        taskTitleTextView.text = task.title
        taskDescriptionTextView.text = task.description
    }
}