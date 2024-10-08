package com.example.kanban_firebase_projmanagement

import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class KanbanActivity : AppCompatActivity() {

    private lateinit var columns: MutableList<Column>
    private lateinit var columnsAdapter: ColumnAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var  db: FirebaseFirestore
    private var user: FirebaseUser? = null

    private val todoColumnId = "to_do"
    private val inProgressColumnId = "in_progress"
    private val doneColumnId = "done"

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kanban)

        //initialize firebase authentication
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        //initialize firestore
        db = FirebaseFirestore.getInstance()

        //initialize empty columns
        columns = mutableListOf(
            Column("To-Do", mutableListOf()),
            Column("In Progress", mutableListOf()),
            Column("Done", mutableListOf())
        )

        val columnsRecyclerView : RecyclerView = findViewById(R.id.columnsRecyclerView)
        columnsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        columnsAdapter = ColumnAdapter(columns, user?.uid ?: "", db)
        columnsRecyclerView.adapter = columnsAdapter

        //load tasks from Firestore when user logs in
        loadTasksFromFirestore()

        //handle add tasks
        val btnAddTask: Button = findViewById(R.id.btn_add_task)
        btnAddTask.setOnClickListener {
            showAddTaskDialog()
        }

        //handle log out button
        val logoutButton: Button = findViewById(R.id.btn_logout)
        logoutButton.setOnClickListener {
            performLogout()

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }


    private fun showAddTaskDialog() {
        //inflate dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        //set up the dialog
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add New Task")
            .setPositiveButton("Add") { dialog, _ ->
                //get input from the dialog
                val taskTitle = dialogView.findViewById<EditText>(R.id.editTextTaskTitle).text.toString()
                val taskDescription = dialogView.findViewById<EditText>(R.id.editTextTaskDescription).text.toString()

                //validate input
                if (taskTitle.isNotEmpty()){
                    //add the new task to the first column
                    val newTask = Task(taskTitle, taskDescription)
                    columns[0].tasks.add(newTask) // add to the first column
                    columnsAdapter.notifyItemChanged(0)//notify adapter

                    //save task to Firestore
                    saveTasktoFirestore(todoColumnId, newTask)

                    Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Task title cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") {dialog, _ ->
                dialog.dismiss()
            }

        // show the dialog
        dialogBuilder.create().show()

    }

    private fun saveTasktoFirestore(columnId: String, task: Task){
        val userId = user?.uid ?: return

        val taskId = task.taskId ?: db.collection("users")
            .document(userId)
            .collection("kanban_columns")
            .document(columnId)
            .collection("tasks")
            .document().id

        task.taskId = taskId

        db.collection("users")
            .document(userId)
            .collection("kanban_columns")
            .document(columnId)
            .collection("tasks")
            .document(taskId)
            .set(task)
            .addOnSuccessListener {
                Toast.makeText(this, "Tasked saved to Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save task: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun loadTasksFromFirestore() {
        val userId = user?.uid ?: return

        val columnIds = listOf(todoColumnId, inProgressColumnId, doneColumnId)

        columnIds.forEachIndexed { index, columnId ->
            db.collection("users")
                .document(userId)
                .collection("kanban_columns")
                .document(columnId)
                .collection("tasks")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tasks = querySnapshot.documents.map { it.toObject(Task::class.java)!! }
                    columns[index].tasks.clear()
                    columns[index].tasks.addAll(tasks)
                    columnsAdapter.notifyItemChanged(index)
                }
                .addOnFailureListener{ e ->
                    Toast.makeText(this, "Failed to load tasks", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun performLogout() {
        //clear any saved user data (if needed)
        val sharedPreferences= getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        //show a logged out message
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()

        //redirect to main activity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }



}
