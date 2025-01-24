package com.example.practica_7

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTask : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)
        var addBt : Button = findViewById(R.id.addTask)
        var volverBt : Button = findViewById(R.id.backBt)
        var tarea : EditText = findViewById(R.id.taskName)
        var descripcion : EditText = findViewById(R.id.taskDescription)
        val fecha : TextView = findViewById(R.id.taskDate)

        fecha.setOnClickListener(){
            mostrarDatePicker(fecha)
        }

        addBtFunction(addBt, tarea, descripcion, fecha)

        volverBt.setOnClickListener {
            finish()
        }
    }

    fun addBtFunction(addBt: Button, tarea: EditText, descripcion: EditText, fecha: TextView) {
        if (isSelected()) {
            addBt.text = "Editar"
            inicializar(addBt)
            addBt.setOnClickListener {
                if (tarea.text.isNotEmpty()) {
                    val nombre = tarea.text.toString()
                    val desc = descripcion.text.toString()
                    val fecha = fecha.text.toString()
                    val taskDao = (application as TaskApp).room.taskDao()
                    val task = Task(
                        intent.getIntExtra("taskId", 0),
                        nombre,
                        desc,
                        fecha,
                        intent.getBooleanExtra("taskState", false)
                    )

                    GlobalScope.launch {
                        Log.d("Task", "ha cambiado:" + haveChanged(task, taskDao).toString())
                        if (haveChanged(task, taskDao)){
                            taskDao.update(task)
                        }
                    }

                    finish()
                } else {
                    Toast.makeText(this, "No puedes introducir una tarea vacía", Toast.LENGTH_LONG)
                        .show()
                }
            }
        } else {
            addBt.setOnClickListener {
                if (tarea.text.isNotEmpty()) {
                    val nombre = tarea.text.toString()
                    val desc = descripcion.text.toString()
                    val fecha = fecha.text.toString()
                    val estado = false
                    val taskDao = (application as TaskApp).room.taskDao()
                    var task = Task(0, nombre, desc,fecha, estado)
                    GlobalScope.launch {
                        taskDao.insert(task)
                    }
                    finish()
                } else {
                    Toast.makeText(this, "No puedes introducir una tarea vacía", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    suspend fun haveChanged(task: Task, taskDao: TaskDao) : Boolean{
        val currenTask : Task = taskDao.getTaskById(intent.getIntExtra("taskId", 0))
        if (currenTask.nombre != task.nombre || currenTask.descripcion != task.descripcion  || currenTask.fecha != task.fecha){
            return true
        }
        return false
    }

    fun isSelected() : Boolean{
        return intent.getBooleanExtra("IS_EDIT", false)
    }

    fun inicializar(addBt : Button){
        val tarea : EditText = findViewById(R.id.taskName)
        val descripcion : EditText = findViewById(R.id.taskDescription)
        val fecha : TextView = findViewById(R.id.taskDate)
        tarea.setText(intent.getStringExtra("taskName"))
        descripcion.setText(intent.getStringExtra("taskDesc"))
        fecha.text = intent.getStringExtra("taskDate")
    }

    fun mostrarDatePicker(fecha : TextView){
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateString = obtenerFecha(calendar.time)
                fecha.setText(dateString)
                println("Fecha seleccionada: $dateString")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    fun obtenerFecha(date: Date) : String{
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }
}