package com.example.kanban_firebase_projmanagement

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class TaskAdapter(
    private val columnTitle: String,
    private val tasks: MutableList<Task>,
    private val db: FirebaseFirestore,
    private val userId: String,
    private val columnId: String
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val taskTitleTextView: TextView = itemView.findViewById(R.id.taskTitleTextView)
        val taskDescriptionView: TextView = itemView.findViewById(R.id.taskDescriptionTextView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)

        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    showEditDeleteTaskDialog(tasks[position], position, itemView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TaskViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitleTextView.text = task.title
        holder.taskDescriptionView.text = task.description

        //set different background colours for tasks in different columns
        val colourRes = when (columnTitle){
            "To-Do" -> android.R.color.holo_blue_bright
            "In Progress" -> android.R.color.holo_purple
            "Done" -> android.R.color.holo_green_light
            else -> android.R.color.darker_gray
        }
        holder.cardView.setCardBackgroundColor((holder.itemView.context.getColor(colourRes)))
    }

    override fun getItemCount(): Int = tasks.size

    fun moveItem(fromPosition: Int, toPosition: Int){
        val fromTask = tasks.removeAt(fromPosition)
        tasks.add(toPosition, fromTask)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun removeItem(position: Int){
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun showEditDeleteTaskDialog(task: Task, position: Int, view: View) {
        val dialogBuilder = AlertDialog.Builder(view.context)
        val dialogView = LayoutInflater.from(view.context).inflate(R.layout.dialog_edit_delete_task, null)
        dialogBuilder.setView(dialogView)

        val taskTitle = dialogView.findViewById<EditText>(R.id.editTextTaskTitle)
        val taskDescription = dialogView.findViewById<EditText>(R.id.editTextTaskDescription)

        taskTitle.setText(task.title)
        taskDescription.setText(task.description)

        dialogBuilder.setPositiveButton("Update") { dialog, _ ->
            val updatedTitle = taskTitle.text.toString()
            val updatedDescription = taskDescription.text.toString()

            if (updatedTitle.isNotEmpty()) {
                task.title = updatedTitle
                task.description = updatedDescription

                notifyItemChanged(position)
                updateTaskInFirestore(task)
            } else {
                Toast.makeText(view.context, "Task title cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialogBuilder.setNeutralButton("Delete") { dialog, _ ->
            removeItem(position)
            deleteTaskFromFirestore(task)
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.create().show()
    }

    private fun updateTaskInFirestore(task: Task) {
        task.taskId?.let { taskId ->
            db.collection("users")
                .document(userId)
                .collection("kanban_columns")
                .document(columnId)
                .collection("tasks")
                .document(taskId)
                .set(task)
                .addOnSuccessListener {
                    // Task updated successfully
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
    }

    private fun deleteTaskFromFirestore(task: Task) {
        task.taskId?.let { taskId ->
            db.collection("users")
                .document(userId)
                .collection("kanban_columns")
                .document(columnId)
                .collection("tasks")
                .document(taskId)
                .delete()
                .addOnSuccessListener {
                    // Task deleted successfully
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
    }
}
