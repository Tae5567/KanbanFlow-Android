package com.example.kanban_firebase_projmanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class ColumnAdapter(private val columns: MutableList<Column>,
                    private val userId: String,
                    private val db: FirebaseFirestore
): RecyclerView.Adapter<ColumnAdapter.ColumnViewHolder>() {

    inner class ColumnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val columnTitle: TextView = itemView.findViewById(R.id.columnTitleTextView)
        val taskRecyclerView: RecyclerView = itemView.findViewById(R.id.tasksRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColumnViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_column, parent, false)
        return ColumnViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColumnViewHolder, position: Int) {
        val column = columns[position]
        holder.columnTitle.text = column.title

        val columnId = when (column.title) {
            "To-Do" -> "to_do"
            "In Progress" -> "in_progress"
            "Done" -> "done"
            else -> ""
        }

        val taskAdapter = TaskAdapter(column.title, column.tasks, db, userId, columnId)
        holder.taskRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.taskRecyclerView.adapter = taskAdapter

        // attach ItemTouchHelper to the RecyclerView of each column
        val callback = TaskTouchHelperCallback(taskAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(holder.taskRecyclerView)
    }

    override fun getItemCount(): Int = columns.size

}