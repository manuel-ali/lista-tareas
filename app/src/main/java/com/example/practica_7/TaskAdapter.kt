package com.example.practica_7
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskDeleted: (Task) -> Unit,
    private val onTaskSelected: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTitle : TextView = itemView.findViewById(R.id.taskTitle)
        var taskCheckBox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
        val deleteTaskButton: ImageButton = itemView.findViewById(R.id.taskDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        var taskDao: TaskDao = (holder.itemView.context.applicationContext as TaskApp).room.taskDao()
        var task = tasks[position]
        holder.taskTitle.text = task.nombre
        holder.taskCheckBox.isChecked = task.estado

        holder.taskTitle.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                taskDao.update(task)
            }
        }

        holder.taskCheckBox.setOnCheckedChangeListener(null)

        holder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                taskDao.updateEstado(isChecked, task.id)
                withContext(Dispatchers.Main) {
                    holder.taskCheckBox.isChecked = isChecked
                }
            }
        }

        holder.deleteTaskButton.setOnClickListener {
            onTaskDeleted(task)
            CoroutineScope(Dispatchers.IO).launch {
                taskDao.delete(task)
            }
        }

        holder.taskTitle.setOnClickListener {
            onTaskSelected(task)
        }
    }

    override fun getItemCount(): Int = tasks.size
}