package com.example.kanban_firebase_projmanagement

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class TaskTouchHelperCallback(private val adapter: TaskAdapter) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = 0 //disable swiping
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove( recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) : Boolean{
        val fromPosition = source.adapterPosition
        val toPosition = target.adapterPosition
        adapter.moveItem(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int ){
        // no swipe functionality needed here
    }
}